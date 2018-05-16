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
package de.valtech.aecu.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.valtech.aecu.service.ExecutionResult;
import de.valtech.aecu.service.HistoryEntry;

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
