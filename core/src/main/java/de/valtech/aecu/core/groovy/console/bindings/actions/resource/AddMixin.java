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
 * Adds a mixin to a resource/node if the mixin exists.
 *
 * @author Bart Thierens
 * @author Eric Manzi
 */
public class AddMixin implements Action {

    private final String mixinName;

    public AddMixin(String mixinName) {
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
            MixinUtil.ensureMixin(node, mixinName);
            return String.format("Adding mixin %s to %s", mixinName, resource.getPath());
        } catch (NoSuchNodeTypeException nsnte) {
            return "WARNING: non-existing mixin: " + mixinName;
        }
        catch (RepositoryException re) {
            throw new PersistenceException("Problem when adding mixin to node", re);
        }
    }

}
