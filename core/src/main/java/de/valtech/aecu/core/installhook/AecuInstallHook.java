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

import org.apache.jackrabbit.vault.fs.api.ProgressTrackerListener;
import org.apache.jackrabbit.vault.packaging.InstallContext;
import org.apache.jackrabbit.vault.packaging.InstallHook;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InstallHook handling installation of groovy scripts.
 */
public class AecuInstallHook implements InstallHook {

    private static final Logger LOG = LoggerFactory.getLogger(AecuInstallHook.class);

    private final BundleContext bundleContext;
    private AecuTrackerListener listener;

    public AecuInstallHook() {
        Bundle currentBundle = FrameworkUtil.getBundle(this.getClass());
        if (currentBundle == null) {
            throw new IllegalStateException("The class " + this.getClass() + " was not loaded through a bundle classloader");
        }
        bundleContext = currentBundle.getBundleContext();
        if (bundleContext == null) {
            throw new IllegalStateException("Could not get bundle context for bundle " + currentBundle);
        }

    }

    private ServiceReference<AecuService> getAecuServiceReference() {
        ServiceReference<AecuService> aecuServiceReference = bundleContext.getServiceReference(AecuService.class);
        if (aecuServiceReference == null) {
            throw new IllegalStateException("Could not retrieve service reference for AECUService.");
        }
        return aecuServiceReference;
    }

    @Override
    public void execute(InstallContext installContext) throws PackageException {
        LOG.info("Executing in phase {}", installContext.getPhase());
        ServiceReference<AecuService> aecuServiceReference = getAecuServiceReference();
        AecuService aecuService = bundleContext.getService(aecuServiceReference);
        if (aecuService == null) {
            throw new IllegalStateException("Could not get the AECU service, verify that the bundle was installed correctly!");
        }
        try {
            switch (installContext.getPhase()) {
                case PREPARE:
                    ProgressTrackerListener originalListener = installContext.getOptions().getListener();
                    listener = new AecuTrackerListener(originalListener, aecuService, "A", "M", "-");
                    installContext.getOptions().setListener(listener);
                    break;
                case INSTALLED:
                    boolean isDryRun = installContext.getOptions().isDryRun();
                    if (isDryRun) {
                        LOG.debug("Skipping any modifications, dry run mode selected");
                    } else {
                        HistoryEntry installationHistory = aecuService.createHistoryEntry();
                        for (String groovyScriptPath : listener.getGroovyScriptPaths()) {
                            LOG.info("Executing script {}", groovyScriptPath);
                            ExecutionResult result = aecuService.execute(groovyScriptPath);
                            installationHistory = aecuService.storeExecutionInHistory(installationHistory, result);
                            LOG.info("Executed script {}: {}", groovyScriptPath, result.getOutput());
                        }
                        aecuService.finishHistoryEntry(installationHistory);
                        // TODO: from my point of view the installation should not be failed in case
                        // a fallback script was executed successfully
                        if (!installationHistory.getResult().equals(HistoryEntry.RESULT.SUCCESS)) {
                            throw new PackageException("Failed installation, check installation history at "
                                    + installationHistory.getRepositoryPath());
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (AecuException e) {
            throw new PackageException(e);
        } finally {
            if (aecuServiceReference != null) {
                bundleContext.ungetService(aecuServiceReference);
            }
        }
    }

}
