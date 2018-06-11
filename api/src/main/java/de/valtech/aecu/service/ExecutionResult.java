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

import org.apache.commons.lang3.StringUtils;

/**
 * Result of a script execution.
 * 
 * @author Roland Gruber
 */
public class ExecutionResult {

    private boolean success;
    private String output;
    private String time;
    private String result;
    private ExecutionResult fallbackResult;
    private String path;
    
    /**
     * Constructor
     * 
     * @param success execution was successful
     * @param time execution time
     * @param result result 
     * @param output script output
     * @param fallbackResult fallback script result
     * @param path script path
     */
    public ExecutionResult(boolean success, String time, String result, String output, ExecutionResult fallbackResult, String path) {
        this.success = success;
        this.output = output;
        this.time = time;
        this.result = result;
        this.fallbackResult = fallbackResult;
        this.path = path;
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
     * Returns the script result.
     * 
     * @return output
     */
    public String getResult() {
        return result;
    }
    
    /**
     * Returns the script output.
     * 
     * @return output
     */
    public String getOutput() {
        return output;
    }
    
    /**
     * Returns the execution time.
     * 
     * @return time
     */
    public String getTime() {
        return time;
    }

    /**
     * Returns the fallback script result if any.
     * 
     * @return result
     */
    public ExecutionResult getFallbackResult() {
        return fallbackResult;
    }
    
    /**
     * Returns the script path.
     * 
     * @return path
     */
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        StringBuilder stringVal = new StringBuilder(
            "Successful: " + Boolean.toString(success) +
            "Path: " + path
        );
        if (StringUtils.isNotBlank(time)) {
            stringVal.append("\n" + "Execution time: " + time);
        }
        if (StringUtils.isNotBlank(result)) {
            stringVal.append("\n" + "Result: " + result);
        }
        stringVal.append("\n" + "Output: " + output);
        if (fallbackResult != null) {
            stringVal.append("Fallback script executed:\n" + fallbackResult.toString());
        }
        return stringVal.toString();
    }

}
