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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByHasProperty;

/**
 * Tests FilterByHasProperty
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterByHasPropertyTest {

    private static final String NAME = "name";
    private static final String VALUE = "value";

    @Mock
    private Resource resource;

    @Mock
    ValueMap values;

    @Before
    public void setup() {
        when(resource.getValueMap()).thenReturn(values);
    }

    @Test
    public void filterAttributeNullValue() {
        FilterByHasProperty filter = new FilterByHasProperty(NAME);
        when(values.get(NAME)).thenReturn(null);

        assertFalse(filter.filter(resource, new StringBuilder()));
    }

    @Test
    public void filterAttributeNonNullValue() {
        FilterByHasProperty filter = new FilterByHasProperty(NAME);
        when(values.get(NAME)).thenReturn(VALUE);

        assertTrue(filter.filter(resource, new StringBuilder()));
    }

}
