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
package de.valtech.aecu.core.history;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.ExecutionState;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.api.service.HistoryEntry.RESULT;
import de.valtech.aecu.api.service.HistoryEntry.STATE;
import de.valtech.aecu.core.service.HistoryEntryImpl;

/**
 * Tests HistoryUtil
 *
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HistoryUtilTest {

    @Spy
    private HistoryUtil historyUtil;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Resource resource;

    @Mock
    private ModifiableValueMap valueMap;

    @Mock(name = "base")
    private Resource base;

    @BeforeEach
    public void setup() {
        when(resource.adaptTo(ModifiableValueMap.class)).thenReturn(valueMap);
        when(base.getPath()).thenReturn(HistoryUtil.HISTORY_BASE);
        when(resolver.getResource(HistoryUtil.HISTORY_BASE)).thenReturn(base);

        Resource year = mock(Resource.class, "year");
        when(year.getName()).thenReturn("2000");
        when(year.getResourceResolver()).thenReturn(resolver);
        when(base.listChildren()).thenReturn(Arrays.asList(year).iterator());
        ValueMap yearMap = mock(ValueMap.class);
        when(year.adaptTo(ValueMap.class)).thenReturn(yearMap);
        when(yearMap.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn(JcrResourceConstants.NT_SLING_ORDERED_FOLDER);
        when(year.getParent()).thenReturn(base);

        Resource month = mock(Resource.class, "month");
        when(month.getName()).thenReturn("3");
        when(month.getResourceResolver()).thenReturn(resolver);
        when(year.listChildren()).thenReturn(Arrays.asList(month).iterator());
        when(year.getChildren()).thenReturn(Arrays.asList(month));
        ValueMap monthMap = mock(ValueMap.class);
        when(month.adaptTo(ValueMap.class)).thenReturn(monthMap);
        when(monthMap.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn(JcrResourceConstants.NT_SLING_ORDERED_FOLDER);
        when(month.getParent()).thenReturn(year);

        Resource day = mock(Resource.class, "day");
        when(day.getName()).thenReturn("5");
        when(day.getResourceResolver()).thenReturn(resolver);
        when(month.listChildren()).thenReturn(Arrays.asList(day).iterator());
        when(month.getChildren()).thenReturn(Arrays.asList(day));
        ValueMap dayMap = mock(ValueMap.class);
        when(day.adaptTo(ValueMap.class)).thenReturn(dayMap);
        when(dayMap.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn(JcrResourceConstants.NT_SLING_ORDERED_FOLDER);
        when(day.getParent()).thenReturn(month);

        Resource historyEntry = mock(Resource.class, "historyEntry");
        when(historyEntry.getName()).thenReturn("123456789");
        when(historyEntry.getResourceResolver()).thenReturn(resolver);
        when(day.listChildren()).thenReturn(Arrays.asList(historyEntry).iterator());
        when(day.getChildren()).thenReturn(Arrays.asList(historyEntry));
        ValueMap historyMap = mock(ValueMap.class);
        when(historyEntry.adaptTo(ValueMap.class)).thenReturn(historyMap);
        when(historyEntry.getParent()).thenReturn(day);
        when(historyMap.containsKey(HistoryUtil.ATTR_STATE)).thenReturn(true);
        when(historyMap.get(HistoryUtil.ATTR_STATE, String.class)).thenReturn(STATE.FINISHED.name());
        when(historyMap.containsKey(HistoryUtil.ATTR_START)).thenReturn(true);
        when(historyMap.get(HistoryUtil.ATTR_START, Calendar.class)).thenReturn(Calendar.getInstance());
        when(historyMap.containsKey(HistoryUtil.ATTR_END)).thenReturn(true);
        when(historyMap.get(HistoryUtil.ATTR_END, Calendar.class)).thenReturn(Calendar.getInstance());

        Resource singleResult = mock(Resource.class, "singeResult");
        when(singleResult.getResourceResolver()).thenReturn(resolver);
        when(historyEntry.getChildren()).thenReturn(Arrays.asList(singleResult));
        ValueMap singleResultMap = mock(ValueMap.class);
        when(singleResultMap.get(HistoryUtil.ATTR_RUN_STATE, ExecutionState.FAILED.name()))
                .thenReturn(ExecutionState.FAILED.name());
        when(singleResult.adaptTo(ValueMap.class)).thenReturn(singleResultMap);

        Resource fallbackResult = mock(Resource.class, "fallbackResult");
        when(fallbackResult.getResourceResolver()).thenReturn(resolver);
        when(singleResult.getChild(HistoryUtil.NODE_FALLBACK)).thenReturn(fallbackResult);
        when(singleResult.getChildren()).thenReturn(Arrays.asList(fallbackResult));
        ValueMap fallbackMap = mock(ValueMap.class);
        when(fallbackMap.get(HistoryUtil.ATTR_RUN_STATE, ExecutionState.FAILED.name())).thenReturn(ExecutionState.SUCCESS.name());
        when(fallbackResult.adaptTo(ValueMap.class)).thenReturn(fallbackMap);
    }

    @Test
    public void createPath_Existing() throws AecuException {
        String path = "/var/aecu/2018/5";
        when(resolver.getResource(path)).thenReturn(mock(Resource.class));

        historyUtil.createPath(path, resolver, JcrResourceConstants.NT_SLING_FOLDER);

        verify(historyUtil, times(1)).createPath(anyString(), eq(resolver), eq(JcrResourceConstants.NT_SLING_FOLDER));
    }

    @Test
    public void createPath_NotExisting() throws AecuException, PersistenceException {
        String path = "/var/aecu/2018/5";
        when(resolver.getResource("/var/aecu/2018")).thenReturn(mock(Resource.class));

        historyUtil.createPath(path, resolver, JcrResourceConstants.NT_SLING_FOLDER);

        verify(historyUtil, times(1)).createPath(anyString(), eq(resolver), eq(JcrResourceConstants.NT_SLING_FOLDER));
        verify(resolver, times(1)).create(any(Resource.class), eq("5"), any());
    }

    @Test
    public void createHistoryEntry() throws AecuException {
        when(resolver.getResource(Mockito.anyString())).thenReturn(resource);

        HistoryEntry entry = historyUtil.createHistoryEntry(resolver);

        assertEquals(STATE.RUNNING, entry.getState());
    }

    @Test
    public void storeExecutionInHistory() throws AecuException {
        when(resolver.getResource(Mockito.anyString())).thenReturn(resource);
        HistoryEntry history = mock(HistoryEntry.class);
        ExecutionResult result = mock(ExecutionResult.class);
        when(result.getState()).thenReturn(ExecutionState.SUCCESS);
        when(result.getOutput()).thenReturn("out");
        when(result.getResult()).thenReturn("result");
        when(result.getTime()).thenReturn("time");

        historyUtil.storeExecutionInHistory(history, result, resolver);

        verify(valueMap, times(1)).put("runState", ExecutionState.SUCCESS.name());
    }

    @Test
    public void finishHistoryEntry() {
        when(resolver.getResource(Mockito.anyString())).thenReturn(resource);
        HistoryEntry history = mock(HistoryEntryImpl.class);
        when(history.getRepositoryPath()).thenReturn("path");
        when(history.getResult()).thenReturn(RESULT.SUCCESS);

        historyUtil.finishHistoryEntry(history, resolver);

        verify(valueMap, times(1)).put("state", STATE.FINISHED.name());
    }

    @Test
    public void getHistory() {
        List<HistoryEntry> entries = historyUtil.getHistory(0, 1, resolver);

        assertEquals(1, entries.size());
    }

    @Test
    public void purgeHistory() throws PersistenceException {
        historyUtil.purgeHistory(resolver, 1);

        verify(resolver, times(6)).delete(Mockito.any());
    }

}
