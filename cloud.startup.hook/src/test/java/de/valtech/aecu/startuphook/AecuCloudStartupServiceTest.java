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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.JobManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.api.service.HistoryEntry.STATE;

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

    @Mock
    private HistoryEntry historyEntry;

    @Mock
    private JobManager jobManager;

    @InjectMocks
    @Spy
    private AecuCloudStartupService startupService;

    @BeforeEach
    public void setUp() throws Exception {
        when(resolverService.getAdminResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(jobManager.addJob(anyString(), any())).thenReturn(null);
        doReturn(true).when(session).hasPermission(anyString(), anyString());
        doReturn(true).when(startupService).waitForServices();
    }

    @Test
    public void testMigration_compositeNodeStore() throws Exception {
        doReturn(false).when(session).hasCapability(anyString(), any(), any());

        startupService.checkAndRunMigration();

        verify(jobManager, times(1)).addJob(AecuStartupJobConsumer.JOB_TOPIC, null);
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

        verify(jobManager, never()).addJob(AecuStartupJobConsumer.JOB_TOPIC, null);
    }

    @Test
    public void migrationInProgress_noHistory() throws AecuException {
        when(aecuService.getHistory(0, 1)).thenReturn(Collections.emptyList());

        assertFalse(startupService.isMigrationInProgress());
    }

    @Test
    public void migrationInProgress_notRunning() throws AecuException {
        when(historyEntry.getState()).thenReturn(STATE.FINISHED);
        List<HistoryEntry> history = new ArrayList<>();
        history.add(historyEntry);
        when(aecuService.getHistory(0, 1)).thenReturn(history);

        assertFalse(startupService.isMigrationInProgress());
    }

    @Test
    public void migrationInProgress_runningOverLimit() throws AecuException {
        when(historyEntry.getState()).thenReturn(STATE.RUNNING);
        when(historyEntry.getStart()).thenReturn(new Date(System.currentTimeMillis() - (7200 * 1000)));
        List<HistoryEntry> history = new ArrayList<>();
        history.add(historyEntry);
        when(aecuService.getHistory(0, 1)).thenReturn(history);

        assertFalse(startupService.isMigrationInProgress());
    }

    @Test
    public void migrationInProgress_runningNotOverLimit() throws AecuException {
        when(historyEntry.getState()).thenReturn(STATE.RUNNING);
        when(historyEntry.getStart()).thenReturn(new Date(System.currentTimeMillis() - (100 * 1000)));
        List<HistoryEntry> history = new ArrayList<>();
        history.add(historyEntry);
        when(aecuService.getHistory(0, 1)).thenReturn(history);

        assertTrue(startupService.isMigrationInProgress());
    }

}
