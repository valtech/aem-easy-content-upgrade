/*
 * Copyright 2023 Valtech GmbH
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

import org.apache.sling.api.resource.ValueMap;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterByPropertyIsMultiple;

import java.util.Arrays;
import java.util.List;

/**
 * Tests FilterByPropertyIsMultiple
 *
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FilterByPropertyIsMultipleTest {

    private static final String NAME = "name";
    private static final List<String> MULTIPLE_VALUE = Arrays.asList("value1", "value2");

    @Mock
    private Resource resource;

    @Mock
    ValueMap values;

    @Mock
    ValueMap valueMap;

    @BeforeEach
    public void setup() {
        when(resource.getValueMap()).thenReturn(values);
    }

    @Test
    void filterAttributeArray() {
        Object[] attrArray = new Object[]{"value1", "value2", "value3"};
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("name")).thenReturn(attrArray);

        FilterByPropertyIsMultiple filter = new FilterByPropertyIsMultiple("name", attrArray);

        assertTrue(filter.filter(resource, new StringBuilder()));
    }



    @Test
    public void filterAttributeList() {
        FilterByPropertyIsMultiple filter = new FilterByPropertyIsMultiple(NAME, MULTIPLE_VALUE);
        when(values.get(NAME)).thenReturn(MULTIPLE_VALUE);

        assertTrue(filter.filter(resource, new StringBuilder()));
    }

    @Test
    public void filterAttributeNonMultiple() {
        FilterByPropertyIsMultiple filter = new FilterByPropertyIsMultiple(NAME, "value");
        when(values.get(NAME)).thenReturn("value");

        assertFalse(filter.filter(resource, new StringBuilder()));
    }

}
