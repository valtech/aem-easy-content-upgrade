/*
 * Copyright 2022 Bart Thierens and Valtech GmbH
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
package de.valtech.aecu.startuphook;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import javax.jcr.Session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RuntimeHelperTest {

    @Mock
    private Session session;

    @Test
    public void testIsCompositeNodeStore() throws Exception {
        assertTrue(mockIsCompositeNodeStore(true, true));
    }

    @Test
    public void testIsNoCompositeNodeStoreByPermission() throws Exception {
        assertFalse(mockIsCompositeNodeStore(false, true));
    }

    @Test
    public void testIsNoCompositeNodeStoreByCapability() throws Exception {
        assertFalse(mockIsCompositeNodeStore(true, false));
    }

    @Test
    public void testIsNoCompositeNodeStoreByPermissionAndCapability() throws Exception {
        assertFalse(mockIsCompositeNodeStore(false, false));
    }

    private boolean mockIsCompositeNodeStore(boolean hasPermission, boolean hasNoCapability) throws Exception {
        doReturn(hasPermission).when(session).hasPermission(anyString(), anyString());
        doReturn(!hasNoCapability).when(session).hasCapability(anyString(), any(), any());
        return RuntimeHelper.isCompositeNodeStore(session);
    }

    @Test
    public void testWrappedException() throws Exception {
        IllegalStateException ise = assertThrows(IllegalStateException.class, () -> RuntimeHelper.isCompositeNodeStore(null));
        assertEquals("Unable to check if session is uses a composite node store", ise.getMessage());
    }

}
