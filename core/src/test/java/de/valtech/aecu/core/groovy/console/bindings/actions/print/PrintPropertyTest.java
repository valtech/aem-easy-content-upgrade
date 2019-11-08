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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests PrintProperty
 * 
 * @author sravan
 * @author Roxana Muresan
 */
@RunWith(MockitoJUnitRunner.class)
public class PrintPropertyTest {

    private final String NOT_DEFINED_PROPERTY = "notDefinedProperty";
    private final String ANY_VALUE = "any_value";
    private final String EXISTING_SINGLE_VALUE_PROPERTY = "existingSingleValueProperty";
    private final String EXISTING_MULTI_VALUE_PROPERTY = "existingMultiValueProperty";

    @Mock
    private Resource resource;
    @Mock
    private ValueMap properties;
    @Mock
    private Node node;
    @Mock
    private Property singleValueProperty;
    @Mock
    private Property multiValueProperty;

    @Before
    public void init() throws RepositoryException {
        Value value1 = mock(Value.class);
        Value value2 = mock(Value.class);
        Value[] values = {value1, value2};
        when(value1.getString()).thenReturn(ANY_VALUE);
        when(value2.getString()).thenReturn(ANY_VALUE);
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(node.hasProperty(NOT_DEFINED_PROPERTY)).thenReturn(false);
        when(node.hasProperty(EXISTING_SINGLE_VALUE_PROPERTY)).thenReturn(true);
        when(node.getProperty(EXISTING_SINGLE_VALUE_PROPERTY)).thenReturn(singleValueProperty);
        when(node.hasProperty(EXISTING_MULTI_VALUE_PROPERTY)).thenReturn(true);
        when(node.getProperty(EXISTING_MULTI_VALUE_PROPERTY)).thenReturn(multiValueProperty);
        when(multiValueProperty.isMultiple()).thenReturn(true);
        when(singleValueProperty.isMultiple()).thenReturn(false);
        when(singleValueProperty.getValue()).thenReturn(value1);
        when(singleValueProperty.getValue().getString()).thenReturn(ANY_VALUE);
        when(multiValueProperty.getValues()).thenReturn(values);
    }

    @Test
    public void test_doAction_noProperty() throws RepositoryException {
        PrintProperty printPropertyAction = new PrintProperty(NOT_DEFINED_PROPERTY);
        String result = printPropertyAction.doAction(resource);

        verify(singleValueProperty, never()).isMultiple();

        assertTrue(result.contains(NOT_DEFINED_PROPERTY + " not defined"));
    }

    @Test
    public void test_doAction_existingSingleValueProperty() throws RepositoryException {
        PrintProperty printPropertyAction = new PrintProperty(EXISTING_SINGLE_VALUE_PROPERTY);
        String result = printPropertyAction.doAction(resource);

        verify(singleValueProperty, atLeastOnce()).isMultiple();
        verify(singleValueProperty, atLeastOnce()).getValue();
        assertTrue(result.contains(EXISTING_SINGLE_VALUE_PROPERTY + " = " + ANY_VALUE));
    }

    @Test
    public void test_doAction_existingMultiValueProperty() throws RepositoryException {
        PrintProperty printPropertyAction = new PrintProperty(EXISTING_MULTI_VALUE_PROPERTY);
        String result = printPropertyAction.doAction(resource);

        verify(multiValueProperty, atLeastOnce()).isMultiple();
        verify(multiValueProperty, atLeastOnce()).getValues();
        assertTrue(result.contains(EXISTING_MULTI_VALUE_PROPERTY + " = [" + ANY_VALUE + ", " + ANY_VALUE + "]"));
    }

}
