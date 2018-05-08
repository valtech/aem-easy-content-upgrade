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

/**
 * Result of a script execution.
 * 
 * @author Roland Gruber
 */
public class ExecutionResult {

    private boolean success;
    
    private String output;
    
    /**
     * Constructor
     * 
     * @param success execution was successful
     * @param output script output
     */
    public ExecutionResult(boolean success, String output) {
        this.success = success;
        this.output = output;
    }
    
    /**
     * Returns if execution was successful.
     * 
     * @return successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the script output.
     * 
     * @return output
     */
    public String getOutput() {
        return output;
    }
    
    @Override
    public String toString() {
        return "Successful: " + Boolean.toString(success) + "\n"
            + "Output: " + output;
    }

}
