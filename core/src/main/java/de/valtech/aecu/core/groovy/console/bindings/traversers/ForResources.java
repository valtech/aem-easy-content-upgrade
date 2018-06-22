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

public class ForResources implements TraversData {

    private String[] paths;

    public ForResources(String[] paths) {
        this.paths = paths;
    }

    @Override
    public void traverse(ResourceResolver resourceResolver, FilterBy filter, Action action) throws PersistenceException {
        if (paths != null && paths.length > 0) {
            for (String path : paths) {
                if (path != null) {
                    Resource resource = resourceResolver.getResource(path);
                    if (filter == null || filter.filter(resource)) {
                        if (action != null) {
                            action.doAction(resource);
                        }
                    }
                }
            }
            resourceResolver.commit();
        }
    }
}
