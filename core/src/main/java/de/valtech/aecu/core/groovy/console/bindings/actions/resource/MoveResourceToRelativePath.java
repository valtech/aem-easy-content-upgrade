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

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.actions.util.PageUtil;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * @author Roxana Muresan
 */
public class MoveResourceToRelativePath implements Action {

    private String relativePath;
    private ResourceResolver resourceResolver;
    private boolean dryRun;

    /**
     * Constructor
     * 
     * @param relativePath relative path
     * @param context      binding context
     */
    public MoveResourceToRelativePath(@Nonnull String relativePath, @Nonnull BindingContext context) {
        this.relativePath = relativePath;
        this.resourceResolver = context.getResolver();
        dryRun = context.isDryRun();
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        Resource destinationResource = resourceResolver.getResource(resource, relativePath);
        if (destinationResource != null) {
            String sourceAbsPAth = resource.getPath();
            String destinationAsPath = destinationResource.getPath();
            PageUtil pageUtil = new PageUtil();
            if (pageUtil.isPageResource(resource)) {
                PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                try {
                    if (!dryRun) {
                        pageManager.move(resource, destinationAsPath + "/" + resource.getName(), null, false, false, null);
                    }
                } catch (WCMException | IllegalArgumentException e) {
                    throw new PersistenceException("Unable to move " + sourceAbsPAth + ": " + e.getMessage());
                }
            } else {
                resourceResolver.move(sourceAbsPAth, destinationAsPath);
            }

            return "Moved " + sourceAbsPAth + " to path " + destinationAsPath;
        }
        return "WARNING: could not read move destination resource " + relativePath;
    }

}
