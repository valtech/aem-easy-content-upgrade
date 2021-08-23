package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Reorders a subnode of a resource.
 * 
 * @author Roland Gruber
 */
public class ReorderNode implements Action {

    protected String nameOfNodeToMove;
    protected String newSuccessor;
    private BindingContext context;

    /**
     * Constructor
     * 
     * @param newPrimaryType new type
     */
    public ReorderNode(@Nonnull String nameOfNodeToMove, @Nullable String newSuccessor, @Nonnull BindingContext context) {
        this.nameOfNodeToMove = nameOfNodeToMove;
        this.newSuccessor = newSuccessor;
        this.context = context;
    }

    @Override
    public String doAction(@Nonnull Resource resource) throws PersistenceException {
        Node node = resource.adaptTo(Node.class);
        try {
            if (null != node) {
                if (!context.isDryRun()) {
                    node.orderBefore(nameOfNodeToMove, newSuccessor);
                }
                return "Reordered " + nameOfNodeToMove + " on resource " + resource.getPath();
            }
        } catch (RepositoryException e) {
            throw new PersistenceException("ERROR: could not reorder " + nameOfNodeToMove + " on resource " + resource.getPath(),
                    e);
        }
        return "WARNING: could not reorder " + nameOfNodeToMove;
    }

}
