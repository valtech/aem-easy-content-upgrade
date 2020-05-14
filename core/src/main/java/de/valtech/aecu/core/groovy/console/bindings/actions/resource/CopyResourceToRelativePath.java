/*
 * Copyright 2018 - 2020 Valtech GmbH
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
package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.actions.util.PageUtil;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.core.fs.FileSystem;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * @author Roxana Muresan
 */
public class CopyResourceToRelativePath implements Action {

    private String relativePath;
    private String newName;
    private BindingContext context;

    /**
     * Constructor
     * 
     * @param relativePath     relative path
     * @param context binding context
     */
    public CopyResourceToRelativePath(@Nonnull String relativePath, String newName, @Nonnull BindingContext context) {
        this.relativePath = relativePath;
        this.newName = newName;
        this.context = context;
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        ResourceResolver resourceResolver = context.getResolver();
        Resource destinationParentResource = resourceResolver.getResource(resource, relativePath);
        if (destinationParentResource != null) {
            String sourcePath = resource.getPath();
            String destinationName = StringUtils.isNotEmpty(newName) ? newName : resource.getName();
            String destinationPath = destinationParentResource.getPath() + FileSystem.SEPARATOR + destinationName;

            PageUtil pageUtil = new PageUtil();
            if (pageUtil.isPageResource(resource)) {
                PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                try {
                    pageManager.copy(resource, destinationPath, null, false, false, false);
                } catch (WCMException | IllegalArgumentException e) {
                    throw new PersistenceException("Unable to copy " + sourcePath + " as " + destinationPath + ": " + e.getMessage());
                }
            } else if (!context.isDryRun()){
                try {
                    Session session = resourceResolver.adaptTo(Session.class);
                    session.getWorkspace().copy(sourcePath, destinationPath);
                } catch (RepositoryException e) {
                    throw new PersistenceException("Unable to copy " + sourcePath + " as " + destinationPath + ": " + e.getMessage());
                }
            }
            return "Copied " + sourcePath + " to " + destinationPath;
        }
        return "WARNING: could not read copy destination resource " + relativePath;
    }

}
