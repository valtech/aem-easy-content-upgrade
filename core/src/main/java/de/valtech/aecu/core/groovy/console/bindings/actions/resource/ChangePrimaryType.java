package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;

/**
 * Changes the jcr:primaryType of a node.
 * 
 * @author Sajith
 */
public class ChangePrimaryType implements Action {

    protected String newPrimaryType;

    /**
     * Constructor
     * 
     * @param newPrimaryType new type
     */
    public ChangePrimaryType(@Nonnull String newPrimaryType) {
        this.newPrimaryType = newPrimaryType;
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        Node node = resource.adaptTo(Node.class);
        try {
            if (null != node) {
                node.setPrimaryType(newPrimaryType);
                return "Updated jcr:primaryType to " + newPrimaryType;
            }
        } catch (RepositoryException e) {
            throw new PersistenceException("ERROR: could not update jcr:primaryType to " + newPrimaryType, e);
        }
        return "WARNING: could not update jcr:primaryType to " + newPrimaryType;
    }
}
