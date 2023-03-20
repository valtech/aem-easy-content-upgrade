package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import javax.jcr.Node;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests AddMixin
 *
 * @author Bart Thierens
 */
@ExtendWith(MockitoExtension.class)
class AddMixinTest {

    @Mock
    private Resource resource;

    @Mock
    private Node node;

    @Mock
    private NodeType versionableNodeType;

    @Test
    void testDoAddMixin() throws Exception {
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(node.getMixinNodeTypes()).thenReturn(new NodeType[0]);
        doNothing().when(node).addMixin(JcrConstants.MIX_VERSIONABLE);
        when(node.canAddMixin(JcrConstants.MIX_VERSIONABLE)).thenReturn(true);

        AddMixin action = new AddMixin(JcrConstants.MIX_VERSIONABLE);
        action.doAction(resource);
        verify(node, times(1)).canAddMixin(JcrConstants.MIX_VERSIONABLE);
        verify(node, times(1)).addMixin(JcrConstants.MIX_VERSIONABLE);
    }

    @Test
    void testDoAddEmptyMixin() throws Exception {
        AddMixin action = new AddMixin(null);
        assertEquals("WARNING: mixin name is empty", action.doAction(resource));
        verify(node, times(0)).canAddMixin(JcrConstants.MIX_VERSIONABLE);
        verify(node, times(0)).addMixin(JcrConstants.MIX_VERSIONABLE);
    }

    @Test
    void testAddPresentMixin() throws Exception {
        when(versionableNodeType.isNodeType(JcrConstants.MIX_VERSIONABLE)).thenReturn(true);
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(node.getMixinNodeTypes()).thenReturn(new NodeType[]{versionableNodeType});

        AddMixin action = new AddMixin(JcrConstants.MIX_VERSIONABLE);
        action.doAction(resource);
        verify(node, times(0)).canAddMixin(JcrConstants.MIX_VERSIONABLE);
        verify(node, times(0)).addMixin(JcrConstants.MIX_VERSIONABLE);
    }

    @Test
    void testAddNonExistingMixin() throws Exception {
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(node.getMixinNodeTypes()).thenReturn(new NodeType[0]);
        when(node.canAddMixin(anyString())).thenReturn(true);
        doThrow(NoSuchNodeTypeException.class).when(node).addMixin(anyString());

        String nonExistingMixin = "mix:max";
        AddMixin action = new AddMixin(nonExistingMixin);
        assertEquals("WARNING: non-existing mixin: " + nonExistingMixin, action.doAction(resource));
    }

}