/*
 *  Copyright 2018 Valtech GmbH
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package de.valtech.aecu.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.icfolson.aem.groovy.console.GroovyConsoleService;
import com.icfolson.aem.groovy.console.response.RunScriptResponse;

import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;
import de.valtech.aecu.service.AecuException;
import de.valtech.aecu.service.AecuService;
import de.valtech.aecu.service.ExecutionResult;
import de.valtech.aecu.service.HistoryEntry;
import de.valtech.aecu.service.HistoryEntry.RESULT;
import de.valtech.aecu.service.HistoryEntry.STATE;

/**
 * AECU service.
 * 
 * @author Roland Gruber
 */
@Component(service=AecuService.class)
public class AecuServiceImpl implements AecuService {
    
    private static final String ATTR_RUN_OUTPUT = "runOutput";

    private static final String ATTR_RUN_SUCCESS = "runSuccess";

    private static final String ATTR_RUN_RESULT = "runResult";

    private static final String ATTR_RUN_TIME = "runTime";

    private static final String ATTR_RESULT = "result";

    private static final String ATTR_STATE = "state";

    private static final String ATTR_START = "start";

    private static final String ATTR_END = "end";

    private static final String HISTORY_BASE = "/var/aecu";

    @Reference
    private ServiceResourceResolverService resolverService;
    
    @Reference
    private SlingSettingsService slingSettings;
    
    @Reference
    private GroovyConsoleService groovyConsoleService;

    @Override
    public String getVersion() {
        return FrameworkUtil.getBundle(AecuServiceImpl.class).getVersion().toString();
    }

    @Override
    public List<String> getFiles(String path) throws AecuException {
        try (ResourceResolver resolver = resolverService.getServiceResourceResolver()) {
            return findCandidates(resolver, path);
        }
        catch (LoginException e) {
            throw new AecuException("Unable to get service resource resolver", e);
        }
    }

    /**
     * Finds all candidates for scripts to run.
     * 
     * @param resolver service resource resolver
     * @param path starting path
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
            for (Resource child : resource.getChildren()) {
                candidates.addAll(findCandidates(resolver, child.getPath()));
            }
        }
        else if (isValidScriptName(resource.getName())) {
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
        return JcrResourceConstants.NT_SLING_FOLDER.equals(type)
                        || JcrResourceConstants.NT_SLING_ORDERED_FOLDER.equals(type)
                        || JcrConstants.NT_FOLDER.equals(type);
    }

    /**
     * Checks if the folder matches the system's run modes if specified in folder name.
     * 
     * @param name resource name
     * @return matches run modes
     */
    protected boolean matchesRunmodes(String name) {
        if (!name.contains(".")) {
            return true;
        }
        Set<String> runModes = slingSettings.getRunModes();
        String runModeString = name.substring(name.indexOf(".") + 1);
        String[] combinations = runModeString.split(";");
        for (String combination : combinations) {
            String[] modes = combination.split("\\.");
            if (runModes.containsAll(Arrays.asList(modes))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the name is a valid script.
     * 
     * @param name file name
     * @return is valid
     */
    protected boolean isValidScriptName(String name) {
        if (!name.endsWith(".groovy")) {
            return false;
        }
        if (name.contains(".fallback.")) {
            return false;
        }
        return true;
    }

    @Override
    public ExecutionResult execute(String path) throws AecuException {
        try (ResourceResolver resolver = resolverService.getServiceResourceResolver()) {
            Resource resource = resolver.getResource(path);
            if (resource == null) {
                throw new AecuException("Path is invalid");
            }
            if (!isValidScriptName(resource.getName())) {
                throw new AecuException("Invalid script name");
            }
            ExecutionResult result = executeScript(resolver, path);
            return result;
        }
        catch (LoginException e) {
            throw new AecuException("Unable to get service resource resolver", e);
        }
    }

    /**
     * Executes the script.
     * 
     * @param resolver resource resolver
     * @param path path
     * @return result
     */
    private ExecutionResult executeScript(ResourceResolver resolver, String path) {
        GroovyConsoleRequest request = new GroovyConsoleRequest(resolver);
        RunScriptResponse response = groovyConsoleService.runScript(request, path);
        boolean success = StringUtils.isBlank(response.getExceptionStackTrace());
        String result = response.getResult();
        ExecutionResult fallbackResult = null;
        if (!success && (getFallbackScript(resolver, path) != null)) {
            fallbackResult = executeScript(resolver, getFallbackScript(resolver, path));
        }
        return new ExecutionResult(success, response.getRunningTime(), result, response.getOutput() + response.getExceptionStackTrace(), fallbackResult);
    }
    
    /**
     * Returns the fallback script name if any exists.
     * 
     * @param resolver resource resolver
     * @param path original script path
     * @return fallback script path
     */
    protected String getFallbackScript(ResourceResolver resolver, String path) {
        String name = path.substring(path.lastIndexOf("/") + 1);
        if (name.contains(".fallback.")) {
            // skip if script is a fallback script itself
            return null;
        }
        String baseName = name.substring(0, name.indexOf("."));
        String fallbackPath = path.substring(0, path.lastIndexOf("/") + 1) + baseName + ".fallback.groovy";
        if (resolver.getResource(fallbackPath) != null) {
            return fallbackPath;
        }
        return null;
    }

    @Override
    public HistoryEntry createHistoryEntry() throws AecuException {
        try (ResourceResolver resolver = resolverService.getServiceResourceResolver()) {
            HistoryEntryImpl history = new HistoryEntryImpl();
            Calendar start = new GregorianCalendar();
            String basePath = HISTORY_BASE + "/" + start.get(Calendar.YEAR) + "/" + (start.get(Calendar.MONTH) + 1) + "/" + start.get(Calendar.DAY_OF_MONTH);
            String nodeName = generateHistoryNodeName();
            String nodePath = basePath + "/" + nodeName;
            createPath(basePath, resolver, JcrResourceConstants.NT_SLING_FOLDER);
            createPath(nodePath, resolver, JcrConstants.NT_UNSTRUCTURED);
            Resource resource = resolver.getResource(nodePath);
            ModifiableValueMap values = resource.adaptTo(ModifiableValueMap.class);
            values.put(ATTR_START, start);
            values.put(ATTR_STATE, STATE.RUNNING.name());
            values.put(ATTR_RESULT, RESULT.UNKNOWN.name());
            history.setStart(start.getTime());
            history.setPath(nodePath);
            try {
                resolver.commit();
            } catch (PersistenceException e) {
                throw new AecuException("Unable to create history " + nodePath, e);
            }
            return history;
        }
        catch (LoginException e) {
            throw new AecuException("Unable to get service resource resolver", e);
        }
    }

    /**
     * Creates the folder at the given path if not yet existing.
     * 
     * @param path path
     * @param resolver resource resolver
     * @param primaryType primary type
     * @throws AecuException error creating folder
     */
    protected void createPath(String path, ResourceResolver resolver, String primaryType) throws AecuException {
        Resource folder = resolver.getResource(path);
        if (folder == null) {
            String parent = path.substring(0, path.lastIndexOf("/"));
            String name = path.substring(path.lastIndexOf("/") + 1);
            if (resolver.getResource(parent) == null) {
                createPath(parent, resolver, primaryType);
            }
            Map<String, Object> properties = new HashMap<>();
            properties.put(JcrConstants.JCR_PRIMARYTYPE, primaryType);
            try {
                resolver.create(resolver.getResource(parent), name, properties);
            } catch (PersistenceException e) {
                throw new AecuException("Unable to create " + path, e);
            }
        }
    }

    /**
     * Generates the node name for a history entry.
     * 
     * @return name
     */
    private String generateHistoryNodeName() {
        Random random = new Random();
        return System.currentTimeMillis() + "" + random.nextInt(100000);
    }

    @Override
    public HistoryEntry finishHistoryEntry(HistoryEntry history) throws AecuException {
        try (ResourceResolver resolver = resolverService.getServiceResourceResolver()) {
            Resource resource = resolver.getResource(history.getRepositoryPath());
            ModifiableValueMap values = resource.adaptTo(ModifiableValueMap.class);
            Calendar end = new GregorianCalendar();
            values.put(ATTR_END, end);
            values.put(ATTR_STATE, STATE.FINISHED.name());
            values.put(ATTR_RESULT, history.getResult().name());
            resolver.commit();
            return history;
        }
        catch (LoginException e) {
            throw new AecuException("Unable to get service resource resolver", e);
        }
        catch (PersistenceException e) {
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
            String path = history.getRepositoryPath() + "/" + history.getSingleResults().size();
            saveExecutionResultInHistory(result, path, resolver);
            resolver.commit();
            return history;
        }
        catch (LoginException e) {
            throw new AecuException("Unable to get service resource resolver", e);
        }
        catch (PersistenceException e) {
            throw new AecuException("Unable to add history entry " + history.getRepositoryPath(), e);
        }
    }
    
    private void saveExecutionResultInHistory(ExecutionResult result, String path, ResourceResolver resolver) throws AecuException {
        createPath(path, resolver, "nt:unstructured");
        Resource entry = resolver.getResource(path);
        ModifiableValueMap values = entry.adaptTo(ModifiableValueMap.class);
        values.put(ATTR_RUN_SUCCESS, result.isSuccess());
        if (StringUtils.isNotBlank(result.getOutput())) {
            values.put(ATTR_RUN_OUTPUT, result.getOutput());
        }
        if (StringUtils.isNotBlank(result.getResult())) {
            values.put(ATTR_RUN_RESULT, result.getResult());
        }
        if (StringUtils.isNotBlank(result.getTime())) {
            values.put(ATTR_RUN_TIME, result.getTime());
        }
        if (result.getFallbackResult() != null) {
            String fallbackPath = path + "/fallback";
            saveExecutionResultInHistory(result.getFallbackResult(), fallbackPath, resolver);
        }
    }

}
