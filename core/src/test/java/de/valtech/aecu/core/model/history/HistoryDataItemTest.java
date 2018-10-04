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
package de.valtech.aecu.core.model.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.api.service.HistoryEntry.RESULT;
import de.valtech.aecu.api.service.HistoryEntry.STATE;

/**
 * Tests HistoryDataItem
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class HistoryDataItemTest {

    private static final String PATH = "path";

    @Mock
    private Resource resource;

    @Mock
    private ValueMap valuemap;

    @Mock
    private HistoryEntry history;

    @InjectMocks
    private HistoryDataItem item;

    @Before
    public void setup() {
        when(resource.getValueMap()).thenReturn(valuemap);
        when(valuemap.get(HistoryDataSource.ATTR_HISTORY, HistoryEntry.class)).thenReturn(history);
        when(history.getRepositoryPath()).thenReturn(PATH);
        item.setup();
    }

    @Test
    public void getDate_start() {
        when(history.getStart()).thenReturn(new Date());

        String date = item.getDate();

        assertTrue(StringUtils.isNotBlank(date));
    }

    @Test
    public void getDate_end() {
        when(history.getEnd()).thenReturn(new Date());

        String date = item.getDate();

        assertTrue(StringUtils.isNotBlank(date));
    }

    @Test
    public void getDuration_inprogress() {
        when(history.getState()).thenReturn(STATE.RUNNING);

        String duration = item.getDuration();

        assertTrue(StringUtils.isBlank(duration));
    }

    @Test
    public void getDuration_finished() {
        when(history.getState()).thenReturn(STATE.FINISHED);
        when(history.getStart()).thenReturn(new Date());
        when(history.getEnd()).thenReturn(new Date());

        String duration = item.getDuration();

        assertTrue(StringUtils.isNotBlank(duration));
    }

    @Test
    public void getStatusIcon_success() {
        when(history.getResult()).thenReturn(RESULT.SUCCESS);

        String icon = item.getStatusIcon();

        assertTrue(StringUtils.isNotBlank(icon));
    }

    @Test
    public void getStatusIcon_fail() {
        when(history.getResult()).thenReturn(RESULT.FAILURE);

        String icon = item.getStatusIcon();

        assertTrue(StringUtils.isNotBlank(icon));
    }

    @Test
    public void getStatusIcon_unknown() {
        when(history.getResult()).thenReturn(RESULT.UNKNOWN);

        String icon = item.getStatusIcon();

        assertTrue(StringUtils.isNotBlank(icon));
    }

    @Test
    public void getStatusColor_success() {
        when(history.getResult()).thenReturn(RESULT.SUCCESS);

        String icon = item.getStatusColor();

        assertTrue(StringUtils.isNotBlank(icon));
    }

    @Test
    public void getStatusColor_fail() {
        when(history.getResult()).thenReturn(RESULT.FAILURE);

        String icon = item.getStatusColor();

        assertTrue(StringUtils.isNotBlank(icon));
    }

    @Test
    public void getStatusColor_unknown() {
        when(history.getResult()).thenReturn(RESULT.UNKNOWN);

        String icon = item.getStatusColor();

        assertTrue(StringUtils.isNotBlank(icon));
    }

    @Test
    public void getPath() {
        String path = item.getPath();

        assertEquals(PATH, path);
    }

    @Test
    public void getScriptCount_null() {
        int count = item.getScriptCount();

        assertEquals(0, count);
    }

    @Test
    public void getScriptCount_nonNull() {
        ExecutionResult result = mock(ExecutionResult.class);
        List<ExecutionResult> results = Arrays.asList(result);
        when(history.getSingleResults()).thenReturn(results);

        int count = item.getScriptCount();

        assertEquals(1, count);
    }

}
