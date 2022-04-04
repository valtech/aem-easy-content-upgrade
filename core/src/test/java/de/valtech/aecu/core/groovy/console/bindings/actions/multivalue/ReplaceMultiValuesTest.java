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
package de.valtech.aecu.core.groovy.console.bindings.actions.multivalue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Tests ReplaceMultiValues
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ReplaceMultiValuesTest {

    private static final String VAL1 = "val1";
    private static final String VAL2 = "val2";
    private static final String VAL3 = "val3";
    private static final String VAL4 = "val4";
    private static final String VAL5 = "val5";

    private static final String ATTR = "attr";

    @Mock
    private Resource resource;

    @Mock
    private ModifiableValueMap valueMap;

    @BeforeEach
    public void setup() {
        when(resource.adaptTo(ModifiableValueMap.class)).thenReturn(valueMap);
    }

    @Test
    public void doAction() throws PersistenceException {
        ReplaceMultiValues action = new ReplaceMultiValues(ATTR, new String[] {VAL1, VAL2}, new String[] {VAL3, VAL4});

        action.doAction(resource);

        verify(valueMap, times(1)).put(ATTR, new String[] {});
    }

    @Test
    public void doAction_existingAttribute() throws PersistenceException {
        when(valueMap.get(ATTR, String[].class)).thenReturn(new String[] {VAL5, VAL2});
        ReplaceMultiValues action = new ReplaceMultiValues(ATTR, new String[] {VAL1, VAL2}, new String[] {VAL3, VAL4});

        action.doAction(resource);

        verify(valueMap, times(1)).put(ATTR, new String[] {VAL5, VAL4});
    }

    @Test
    public void doAction_existingAttribute2() throws PersistenceException {
        when(valueMap.get(ATTR, String[].class)).thenReturn(new String[] {VAL1, VAL2, VAL5});
        ReplaceMultiValues action = new ReplaceMultiValues(ATTR, new String[] {VAL1, VAL2}, new String[] {VAL3, VAL4});

        action.doAction(resource);

        verify(valueMap, times(1)).put(ATTR, new String[] {VAL3, VAL4, VAL5});
    }

}
