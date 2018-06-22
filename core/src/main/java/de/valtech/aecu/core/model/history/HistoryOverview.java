/*
 *  Copyright 2018 Valtech GmbH
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package de.valtech.aecu.core.model.history;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import de.valtech.aecu.core.history.HistoryUtil;
import de.valtech.aecu.service.ExecutionResult;
import de.valtech.aecu.service.HistoryEntry;

/**
 * Sling model for history overview area.
 * 
 * @author Roland Gruber
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class HistoryOverview {
    
    @SlingObject
    private SlingHttpServletRequest request;
    
    @SlingObject
    private ResourceResolver resolver;
    
    private HistoryEntry historyEntry;
    
    private final DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    
    /**
     * Reads the history entry from CRX.
     */
    @PostConstruct
    public void init() {
        RequestParameter entryParam = request.getRequestParameter("entry");
        if (entryParam == null) {
            return;
        }
        String path = entryParam.getString();
        Resource historyResource = resolver.getResource(path);
        if (historyResource == null) {
            return;
        }
        HistoryUtil historyUtil = new HistoryUtil();
        historyEntry = historyUtil.readHistoryEntry(historyResource);
    }
    
    /**
     * Returns the history entry.
     * 
     * @return history
     */
    public HistoryEntry getHistory() {
        return historyEntry;
    }

    /**
     * Returns the start as formatted string.
     * 
     * @return start date
     */
    public String getStart() {
        if ((historyEntry == null) || (historyEntry.getStart() == null)) {
            return StringUtils.EMPTY;
        }
        return format.format(historyEntry.getStart());
    }
    
    /**
     * Returns the end as formatted string.
     * 
     * @return end date
     */
    public String getEnd() {
        if ((historyEntry == null) || (historyEntry.getEnd() == null)) {
            return StringUtils.EMPTY;
        }
        return format.format(historyEntry.getEnd());
    }
    
    /**
     * Returns the percentages of successful and failed scripts. 
     * 
     * @return percentages (successful, failed)
     */
    public Pair<String, String> getPercentages() {
        int countAll = historyEntry.getSingleResults().size();
        if (countAll == 0) {
            return Pair.of("0", "0");
        }
        double countOk = 0;
        double countFailed = 0;
        for (ExecutionResult result : historyEntry.getSingleResults()) {
            if (result.isSuccess()) {
                countOk++;
            }
            else {
                countFailed++;
            }
        }
        BigDecimal percentageOk = new BigDecimal((countOk / countAll) * 100);
        BigDecimal percentageFailed = new BigDecimal((countFailed / countAll) * 100);
        String valueOk = percentageOk.round(new MathContext(2)).toPlainString();
        String valueFailed = percentageFailed.round(new MathContext(2)).toPlainString();
        return Pair.of(valueOk, valueFailed);
    }
    
}
