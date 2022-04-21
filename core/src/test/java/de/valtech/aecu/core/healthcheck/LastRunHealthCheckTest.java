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
package de.valtech.aecu.core.healthcheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.hc.api.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.ExecutionState;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.api.service.HistoryEntry.STATE;
import de.valtech.aecu.core.service.HistoryEntryImpl;

/**
 * Tests LastRunHealthCheck
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LastRunHealthCheckTest {

    @Mock
    private AecuService service;

    @InjectMocks
    private LastRunHealthCheck check = new LastRunHealthCheck();

    private List<HistoryEntry> history = new ArrayList<>();

    @BeforeEach
    public void setup() throws AecuException {
        when(service.getHistory(0, 1)).thenReturn(history);
    }

    @Test
    public void execute_noHistory() {
        Result result = check.execute();

        assertEquals(Result.Status.OK, result.getStatus());
    }

    @Test
    public void execute_unknown() {
        HistoryEntryImpl entry = new HistoryEntryImpl();
        entry.setState(STATE.RUNNING);
        history.add(entry);

        Result result = check.execute();

        assertEquals(Result.Status.WARN, result.getStatus());
    }

    @Test
    public void execute_ok() {
        HistoryEntryImpl entry = new HistoryEntryImpl();
        entry.setState(STATE.FINISHED);
        ExecutionResult singleResult = new ExecutionResult(ExecutionState.SUCCESS, "", "", "", null, "");
        entry.addSingleResult(singleResult);
        history.add(entry);

        Result result = check.execute();

        assertEquals(Result.Status.OK, result.getStatus());
    }

    @Test
    public void execute_fail() {
        HistoryEntryImpl entry = new HistoryEntryImpl();
        entry.setState(STATE.FINISHED);
        ExecutionResult singleResult = new ExecutionResult(ExecutionState.FAILED, "", "", "", null, "");
        entry.addSingleResult(singleResult);
        history.add(entry);

        Result result = check.execute();

        assertEquals(Result.Status.CRITICAL, result.getStatus());
    }

}
