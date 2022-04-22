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
package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.value.StringValue;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Tests ReplaceResourcePropertyValuesRegex
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ReplaceResourcePropertyValuesRegexTest {

    private static final String PROP3 = "prop3";
    private static final String PROP2 = "prop2";
    private static final String PROP1 = "prop1";
    private static final String NEW_VAL = "newVal";
    private static final String OLD_VAL = "oldVal";
    private static final String PATH = "/content/path";

    @Mock
    private Resource resource;

    @Mock
    private Node node;

    @Mock
    private PropertyIterator propertyIterator;

    private Value val31 = new StringValue("val3_oldVal_suffix");
    private Value val32 = new StringValue("val3_test1_suffix");

    @Mock
    private Property prop1;

    @Mock
    private Property prop2;

    @Mock
    private Property prop3;

    @Mock
    private Property prop4;

    @BeforeEach
    public void setup() throws RepositoryException {
        when(resource.getPath()).thenReturn(PATH);
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(node.getProperties()).thenReturn(propertyIterator);
        when(propertyIterator.hasNext()).thenReturn(true, true, true, true, false);
        when(propertyIterator.nextProperty()).thenReturn(prop1, prop2, prop3, prop4);
        when(prop1.getString()).thenReturn("val1_oldVal_suffix");
        when(prop2.getString()).thenReturn("val2_test2_suffix");
        when(prop3.isMultiple()).thenReturn(true);
        when(prop3.getValues()).thenReturn(new Value[] {val31, val32});
        when(prop1.getName()).thenReturn(PROP1);
        when(prop2.getName()).thenReturn(PROP2);
        when(prop3.getName()).thenReturn(PROP3);
        when(prop1.getType()).thenReturn(PropertyType.STRING);
        when(prop2.getType()).thenReturn(PropertyType.STRING);
        when(prop3.getType()).thenReturn(PropertyType.STRING);
        when(prop4.getType()).thenReturn(PropertyType.BOOLEAN);
    }

    @Test
    public void valueMatches() {
        ReplaceResourcePropertyValues action = new ReplaceResourcePropertyValuesRegex(OLD_VAL, NEW_VAL, Arrays.asList());

        assertTrue(action.valueMatches("oldVal"));
        assertTrue(action.valueMatches("1oldVal"));
        assertTrue(action.valueMatches("oldVal1"));
        assertTrue(action.valueMatches("1oldVal1"));
        assertFalse(action.valueMatches("1newVal1"));
    }

    @Test
    public void getNewValue() {
        ReplaceResourcePropertyValues action = new ReplaceResourcePropertyValuesRegex(OLD_VAL, NEW_VAL, Arrays.asList());

        assertEquals("newVal", action.getNewValue("oldVal"));
        assertEquals("1newVal", action.getNewValue("1oldVal"));
        assertEquals("newVal1", action.getNewValue("oldVal1"));
        assertEquals("1newVal1", action.getNewValue("1oldVal1"));
        assertEquals("1newVal11newVal1", action.getNewValue("1oldVal11oldVal1"));
    }

    @Test
    public void getNewValue_matcherGroup() {
        ReplaceResourcePropertyValues action =
                new ReplaceResourcePropertyValuesRegex("(" + OLD_VAL + ")", NEW_VAL + "-$1", Arrays.asList());

        assertEquals("newVal-oldVal", action.getNewValue("oldVal"));
        assertEquals("newVal-oldVal#newVal-oldVal", action.getNewValue("oldVal#oldVal"));
    }

    @Test
    public void doAction_allProperties() throws PersistenceException, ValueFormatException, VersionException, LockException,
            ConstraintViolationException, RepositoryException {
        ReplaceResourcePropertyValues action = new ReplaceResourcePropertyValuesRegex(OLD_VAL, NEW_VAL, Arrays.asList());

        String result = action.doAction(resource);

        assertEquals("Updated values from " + OLD_VAL + " to " + NEW_VAL + " in " + PATH, result);
        verify(prop1, times(1)).setValue("val1_newVal_suffix");
        verify(prop2, never()).setValue(Mockito.anyString());
        verify(prop3, times(1)).setValue(Mockito.any(Value[].class));
        verify(prop4, never()).setValue(Mockito.anyString());
    }

    @Test
    public void doAction_prop1Only() throws PersistenceException, ValueFormatException, VersionException, LockException,
            ConstraintViolationException, RepositoryException {
        ReplaceResourcePropertyValues action = new ReplaceResourcePropertyValuesRegex(OLD_VAL, NEW_VAL, Arrays.asList(PROP1));

        String result = action.doAction(resource);

        assertEquals("Updated values from " + OLD_VAL + " to " + NEW_VAL + " in " + PATH, result);
        verify(prop1, times(1)).setValue("val1_newVal_suffix");
        verify(prop2, never()).setValue(Mockito.anyString());
        verify(prop3, never()).setValue(Mockito.any(Value[].class));
        verify(prop4, never()).setValue(Mockito.anyString());
    }

    @Test
    public void doAction_noMatch() throws PersistenceException, ValueFormatException, VersionException, LockException,
            ConstraintViolationException, RepositoryException {
        ReplaceResourcePropertyValues action = new ReplaceResourcePropertyValuesRegex("nomatch", NEW_VAL, Arrays.asList(PROP1));

        String result = action.doAction(resource);

        assertEquals("", result);
        verify(prop1, never()).setValue("val1_newVal_suffix");
        verify(prop2, never()).setValue(Mockito.anyString());
        verify(prop3, never()).setValue(Mockito.any(Value[].class));
        verify(prop4, never()).setValue(Mockito.anyString());
    }

    @Test
    public void doAction_matchesButNoChange() throws PersistenceException {
        ReplaceResourcePropertyValues action = new ReplaceResourcePropertyValuesRegex("(oldVal)", "$1", Arrays.asList());

        String result = action.doAction(resource);

        assertEquals("", result);
    }

}
