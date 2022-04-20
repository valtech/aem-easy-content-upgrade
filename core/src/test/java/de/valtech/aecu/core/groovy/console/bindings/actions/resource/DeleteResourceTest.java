/*
 * Copyright 2020 - 2022 Valtech GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Tests DeleteResourceTest
 * 
 * @author sravan
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
        assertEquals(result, "Deleted resource " + COMPONENT);
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
        String expectedResult = String.format("Deleted child resource(s) [%s]. Child resource(s) [%s] were not found.",
                COMPONENT + PATH_SEPARATOR + CHILD1 + ", " + COMPONENT + PATH_SEPARATOR + CHILD3,
                COMPONENT + PATH_SEPARATOR + CHILD2);
        verify(resourceResolver, times(2)).delete(any(Resource.class));
        assertEquals(result, expectedResult);
    }
}
