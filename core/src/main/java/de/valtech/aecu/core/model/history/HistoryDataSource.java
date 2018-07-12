/*
 *  Copyright 2018 Valtech GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package de.valtech.aecu.core.model.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.AbstractDataSource;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

import de.valtech.aecu.service.AecuException;
import de.valtech.aecu.service.AecuService;
import de.valtech.aecu.service.HistoryEntry;

/**
 * Datasource model for history overview page.
 *
 * @author Roland Gruber
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class HistoryDataSource {

    private static final String ITEM_TYPE = "valtech/aecu/tools/history/dataitem";
    public static final String ATTR_HISTORY = "history";

    private Logger LOG = LoggerFactory.getLogger(DataSource.class);

    @SlingObject
    SlingHttpServletRequest request;

    @OSGiService
    AecuService aecuService;

    @PostConstruct
    public void setup() {
        String[] selectors = request.getRequestPathInfo().getSelectors();
        int offset = 0;
        int limit = 50;
        if (selectors.length > 1) {
            offset = Integer.parseInt(selectors[0]);
            limit = Integer.parseInt(selectors[1]);
        }
        request.setAttribute(DataSource.class.getName(), getResourceIterator(offset, limit));
    }

    /**
     * Returns the history entries.
     *
     * @param offset offset where to start reading
     * @param limit  maximum number of entries to return
     * @return entries
     */
    private DataSource getResourceIterator(int offset, int limit) {
        return new AbstractDataSource() {

            @Override
            public Iterator<Resource> iterator() {
                List<Resource> entries = new ArrayList<>();
                try {
                    List<HistoryEntry> historyEntries = aecuService.getHistory(offset, limit + 1);
                    for (HistoryEntry historyEntry : historyEntries) {
                        ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
                        vm.put(ATTR_HISTORY, historyEntry);
                        entries.add(new ValueMapResource(request.getResourceResolver(), historyEntry.getRepositoryPath(), ITEM_TYPE, vm));
                    }
                } catch (AecuException e) {
                    LOG.error("Unable to read history entries", e);
                }
                return entries.iterator();
            }

        };
    }

}
