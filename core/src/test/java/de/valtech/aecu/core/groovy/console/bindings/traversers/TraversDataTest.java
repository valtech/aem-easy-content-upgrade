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
package de.valtech.aecu.core.groovy.console.bindings.traversers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests for TraversData.
 * 
 * @author Roland Gruber
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TraversDataTest {

    @Spy
    private TraversData traversData;

    @Mock
    private Resource resource;

    @Mock
    private ValueMap valueMap;

    @Before
    public void setup() {
        when(resource.getValueMap()).thenReturn(valueMap);
    }

    @Test
    public void isResourceValid_valid() {
        when(valueMap.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn("type");

        assertTrue(traversData.isResourceValid(resource));
    }

    @Test
    public void isResourceValid_invalid() {
        when(valueMap.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenThrow(IllegalArgumentException.class);

        assertFalse(traversData.isResourceValid(resource));
    }

}
