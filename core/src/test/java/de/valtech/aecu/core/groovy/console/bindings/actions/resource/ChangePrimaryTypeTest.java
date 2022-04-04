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
package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

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
 * Tests ChangePrimaryTypeTest
 *
 * @author Sajith
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ChangePrimaryTypeTest {

    private static final String PATH = "path";

    private static final String newPrimaryType = "nt:unstructured";

    @Mock
    private Resource resource;

    @Mock
    private Node node;

    @BeforeEach
    public void setup() {
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(resource.getPath()).thenReturn(PATH);
    }

    @Test
    public void test_doAction_setPrimaryType() throws PersistenceException, RepositoryException {
        ChangePrimaryType changePrimaryType = new ChangePrimaryType(newPrimaryType);
        String result = changePrimaryType.doAction(resource);
        verify(node, times(1)).setPrimaryType(newPrimaryType);
        assertEquals("Updated jcr:primaryType to " + newPrimaryType + " for resource " + PATH, result);
    }

    @Test
    public void test_doAction_nodeIsNull() throws PersistenceException, RepositoryException {
        when(resource.adaptTo(Node.class)).thenReturn(null);
        ChangePrimaryType changePrimaryType = new ChangePrimaryType(newPrimaryType);
        changePrimaryType.doAction(resource);
        verify(node, never()).setPrimaryType(newPrimaryType);
    }
}
