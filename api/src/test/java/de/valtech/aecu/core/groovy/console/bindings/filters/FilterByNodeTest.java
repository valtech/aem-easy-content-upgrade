/*
 * Copyright 2018 Valtech GmbH
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

package de.valtech.aecu.core.groovy.console.bindings.filters;

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByNode;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Vugar Aghayev
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterByNodeTest {

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Resource resource;

    @Mock
    private Resource relativePathResource;

    @Mock
    private Resource absolutePathResource;

    @Before
    public void setup() {
        when(resource.getResourceResolver()).thenReturn(resolver);
        when(resource.getChild("climbing-on-kalymnos-island--greece/jcr:content")).thenReturn(relativePathResource);
    }

    @Test
    public void test_whenNodeNotFound_returnFalse() {

        assertFalse(new FilterByNode("/content/we-retail/ca/climbing-on-kalymnos-island--greece/jcr:content").filter(resource, new StringBuilder()));

        assertFalse(new FilterByNode("climbing-on-island--greece/jcr:content").filter(resource, new StringBuilder()));
    }

    @Test
    public void test_whenSubNodeFound_returnTrue() {

        assertTrue(new FilterByNode("climbing-on-kalymnos-island--greece/jcr:content").filter(resource, new StringBuilder()));

    }

    @Test
    public void test_whenAbsolutePathNodeFound_returnTrue() {
        when(resource.getResourceResolver().getResource("/content/we-retail/ca/en/experience/climbing-on-kalymnos-island--greece/jcr:content")).thenReturn(absolutePathResource);

        assertTrue(new FilterByNode("/content/we-retail/ca/en/experience/climbing-on-kalymnos-island--greece/jcr:content").filter(resource, new StringBuilder()));

    }
    @Test
    public void test_whenNodePathIsEmpty_returnTrue() {

        assertTrue(new FilterByNode("").filter(resource, new StringBuilder()));

        assertTrue(new FilterByNode("   ").filter(resource, new StringBuilder()));

    }

    @Test
    public void test_whenNodePathIsNull_returnTrue() {

        assertTrue(new FilterByNode(null).filter(resource, new StringBuilder()));

    }
}
