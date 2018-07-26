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

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.core.history.HistoryUtil;

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
     * Returns the duration.
     *
     * @return duration
     */
    public String getDuration() {
        Duration duration = Duration.between(historyEntry.getStart().toInstant(), historyEntry.getEnd().toInstant());
        long seconds = duration.getSeconds();
        if (seconds > 0) {
            return duration.getSeconds() + "s";
        }
        return (duration.getNano() / 1000000) + "ms";
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
            } else {
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
