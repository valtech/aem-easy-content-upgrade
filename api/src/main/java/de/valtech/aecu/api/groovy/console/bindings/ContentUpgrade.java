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
package de.valtech.aecu.api.groovy.console.bindings;

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterBy;

import org.apache.sling.api.resource.PersistenceException;

import java.util.Map;

/**
 * This class provides the builder methods to perform a content upgrade.
 * 
 * @author Roxana Muresan
 */
public interface ContentUpgrade {

    /**
     * Loops for given list of resources.
     * 
     * @param paths list of paths
     * @return upgrade object
     **/
    ContentUpgrade forResources(String[] paths);

    /**
     * Loops for all child resources of the given path. The path itself is not included.
     * 
     * @param path path
     * @return upgrade object
     **/
    ContentUpgrade forChildResourcesOf(String path);

    /**
     * Loops recursive for all child resources of the given path. The path itself can be included or not, depending on the value of includeRootResource.
     *
     * @param path
     * @param includeRootResource
     * @return
     */
    ContentUpgrade forDescendantResourcesOf(String path, boolean includeRootResource);

    /**
     * Filters by properties.
     * 
     * @param conditionProperties properties to filter
     * @return upgrade object
     **/
    ContentUpgrade filterByProperties(Map<String, String> conditionProperties);

    /**
     * Filters by node name exact match.
     * 
     * @param nodeName node name
     * @return upgrade object
     */
    ContentUpgrade filterByNodeName(String nodeName);

    /**
     * Filters by node name using regular expression.
     * 
     * @param regex regular expression (Java standard pattern)
     * @return upgrade object
     */
    ContentUpgrade filterByNodeNameRegex(String regex);

    /**
     * Filters by using the given filter.
     * 
     * @param filter filter
     * @return upgrade object
     */
    ContentUpgrade filterWith(FilterBy filter);

    /**
     * Sets a property value.
     * 
     * @param name  property name
     * @param value property value
     * @return upgrade object
     **/
    ContentUpgrade doSetProperty(String name, Object value);

    /**
     * Deletes a property if existing.
     * 
     * @param name property name
     * @return upgrade object
     */
    ContentUpgrade doDeleteProperty(String name);

    /**
     * Renames a property if existing.
     * 
     * @param oldName old property name
     * @param newName new property name
     * @return upgrade object
     */
    ContentUpgrade doRenameProperty(String oldName, String newName);

    /**
     * Copies a property to a relative path.
     * 
     * @param name                 property name
     * @param newName              new property name
     * @param relativeResourcePath relative path
     * @return upgrade object
     */
    ContentUpgrade doCopyPropertyToRelativePath(String name, String newName, String relativeResourcePath);

    /**
     * Moves a property to a relative path.
     * 
     * @param name                 property name
     * @param newName              new property name
     * @param relativeResourcePath relative path
     * @return upgrade object
     */
    ContentUpgrade doMovePropertyToRelativePath(String name, String newName, String relativeResourcePath);

    /**
     * Adds values to a multivalue property.
     * 
     * @param name   property name
     * @param values values
     * @return upgrade object
     */
    ContentUpgrade doAddValuesToMultiValueProperty(String name, String[] values);

    /**
     * Removes values of a multivalue property.
     * 
     * @param name   property name
     * @param values values to remove
     * @return upgrade object
     */
    ContentUpgrade doRemoveValuesOfMultiValueProperty(String name, String[] values);

    /**
     * Replaces values in a multivalue property.
     * 
     * @param name      property name
     * @param oldValues values to remove
     * @param newValues values to add
     * @return upgrade object
     */
    ContentUpgrade doReplaceValuesOfMultiValueProperty(String name, String[] oldValues, String[] newValues);

    /**
     * Copies a resource to a relative path.
     * 
     * @param relativePath path
     * @return upgrade object
     */
    ContentUpgrade doCopyResourceToRelativePath(String relativePath);

    /**
     * Moves a resource to a relative path.
     * 
     * @param relativePath path
     * @return upgrade object
     */
    ContentUpgrade doMoveResourceToRelativePath(String relativePath);

    /**
     * Deletes the resource.
     * 
     * @return upgrade object
     */
    ContentUpgrade doDeleteResource();

    /**
     * Print path
     * 
     * @return upgrade object
     */
    ContentUpgrade printPath();

    /**
     * Saves all changes to repository.
     * 
     * @return output
     * @throws PersistenceException error during execution
     */
    StringBuffer run() throws PersistenceException;

    /**
     * Performs a dry-run. No changes are written to CRX.
     * 
     * @return output
     * @throws PersistenceException error doing dry-run
     */
    StringBuffer dryRun() throws PersistenceException;

    /**
     * Executes a run or a dryRun depending on the dryRun parameter value.
     * @param dryRun
     * @return
     * @throws PersistenceException
     */
    StringBuffer run(boolean dryRun) throws PersistenceException;
}
