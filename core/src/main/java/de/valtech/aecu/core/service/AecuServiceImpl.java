/*
 * Copyright 2018 - 2019 Valtech GmbH
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
package de.valtech.aecu.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icfolson.aem.groovy.console.GroovyConsoleService;
import com.icfolson.aem.groovy.console.response.RunScriptResponse;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.ExecutionState;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.api.service.HistoryEntry.STATE;
import de.valtech.aecu.core.history.HistoryUtil;
import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;

/**
 * AECU service.
 *
 * @author Roland Gruber
 */
@Component(service = AecuService.class)
public class AecuServiceImpl implements AecuService {

    private static final String ERR_NO_RESOLVER = "Unable to get service resource resolver";
    protected static final String DIR_FALLBACK_SCRIPT = "fallback.groovy";

    private static final Logger LOG = LoggerFactory.getLogger(AecuServiceImpl.class);

    @Reference
    private ServiceResourceResolverService resolverService;

    @Reference
    private SlingSettingsService slingSettings;

    @Reference
    private GroovyConsoleService groovyConsoleService;

    @Reference
    private HistoryUtil historyUtil;

    @Override
    public String getVersion() {
        return FrameworkUtil.getBundle(AecuServiceImpl.class).getVersion().toString();
    }

    @Override
    public List<String> getFiles(String path) throws AecuException {
        try (ResourceResolver resolver = resolverService.getServiceResourceResolver()) {
            return findCandidates(resolver, path);
        } catch (LoginException e) {
            throw new AecuException(ERR_NO_RESOLVER, e);
        }
    }

    /**
     * Finds all candidates for scripts to run.
     *
     * @param resolver service resource resolver
     * @param path     starting path
     * @return candidate list
     * @throws AecuException error finding candidates
     */
    private List<String> findCandidates(ResourceResolver resolver, String path) throws AecuException {
        if (path == null) {
            throw new AecuException("Path is null");
        }
        Resource resource = resolver.getResource(path);
        if (resource == null) {
            throw new AecuException("Path is invalid");
        }
        List<String> candidates = new ArrayList<>();
        if (isFolder(resource) && matchesRunmodes(resource.getName())) {
            List<String> childNames = new ArrayList<>();
            for (Resource child : resource.getChildren()) {
                childNames.add(child.getName());
            }
            childNames.sort(null);
            for (String childName : childNames) {
                candidates.addAll(findCandidates(resolver, resource.getChild(childName).getPath()));
            }
        } else if (isValidScriptName(resource.getName())) {
            candidates.add(path);
        }
        return candidates;
    }

    /**
     * Checks if the resource is a folder.
     *
     * @param resource resource
     * @return is folder
     */
    private boolean isFolder(Resource resource) {
        String type = resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, String.class);
        return JcrResourceConstants.NT_SLING_FOLDER.equals(type) || JcrResourceConstants.NT_SLING_ORDERED_FOLDER.equals(type)
                || JcrConstants.NT_FOLDER.equals(type);
    }

    @Override
    public boolean matchesRunmodes(String name) {
        if (!name.contains(".")) {
            return true;
        }
        Set<String> runModes = slingSettings.getRunModes();
        String runModeString = name.substring(name.indexOf('.') + 1);
        String[] combinations = runModeString.split(";");
        for (String combination : combinations) {
            String[] modes = combination.split("\\.");
            if (runModes.containsAll(Arrays.asList(modes))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValidScriptName(String name) {
        if (!name.endsWith(".groovy")) {
            return false;
        }
        return !name.contains(".fallback.") && !DIR_FALLBACK_SCRIPT.equals(name);
    }

    @Override
    public ExecutionResult execute(String path) throws AecuException {
        try (ResourceResolver resolver = resolverService.getContentMigratorResourceResolver()) {
            Resource resource = resolver.getResource(path);
            if (resource == null) {
                throw new AecuException("Path is invalid");
            }
            if (!isValidScriptName(resource.getName())) {
                throw new AecuException("Invalid script name");
            }
            return executeScript(resolver, path);
        } catch (LoginException e) {
            throw new AecuException(ERR_NO_RESOLVER, e);
        }
    }

    /**
     * Executes the script.
     *
     * @param resolver resource resolver
     * @param path     path
     * @return result
     */
    private ExecutionResult executeScript(ResourceResolver resolver, String path) {
        GroovyConsoleRequest request = new GroovyConsoleRequest(resolver);
        LOG.info("Executing script " + path);
        RunScriptResponse response = groovyConsoleService.runScript(request, path);
        boolean success = StringUtils.isBlank(response.getExceptionStackTrace());
        if (success) {
            LOG.info("Executed script " + path + " with status OK");
        } else {
            LOG.error("Executed script " + path + " with status FAILED");
        }
        String result = response.getResult();
        ExecutionResult fallbackResult = null;
        if (!success && (getFallbackScript(resolver, path) != null)) {
            fallbackResult = executeScript(resolver, getFallbackScript(resolver, path));
        }
        ExecutionState state = success ? ExecutionState.SUCCESS : ExecutionState.FAILED;
        return new ExecutionResult(state, response.getRunningTime(), result,
                response.getOutput() + response.getExceptionStackTrace(), fallbackResult, path);
    }

    /**
     * Returns the fallback script name if any exists.
     *
     * @param resolver resource resolver
     * @param path     original script path
     * @return fallback script path
     */
    protected String getFallbackScript(ResourceResolver resolver, String path) {
        String name = path.substring(path.lastIndexOf('/') + 1);
        if (name.contains(".fallback.") || DIR_FALLBACK_SCRIPT.equals(name)) {
            // skip if script is a fallback script itself
            return null;
        }
        String baseName = name.substring(0, name.indexOf('.'));
        String fallbackPath = path.substring(0, path.lastIndexOf('/') + 1) + baseName + ".fallback.groovy";
        if (resolver.getResource(fallbackPath) != null) {
            return fallbackPath;
        }
        String directoryFallbackPath = path.substring(0, path.lastIndexOf('/') + 1) + DIR_FALLBACK_SCRIPT;
        if (resolver.getResource(directoryFallbackPath) != null) {
            return directoryFallbackPath;
        }
        return null;
    }

    @Override
    public HistoryEntry createHistoryEntry() throws AecuException {
        try (ResourceResolver resolver = resolverService.getServiceResourceResolver()) {
            HistoryEntry entry = historyUtil.createHistoryEntry(resolver);
            resolver.commit();
            return entry;
        } catch (PersistenceException e) {
            throw new AecuException("Unable to create history", e);
        } catch (LoginException e) {
            throw new AecuException(ERR_NO_RESOLVER, e);
        }
    }

    @Override
    public HistoryEntry finishHistoryEntry(HistoryEntry history) throws AecuException {
        try (ResourceResolver resolver = resolverService.getServiceResourceResolver()) {
            historyUtil.finishHistoryEntry(history, resolver);
            resolver.commit();
            return history;
        } catch (LoginException e) {
            throw new AecuException(ERR_NO_RESOLVER, e);
        } catch (PersistenceException e) {
            throw new AecuException("Unable to finish history " + history.getRepositoryPath(), e);
        }
    }

    @Override
    public HistoryEntry storeExecutionInHistory(HistoryEntry history, ExecutionResult result) throws AecuException {
        if ((history == null) || !STATE.RUNNING.equals(history.getState())) {
            throw new AecuException("Invalid history entry.");
        }
        history.getSingleResults().add(result);
        try (ResourceResolver resolver = resolverService.getServiceResourceResolver()) {
            historyUtil.storeExecutionInHistory(history, result, resolver);
            resolver.commit();
            return history;
        } catch (LoginException e) {
            throw new AecuException(ERR_NO_RESOLVER, e);
        } catch (PersistenceException e) {
            throw new AecuException("Unable to add history entry " + history.getRepositoryPath(), e);
        }
    }

    @Override
    public List<HistoryEntry> getHistory(int startIndex, int count) throws AecuException {
        try (ResourceResolver resolver = resolverService.getServiceResourceResolver()) {
            return historyUtil.getHistory(startIndex, count, resolver);
        } catch (LoginException e) {
            throw new AecuException(ERR_NO_RESOLVER, e);
        }
    }

}
