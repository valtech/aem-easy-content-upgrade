package de.valtech.aecu.core.groovy.console.bindings.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.scribe.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.valtech.aecu.api.groovy.console.bindings.ContentUpgrade;
import de.valtech.aecu.api.groovy.console.bindings.CustomResourceAction;
import de.valtech.aecu.api.groovy.console.bindings.filters.ANDFilter;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterBy;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByHasProperty;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByMultiValuePropContains;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByNodeName;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByNodeNameRegex;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByProperties;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.actions.PrintPath;
import de.valtech.aecu.core.groovy.console.bindings.actions.multivalue.AddMultiValues;
import de.valtech.aecu.core.groovy.console.bindings.actions.multivalue.RemoveMultiValues;
import de.valtech.aecu.core.groovy.console.bindings.actions.multivalue.ReplaceMultiValues;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.AddPageTagsAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.DeletePageAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.RemovePageTagsAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.ReplicatePageAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.page.SetPageTagsAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.CopyPropertyToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.DeleteProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.MovePropertyToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.RenameProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.SetProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.resource.CopyResourceToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.actions.resource.CustomAction;
import de.valtech.aecu.core.groovy.console.bindings.actions.resource.DeleteResource;
import de.valtech.aecu.core.groovy.console.bindings.actions.resource.MoveResourceToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForChildResourcesOf;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForDescendantResourcesOf;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForResources;
import de.valtech.aecu.core.groovy.console.bindings.traversers.TraversData;

public class ContentUpgradeImpl implements ContentUpgrade {

    private static Logger LOG = LoggerFactory.getLogger(ContentUpgrade.class);

    private BindingContext context = null;

    private List<TraversData> traversals = new ArrayList<>();
    private FilterBy filter = null;
    private List<Action> actions = new ArrayList<>();


    public ContentUpgradeImpl(@Nonnull ResourceResolver resourceResolver) {
        this.context = new BindingContext(resourceResolver);
    }

    @Override
    public ContentUpgrade forResources(@Nonnull String[] paths) {
        LOG.debug("forResources: {}", Arrays.toString(paths));
        traversals.add(new ForResources(paths));
        return this;
    }

    @Override
    public ContentUpgrade forChildResourcesOf(@Nonnull String path) {
        LOG.debug("forChildResourcesOf: {}", path);
        traversals.add(new ForChildResourcesOf(path));
        return this;
    }

    @Override
    public ContentUpgrade forDescendantResourcesOf(@Nonnull String path) {
        LOG.debug("forDescendantResourcesOf: {}", path);
        traversals.add(new ForDescendantResourcesOf(path, false));
        return this;
    }

    @Override
    public ContentUpgrade forResourcesInSubtree(@Nonnull String path) {
        LOG.debug("forResourcesInSubtree: {}", path);
        traversals.add(new ForDescendantResourcesOf(path, true));
        return this;
    }

    @Override
    public ContentUpgrade filterByProperties(@Nonnull Map<String, Object> conditionProperties) {
        LOG.debug("filterByProperties: {}", MapUtils.toString(conditionProperties));
        addFilter(new FilterByProperties(conditionProperties));
        return this;
    }

    @Override
    public ContentUpgrade filterByProperty(@Nonnull String name, Object value) {
        LOG.debug("filterByProperty: {} {}", name, value);
        addFilter(new FilterByProperty(name, value));
        return this;
    }

    @Override
    public ContentUpgrade filterByHasProperty(@Nonnull String name) {
        LOG.debug("filterByHasProperty: {} {}", name);
        addFilter(new FilterByHasProperty(name));
        return this;
    }

    @Override
    public ContentUpgrade filterByMultiValuePropContains(@Nonnull String name, @Nonnull Object[] conditionValues) {
        LOG.debug("filterByMultiValuePropContains {} : {}", name, Arrays.toString(conditionValues));
        addFilter(new FilterByMultiValuePropContains(name, conditionValues));
        return this;
    }

    @Override
    public ContentUpgrade filterByNodeName(@Nonnull String nodeName) {
        LOG.debug("filterByNodeName: {}", nodeName);
        addFilter(new FilterByNodeName(nodeName));
        return this;
    }

    @Override
    public ContentUpgrade filterByNodeNameRegex(@Nonnull String regex) {
        LOG.debug("filterByNodeNameRegex: {}", regex);
        addFilter(new FilterByNodeNameRegex(regex));
        return this;
    }

    @Override
    public ContentUpgrade filterWith(@Nonnull FilterBy filter) {
        LOG.debug("filterWith: {}", filter);
        addFilter(filter);
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
        ANDFilter newFilter = new ANDFilter(Arrays.asList(this.filter, filter));
        this.filter = newFilter;
    }

    @Override
    public ContentUpgrade doSetProperty(@Nonnull String name, Object value) {
        LOG.debug("doSetProperty: {} = {}", name, value);
        actions.add(new SetProperty(name, value));
        return this;
    }

    @Override
    public ContentUpgrade doDeleteProperty(@Nonnull String name) {
        LOG.debug("doDeleteProperty: {}", name);
        actions.add(new DeleteProperty(name));
        return this;
    }

    @Override
    public ContentUpgrade doRenameProperty(@Nonnull String oldName, @Nonnull String newName) {
        LOG.debug("doRenameProperty: {} to {}", oldName, newName);
        actions.add(new RenameProperty(oldName, newName));
        return this;
    }

    @Override
    public ContentUpgrade doCopyPropertyToRelativePath(@Nonnull String name, String newName,
            @Nonnull String relativeResourcePath) {
        LOG.debug("doCopyProperty: {} to {}", name, relativeResourcePath);
        actions.add(new CopyPropertyToRelativePath(name, newName, context.getResolver(), relativeResourcePath));
        return this;
    }

    @Override
    public ContentUpgrade doMovePropertyToRelativePath(@Nonnull String name, String newName,
            @Nonnull String relativeResourcePath) {
        LOG.debug("doMoveProperty: {} to {}", name, relativeResourcePath);
        actions.add(new MovePropertyToRelativePath(name, newName, context.getResolver(), relativeResourcePath));
        return this;
    }

    @Override
    public ContentUpgrade doAddValuesToMultiValueProperty(@Nonnull String name, @Nonnull String[] values) {
        LOG.debug("doAddToMultiValueProperty: {} + {}", name, Arrays.toString(values));
        actions.add(new AddMultiValues(name, values));
        return this;
    }

    @Override
    public ContentUpgrade doRemoveValuesOfMultiValueProperty(@Nonnull String name, @Nonnull String[] values) {
        LOG.debug("doRemoveValuesFromMultiValueProperty: {} - {}", name, Arrays.toString(values));
        actions.add(new RemoveMultiValues(name, values));
        return this;
    }

    @Override
    public ContentUpgrade doReplaceValuesOfMultiValueProperty(@Nonnull String name, @Nonnull String[] oldValues,
            @Nonnull String[] newValues) {
        LOG.debug("doReplaceValuesOfMultiValueProperty: {} - {}", name,
                Arrays.toString(oldValues) + " + " + Arrays.toString(newValues));
        actions.add(new ReplaceMultiValues(name, oldValues, newValues));
        return this;
    }

    @Override
    public ContentUpgrade doCopyResourceToRelativePath(@Nonnull String relativePath) {
        LOG.debug("doCopyResource to {}", relativePath);
        actions.add(new CopyResourceToRelativePath(relativePath, context.getResolver()));
        return this;
    }

    @Override
    public ContentUpgrade doMoveResourceToRelativePath(@Nonnull String relativePath) {
        LOG.debug("doMoveResource to {}", relativePath);
        actions.add(new MoveResourceToRelativePath(relativePath, context.getResolver()));
        return this;
    }

    @Override
    public ContentUpgrade doDeleteResource() {
        LOG.debug("doDeleteResource");
        actions.add(new DeleteResource(context.getResolver()));
        return this;
    }

    @Override
    public ContentUpgrade doCustomResourceBasedAction(CustomResourceAction action) {
        LOG.debug("doCustomResourceBasedAction");
        actions.add(new CustomAction(action));
        return this;
    }

    @Override
    public ContentUpgrade doActivateContainingPage() {
        LOG.debug("doActivateContainingPage");
        actions.add(new ReplicatePageAction(true, context));
        return this;
    }

    @Override
    public ContentUpgrade doDeactivateContainingPage() {
        LOG.debug("doDeactivateContainingPage");
        actions.add(new ReplicatePageAction(false, context));
        return this;
    }

    @Override
    public ContentUpgrade doDeleteContainingPage() {
        LOG.debug("doDeleteContainingPage");
        actions.add(new DeletePageAction(context));
        return this;
    }

    @Override
    public ContentUpgrade doAddTagsToContainingPage(String... tags) {
        LOG.debug("doAddTagsToContainingPage");
        actions.add(new AddPageTagsAction(context, tags));
        return this;
    }

    @Override
    public ContentUpgrade doSetTagsForContainingPage(String... tags) {
        LOG.debug("doSetTagsForContainingPage");
        actions.add(new SetPageTagsAction(context, tags));
        return this;
    }

    @Override
    public ContentUpgrade doRemoveTagsFromContainingPage(String... tags) {
        LOG.debug("doRemoveTagsFromContainingPage");
        actions.add(new RemovePageTagsAction(context, tags));
        return this;
    }

    @Override
    public ContentUpgrade printPath() {
        LOG.debug("printPath");
        actions.add(new PrintPath());
        return this;
    }

    @Override
    public StringBuffer run() throws PersistenceException {
        LOG.debug("apply content upgrade");
        return run(false);
    }

    @Override
    public StringBuffer dryRun() throws PersistenceException {
        LOG.debug("apply content upgrade dry");
        return run(true);
    }

    @Override
    public StringBuffer run(boolean dryRun) throws PersistenceException {
        context.setDryRun(dryRun);
        StringBuffer stringBuffer = new StringBuffer("Running content upgrade " + (dryRun ? "DRY" : "") + "...\n");
        for (TraversData traversal : traversals) {
            traversal.traverse(context, filter, actions, stringBuffer, dryRun);
        }
        return stringBuffer;
    }

}
