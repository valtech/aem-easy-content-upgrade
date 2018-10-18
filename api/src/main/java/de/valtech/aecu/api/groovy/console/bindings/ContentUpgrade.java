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

import java.util.Map;

import org.apache.sling.api.resource.PersistenceException;

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterBy;

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
     * Loops recursive for all child resources of the given path. The path itself is not included.
     *
     * @param path path
     * @return upgrade object
     **/
    ContentUpgrade forDescendantResourcesOf(String path);


    /**
     * Loops recursive over all resources contained in the subtree at the given path.
     *
     * @param path path
     * @return upgrade object
     */
    ContentUpgrade forResourcesInSubtree(String path);

    /**
     * Loops over resources found by SQL2 query.
     * 
     * @param query query string
     * @return upgrade object
     */
    ContentUpgrade forResourcesBySql2Query(String query);

    /**
     * Filters by existence of a single property.
     * 
     * @param name property name
     * @return upgrade object
     */
    ContentUpgrade filterByHasProperty(String name);

    /**
     * Filters by a single property.
     * 
     * @param name  property name
     * @param value property value
     * @return upgrade object
     */
    ContentUpgrade filterByProperty(String name, Object value);

    /**
     * Filters by properties. Can be used also for Multi-value properties.
     * 
     * @param conditionProperties properties to filter
     * @return upgrade object
     **/
    ContentUpgrade filterByProperties(Map<String, Object> conditionProperties);

    /**
     * Filters by multi-value with the given name containing the given conditionValues
     *
     * @param name            name of the multi-value property
     * @param conditionValues values to search for
     * @return upgrade object
     */
    ContentUpgrade filterByMultiValuePropContains(String name, Object[] conditionValues);

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
     * Performs a custom action with providing a function.
     * 
     * @param action action to perform on resource
     * @return upgrade object
     */
    ContentUpgrade doCustomResourceBasedAction(CustomResourceAction action);

    /**
     * Activates the page where the resource is located.
     * 
     * @return upgrade object
     */
    ContentUpgrade doActivateContainingPage();

    /**
     * Deactivates the page where the resource is located.
     * 
     * @return upgrade object
     */
    ContentUpgrade doDeactivateContainingPage();

    /**
     * Deletes the page where the resource is located. This will not work if called multiple times
     * for the same page.
     * 
     * @return upgrade object
     */
    ContentUpgrade doDeleteContainingPage();

    /**
     * Adds tags to the containing page of the matching resource.
     * 
     * @param tags tag IDs or paths
     * @return upgrade object
     */
    ContentUpgrade doAddTagsToContainingPage(String... tags);

    /**
     * Sets tags for the containing page of the matching resource. All existing tags are
     * overwritten.
     * 
     * @param tags tag IDs or paths
     * @return upgrade object
     */
    ContentUpgrade doSetTagsForContainingPage(String... tags);

    /**
     * Removes tags from the containing page of the matching resource.
     * 
     * @param tags tag IDs or paths
     * @return upgrade object
     */
    ContentUpgrade doRemoveTagsFromContainingPage(String... tags);

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
     *
     * @param dryRun dryRun option
     * @return output
     * @throws PersistenceException error during execution
     */
    StringBuffer run(boolean dryRun) throws PersistenceException;

}
