package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import static org.junit.Assert.assertEquals;
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

/**
 * Tests ChangePrimaryTypeTest
 *
 * @author Sajith
 */

@RunWith(MockitoJUnitRunner.class)
public class ChangePrimaryTypeTest {

    private static final String PATH = "path";

    private static final String newPrimaryType = "nt:unstructured";

    @Mock
    private Resource resource;

    @Mock
    private Node node;

    @Before
    public void setup() {
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(resource.getPath()).thenReturn(PATH);
    }

    @Test
    public void test_doAction_setPrimaryType() throws PersistenceException, RepositoryException {
        ChangePrimaryType changePrimaryType = new ChangePrimaryType(newPrimaryType);
        String result = changePrimaryType.doAction(resource);
        verify(node, times(1)).setPrimaryType(newPrimaryType);
        assertEquals("Updated jcr:primaryType to " + newPrimaryType + " for resource " + PATH, result);
    }

    @Test
    public void test_doAction_nodeIsNull() throws PersistenceException, RepositoryException {
        when(resource.adaptTo(Node.class)).thenReturn(null);
        ChangePrimaryType changePrimaryType = new ChangePrimaryType(newPrimaryType);
        changePrimaryType.doAction(resource);
        verify(node, never()).setPrimaryType(newPrimaryType);
    }
}
