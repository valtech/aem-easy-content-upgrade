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
package de.valtech.aecu.api.service;

import org.apache.commons.lang3.StringUtils;

/**
 * Result of a script execution.
 *
 * @author Roland Gruber
 */
public class ExecutionResult {

    private ExecutionState state;
    private String output;
    private String time;
    private String result;
    private ExecutionResult fallbackResult;
    private String path;

    /**
     * Constructor
     *
     * @param state          execution state
     * @param time           execution time
     * @param result         result
     * @param output         script output
     * @param fallbackResult fallback script result
     * @param path           script path
     */
    public ExecutionResult(ExecutionState state, String time, String result, String output, ExecutionResult fallbackResult,
            String path) {
        this.state = state;
        this.output = output;
        this.time = time;
        this.result = result;
        this.fallbackResult = fallbackResult;
        this.path = path;
    }

    /**
     * Returns the execution state.
     * 
     * @return state
     */
    public ExecutionState getState() {
        return state;
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

    /**
     * Returns the short script path.
     *
     * @return short path
     */
    public String getShortPath() {
        return path.replace(AecuService.AECU_PREFIX + "/", "");
    }

    @Override
    public String toString() {
        StringBuilder stringVal = new StringBuilder("Successful: " + state.name() + "Path: " + path);
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
