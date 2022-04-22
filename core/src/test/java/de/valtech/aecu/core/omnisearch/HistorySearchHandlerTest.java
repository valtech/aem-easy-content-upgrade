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
package de.valtech.aecu.core.omnisearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.query.QueryManager;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;

/**
 * Tests the HistorySearchHandler
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HistorySearchHandlerTest {

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private QueryManager queryManager;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Session session;

    @Mock
    private ValueFactory valueFactory;

    @Mock
    private Workspace workspace;

    @Mock
    private Query query;

    @Mock
    private javax.jcr.query.Query jcrQuery;

    @Mock
    private Resource configResource;

    @InjectMocks
    private HistorySearchHandler handler;

    @BeforeEach
    public void setup() throws RepositoryException {
        when(resolver.getResource(HistorySearchHandler.CONFIG_PATH)).thenReturn(configResource);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(Mockito.any(), Mockito.eq(session))).thenReturn(query);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        when(session.getValueFactory()).thenReturn(valueFactory);
        when(queryManager.createQuery(Mockito.anyString(), Mockito.eq(javax.jcr.query.Query.JCR_SQL2))).thenReturn(jcrQuery);
    }

    @Test
    public void getID() {
        assertEquals(HistorySearchHandler.ID, handler.getID());
    }

    @Test
    public void getModuleConfig() {
        assertEquals(configResource, handler.getModuleConfig(resolver));
    }

    @Test
    public void getPredicateSuggestions() {
        assertTrue(handler.getPredicateSuggestions(resolver, null, null).isEmpty());
    }

    @Test
    public void getResults() {
        Map<String, Object> predicates = new HashMap<>();
        handler.getResults(resolver, predicates, 50, 10);

        verify(queryBuilder, times(1)).createQuery(Mockito.any(), Mockito.eq(session));
        verify(query, times(1)).setHitsPerPage(50L);
        verify(query, times(1)).setStart(10L);
    }

    @Test
    public void getSpellCheckQuery() {
        assertEquals(jcrQuery, handler.getSpellCheckQuery(resolver, ""));
    }

    @Test
    public void getSuggestionQuery() {
        assertEquals(jcrQuery, handler.getSuggestionQuery(resolver, ""));
    }

}
