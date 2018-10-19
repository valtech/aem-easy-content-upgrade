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

package de.valtech.aecu.core.groovy.console.bindings.actions.print;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests PrintProperty
 *
 * @author Roxana Muresan
 */
@RunWith(MockitoJUnitRunner.class)
public class PrintPropertyTest {

    @Mock
    private Resource resource;
    @Mock
    private ValueMap properties;


    @Before
    public void init() {
        when(properties.containsKey("NotDefinedProperty")).thenReturn(false);

        when(properties.containsKey("ExistingProperty")).thenReturn(true);
        when(properties.get("ExistingProperty")).thenReturn("any_value");

        when(resource.getValueMap()).thenReturn(properties);
    }

    @Test
    public void test_doAction_noProperty() {
        PrintProperty printPropertyAction = new PrintProperty("NotDefinedProperty");
        String result = printPropertyAction.doAction(resource);

        verify(properties, never()).get("NotDefinedProperty");

        assertTrue(result.contains("NotDefinedProperty not defined"));
    }

    @Test
    public void test_doAction_existingProperty() {
        PrintProperty printPropertyAction = new PrintProperty("ExistingProperty");
        String result = printPropertyAction.doAction(resource);

        verify(properties, times(1)).get("ExistingProperty");

        assertTrue(result.contains("ExistingProperty=any_value"));
    }

}
