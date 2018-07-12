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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import de.valtech.aecu.service.HistoryEntry;
import de.valtech.aecu.service.HistoryEntry.RESULT;
import de.valtech.aecu.service.HistoryEntry.STATE;

/**
 * Model class for a single history item.
 *
 * @author Roland Gruber
 */
@Model(adaptables = Resource.class)
public class HistoryDataItem {

    private final DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

    @SlingObject
    private Resource resource;

    private HistoryEntry history = null;

    @PostConstruct
    public void setup() {
        history = resource.adaptTo(ValueMap.class).get(HistoryDataSource.ATTR_HISTORY, HistoryEntry.class);
    }

    /**
     * Returns the date of the run.
     *
     * @return date
     */
    public String getDate() {
        return format.format(history.getEnd());
    }

    /**
     * Returns the duration of the run.
     *
     * @return duration
     */
    public String getDuration() {
        if (!STATE.FINISHED.equals(history.getState())) {
            return "";
        }
        Duration duration = Duration.between(history.getStart().toInstant(), history.getEnd().toInstant());
        long seconds = duration.getSeconds();
        if (seconds > 0) {
            return duration.getSeconds() + "s";
        }
        return (duration.getNano() / 1000000) + "ms";
    }

    /**
     * Returns the status icon of the run.
     *
     * @return icon
     */
    public String getStatusIcon() {
        if (RESULT.FAILURE.equals(history.getResult())) {
            return "closeCircle";
        }
        if (RESULT.SUCCESS.equals(history.getResult())) {
            return "checkCircle";
        }
        return "clock";
    }

    /**
     * Returns the status color of the run.
     *
     * @return icon
     */
    public String getStatusColor() {
        if (RESULT.FAILURE.equals(history.getResult())) {
            return "fail";
        }
        if (RESULT.SUCCESS.equals(history.getResult())) {
            return "ok";
        }
        return "inprogress";
    }

    /**
     * Returns the path of the run.
     *
     * @return path
     */
    public String getPath() {
        return history.getRepositoryPath();
    }

}
