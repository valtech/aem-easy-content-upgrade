/*
 * Copyright 2021 Valtech GmbH
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.valtech.aecu.api.groovy.console.bindings.filters.FilterBy;
import de.valtech.aecu.api.groovy.console.bindings.filters.ORFilter;

/**
 * Tests ORFilter
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ORFilterTest {

    @Mock
    private FilterBy matches1;

    @Mock
    private FilterBy matches2;

    @Mock
    private FilterBy notMatches1;

    @Mock
    private FilterBy notMatches2;

    @Mock
    private Resource resource;

    private StringBuilder stringBuilder;

    @BeforeEach
    public void setup() {
        stringBuilder = new StringBuilder();
        when(matches1.filter(resource, stringBuilder)).thenReturn(true);
        when(matches2.filter(resource, stringBuilder)).thenReturn(true);
        when(notMatches1.filter(resource, stringBuilder)).thenReturn(false);
        when(notMatches2.filter(resource, stringBuilder)).thenReturn(false);
    }

    @Test
    public void filter_match1() {
        List<FilterBy> filters = new ArrayList<>(Arrays.asList(matches1));

        ORFilter filter = new ORFilter(filters);

        assertTrue(filter.filter(resource, stringBuilder));
    }

    @Test
    public void filter_match2() {
        List<FilterBy> filters = new ArrayList<>(Arrays.asList(matches1, matches2));

        ORFilter filter = new ORFilter(filters);

        assertTrue(filter.filter(resource, stringBuilder));
    }

    @Test
    public void filter_no_match1() {
        List<FilterBy> filters = new ArrayList<>(Arrays.asList(notMatches1));

        ORFilter filter = new ORFilter(filters);

        assertFalse(filter.filter(resource, stringBuilder));
    }

    @Test
    public void filter_no_match2() {
        List<FilterBy> filters = new ArrayList<>(Arrays.asList(notMatches2, notMatches1));

        ORFilter filter = new ORFilter(filters);

        assertFalse(filter.filter(resource, stringBuilder));
    }

    @Test
    public void filter_mixed1() {
        List<FilterBy> filters = new ArrayList<>(Arrays.asList(matches2, notMatches2, notMatches1));

        ORFilter filter = new ORFilter(filters);

        assertTrue(filter.filter(resource, stringBuilder));
    }

    @Test
    public void filter_mixed2() {
        List<FilterBy> filters = new ArrayList<>(Arrays.asList(notMatches1, notMatches2, matches1));

        ORFilter filter = new ORFilter(filters);

        assertTrue(filter.filter(resource, stringBuilder));
    }

}
