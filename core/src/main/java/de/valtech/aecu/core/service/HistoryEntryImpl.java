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
package de.valtech.aecu.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.HistoryEntry;

/**
 * Implementation of history entry.
 *
 * @author Roland Gruber
 */
public class HistoryEntryImpl implements HistoryEntry {

    private STATE state;
    private String path;
    private Date start;
    private Date end;
    private List<ExecutionResult> singleResults = new ArrayList<>();

    @Override
    public Date getStart() {
        return start;
    }

    @Override
    public Date getEnd() {
        return end;
    }

    @Override
    public List<ExecutionResult> getSingleResults() {
        return singleResults;
    }

    @Override
    public STATE getState() {
        return state;
    }

    @Override
    public RESULT getResult() {
        if (singleResults.isEmpty()) {
            return RESULT.UNKNOWN;
        }
        RESULT result = RESULT.SUCCESS;
        for (ExecutionResult singleResult : singleResults) {
            if (!singleResult.isSuccess()) {
                result = RESULT.FAILURE;
                break;
            }
        }
        return result;
    }

    @Override
    public String getRepositoryPath() {
        return path;
    }

    /**
     * Sets the start date.
     *
     * @param start start date
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * Sets the end date.
     *
     * @param end end date
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * Sets the node path.
     *
     * @param path node path
     */
    public void setRepositoryPath(String path) {
        this.path = path;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Path: " + getRepositoryPath() + "\n");
        output.append("Start: " + getStart() + "\n");
        output.append("End: " + getEnd() + "\n");
        output.append("State: " + getState() + "\n");
        output.append("Result: " + getResult() + "\n\n");
        for (ExecutionResult singleResult : singleResults) {
            output.append(singleResult.toString() + "\n");
        }
        return output.toString();
    }

}
