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
package de.valtech.aecu.core.groovy.console.bindings.traversers;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterBy;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * @author Roxana Muresan
 */
public class ForDescendantResourcesOf extends TraversData {

    private String path;
    private boolean includeRootResource;

    public ForDescendantResourcesOf(@Nonnull String path, boolean includeRootResource) {
        this.path = path;
        this.includeRootResource = includeRootResource;
    }


    @Override
    public void traverse(@Nonnull BindingContext context, FilterBy filter, @Nonnull List<Action> actions,
            @Nonnull StringBuffer stringBuffer, boolean dryRun) throws PersistenceException {
        ResourceResolver resourceResolver = context.getResolver();
        Resource parentResource = resourceResolver.getResource(path);
        if (parentResource != null) {
            if (includeRootResource) {
                applyActionOnResource(parentResource, filter, actions, stringBuffer);
            }
            traverseChildResourcesRecursive(resourceResolver, parentResource, filter, actions, stringBuffer, dryRun);
        }
    }

    private void traverseChildResourcesRecursive(ResourceResolver resourceResolver, Resource resource, FilterBy filter,
            List<Action> actions, StringBuffer stringBuffer, boolean dryRun) throws PersistenceException {
        if (resource == null || !resource.hasChildren()) {
            return;
        }
        Iterator<Resource> childResources = resource.listChildren();
        while (childResources.hasNext()) {
            Resource child = childResources.next();
            if (!isResourceValid(child)) {
                continue;
            }
            applyActionOnResource(child, filter, actions, stringBuffer);
            traverseChildResourcesRecursive(resourceResolver, child, filter, actions, stringBuffer, dryRun);
        }
        if (!dryRun) {
            resourceResolver.commit();
        }
    }

    private void applyActionOnResource(@Nonnull Resource resource, FilterBy filter, List<Action> actions,
            StringBuffer stringBuffer) throws PersistenceException {
        if (filter == null || filter.filter(resource, stringBuffer)) {
            runActions(stringBuffer, resource, actions);
        }
    }
}
