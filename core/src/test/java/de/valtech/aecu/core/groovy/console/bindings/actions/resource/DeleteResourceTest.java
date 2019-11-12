package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import static org.junit.Assert.assertEquals;
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
import org.junit.Before;
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

    private static final String EXPECTED_RESULT =
            "Deleted child resource(s) - [%s] and non-existing child resource(s) - [%s] under parent component";

    private static final String CHILD1 = "child1";
    private static final String CHILD2 = "child2";
    private static final String CHILD3 = "child3";

    @Mock
    private Resource resource;

    @Mock
    private ResourceResolver resourceResolver;

    @Before
    public void init() throws RepositoryException {}


    @Test
    public void test_doAction_noChildResource() throws RepositoryException, PersistenceException {
        when(resourceResolver.hasChanges()).thenReturn(false);
        when(resource.getPath()).thenReturn("component");
        DeleteResource deleteResourceAction = new DeleteResource(resourceResolver);
        String result = deleteResourceAction.doAction(resource);
        String expectedResult = String.format(EXPECTED_RESULT, "", "");
        assertEquals(result, expectedResult);
    }


    @Test
    public void test_doAction_withChildResource() throws RepositoryException, PersistenceException {
        when(resourceResolver.getResource(eq(resource), eq(CHILD1))).thenReturn(mock(Resource.class));
        when(resourceResolver.getResource(eq(resource), eq(CHILD2))).thenReturn(null);
        when(resourceResolver.getResource(eq(resource), eq(CHILD3))).thenReturn(mock(Resource.class));
        when(resourceResolver.hasChanges()).thenReturn(false);
        when(resource.getPath()).thenReturn("component");
        when(resourceResolver.hasChanges()).thenReturn(true);
        doNothing().when(resourceResolver).commit();
        DeleteResource deleteResourceAction = new DeleteResource(resourceResolver, CHILD1, CHILD2, CHILD3);
        String result = deleteResourceAction.doAction(resource);
        verify(resourceResolver, times(1)).commit();
        String expectedResult = String.format(EXPECTED_RESULT, CHILD1 + ", " + CHILD3, CHILD2);
        assertEquals(result, expectedResult);
    }
}
