package de.valtech.aecu.core.groovy.console.bindings;

import de.valtech.aecu.core.groovy.console.bindings.actions.*;
import de.valtech.aecu.core.groovy.console.bindings.actions.properties.*;
import de.valtech.aecu.core.groovy.console.bindings.actions.resource.CopyResourceToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.actions.resource.DeleteResource;
import de.valtech.aecu.core.groovy.console.bindings.actions.resource.MoveResourceToRelativePath;
import de.valtech.aecu.core.groovy.console.bindings.filters.FilterBy;
import de.valtech.aecu.core.groovy.console.bindings.filters.FilterByProperties;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForChildResourcesOf;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForDescendantResourcesOf;
import de.valtech.aecu.core.groovy.console.bindings.traversers.ForResources;
import de.valtech.aecu.core.groovy.console.bindings.traversers.TraversData;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.scribe.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContentUpgrade {

    private static Logger LOG = LoggerFactory.getLogger(ContentUpgrade.class);

    private ResourceResolver resourceResolver = null;

    private List<TraversData> traversals = new ArrayList<>();
    private FilterBy filter = null;
    private List<Action> actions = new ArrayList<>();


    public ContentUpgrade(@Nonnull ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    /** content path filter methods **/
    public ContentUpgrade forResources(@Nonnull String[] paths) {
        LOG.debug("forResources: {}", paths.toString());
        traversals.add(new ForResources(paths));
        return this;
    }

    public ContentUpgrade forChildResourcesOf(@Nonnull String path) {
        LOG.debug("forChildResourcesOf: {}", path);
        traversals.add(new ForChildResourcesOf(path));
        return this;
    }

    public ContentUpgrade forDescendantResourcesOf(@Nonnull String path) {
        LOG.debug("forDescendantResourcesOf: {}", path);
        traversals.add(new ForDescendantResourcesOf(path));
        return this;
    }

    /** filters **/
    public ContentUpgrade filterByProperties(@Nonnull Map<String, String> conditionProperties) {
        LOG.debug("filterByProperties: {}", MapUtils.toString(conditionProperties));
        filter = new FilterByProperties(conditionProperties);
        return this;
    }

    public ContentUpgrade filterWith(@Nonnull FilterBy filter) {
        LOG.debug("filterWith: {}", filter);
        this.filter = filter;
        return this;
    }

    /** properties edit methods **/
    // TODO test with Object and see type conversion!!!
    public ContentUpgrade doSetStringProperty(@Nonnull String name, String value) {
        LOG.debug("doSetStringProperty: {} = {}", name, value);
        actions.add(new SetStringProperty(name, value));
        return this;
    }

    public ContentUpgrade doSetBooleanProperty(@Nonnull String name, Boolean value) {
        LOG.debug("doSetBooleanProperty: {} = {}", name, value);
        actions.add(new SetBooleanProperty(name, value));
        return this;
    }

    public ContentUpgrade doSetIntegerProperty(@Nonnull String name, int value) {
        LOG.debug("doSetIntegerProperty: {} = {}", name, value);
        actions.add(new SetIntegerProperty(name, value));
        return this;
    }

    public ContentUpgrade doDeleteProperty(@Nonnull String name) {
        LOG.debug("doDeleteProperty: {}", name);
        actions.add(new DeleteProperty(name));
        return this;
    }

    public ContentUpgrade doRenameProperty(@Nonnull String oldName, @Nonnull String newName) {
        LOG.debug("doRenameProperty: {} to {}", oldName, newName);
        actions.add(new RenameProperty(oldName, newName));
        return this;
    }

    public ContentUpgrade doCopyPropertyToRelativePath(@Nonnull String name, String newName, @Nonnull String relativeResourcePath) {
        LOG.debug("doCopyProperty: {} to {}", name, relativeResourcePath);
        actions.add(new CopyPropertyToRelativePath(name, newName, resourceResolver, relativeResourcePath));
        return this;
    }

    public ContentUpgrade doMovePropertyToRelativePath(@Nonnull String name, String newName, @Nonnull String relativeResourcePath) {
        LOG.debug("doMoveProperty: {} to {}", name, relativeResourcePath);
        actions.add(new MovePropertyToRelativePath(name, newName, resourceResolver, relativeResourcePath));
        return this;
    }

    /** resource edit methods **/
    public ContentUpgrade doCopyResourceToRelativePath(@Nonnull String relativePath) {
        LOG.debug("doCopyResource to {}", relativePath);
        actions.add(new CopyResourceToRelativePath(relativePath, resourceResolver));
        return this;
    }

    public ContentUpgrade doMoveResourceToRelativePath(@Nonnull String relativePath) {
        LOG.debug("doMoveResource to {}", relativePath);
        actions.add(new MoveResourceToRelativePath(relativePath, resourceResolver));
        return this;
    }

    public ContentUpgrade doDeleteResource() {
        LOG.debug("doDeleteResource");
        actions.add(new DeleteResource(resourceResolver));
        return this;
    }

    /** runner methods **/
    public StringBuffer run() throws PersistenceException {
        LOG.debug("apply content upgrade");
        return run(false);
    }

    public StringBuffer dryRun() throws PersistenceException {
        LOG.debug("apply content upgrade dry");
        return run(true);
    }

    private StringBuffer run(boolean dryRun) throws PersistenceException {
        StringBuffer stringBuffer = new StringBuffer("Running content upgrade " + (dryRun ? "DRY" : "") + "...\n");
        for (TraversData traversal : traversals) {
            for (Action action : actions) {
                traversal.traverse(resourceResolver, filter, action, stringBuffer, dryRun);
            }
        }
        return stringBuffer;
    }
}