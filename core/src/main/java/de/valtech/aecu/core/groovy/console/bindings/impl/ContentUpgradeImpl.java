/*
 * Copyright 2018 - 2022 Valtech GmbH
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
package de.valtech.aecu.core.groovy.console.bindings.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.jcr.query.Query;
import javax.servlet.http.HttpServletResponse;

import de.valtech.aecu.core.groovy.console.bindings.actions.resource.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.orbinson.aem.groovy.console.api.context.ScriptContext;

import de.valtech.aecu.api.groovy.console.bindings.ContentUpgrade;
import de.valtech.aecu.api.groovy.console.bindings.CustomResourceAction;
import de.valtech.aecu.api.groovy.console.bindings.filters.ANDFilter;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterBy;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByHasProperty;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByMultiValuePropContains;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByNodeExistence;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByNodeName;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByNodeNameRegex;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByPathRegex;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByProperties;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByProperty;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByPropertyRegex;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByNodeRootPaths;
import de.valtech.aecu.api.groovy.console.bindings.filters.NOTFilter;
import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.actions.multivalue.AddMultiValues;
import de.valtech.aecu.core.groovy.console.bindings.actions.multivalue.RemoveMultiValues;
import de.valtech.aecu.core.groovy.console.bindings.actions.multivalue.ReplaceMultiValues;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.AddPageTagsAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.DeletePageAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.RemovePageTagsAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.RenderPageAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.ReplicatePageAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.SetPageTagsAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.TreeActivatePageAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.print.PrintJson;
import de.valtech.aecu.core.groovy.console.bindings.actions.print.PrintPath;
import de.valtech.aecu.core.groovy.console.bindings.actions.print.PrintProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.CopyPropertyToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.DeleteProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.JoinProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.MovePropertyToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.RenameProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.SetProperty;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForChildResourcesOf;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForDescendantResourcesOf;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForQuery;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForResources;
import de.valtech.aecu.core.groovy.console.bindings.traversers.TraversData;

/**
 * Implements the content upgrade API.
 *
 * @author Roxana Muresan
 * @author Roland Gruber
 */
public class ContentUpgradeImpl implements ContentUpgrade {

    private static final Logger LOG = LoggerFactory.getLogger(ContentUpgradeImpl.class);

    private BindingContext context = null;
    private ScriptContext scriptContext;

    private List<TraversData> traversals = new ArrayList<>();
    private FilterBy filter = null;
    private List<Action> actions = new ArrayList<>();


    /**
     * Constructor
     *
     * @param resourceResolver resolver
     * @param scriptContext    Groovy context
     */
    public ContentUpgradeImpl(@Nonnull ResourceResolver resourceResolver, ScriptContext scriptContext) {
        this.context = new BindingContext(resourceResolver);
        this.scriptContext = scriptContext;
    }

    @Override
    public ContentUpgrade forResources(@Nonnull String[] paths) {
        traversals.add(new ForResources(paths));
        return this;
    }

    @Override
    public ContentUpgrade forChildResourcesOf(@Nonnull String path) {
        traversals.add(new ForChildResourcesOf(path));
        return this;
    }

    @Override
    public ContentUpgrade forDescendantResourcesOf(@Nonnull String path) {
        traversals.add(new ForDescendantResourcesOf(path, false));
        return this;
    }

    @Override
    public ContentUpgrade forResourcesInSubtree(@Nonnull String path) {
        traversals.add(new ForDescendantResourcesOf(path, true));
        return this;
    }

    @Override
    public ContentUpgrade forResourcesBySql2Query(String query) {
        traversals.add(new ForQuery(query, Query.JCR_SQL2));
        return this;
    }

    @Override
    public ContentUpgrade forResourcesByPropertyQuery(@Nonnull String path, Map<String, String> properties) {
        forResourcesByPropertyQuery(path, properties, JcrConstants.NT_BASE);
        return this;
    }

    @Override
    public ContentUpgrade forResourcesByPropertyQuery(@Nonnull String path, Map<String, String> properties,
                                                      @Nonnull String nodeType) {
        final StringBuilder sbQuery = new StringBuilder();
        sbQuery.append(
                "SELECT * FROM [" + escapeForSql2(nodeType) + "] AS s WHERE ISDESCENDANTNODE(s,'" + escapeForSql2(path) + "') ");
        if (properties != null) {
            properties.forEach((key, value) -> {
                if (key == null || value == null) {
                    LOG.warn("Null key or value provided: Key: {} Value: {}", key, value);
                    return;
                }
                if (value.contains("%")) {
                    sbQuery.append(" AND [" + escapeForSql2(key) + "] LIKE '" + escapeForSql2(value) + "'");
                } else {
                    sbQuery.append(" AND [" + escapeForSql2(key) + "] = '" + escapeForSql2(value) + "'");
                }
            });
        }
        forResourcesBySql2Query(sbQuery.toString());
        return this;
    }

    /**
     * Escapes an argument for SQL2 queries.
     *
     * @param input input value
     * @return escaped output
     */
    private String escapeForSql2(String input) {
        if (StringUtils.isEmpty(input)) {
            return StringUtils.EMPTY;
        }
        return input.replace("'", "''");
    }

    @Override
    public ContentUpgrade filterByProperties(@Nonnull Map<String, Object> conditionProperties) {
        addFilter(new FilterByProperties(conditionProperties));
        return this;
    }

    @Override
    public ContentUpgrade filterByNotProperties(Map<String, Object> conditionProperties) {
        addNotFilter(new FilterByProperties(conditionProperties));
        return this;
    }

    @Override
    public ContentUpgrade filterByProperty(@Nonnull String name, Object value) {
        addFilter(new FilterByProperty(name, value));
        return this;
    }

    @Override
    public ContentUpgrade filterByNotProperty(String name, Object value) {
        addNotFilter(new FilterByProperty(name, value));
        return this;
    }

    @Override
    public ContentUpgrade filterByPropertyRegex(String name, String regex) {
        addFilter(new FilterByPropertyRegex(name, regex));
        return this;
    }

    @Override
    public ContentUpgrade filterByNotPropertyRegex(String name, String regex) {
        addNotFilter(new FilterByPropertyRegex(name, regex));
        return this;
    }

    @Override
    public ContentUpgrade filterByAnyPropertyRegex(String regex) {
        addFilter(new FilterByPropertyRegex(null, regex));
        return this;
    }

    @Override
    public ContentUpgrade filterByNoPropertyRegex(String regex) {
        addNotFilter(new FilterByPropertyRegex(null, regex));
        return this;
    }

    @Override
    public ContentUpgrade filterByHasProperty(@Nonnull String name) {
        addFilter(new FilterByHasProperty(name));
        return this;
    }

    @Override
    public ContentUpgrade filterByNotHasProperty(String name) {
        addNotFilter(new FilterByHasProperty(name));
        return this;
    }

    @Override
    public ContentUpgrade filterByMultiValuePropContains(@Nonnull String name, @Nonnull Object[] conditionValues) {
        addFilter(new FilterByMultiValuePropContains(name, conditionValues));
        return this;
    }

    @Override
    public ContentUpgrade filterByNotMultiValuePropContains(String name, Object[] conditionValues) {
        addNotFilter(new FilterByMultiValuePropContains(name, conditionValues));
        return this;
    }

    @Override
    public ContentUpgrade filterByNodeName(@Nonnull String nodeName) {
        addFilter(new FilterByNodeName(nodeName));
        return this;
    }

    @Override
    public ContentUpgrade filterByNotNodeName(String nodeName) {
        addNotFilter(new FilterByNodeName(nodeName));
        return this;
    }

    @Override
    public ContentUpgrade filterByNodeExists(@Nonnull String path) {
        addFilter(new FilterByNodeExistence(path, true));
        return this;
    }

    @Override
    public ContentUpgrade filterByNodeNotExists(@Nonnull String path) {
        addFilter(new FilterByNodeExistence(path, false));
        return this;
    }

    @Override
    public ContentUpgrade filterByNodeNameRegex(@Nonnull String regex) {
        addFilter(new FilterByNodeNameRegex(regex));
        return this;
    }

    @Override
    public ContentUpgrade filterByNotNodeNameRegex(String regex) {
        addNotFilter(new FilterByNodeNameRegex(regex));
        return this;
    }

    @Override
    public ContentUpgrade filterByPathRegex(@Nonnull String regex) {
        addFilter(new FilterByPathRegex(regex));
        return this;
    }

    @Override
    public ContentUpgrade filterByNotPathRegex(String regex) {
        addNotFilter(new FilterByPathRegex(regex));
        return this;
    }

    @Override
    public ContentUpgrade filterByNodeRootPaths(List<String> rootPaths) {
        addFilter(new FilterByNodeRootPaths(rootPaths));
        return this;
    }

    @Override
    public ContentUpgrade filterWith(@Nonnull FilterBy filter) {
        addFilter(filter);
        return this;
    }

    @Override
    public ContentUpgrade filterNotWith(FilterBy filter) {
        addNotFilter(filter);
        return this;
    }

    /**
     * Adds another filter. If there is already a filter then an AND filter will be created.
     *
     * @param filter filter
     */
    private void addFilter(@Nonnull FilterBy filter) {
        if (this.filter == null) {
            this.filter = filter;
            return;
        }
        if (this.filter instanceof ANDFilter) {
            ((ANDFilter) this.filter).addFilter(filter);
        }
        ANDFilter newFilter = new ANDFilter(new ArrayList<>(Arrays.asList(this.filter, filter)));
        this.filter = newFilter;
    }

    /**
     * Adds a new NOTFilter based on the given filter.
     *
     * @param filter filter to negate
     */
    private void addNotFilter(@Nonnull FilterBy filter) {
        addFilter(new NOTFilter(filter));
    }

    @Override
    public ContentUpgrade doSetProperty(@Nonnull String name, Object value) {
        actions.add(new SetProperty(name, value, null, "nt:unstructured"));
        return this;
    }

    @Override
    public ContentUpgrade doSetProperty(@Nonnull String name, Object value, String pathToSubnode) {
        actions.add(new SetProperty(name, value, pathToSubnode, "nt:unstructured"));
        return this;
    }

    @Override
    public ContentUpgrade doSetProperty(@Nonnull String name, Object value, String pathToSubnode, String primaryType) {
        actions.add(new SetProperty(name, value, pathToSubnode, primaryType));
        return this;
    }

    @Override
    public ContentUpgrade doJoinProperty(@Nonnull String name) {
        actions.add(new JoinProperty(name));
        return this;
    }

    @Override
    public ContentUpgrade doJoinProperty(@Nonnull String name, Object value) {
        actions.add(new JoinProperty(name, value));
        return this;
    }

    @Override
    public ContentUpgrade doJoinProperty(@Nonnull String name, Object value, String separator) {
        actions.add(new JoinProperty(name, value, separator));
        return this;
    }

    @Override
    public ContentUpgrade doDeleteProperty(@Nonnull String name) {
        actions.add(new DeleteProperty(name, null));
        return this;
    }

    @Override
    public ContentUpgrade doDeleteProperty(@Nonnull String name, String pathToSubnode) {
        actions.add(new DeleteProperty(name, pathToSubnode));
        return this;
    }

    @Override
    public ContentUpgrade doRenameProperty(@Nonnull String oldName, @Nonnull String newName) {
        actions.add(new RenameProperty(oldName, newName, null));
        return this;
    }

    @Override
    public ContentUpgrade doRenameProperty(@Nonnull String oldName, @Nonnull String newName, String pathToSubnode) {
        actions.add(new RenameProperty(oldName, newName, pathToSubnode));
        return this;
    }

    @Override
    public ContentUpgrade doCopyPropertyToRelativePath(@Nonnull String name, String newName,
                                                       @Nonnull String relativeResourcePath) {
        actions.add(new CopyPropertyToRelativePath(name, newName, context.getResolver(), relativeResourcePath));
        return this;
    }

    @Override
    public ContentUpgrade doMovePropertyToRelativePath(@Nonnull String name, String newName,
                                                       @Nonnull String relativeResourcePath) {
        actions.add(new MovePropertyToRelativePath(name, newName, context.getResolver(), relativeResourcePath));
        return this;
    }

    @Override
    public ContentUpgrade doAddValuesToMultiValueProperty(@Nonnull String name, @Nonnull String[] values) {
        actions.add(new AddMultiValues(name, values));
        return this;
    }

    @Override
    public ContentUpgrade doRemoveValuesOfMultiValueProperty(@Nonnull String name, @Nonnull String[] values) {
        actions.add(new RemoveMultiValues(name, values));
        return this;
    }

    @Override
    public ContentUpgrade doReplaceValuesOfMultiValueProperty(@Nonnull String name, @Nonnull String[] oldValues,
                                                              @Nonnull String[] newValues) {
        actions.add(new ReplaceMultiValues(name, oldValues, newValues));
        return this;
    }

    @Override
    public ContentUpgrade doReplaceValueInAllProperties(String oldValue, String newValue) {
        actions.add(new ReplaceResourcePropertyValues(oldValue, newValue, Collections.emptyList()));
        return this;
    }

    @Override
    public ContentUpgrade doReplaceValueInProperties(String oldValue, String newValue, String[] propertyNames) {
        actions.add(new ReplaceResourcePropertyValues(oldValue, newValue, Arrays.asList(propertyNames)));
        return this;
    }

    @Override
    public ContentUpgrade doReplaceValueInAllPropertiesRegex(String searchRegex, String replacement) {
        actions.add(new ReplaceResourcePropertyValuesRegex(searchRegex, replacement, Collections.emptyList()));
        return this;
    }

    @Override
    public ContentUpgrade doReplaceValueInPropertiesRegex(String searchRegex, String replacement, String[] propertyNames) {
        actions.add(new ReplaceResourcePropertyValuesRegex(searchRegex, replacement, Arrays.asList(propertyNames)));
        return this;
    }

    @Override
    public ContentUpgrade doChangePrimaryType(@Nonnull String newPrimaryType) {
        actions.add(new ChangePrimaryType(newPrimaryType));
        return this;
    }

    @Override
    public ContentUpgrade doRename(String newName) {
        actions.add(new RenameResource(context.getResolver(), newName));
        return this;
    }

    @Override
    public ContentUpgrade doCopyResourceToRelativePath(@Nonnull String relativePath) {
        actions.add(new CopyResourceToRelativePath(relativePath, null, context));
        return this;
    }

    @Override
    public ContentUpgrade doCopyResourceToRelativePath(@Nonnull String relativePath, String newName) {
        actions.add(new CopyResourceToRelativePath(relativePath, newName, context));
        return this;
    }

    @Override
    public ContentUpgrade doMoveResourceToRelativePath(@Nonnull String relativePath) {
        actions.add(new MoveResourceToRelativePath(relativePath, context));
        return this;
    }

    @Override
    public ContentUpgrade doMoveResourceToPathRegex(@Nonnull String matchPattern, @Nonnull String targetPathExpr) {
        actions.add(new MoveResourceToPathRegex(matchPattern, targetPathExpr, context));
        return this;
    }

    @Override
    public ContentUpgrade doReorderNode(String nameOfNodeToMove, String newSuccessor) {
        actions.add(new ReorderNode(nameOfNodeToMove, newSuccessor, context));
        return this;
    }

    @Override
    public ContentUpgrade doDeleteResource(String... children) {
        actions.add(new DeleteResource(context.getResolver(), children));
        return this;
    }

    @Override
    public ContentUpgrade doCreateResource(String name, String primaryType) {
        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put(JcrConstants.JCR_PRIMARYTYPE, primaryType);
        actions.add(new CreateResource(name, propertyMap, null, context.getResolver()));
        return this;
    }

    @Override
    public ContentUpgrade doCreateResource(String name, String primaryType, Map<String, Object> properties) {
        Map<String, Object> propertyMap = (properties == null) ? new HashMap<>() : properties;
        propertyMap.put(JcrConstants.JCR_PRIMARYTYPE, primaryType);
        actions.add(new CreateResource(name, propertyMap, null, context.getResolver()));
        return this;
    }

    @Override
    public ContentUpgrade doCreateResource(String name, String primaryType, String relativePath) {
        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put(JcrConstants.JCR_PRIMARYTYPE, primaryType);
        actions.add(new CreateResource(name, propertyMap, relativePath, context.getResolver()));
        return this;
    }

    @Override
    public ContentUpgrade doCreateResource(String name, String primaryType, Map<String, Object> properties, String relativePath) {
        Map<String, Object> propertyMap = (properties == null) ? new HashMap<>() : properties;
        propertyMap.put(JcrConstants.JCR_PRIMARYTYPE, primaryType);
        actions.add(new CreateResource(name, propertyMap, relativePath, context.getResolver()));
        return this;
    }

    @Override
    public ContentUpgrade doActivateResource() {
        actions.add(new ReplicateResourceAction(true, context));
        return this;
    }

    @Override
    public ContentUpgrade doDeactivateResource() {
        actions.add(new ReplicateResourceAction(false, context));
        return this;
    }

    @Override
    public ContentUpgrade doCustomResourceBasedAction(CustomResourceAction action) {
        actions.add(new CustomAction(action));
        return this;
    }

    @Override
    public ContentUpgrade doActivateContainingPage() {
        actions.add(new ReplicatePageAction(true, context));
        return this;
    }

    @Override
    public ContentUpgrade doTreeActivateContainingPage() {
        actions.add(new TreeActivatePageAction(false, context));
        return this;
    }

    @Override
    public ContentUpgrade doTreeActivateContainingPage(boolean skipDeactivated) {
        actions.add(new TreeActivatePageAction(skipDeactivated, context));
        return this;
    }

    @Override
    public ContentUpgrade doDeactivateContainingPage() {
        actions.add(new ReplicatePageAction(false, context));
        return this;
    }

    @Override
    public ContentUpgrade doDeleteContainingPage() {
        actions.add(new DeletePageAction(context));
        return this;
    }

    @Override
    public ContentUpgrade doAddTagsToContainingPage(String... tags) {
        actions.add(new AddPageTagsAction(context, tags));
        return this;
    }

    @Override
    public ContentUpgrade doSetTagsForContainingPage(String... tags) {
        actions.add(new SetPageTagsAction(context, tags));
        return this;
    }

    @Override
    public ContentUpgrade doRemoveTagsFromContainingPage(String... tags) {
        actions.add(new RemovePageTagsAction(context, tags));
        return this;
    }

    @Override
    public ContentUpgrade doCheckPageRendering() {
        return doCheckPageRendering(HttpServletResponse.SC_OK);
    }

    @Override
    public ContentUpgrade doCheckPageRendering(int code) {
        actions.add(new RenderPageAction(context, code, null, null));
        return this;
    }

    @Override
    public ContentUpgrade doCheckPageRendering(String textPresent) {
        return doCheckPageRendering(textPresent, null);
    }

    @Override
    public ContentUpgrade doCheckPageRendering(String textPresent, String textNotPresent) {
        actions.add(new RenderPageAction(context, HttpServletResponse.SC_OK, textPresent, textNotPresent));
        return this;
    }

    @Override
    public ContentUpgrade doAddMixin(String mixinName) {
        actions.add(new AddMixin(mixinName));
        return this;
    }

    @Override
    public ContentUpgrade doRemoveMixin(String mixinName) {
        actions.add(new RemoveMixin(mixinName));
        return this;
    }

    @Override
    public ContentUpgrade printPath() {
        LOG.debug("printPath");
        actions.add(new PrintPath());
        return this;
    }

    @Override
    public ContentUpgrade printProperty(@Nonnull String property) {
        LOG.debug("printProperty {}", property);
        actions.add(new PrintProperty(property));
        return this;
    }

    @Override
    public ContentUpgrade printJson() {
        LOG.debug("printJson");
        actions.add(new PrintJson());
        return this;
    }

    @Override
    public void run() throws PersistenceException, AecuException {
        LOG.debug("apply content upgrade");
        run(false);
    }

    @Override
    public void dryRun() throws PersistenceException, AecuException {
        LOG.debug("apply content upgrade dry");
        run(true);
    }

    @Override
    public void run(boolean dryRun) throws PersistenceException, AecuException {
        context.setDryRun(dryRun);
        StringBuilder output = new StringBuilder("Running content upgrade " + (dryRun ? "DRY" : "") + "...\n");
        for (TraversData traversal : traversals) {
            traversal.traverse(context, filter, actions, output, dryRun);
        }
        if (!dryRun) {
            context.getResolver().commit();
        }
        output.append("\n\n");
        scriptContext.getPrintStream().append(output);
    }

    @Override
    public ContentUpgrade doCreateLabel(String language, String key, String value) {
        actions.add(new CreateLabel(language, key, value));
        return this;
    }

}
