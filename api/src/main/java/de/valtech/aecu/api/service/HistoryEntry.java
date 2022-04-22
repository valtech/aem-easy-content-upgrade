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

import java.util.Date;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * History entry for an execution run.
 *
 * @author Roland Gruber
 */
@ProviderType
public interface HistoryEntry {

    /**
     * Execution state (e.g. running)
     */
    public enum STATE {
        /** Execution ongoing */
        RUNNING("Running"),
        /** Execution finished */
        FINISHED("Finished");

        private String label;

        /**
         * Constructor
         * 
         * @param label label
         */
        private STATE(String label) {
            this.label = label;
        }

        /**
         * Returns the human-readable label for this result.
         * 
         * @return label
         */
        public String getLabel() {
            return label;
        }
    };

    /**
     * Execution result (e.g. successful)
     */
    public enum RESULT {
        /** All scripts executed successfully */
        SUCCESS("Success"),
        /** Execution of one or more scripts failed */
        FAILURE("Failed"),
        /** Execution not yet finished */
        UNKNOWN("Unknown");

        private String label;

        /**
         * Constructor
         * 
         * @param label label
         */
        private RESULT(String label) {
            this.label = label;
        }

        /**
         * Returns the human-readable label for this result.
         * 
         * @return label
         */
        public String getLabel() {
            return label;
        }
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

    /**
     * Returns the path in repository where the history is stored.
     *
     * @return path
     */
    String getRepositoryPath();

}
