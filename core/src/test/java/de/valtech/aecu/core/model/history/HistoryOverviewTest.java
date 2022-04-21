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
package de.valtech.aecu.core.model.history;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.ExecutionState;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.core.history.HistoryUtil;
import de.valtech.aecu.core.model.history.HistoryOverview.DonutData;
import de.valtech.aecu.core.security.AccessValidationService;
import de.valtech.aecu.core.service.HistoryEntryImpl;

/**
 * Tests HistoryOverview
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HistoryOverviewTest {

    private static final String PATH = "path";

    @InjectMocks
    private HistoryOverview overview;

    @Mock
    private HistoryUtil historyUtil;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Resource historyResource;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private AccessValidationService accessValidationService;

    @BeforeEach
    public void setup() {
        RequestParameter param = mock(RequestParameter.class);
        when(request.getRequestParameter("entry")).thenReturn(param);
        when(param.getString()).thenReturn(PATH);
        when(resolver.getResource(PATH)).thenReturn(historyResource);
        when(accessValidationService.canReadHistory(request)).thenReturn(true);
    }

    @Test
    public void getHistory_noAccess() {
        when(accessValidationService.canReadHistory(request)).thenReturn(false);

        overview.init();
        HistoryEntry history = overview.getHistory();

        assertNull(history);
    }

    @Test
    public void getHistory_noParam() {
        when(request.getRequestParameter("entry")).thenReturn(null);

        overview.init();
        HistoryEntry history = overview.getHistory();

        assertNull(history);
    }

    @Test
    public void getHistory() {
        HistoryEntry entry = new HistoryEntryImpl();
        when(historyUtil.readHistoryEntry(historyResource)).thenReturn(entry);

        overview.init();
        HistoryEntry history = overview.getHistory();

        assertEquals(entry, history);
    }

    @Test
    public void getStart_empty() {
        overview.init();
        String start = overview.getStart();

        assertEquals(StringUtils.EMPTY, start);
    }

    @Test
    public void getStart() {
        HistoryEntryImpl entry = new HistoryEntryImpl();
        Date now = new Date();
        entry.setStart(now);
        when(historyUtil.readHistoryEntry(historyResource)).thenReturn(entry);

        overview.init();
        String start = overview.getStart();

        assertNotNull(start);
    }

    @Test
    public void getEnd_empty() {
        overview.init();
        String end = overview.getEnd();

        assertEquals(StringUtils.EMPTY, end);
    }

    @Test
    public void getEnd() {
        HistoryEntryImpl entry = new HistoryEntryImpl();
        Date now = new Date();
        entry.setEnd(now);
        when(historyUtil.readHistoryEntry(historyResource)).thenReturn(entry);

        overview.init();
        String end = overview.getEnd();

        assertNotNull(end);
    }

    @Test
    public void getDuration() {
        HistoryEntryImpl entry = new HistoryEntryImpl();
        Date now = new Date();
        entry.setStart(now);
        entry.setEnd(now);
        when(historyUtil.readHistoryEntry(historyResource)).thenReturn(entry);

        overview.init();
        String duration = overview.getDuration();

        assertNotNull(duration);
    }

    @Test
    public void getDonutData() {
        HistoryEntryImpl entry = new HistoryEntryImpl();
        entry.addSingleResult(new ExecutionResult(ExecutionState.SKIPPED, "5s", "", null, null, "path"));
        entry.addSingleResult(new ExecutionResult(ExecutionState.SUCCESS, "5s", "", null, null, "path"));
        entry.addSingleResult(new ExecutionResult(ExecutionState.SUCCESS, "5s", "", null, null, "path"));
        entry.addSingleResult(new ExecutionResult(ExecutionState.FAILED, "5s", "", null, null, "path"));
        Date now = new Date();
        entry.setStart(now);
        entry.setEnd(now);
        when(historyUtil.readHistoryEntry(historyResource)).thenReturn(entry);

        overview.init();
        DonutData data = overview.getDonutData();

        assertEquals("50", data.getOkLength());
        assertEquals("75", data.getFailedLength());
        assertEquals("25", data.getFailedRemainder());
        assertEquals("50", data.getOkRemainder());
        assertEquals("50", data.getPercentageOk());
    }

}
