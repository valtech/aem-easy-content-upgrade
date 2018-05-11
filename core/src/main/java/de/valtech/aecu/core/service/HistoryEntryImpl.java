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

    private String path;
    private Date start;
    private Date end;
    private List<ExecutionResult> singleResults;

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
        // TODO Auto-generated method stub
        return STATE.RUNNING;
    }
    @Override
    public RESULT getResult() {
        // TODO Auto-generated method stub
        return RESULT.UNKNOWN;
    }
    @Override
    public String getRepositoryPath() {
        // TODO Auto-generated method stub
        return path;
    }

}
