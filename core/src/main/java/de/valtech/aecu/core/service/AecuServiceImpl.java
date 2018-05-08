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
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.LoginException;
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

/**
 * AECU service.
 * 
 * @author Roland Gruber
 */
@Component(service=AecuService.class)
public class AecuServiceImpl implements AecuService {
    
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
        return new ExecutionResult(success, response.getRunningTime(), response.getOutput() + response.getExceptionStackTrace());
    }

}
