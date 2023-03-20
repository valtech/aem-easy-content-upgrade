package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.actions.util.MixinUtil;
import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

/**
 * Removes a mixin from a resource/node if the mixin is present.
 *
 * @author Bart Thierens
 * @author Eric Manzi
 */
public class RemoveMixin implements Action {

    private final String mixinName;

    public RemoveMixin(String mixinName) {
        this.mixinName = mixinName;
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        if (StringUtils.isBlank(mixinName)) {
            return "WARNING: mixin name is empty";
        }
        Node node = resource.adaptTo(Node.class);
        if (node == null) {
            return "WARNING: could not get node for " + resource.getPath();
        }

        try {
            if (MixinUtil.hasMixin(node, mixinName)) {
                node.removeMixin(mixinName);
                return String.format("Removing mixin %s from %s", mixinName, resource.getPath());
            }
            return String.format("No mixin %s present on %s", mixinName, resource.getPath());
        } catch (NoSuchNodeTypeException nsnte) {
            return "WARNING: non-existing mixin: " + mixinName;
        } catch (RepositoryException re) {
            throw new PersistenceException("Problem when removing mixin from node", re);
        }
    }

}
