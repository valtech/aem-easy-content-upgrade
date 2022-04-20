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

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByMultiValuePropContains;
import groovy.lang.GString;

/**
 * Tests FilterByMultiValuePropContains
 * 
 * @author Roland Gruber
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FilterByMultiValuePropContainsTest {

    private static final String NAME1 = "name";
    private static final String VALUE1 = "value";
    private static final String NAME2 = "name2";
    private static final String VALUE2 = "value2";
    private static final String NAME3 = "name3";
    private static final GString VALUE_G = new GStringImpl(new Object[] {2}, new String[] {"value"});

    @Mock
    private Resource resource;

    @Mock
    ValueMap values;

    @BeforeEach
    public void setup() {
        when(resource.getValueMap()).thenReturn(values);
        when(values.get(NAME1)).thenReturn(new Object[] {VALUE1, VALUE2});
        when(values.get(NAME2)).thenReturn(new Object[] {VALUE2});
    }

    @Test
    public void filterAttributeNull_filterValueEmpty() {
        Object[] filterAttributes = new Object[0];
        FilterByMultiValuePropContains filter = new FilterByMultiValuePropContains(NAME1, filterAttributes);

        assertTrue(filter.filter(resource, new StringBuilder()));
    }

    @Test
    public void filterAttributeNull_filterValueNonNull() {
        Object[] filterAttributes = new Object[] {VALUE1};
        FilterByMultiValuePropContains filter = new FilterByMultiValuePropContains(NAME3, filterAttributes);

        assertFalse(filter.filter(resource, new StringBuilder()));
    }

    @Test
    public void filterAttributeNonNull_filterValueNonNull() {
        Object[] filterAttributes = new Object[] {VALUE1};
        FilterByMultiValuePropContains filter = new FilterByMultiValuePropContains(NAME1, filterAttributes);

        assertTrue(filter.filter(resource, new StringBuilder()));
    }

    @Test
    public void filterAttributeNonNull_filterValueMulti() {
        Object[] filterAttributes = new Object[] {VALUE1, VALUE2};
        FilterByMultiValuePropContains filter = new FilterByMultiValuePropContains(NAME1, filterAttributes);

        assertTrue(filter.filter(resource, new StringBuilder()));
    }

    @Test
    public void filterAttributeGString() {
        Object[] filterAttributes = new Object[] {VALUE_G};
        FilterByMultiValuePropContains filter = new FilterByMultiValuePropContains(NAME2, filterAttributes);

        assertTrue(filter.filter(resource, new StringBuilder()));
    }

}
