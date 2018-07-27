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
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterBy;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByNodeName;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByNodeNameRegex;
import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByProperties;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.actions.PrintPath;
import de.valtech.aecu.core.groovy.console.bindings.actions.multivalue.AddMultiValues;
import de.valtech.aecu.core.groovy.console.bindings.actions.multivalue.RemoveMultiValues;
import de.valtech.aecu.core.groovy.console.bindings.actions.multivalue.ReplaceMultiValues;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.CopyPropertyToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.DeleteProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.MovePropertyToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.RenameProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.SetProperty;
import de.valtech.aecu.core.groovy.console.bindings.actions.resource.CopyResourceToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.actions.resource.DeleteResource;
import de.valtech.aecu.core.groovy.console.bindings.actions.resource.MoveResourceToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForChildResourcesOf;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForDescendantResourcesOf;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForResources;
import de.valtech.aecu.core.groovy.console.bindings.traversers.TraversData;

public class ContentUpgradeImpl implements ContentUpgrade {

    private static Logger LOG = LoggerFactory.getLogger(ContentUpgrade.class);

    private ResourceResolver resourceResolver = null;

    private List<TraversData> traversals = new ArrayList<>();
    private FilterBy filter = null;
    private List<Action> actions = new ArrayList<>();


    public ContentUpgradeImpl(@Nonnull ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    @Override
    public ContentUpgrade forResources(@Nonnull String[] paths) {
        LOG.debug("forResources: {}", paths.toString());
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
    public ContentUpgrade forDescendantResourcesOf(@Nonnull String path, boolean includeRootResource) {
        LOG.debug("forDescendantResourcesOf: {}", path);
        traversals.add(new ForDescendantResourcesOf(path, includeRootResource));
        return this;
    }

    @Override
    public ContentUpgrade filterByProperties(@Nonnull Map<String, String> conditionProperties) {
        LOG.debug("filterByProperties: {}", MapUtils.toString(conditionProperties));
        filter = new FilterByProperties(conditionProperties);
        return this;
    }

    @Override
    public ContentUpgrade filterByNodeName(@Nonnull String nodeName) {
        LOG.debug("filterByNodeName: {}", nodeName);
        filter = new FilterByNodeName(nodeName);
        return this;
    }

    @Override
    public ContentUpgrade filterByNodeNameRegex(@Nonnull String regex) {
        LOG.debug("filterByNodeNameRegex: {}", regex);
        filter = new FilterByNodeNameRegex(regex);
        return this;
    }

    @Override
    public ContentUpgrade filterWith(@Nonnull FilterBy filter) {
        LOG.debug("filterWith: {}", filter);
        this.filter = filter;
        return this;
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
        actions.add(new CopyPropertyToRelativePath(name, newName, resourceResolver, relativeResourcePath));
        return this;
    }

    @Override
    public ContentUpgrade doMovePropertyToRelativePath(@Nonnull String name, String newName,
            @Nonnull String relativeResourcePath) {
        LOG.debug("doMoveProperty: {} to {}", name, relativeResourcePath);
        actions.add(new MovePropertyToRelativePath(name, newName, resourceResolver, relativeResourcePath));
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
        actions.add(new CopyResourceToRelativePath(relativePath, resourceResolver));
        return this;
    }

    @Override
    public ContentUpgrade doMoveResourceToRelativePath(@Nonnull String relativePath) {
        LOG.debug("doMoveResource to {}", relativePath);
        actions.add(new MoveResourceToRelativePath(relativePath, resourceResolver));
        return this;
    }

    @Override
    public ContentUpgrade doDeleteResource() {
        LOG.debug("doDeleteResource");
        actions.add(new DeleteResource(resourceResolver));
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
        StringBuffer stringBuffer = new StringBuffer("Running content upgrade " + (dryRun ? "DRY" : "") + "...\n");
        for (TraversData traversal : traversals) {
            traversal.traverse(resourceResolver, filter, actions, stringBuffer, dryRun);
        }
        return stringBuffer;
    }
}
