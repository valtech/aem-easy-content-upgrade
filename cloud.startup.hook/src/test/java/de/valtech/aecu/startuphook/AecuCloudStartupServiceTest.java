/*
 * Copyright 2022 Bart Senn and Valtech GmbH
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.valtech.aecu.api.service.AecuService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AecuCloudStartupServiceTest {

    @Mock
    private AecuService aecuService;

    @Mock
    private ServiceResourceResolverService resolverService;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Session session;

    @InjectMocks
    @Spy
    private AecuCloudStartupService startupService;

    @BeforeEach
    public void setUp() throws Exception {
        when(resolverService.getAdminResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        doReturn(true).when(session).hasPermission(anyString(), anyString());
        doReturn(true).when(startupService).waitForServices();
    }

    @Test
    public void testMigration_compositeNodeStore() throws Exception {
        doReturn(false).when(session).hasCapability(anyString(), any(), any());

        startupService.checkAndRunMigration();

        verify(aecuService, times(1)).executeWithInstallHookHistory(AecuService.AECU_APPS_PATH_PREFIX);
    }

    @Test
    public void testMigration_compositeNodeStoreButServicesNotOk() throws Exception {
        doReturn(false).when(session).hasCapability(anyString(), any(), any());
        doReturn(false).when(startupService).waitForServices();

        assertThrows(IllegalStateException.class, () -> startupService.checkAndRunMigration());
    }

    @Test
    public void testMigration_noCompositeNodeStore() throws Exception {
        doReturn(true).when(session).hasCapability(anyString(), any(), any());

        startupService.checkAndRunMigration();

        verify(aecuService, never()).executeWithInstallHookHistory(AecuService.AECU_APPS_PATH_PREFIX);
    }

}
