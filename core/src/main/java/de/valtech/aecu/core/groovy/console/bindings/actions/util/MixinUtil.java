package de.valtech.aecu.core.groovy.console.bindings.actions.util;

import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Utility class for mixin related actions
 *
 * @author Bart Thierens
 */
public class MixinUtil {

    private MixinUtil() {
        throw new UnsupportedOperationException("Cannot instantiate MixinUtil");
    }

    /**
     * Checks if a mixin is present on a node
     * @param node the non-null node to check
     * @param mixin the non-null mixin to check (can be a non-existing mixin type)
     * @return true if the mixin is present on the node, otherwise false
     * @throws RepositoryException if an exception occurs while performing repository operations
     */
    public static boolean hasMixin(@Nonnull Node node, @Nonnull String mixin) throws RepositoryException {
        return Arrays.stream(node.getMixinNodeTypes())
                .anyMatch(nodeType -> nodeType.isNodeType(mixin));
    }

    /**
     * Ensures a mixin is set on a node if it wasn't already
     * @param node the non-null node to add the mixin to
     * @param mixin the non-null mixin to add
     * @throws RepositoryException if an exception occurs while performing repository operations
     */
    public static void ensureMixin(@Nonnull Node node, @Nonnull String mixin) throws RepositoryException {
        if (!hasMixin(node, mixin) && node.canAddMixin(mixin)) {
            node.addMixin(mixin);
        }
    }

}
