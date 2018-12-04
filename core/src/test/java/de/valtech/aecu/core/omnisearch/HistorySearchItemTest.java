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
package de.valtech.aecu.core.omnisearch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.ExecutionState;
import de.valtech.aecu.core.service.HistoryEntryImpl;

/**
 * Tests HistorySearchItem
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class HistorySearchItemTest {

    @Mock
    private SlingHttpServletRequest request;

    @Spy
    @InjectMocks
    private HistorySearchItem item;

    private static final String RESULT_TEXT = "result_text_sometextsometextsometextsometextsometext_endofresult";
    private static final String OUTPUT_TEXT = "output_text_sometextsometextsometextsometextsometext_endoftext";
    private static final String PATH_TEXT = "path_text_sometextsometext111sometextsometextsometext_endofpath";
    private static final String FALLBACK_TEXT = "fallback_text_sometextsometext111sometextsometextsometext_endofpath";

    @Before
    public void setup() {
        HistoryEntryImpl history = new HistoryEntryImpl();
        ExecutionResult fallback = new ExecutionResult(ExecutionState.SUCCESS, "2018", FALLBACK_TEXT, "", null, "");
        history.getSingleResults()
                .add(new ExecutionResult(ExecutionState.SUCCESS, "2018", RESULT_TEXT, OUTPUT_TEXT, fallback, PATH_TEXT));
        doReturn(history).when(item).readHistory();
        item.setup();
    }

    @Test
    public void getFragment_path_start() {
        item.searchTerm = "path_";

        String snippet = item.getFragment();
        assertEquals("path_text_sometextsometex...", snippet);
    }

    @Test
    public void getFragment_path_end() {
        item.searchTerm = "_endofpath";

        String snippet = item.getFragment();
        assertEquals("...textsometextsometext_endofpath", snippet);
    }

    @Test
    public void getFragment_path_middle() {
        item.searchTerm = "111";

        String snippet = item.getFragment();
        assertEquals("...ext_sometextsometext111sometextsometextsome...", snippet);
    }

    @Test
    public void getFragment_result() {
        item.searchTerm = "result_";

        String snippet = item.getFragment();
        assertEquals("result_text_sometextsometex...", snippet);
    }

    @Test
    public void getFragment_output() {
        item.searchTerm = "output_";

        String snippet = item.getFragment();
        assertEquals("output_text_sometextsometex...", snippet);
    }

    @Test
    public void getFragment_fallback() {
        item.searchTerm = "fallback_";

        String snippet = item.getFragment();
        assertEquals("fallback_text_sometextsometex...", snippet);
    }

    @Test
    public void getFragment_fallbackUpper() {
        item.searchTerm = "FALLBACK_";

        String snippet = item.getFragment();
        assertEquals("fallback_text_sometextsometex...", snippet);
    }

}
