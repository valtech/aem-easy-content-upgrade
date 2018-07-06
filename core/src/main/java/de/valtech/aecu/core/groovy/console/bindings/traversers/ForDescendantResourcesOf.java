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
package de.valtech.aecu.core.groovy.console.bindings.traversers;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.filters.FilterBy;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * @author Roxana Muresan
 */
public class ForDescendantResourcesOf implements TraversData {

    private String path;

    public ForDescendantResourcesOf(@Nonnull String path) {
        this.path = path;
    }


    @Override
    public void traverse(@Nonnull ResourceResolver resourceResolver, FilterBy filter, @Nonnull Action action, @Nonnull StringBuffer stringBuffer, boolean dryRun) throws PersistenceException {
        Resource parentResource = resourceResolver.getResource(path);
        if (parentResource != null) {
            traverseChildResourcesRecursive(resourceResolver, parentResource, filter, action, stringBuffer, dryRun);
        }
    }

    private void traverseChildResourcesRecursive(ResourceResolver resourceResolver, Resource resource, FilterBy filter, Action action, StringBuffer stringBuffer, boolean dryRun) throws PersistenceException {
        if (resource != null && resource.hasChildren()) {
            Iterator<Resource> childResources = resource.listChildren();
            while (childResources.hasNext()) {
                Resource child = childResources.next();
                if (filter == null || filter.filter(child)) {
                    stringBuffer.append(action.doAction(child) + "\n");
                }
                traverseChildResourcesRecursive(resourceResolver, child, filter, action, stringBuffer, dryRun);
            }
            if (!dryRun) {
                resourceResolver.commit();
            }
        }
    }

}
