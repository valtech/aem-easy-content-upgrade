/*
 * Copyright 2018 - 2019 Valtech GmbH
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

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.xss.XSSAPI;

import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.core.history.HistoryUtil;
import de.valtech.aecu.core.model.history.HistoryDataItem;

/**
 * Model class for a single history item in omni search.
 *
 * @author Roland Gruber
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class HistorySearchItem extends HistoryDataItem {

    private static final String POST_FULLTEXT = "fulltext";
    private static final String DOTS = "...";
    private static final int RELATED_LENGTH = 20;
    private static final String HIGHLIGHT_START = "<span class=\"aecu-highlight\">";
    private static final String HIGHLIGHT_END = "</span>";

    @SlingObject
    private Resource resource;

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private XSSAPI xssApi;

    protected String searchTerm;
    private Resource entryResource;

    @Override
    @PostConstruct
    public void setup() {
        history = readHistory();
        searchTerm = request.getParameter(POST_FULLTEXT);
    }

    protected HistoryEntry readHistory() {
        HistoryUtil util = new HistoryUtil();
        entryResource = util.getHistoryEntryResource(resource);
        return util.readHistoryEntry(entryResource);
    }

    /**
     * Reads the single execution result.
     * 
     * @return result
     */
    protected ExecutionResult readSingleResult() {
        HistoryUtil util = new HistoryUtil();
        return util.readHistorySingleResult(resource);
    }

    /**
     * Returns a text fragment that matches the search term.
     * 
     * @return fragment
     */
    public String getFragment() {
        if (StringUtils.isBlank(searchTerm)) {
            return StringUtils.EMPTY;
        }
        ExecutionResult result = readSingleResult();
        String snippet = extractFromResult(result);
        if (StringUtils.isNotBlank(snippet)) {
            return snippet;
        }
        return StringUtils.EMPTY;
    }

    private String extractFromResult(ExecutionResult result) {
        if (StringUtils.containsIgnoreCase(result.getPath(), searchTerm)) {
            return createSnippet(result.getPath());
        }
        if (StringUtils.containsIgnoreCase(result.getOutput(), searchTerm)) {
            return createSnippet(result.getOutput());
        }
        if (StringUtils.containsIgnoreCase(result.getResult(), searchTerm)) {
            return createSnippet(result.getResult());
        }
        if (result.getFallbackResult() != null) {
            return extractFromResult(result.getFallbackResult());
        }
        return StringUtils.EMPTY;
    }

    /**
     * Creates a snippet that contains the search term.
     * 
     * @param text text to shorten
     * @return snippet
     */
    private String createSnippet(String text) {
        int position = StringUtils.indexOfIgnoreCase(text, searchTerm);
        if (position == -1) {
            return StringUtils.EMPTY;
        }
        int start = 0;
        int end = text.length();
        if (position > 2 + RELATED_LENGTH) {
            start = position - RELATED_LENGTH;
        }
        if ((position + searchTerm.length() + RELATED_LENGTH) < text.length()) {
            end = position + searchTerm.length() + RELATED_LENGTH;
        }
        String prefix = (start > 0) ? DOTS : StringUtils.EMPTY;
        String postfix = (end < text.length() - 3) ? DOTS : StringUtils.EMPTY;
        String snippet = prefix + text.substring(start, end) + postfix;
        return highlight(snippet);
    }

    /**
     * Adds extra HTML to highlight the search term.
     * 
     * @param snippet text snippet
     * @return formatted snippet
     */
    private String highlight(String snippet) {
        String escapedSnippet = xssApi.encodeForHTML(snippet);
        Pattern pattern = Pattern.compile("(" + Pattern.quote(searchTerm) + ")", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(escapedSnippet).replaceAll(HIGHLIGHT_START + "$1" + HIGHLIGHT_END);
    }

    /**
     * Returns the selected script in a run.
     * 
     * @return script path
     */
    public String getSelectedScript() {
        Resource scriptResource = resource;
        if (HistoryUtil.NODE_FALLBACK.equals(resource.getName())) {
            scriptResource = scriptResource.getParent();
        }
        return scriptResource.getValueMap().get(HistoryUtil.ATTR_PATH, String.class);
    }

    /**
     * Returns the base path of the run.
     * 
     * @return run path
     */
    public String getRunPath() {
        return entryResource.getPath();
    }

}
