package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests DeleteResourceTest
 * 
 * @author sravan
 */
@RunWith(MockitoJUnitRunner.class)
public class DeleteResourceTest {

    private static final String CHILD1 = "child1";
    private static final String CHILD2 = "child2";
    private static final String CHILD3 = "child3";
    private static final String COMPONENT = "component";
    private static final String PATH_SEPARATOR = "/";

    @Mock
    private Resource resource;

    @Mock
    private ResourceResolver resourceResolver;

    @Test
    public void test_doAction_noChildResource() throws RepositoryException, PersistenceException {
        when(resource.getPath()).thenReturn(COMPONENT);
        DeleteResource deleteResourceAction = new DeleteResource(resourceResolver);
        doNothing().when(resourceResolver).delete(resource);
        String result = deleteResourceAction.doAction(resource);
        verify(resourceResolver, times(1)).delete(resource);
        assertEquals(result, "Deleted resource - " + COMPONENT);
    }

    @Test
    public void test_doAction_withChildResource() throws RepositoryException, PersistenceException {
        when(resourceResolver.getResource(eq(resource), eq(CHILD1))).thenReturn(mock(Resource.class));
        when(resourceResolver.getResource(eq(resource), eq(CHILD2))).thenReturn(null);
        when(resourceResolver.getResource(eq(resource), eq(CHILD3))).thenReturn(mock(Resource.class));
        when(resource.getPath()).thenReturn(COMPONENT);
        doNothing().when(resourceResolver).delete(any(Resource.class));
        DeleteResource deleteResourceAction = new DeleteResource(resourceResolver, CHILD1, CHILD2, CHILD3);
        String result = deleteResourceAction.doAction(resource);
        String expectedResult = String.format("Deleted child resource(s) - [%s]. Child resource(s) - [%s] were not found.",
                COMPONENT + PATH_SEPARATOR + CHILD1 + ", " + COMPONENT + PATH_SEPARATOR + CHILD3,
                COMPONENT + PATH_SEPARATOR + CHILD2);
        verify(resourceResolver, times(2)).delete(any(Resource.class));
        assertEquals(result, expectedResult);
    }
}
