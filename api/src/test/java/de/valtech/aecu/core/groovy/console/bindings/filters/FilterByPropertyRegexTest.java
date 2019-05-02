/*
 * Copyright 2019 Valtech GmbH
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

import java.util.Set;
import java.util.TreeSet;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByPropertyRegex;

/**
 * Tests FilterByPropertyRegex
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterByPropertyRegexTest {

    private static final String REGEX = ".*value.*";
    private static final String NAME1 = "name1";
    private static final String NAME2 = "name2";
    private static final String NAME3 = "name3";
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final String VALUE3 = "value3";

    @Mock
    private Resource resource;

    @Mock
    ValueMap values;

    @Before
    public void setup() {
        when(resource.getValueMap()).thenReturn(values);
        Set<String> keys = new TreeSet<>();
        keys.add(NAME1);
        keys.add(NAME2);
        keys.add(NAME3);
        when(values.keySet()).thenReturn(keys);
    }

    @Test
    public void filterAttributeValueNull() {
        FilterByPropertyRegex filter = new FilterByPropertyRegex(NAME1, REGEX);
        when(values.get(NAME1, String.class)).thenReturn(null);

        assertFalse(filter.filter(resource, new StringBuffer()));
    }

    @Test
    public void filterAttributeValueMatches() {
        FilterByPropertyRegex filter = new FilterByPropertyRegex(NAME1, REGEX);
        when(values.get(NAME1, String.class)).thenReturn(VALUE1);

        assertTrue(filter.filter(resource, new StringBuffer()));
    }

    @Test
    public void filterAttributeValueNotMatches() {
        FilterByPropertyRegex filter = new FilterByPropertyRegex(NAME1, REGEX);
        when(values.get(NAME1, String.class)).thenReturn("test");

        assertFalse(filter.filter(resource, new StringBuffer()));
    }

    @Test
    public void filterAttributeMultiPropertiesMatches1() {
        FilterByPropertyRegex filter = new FilterByPropertyRegex(null, ".*value1.*");
        when(values.get(NAME1, String.class)).thenReturn(VALUE1);

        assertTrue(filter.filter(resource, new StringBuffer()));
    }

    @Test
    public void filterAttributeMultiPropertiesMatches2() {
        FilterByPropertyRegex filter = new FilterByPropertyRegex(null, ".*value2.*");
        when(values.get(NAME1, String.class)).thenReturn(VALUE1);
        when(values.get(NAME2, String.class)).thenReturn(VALUE2);

        assertTrue(filter.filter(resource, new StringBuffer()));
    }

    @Test
    public void filterAttributeMultiPropertiesMatches3() {
        FilterByPropertyRegex filter = new FilterByPropertyRegex(null, ".*value3.*");
        when(values.get(NAME1, String.class)).thenReturn(VALUE1);
        when(values.get(NAME2, String.class)).thenReturn(VALUE2);
        when(values.get(NAME3, String.class)).thenReturn(VALUE3);

        assertTrue(filter.filter(resource, new StringBuffer()));
    }

    @Test
    public void filterAttributeMultiPropertiesNotMatches() {
        FilterByPropertyRegex filter = new FilterByPropertyRegex(null, ".*test.*");
        when(values.get(NAME1, String.class)).thenReturn(VALUE1);
        when(values.get(NAME2, String.class)).thenReturn(VALUE2);
        when(values.get(NAME3, String.class)).thenReturn(VALUE3);

        assertFalse(filter.filter(resource, new StringBuffer()));
    }

}
