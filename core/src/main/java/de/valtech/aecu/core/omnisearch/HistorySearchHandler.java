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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.omnisearch.api.suggestion.PredicateSuggestion;
import com.adobe.granite.omnisearch.spi.core.OmniSearchHandler;
import com.day.cq.i18n.I18n;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

import de.valtech.aecu.core.history.HistoryUtil;

/**
 * Omnisearch handler for AECU history.
 * 
 * @author Roland Gruber
 */
@Component
public class HistorySearchHandler implements OmniSearchHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HistorySearchHandler.class);

    @Reference
    private QueryBuilder queryBuilder;

    @Override
    public String getID() {
        return "AECU History";
    }

    @Override
    public Resource getModuleConfig(ResourceResolver resourceResolver) {
        return resourceResolver.getResource("/apps/valtech/aecu/omnisearch/content/metadata/aecuHistory");
    }

    @Override
    public List<PredicateSuggestion> getPredicateSuggestions(ResourceResolver arg0, I18n arg1, String arg2) {
        return new ArrayList<>();
    }

    @Override
    public SearchResult getResults(ResourceResolver resolver, Map<String, Object> predicateParameters, long limit, long offset) {
        Map<String, Object> predicates = new HashMap<>(predicateParameters);
        predicates.put("path", HistoryUtil.HISTORY_BASE);
        PredicateGroup predicateGroup = PredicateGroup.create(predicates);
        com.day.cq.search.Query query = queryBuilder.createQuery(predicateGroup, resolver.adaptTo(Session.class));
        if (limit != 0) {
            query.setHitsPerPage(limit);
        }
        if (offset != 0) {
            query.setStart(offset);
        }
        return query.getResult();
    }

    @Override
    public Query getSpellCheckQuery(ResourceResolver resourceResolver, String searchTerm) {
        try {
            Session session = resourceResolver.adaptTo(Session.class);
            if (session == null) {
                return null;
            }
            final String queryStr = "SELECT [rep:spellcheck()] FROM [nt:base] as s WHERE [jcr:path] = '"
                    + HistoryUtil.HISTORY_BASE + "' AND SPELLCHECK($searchTerm)";
            final Query query = setupQuery(session, queryStr);
            ValueFactory valueFactory = session.getValueFactory();
            query.bindValue("searchTerm", valueFactory.createValue(searchTerm));
            return query;
        } catch (RepositoryException e) {
            LOG.error("Unable to create spell check query", e);
        }
        return null;
    }

    @Override
    public Query getSuggestionQuery(ResourceResolver resourceResolver, String searchTerm) {
        try {
            Session session = resourceResolver.adaptTo(Session.class);
            if (session == null) {
                return null;
            }
            final String queryStr =
                    "SELECT [rep:suggest()] FROM [dam:Asset] as s WHERE SUGGEST($searchTerm) AND ISDESCENDANTNODE(["
                            + HistoryUtil.HISTORY_BASE + "])";
            final Query query = setupQuery(session, queryStr);
            ValueFactory valueFactory = session.getValueFactory();
            query.bindValue("searchTerm", valueFactory.createValue(searchTerm));
            return query;
        } catch (RepositoryException e) {
            LOG.error("Unable to create spell check query", e);
        }
        return null;
    }

    /**
     * Create query.
     * 
     * @param session     session
     * @param queryString query string
     * @return query
     * @throws RepositoryException unable to create query
     */
    private Query setupQuery(Session session, String queryString) throws RepositoryException {
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        return queryManager.createQuery(queryString, Query.JCR_SQL2);
    }

}
