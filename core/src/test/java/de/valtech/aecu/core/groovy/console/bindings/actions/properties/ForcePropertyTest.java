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
package de.valtech.aecu.core.groovy.console.bindings.actions.properties;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests ForceProperty
 *
 * @author Yves De Bruyne
 */
@RunWith(MockitoJUnitRunner.class)
public class ForcePropertyTest {

    private static final String VAL1 = "val1";
    private static final String[] VAL_CHANGE_TYPE = new String[]{"val1"};

    private static final String ATTR = "attr";

    @Mock
    private Resource resource;

    @Mock
    private ModifiableValueMap valueMap;

    @Before
    public void setup() {
        when(resource.adaptTo(ModifiableValueMap.class)).thenReturn(valueMap);
    }

    @Test
    public void doActionWithExistingProperty() throws PersistenceException {
        ForceProperty action = new ForceProperty(ATTR, VAL1);

        action.doAction(resource);

        verify(valueMap, times(1)).put(ATTR, VAL1);
    }

    @Test
    public void doActionWithChangeType() throws PersistenceException {
        SetProperty setAction = new SetProperty(ATTR, VAL1);
        ForceProperty action = new ForceProperty(ATTR, VAL_CHANGE_TYPE);

        setAction.doAction(resource);
        action.doAction(resource);

        verify(valueMap, times(1)).put(ATTR, VAL1);
        verify(valueMap, times(1)).remove(ATTR);
        verify(valueMap, times(1)).put(ATTR, VAL_CHANGE_TYPE);
    }

    @Test
    public void doActionWithNewAttribute() throws PersistenceException {
        ForceProperty action = new ForceProperty(ATTR, VAL1);

        action.doAction(resource);

        verify(valueMap, times(1)).put(ATTR, VAL1);
    }

}
