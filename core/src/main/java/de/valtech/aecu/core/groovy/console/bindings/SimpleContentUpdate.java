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
package de.valtech.aecu.core.groovy.console.bindings;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.actions.RemoveProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.RenameProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.SetProperty;
import de.valtech.aecu.core.groovy.console.bindings.filters.FilterBy;
import de.valtech.aecu.core.groovy.console.bindings.filters.FilterByProperties;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForChildResourcesOf;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForDescendantResourcesOf;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForResources;
import de.valtech.aecu.core.groovy.console.bindings.traversers.TraversData;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Groovy Console Bindings: Simple Content Update
 * @author Roxana Muresan
 */
public class SimpleContentUpdate {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleContentUpdate.class);

    private ResourceResolver resourceResolver;// TODO system user resolver!!

    private Map<TraversData, FilterBy> traversalsWithFilter = new HashMap<>();
    private List<Action> actions = new ArrayList<>();


    public SimpleContentUpdate(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    /** content filter methods **/
    public SimpleContentUpdate forResources(String[] paths) {
        traversalsWithFilter.put(new ForResources(paths), null);
        return this;
    }

    public SimpleContentUpdate forChildResourcesOf(String path) {
        traversalsWithFilter.put(new ForChildResourcesOf(path), null);
        return this;
    }

    public SimpleContentUpdate forChildResourcesOfWithProperties(String path, Map<String, String> conditionProperties) {
        traversalsWithFilter.put(new ForChildResourcesOf(path), new FilterByProperties(conditionProperties));
        return this;
    }

    public SimpleContentUpdate forDescendantResourcesOf(String path) {
        traversalsWithFilter.put(new ForDescendantResourcesOf(path), null);
        return this;
    }

    public SimpleContentUpdate forDescendantResourcesOfWithProperties(String path, Map<String, String> conditionProperties) {
        traversalsWithFilter.put(new ForDescendantResourcesOf(path), new FilterByProperties(conditionProperties));
        return this;
    }

    /** properties edit methods **/
    public SimpleContentUpdate doSetProperty(String name, String value) {
        actions.add(new SetProperty(name, value));
        return this;
    }

    public SimpleContentUpdate doRemoveProperty(String name) {
        actions.add(new RemoveProperty(name));
        return this;
    }

    public SimpleContentUpdate doRenameProperty(String oldName, String newName) {
        actions.add(new RenameProperty(oldName, newName));
        return this;
    }

    public String apply() throws PersistenceException {
        StringBuffer stringBuffer = new StringBuffer("SimpleContentUpdate.apply()\n");
        for (Map.Entry<TraversData, FilterBy> traversWithFilter : traversalsWithFilter.entrySet()) {
            for (Action action : actions) {
                traversWithFilter.getKey().traverse(resourceResolver, traversWithFilter.getValue(), action, stringBuffer);
            }
        }
        return stringBuffer.toString();
    }

}