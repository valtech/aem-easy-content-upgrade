package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Tests ReorderNode
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class ReorderNodeTest {

    private static final String NODE_TO_MOVE = "toMove";

    private static final String NEW_SUCCESSOR = "newSuccessor";

    @Mock
    private Resource resource;

    @Mock
    private Node node;

    @Mock
    private BindingContext context;

    @Before
    public void setup() {
        when(resource.adaptTo(Node.class)).thenReturn(node);
    }

    @Test
    public void test_doAction_dryRun() throws RepositoryException, PersistenceException {
        when(context.isDryRun()).thenReturn(true);

        ReorderNode action = new ReorderNode(NODE_TO_MOVE, NEW_SUCCESSOR, context);
        action.doAction(resource);

        verify(node, never()).orderBefore(NODE_TO_MOVE, NEW_SUCCESSOR);
    }

    @Test
    public void test_doAction_run() throws RepositoryException, PersistenceException {
        when(context.isDryRun()).thenReturn(false);

        ReorderNode action = new ReorderNode(NODE_TO_MOVE, NEW_SUCCESSOR, context);
        action.doAction(resource);

        verify(node, times(1)).orderBefore(NODE_TO_MOVE, NEW_SUCCESSOR);
    }

}
