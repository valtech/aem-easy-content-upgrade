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
package de.valtech.aecu.service;

import java.util.Date;
import java.util.List;

/**
 * History entry for an execution run.
 * 
 * @author Roland Gruber
 *
 */
public interface HistoryEntry {
    
    /**
     * Execution state (e.g. running)
     */
    public enum STATE {
        STARTED,
        RUNNING,
        FINISHED
    };
    
    /**
     * Execution result (e.g. successful)
     */
    public enum RESULT {
        SUCCESS,
        FAILURE,
        UNKNOWN
    };
    
    /**
     * Returns the start time of the execution.
     * 
     * @return start
     */
    Date getStart();
    
    /**
     * Returns the end time of the execution.
     * 
     * @return end
     */
    Date getEnd();
    
    /**
     * Returns the single script runs.
     * 
     * @return single results
     */
    List<ExecutionResult> getSingleResults();
    
    /**
     * Returns the current state of the run.
     * 
     * @return state
     */
    STATE getState();
    
    /**
     * Returns the global result of the run.
     * 
     * @return result
     */
    RESULT getResult();
    
}
