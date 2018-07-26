package de.valtech.aecu.api.groovy.console.bindings;

import java.util.Map;

import org.apache.sling.api.resource.PersistenceException;

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterBy;


public interface ContentUpgrade {

    /**
     * Loops for given list of resources.
     * 
     * @param paths list of paths
     * @return upgrade object
     **/
    ContentUpgrade forResources(String[] paths);

    ContentUpgrade forChildResourcesOf(String path);

    ContentUpgrade forDescendantResourcesOf(String path);

    /**
     * Filters by properties.
     * 
     * @param conditionProperties properties to filter
     * @return upgrade object
     **/
    ContentUpgrade filterByProperties(Map<String, String> conditionProperties);

    ContentUpgrade filterByNodeName(String nodeName);

    ContentUpgrade filterByNodeNameRegex(String regex);

    ContentUpgrade filterWith(FilterBy filter);

    /**
     * Sets a property value.
     * 
     * @param name  property name
     * @param value property value
     * @return upgrade object
     **/
    ContentUpgrade doSetProperty(String name, Object value);

    ContentUpgrade doDeleteProperty(String name);

    ContentUpgrade doRenameProperty(String oldName, String newName);

    ContentUpgrade doCopyPropertyToRelativePath(String name, String newName, String relativeResourcePath);

    ContentUpgrade doMovePropertyToRelativePath(String name, String newName, String relativeResourcePath);

    ContentUpgrade doAddValuesToMultiValueProperty(String name, String[] values);

    ContentUpgrade doRemoveValuesOfMultiValueProperty(String name, String[] values);

    ContentUpgrade doReplaceValuesOfMultiValueProperty(String name, String[] oldValues, String[] newValues);

    ContentUpgrade doCopyResourceToRelativePath(String relativePath);

    ContentUpgrade doMoveResourceToRelativePath(String relativePath);

    ContentUpgrade doDeleteResource();

    /**
     * Print path
     * 
     * @return upgrade object
     */
    ContentUpgrade printPath();

    StringBuffer run() throws PersistenceException;

    StringBuffer dryRun() throws PersistenceException;

}
