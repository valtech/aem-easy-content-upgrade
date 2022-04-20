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
package de.valtech.aecu.core.model.history;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.ExecutionState;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.core.history.HistoryUtil;
import de.valtech.aecu.core.security.AccessValidationService;

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

    @OSGiService
    private HistoryUtil historyUtil;

    @OSGiService
    private AccessValidationService accessValidationService;

    private HistoryEntry historyEntry;

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Reads the history entry from CRX.
     */
    @PostConstruct
    public void init() {
        if (!accessValidationService.canReadHistory(request)) {
            return;
        }
        RequestParameter entryParam = request.getRequestParameter("entry");
        if (entryParam == null) {
            return;
        }
        String path = entryParam.getString();
        Resource historyResource = resolver.getResource(path);
        if (historyResource == null) {
            return;
        }
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
        if ((historyEntry.getStart() == null) || (historyEntry.getEnd() == null)) {
            return "unknown";
        }
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
     * @return data for donut diagram
     */
    public DonutData getDonutData() {
        int countAll = historyEntry.getSingleResults().size();
        if (countAll == 0) {
            return new DonutData(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        double countOk = 0;
        double countFailed = 0;
        for (ExecutionResult result : historyEntry.getSingleResults()) {
            if (ExecutionState.SUCCESS.equals(result.getState())) {
                countOk++;
            } else if (ExecutionState.FAILED.equals(result.getState())) {
                countFailed++;
            }
        }
        BigDecimal percentageOk = BigDecimal.valueOf((countOk / countAll) * 100);
        BigDecimal percentageFailed = BigDecimal.valueOf((countFailed / countAll) * 100);
        return new DonutData(percentageOk, percentageFailed);
    }

    /**
     * Data for the donut chart.
     */
    public static class DonutData {

        private BigDecimal percentageOk;
        private BigDecimal percentageFail;

        /**
         * Constructor
         * 
         * @param percentageOk   percentage of successful scripts
         * @param percentageFail percentage of failed scripts
         */
        public DonutData(BigDecimal percentageOk, BigDecimal percentageFail) {
            this.percentageOk = percentageOk.round(new MathContext(2));
            this.percentageFail = percentageFail.round(new MathContext(2));
        }

        /**
         * Length (0..100) of filled ok cicle part.
         * 
         * @return length
         */
        public String getOkLength() {
            return percentageOk.toPlainString();
        }

        /**
         * Length (0..100) of non-filled ok cicle part.
         * 
         * @return length
         */
        public String getOkRemainder() {
            return new BigDecimal(100).subtract(percentageOk).toPlainString();
        }

        /**
         * Length (0..100) of filled failed cicle part.
         * 
         * @return length
         */
        public String getFailedLength() {
            // Failed circle is below ok circle. Therefore, the length is the sum of ok and failed.
            return percentageFail.add(percentageOk).toPlainString();
        }

        /**
         * Length (0..100) of non-filled failed cicle part.
         * 
         * @return length
         */
        public String getFailedRemainder() {
            // Failed circle is below ok circle. Therefore, the length is 100 minus the sum of ok
            // and failed.
            return new BigDecimal(100).subtract(percentageFail).subtract(percentageOk).toPlainString();
        }

        /**
         * Returns the percentage text for the circle center.
         * 
         * @return percentage
         */
        public String getPercentageOk() {
            return percentageOk.toPlainString();
        }

    }

}
