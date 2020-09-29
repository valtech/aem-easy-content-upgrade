package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests ChangePrimaryTypeTest
 *
 * @author Sajith
 */

@RunWith(MockitoJUnitRunner.class)
public class ChangePrimaryTypeTest {

    private static final String newPrimaryType = "nt:unstructured";

    @Mock
    private Resource resource;

    @Mock
    private Node node;

    @Before
    public void setup() {
        when(resource.adaptTo(Node.class)).thenReturn(node);
    }

    @Test
    public void test_doAction_setPrimaryType() throws PersistenceException, RepositoryException {
        ChangePrimaryType changePrimaryType = new ChangePrimaryType(newPrimaryType);
        String result = changePrimaryType.doAction(resource);
        verify(node, times(1)).setPrimaryType(newPrimaryType);
        assertEquals("Updated jcr:primaryType to " + newPrimaryType, result);
    }

    @Test
    public void test_doAction_nodeIsNull() throws PersistenceException, RepositoryException {
        when(resource.adaptTo(Node.class)).thenReturn(null);
        ChangePrimaryType changePrimaryType = new ChangePrimaryType(newPrimaryType);
        changePrimaryType.doAction(resource);
        verify(node, never()).setPrimaryType(newPrimaryType);
    }
}
