package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import javax.jcr.Node;
import javax.jcr.nodetype.NodeType;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests RemoveMixin
 *
 * @author Bart Thierens
 */
@ExtendWith(MockitoExtension.class)
class RemoveMixinTest {

    @Mock
    private Resource resource;

    @Mock
    private Node node;

    @Mock
    private NodeType versionableNodeType;

    @Test
    void testDoRemoveMixin() throws Exception {
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(versionableNodeType.isNodeType(JcrConstants.MIX_VERSIONABLE)).thenReturn(true);
        when(node.getMixinNodeTypes()).thenReturn(new NodeType[]{versionableNodeType});
        doNothing().when(node).removeMixin(JcrConstants.MIX_VERSIONABLE);

        RemoveMixin action = new RemoveMixin(JcrConstants.MIX_VERSIONABLE);
        action.doAction(resource);
        verify(node, times(1)).removeMixin(JcrConstants.MIX_VERSIONABLE);
    }

    @Test
    void testDoRemoveEmptyMixin() throws Exception {
        RemoveMixin action = new RemoveMixin(null);
        assertEquals("WARNING: mixin name is empty", action.doAction(resource));
        verify(node, times(0)).removeMixin(JcrConstants.MIX_VERSIONABLE);
    }

    @Test
    void testRemoveNotPresentMixin() throws Exception {
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(resource.getPath()).thenReturn("/content/some/path");
        when(node.getMixinNodeTypes()).thenReturn(new NodeType[0]);

        RemoveMixin action = new RemoveMixin(JcrConstants.MIX_VERSIONABLE);
        assertEquals(String.format("No mixin %s present on %s", JcrConstants.MIX_VERSIONABLE, resource.getPath()), action.doAction(resource));
        verify(node, times(0)).removeMixin(JcrConstants.MIX_VERSIONABLE);
    }

}