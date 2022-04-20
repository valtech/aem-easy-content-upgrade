/*
 * Copyright 2018 - 2022 Valtech GmbH
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.codehaus.groovy.runtime.GStringImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByProperty;
import groovy.lang.GString;

/**
 * Tests FilterByProperty
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FilterByPropertyTest {

    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final GString VALUE_G = new GStringImpl(new Object[] {3}, new String[] {"value"});

    @Mock
    private Resource resource;

    @Mock
    ValueMap values;

    @BeforeEach
    public void setup() {
        when(resource.getValueMap()).thenReturn(values);
    }

    @Test
    public void filterAttributeNullValueNull() {
        FilterByProperty filter = new FilterByProperty(NAME, null);
        when(values.get(NAME)).thenReturn(null);

        assertTrue(filter.filter(resource, new StringBuilder()));
    }

    @Test
    public void filterAttributeNullValueNonNull() {
        FilterByProperty filter = new FilterByProperty(NAME, VALUE);
        when(values.get(NAME)).thenReturn(null);

        assertFalse(filter.filter(resource, new StringBuilder()));
    }

    @Test
    public void filterAttributeNonNullValueNull() {
        FilterByProperty filter = new FilterByProperty(NAME, null);
        when(values.get(NAME)).thenReturn(VALUE);

        assertFalse(filter.filter(resource, new StringBuilder()));
    }

    @Test
    public void filterAttributeNonNullValueNonNull() {
        FilterByProperty filter = new FilterByProperty(NAME, VALUE);
        when(values.get(NAME)).thenReturn(VALUE);

        assertTrue(filter.filter(resource, new StringBuilder()));
    }

    @Test
    public void filterAttributeGString() {
        FilterByProperty filter = new FilterByProperty(NAME, VALUE_G);
        when(values.get(NAME)).thenReturn(VALUE + "3");

        assertTrue(filter.filter(resource, new StringBuilder()));
    }

}
