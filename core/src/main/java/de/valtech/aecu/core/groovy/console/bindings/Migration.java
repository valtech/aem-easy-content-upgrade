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
import org.scribe.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Migration {

    private static Logger LOG = LoggerFactory.getLogger(Migration.class);

    private ResourceResolver resourceResolver = null;

    private List<TraversData> traversals = new ArrayList<>();
    private FilterBy filter = null;
    private List<Action> actions = new ArrayList<>();


    public Migration(@Nonnull ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    /** content filter methods **/
    public Migration forResources(@Nonnull String[] paths) {
        LOG.debug("forResources: {}", paths.toString());
        traversals.add(new ForResources(paths));
        return this;
    }

    public Migration forChildResourcesOf(@Nonnull String path) {
        LOG.debug("forChildResourcesOf: {}", path);
        traversals.add(new ForChildResourcesOf(path));
        return this;
    }

    public Migration forDescendantResourcesOf(@Nonnull String path) {
        LOG.debug("forDescendantResourcesOf: {}", path);
        traversals.add(new ForDescendantResourcesOf(path));
        return this;
    }

    /** filters **/
    public Migration filterByProperties(@Nonnull Map<String, String> conditionProperties) {
        LOG.debug("filterByProperties: {}", MapUtils.toString(conditionProperties));
        filter = new FilterByProperties(conditionProperties);
        return this;
    }

    public Migration filterWith(@Nonnull FilterBy filter) {
        LOG.debug("filterWith: {}", filter);
        this.filter = filter;
        return this;
    }

    /** properties edit methods **/
    public Migration doSetProperty(@Nonnull String name, String value) {
        LOG.debug("doSetProperty: {} = {}", name, value);
        actions.add(new SetProperty(name, value));
        return this;
    }

    public Migration doRemoveProperty(@Nonnull String name) {
        LOG.debug("doRemoveProperty: {}", name);
        actions.add(new RemoveProperty(name));
        return this;
    }

    public Migration doRenameProperty(@Nonnull String oldName, @Nonnull String newName) {
        LOG.debug("doRenameProperty: {} to {}", oldName, newName);
        actions.add(new RenameProperty(oldName, newName));
        return this;
    }

    public StringBuffer apply() throws PersistenceException {
        LOG.debug("apply migration");
        StringBuffer stringBuffer = new StringBuffer("Running migration...\n");
        for (TraversData traversal : traversals) {
            for (Action action : actions) {
                traversal.traverse(resourceResolver, filter, action, stringBuffer);
            }
        }
        return stringBuffer;
    }
}
