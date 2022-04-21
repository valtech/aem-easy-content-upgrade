package de.valtech.aecu.core.util.runtime;

import javax.jcr.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
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
        assertTrue(ise.getMessage().contains("Could not check if session is connected to a composite node store"));
    }

}
