/*
 * Copyright 2018 Valtech GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.valtech.aecu.core.installhook;

import de.valtech.aecu.service.AecuException;
import de.valtech.aecu.service.AecuService;
import de.valtech.aecu.service.ExecutionResult;
import de.valtech.aecu.service.HistoryEntry;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.fs.api.ProgressTrackerListener;
import org.apache.jackrabbit.vault.fs.io.Archive;
import org.apache.jackrabbit.vault.packaging.InstallContext;
import org.apache.jackrabbit.vault.packaging.InstallHook;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * InstallHook handling installation of groovy scripts. The InstallHook gathers groovy scripts
 * contained in the installed vault package and executes them depending on active runmodes and if
 * the script has been added, modified or never executed. <br>
 * Example usage in content-package-maven-plugin:
 * 
 * <pre>
 * {@code
 * <plugin>
 *     <groupId>com.day.jcr.vault</groupId>
 *     <artifactId>content-package-maven-plugin</artifactId>
 *     <extensions>true</extensions>
 *     <configuration>
 *         <filterSource>src/main/content/META-INF/vault/filter.xml</filterSource>
 *         <verbose>true</verbose>
 *         <failOnError>true</failOnError>
 *         <group>Valtech</group>
 *         <properties>
 *             <installhook.aecu.class>de.valtech.aecu.core.installhook.AecuInstallHook</installhook.aecu.class>
 *         </properties>
 *     </configuration>
 * </plugin>
 * }
 * </pre>
 */
public class AecuInstallHook implements InstallHook {

    private static final Logger LOG = LoggerFactory.getLogger(AecuInstallHook.class);

    private final OsgiServiceProvider osgiServiceProvider;
    private AecuTrackerListener listener;

    public AecuInstallHook() {
        this.osgiServiceProvider = new OsgiServiceProvider(this.getClass());
    }

    @Override
    public void execute(InstallContext installContext) throws PackageException {
        LOG.info("Executing in phase {}", installContext.getPhase());
        ServiceReference<AecuService> aecuServiceReference = osgiServiceProvider.getServiceReference(AecuService.class);
        AecuService aecuService = osgiServiceProvider.getService(aecuServiceReference);

        try {
            switch (installContext.getPhase()) {
                case PREPARE:
                    ProgressTrackerListener originalListener = installContext.getOptions().getListener();
                    listener = new AecuTrackerListener(originalListener, aecuService);
                    installContext.getOptions().setListener(listener);
                    break;
                case INSTALLED:

                    Archive archive = installContext.getPackage().getArchive();
                    List<String> allValidScriptCandidatesInArchive = findCandidates("", archive.getJcrRoot(), aecuService);
                    List<String> scriptsForInstallation =
                            getScriptsForExecution(allValidScriptCandidatesInArchive, installContext);

                    if (!scriptsForInstallation.isEmpty()) {
                        HistoryEntry installationHistory = executeScripts(scriptsForInstallation, aecuService, installContext);
                        if (!HistoryEntry.RESULT.SUCCESS.equals(installationHistory.getResult())) {
                            throw new PackageException("Failed installation, check installation history at "
                                    + installationHistory.getRepositoryPath());
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (IOException | AecuException e) {
            throw new PackageException(e);
        } finally {
            osgiServiceProvider.ungetService(aecuServiceReference);
        }
    }

    private List<String> getScriptsForExecution(List<String> allValidScriptCandidatesInArchive, InstallContext installContext) {
        List<String> scriptsForExecution = new ArrayList<>();
        List<String> modifiedOrAddedScriptPaths = listener.getModifiedOrAddedPaths();
        for (String groovyScriptPath : allValidScriptCandidatesInArchive) {
            try {
                HookExecutionHistory hookExecutionHistory =
                        new HookExecutionHistory(installContext.getSession(), groovyScriptPath);
                if (shouldExecute(modifiedOrAddedScriptPaths, groovyScriptPath, hookExecutionHistory)) {
                    scriptsForExecution.add(groovyScriptPath);
                }
            } catch (AecuException e) {
                listener.logError("Could not obtain execution history for " + groovyScriptPath, e);
            }

        }
        return scriptsForExecution;
    }

    private boolean shouldExecute(List<String> modifiedOrAddedScriptPaths, String groovyScriptPath,
            HookExecutionHistory hookExecutionHistory) {
        return modifiedOrAddedScriptPaths.contains(groovyScriptPath) || !hookExecutionHistory.hasBeenExecutedBefore();
    }

    private HistoryEntry executeScripts(List<String> scriptsForExecution, AecuService aecuService, InstallContext installContext)
            throws AecuException, IOException {
        HistoryEntry installationHistory = aecuService.createHistoryEntry();
        for (String groovyScriptPath : scriptsForExecution) {
            HookExecutionHistory hookExecutionHistory = new HookExecutionHistory(installContext.getSession(), groovyScriptPath);
            try {
                installationHistory = executeScript(aecuService, installationHistory, groovyScriptPath);
                hookExecutionHistory.setExecuted();
            } catch (AecuException e) {
                listener.logError("Error executing script " + groovyScriptPath, e);
            }
        }
        installationHistory = aecuService.finishHistoryEntry(installationHistory);
        return installationHistory;
    }

    private HistoryEntry executeScript(AecuService aecuService, HistoryEntry installationHistory, String groovyScriptPath)
            throws AecuException {
        listener.logMessage("Executing script " + groovyScriptPath);
        ExecutionResult result = aecuService.execute(groovyScriptPath);
        installationHistory = aecuService.storeExecutionInHistory(installationHistory, result);
        listener.logMessage("Executed script " + groovyScriptPath + ", output: " + result.getOutput());
        return installationHistory;
    }

    // mildly duplicated
    private List<String> findCandidates(String parent, Archive.Entry entry, AecuService aecuService) {
        List<String> candidates = new ArrayList<>();
        if (entry == null) {
            return candidates;
        }
        String entryName = entry.getName();
        String entryPath = parent + "/" + entryName;

        if (entry.isDirectory() && aecuService.matchesRunmodes(entryName)) {
            for (Archive.Entry childEntry : entry.getChildren()) {
                candidates.addAll(findCandidates(entryPath, childEntry, aecuService));
            }
        } else if (aecuService.isValidScriptName(entryName)) {
            candidates.add(StringUtils.substringAfter(entryPath, "/jcr_root"));
        }
        return candidates;
    }

}
