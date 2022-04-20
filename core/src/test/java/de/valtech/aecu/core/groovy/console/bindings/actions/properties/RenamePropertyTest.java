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
package de.valtech.aecu.core.groovy.console.bindings.actions.properties;

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
 * Tests RenameProperty
 * 
 * @author Roland Gruber
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RenamePropertyTest {

    private static final String VAL1 = "val1";

    private static final String ATTR_OLD = "attrOld";
    private static final String ATTR_NEW = "attrNew";

    private static final String SUBNODE_PATH = "some/path";

    @Mock
    private Resource resource;

    @Mock
    private Resource subNode;

    @Mock
    private ModifiableValueMap valueMap;

    @Mock
    private ModifiableValueMap valueMapSubnode;

    @BeforeEach
    public void setup() {
        when(resource.adaptTo(ModifiableValueMap.class)).thenReturn(valueMap);
        when(subNode.adaptTo(ModifiableValueMap.class)).thenReturn(valueMapSubnode);
        when(resource.getChild(SUBNODE_PATH)).thenReturn(subNode);
    }

    @Test
    public void doAction() throws PersistenceException {
        when(valueMap.containsKey(ATTR_OLD)).thenReturn(true);
        when(valueMap.remove(ATTR_OLD)).thenReturn(VAL1);
        RenameProperty action = new RenameProperty(ATTR_OLD, ATTR_NEW, null);

        action.doAction(resource);

        verify(valueMap, times(1)).remove(ATTR_OLD);
        verify(valueMap, times(1)).put(ATTR_NEW, VAL1);
    }

    @Test
    public void doAction_subnode() throws PersistenceException {
        when(valueMapSubnode.containsKey(ATTR_OLD)).thenReturn(true);
        when(valueMapSubnode.remove(ATTR_OLD)).thenReturn(VAL1);
        RenameProperty action = new RenameProperty(ATTR_OLD, ATTR_NEW, SUBNODE_PATH);

        action.doAction(resource);

        verify(valueMapSubnode, times(1)).remove(ATTR_OLD);
        verify(valueMapSubnode, times(1)).put(ATTR_NEW, VAL1);
    }

}
