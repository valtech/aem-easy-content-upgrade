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
package de.valtech.aecu.api.service;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Service interface for AECU. Use this to execute scripts or query the history. <br>
 * <br>
 * How to perform an execution:
 * <ol>
 * <li>Get a list of files to execute using {@link #getFiles(String) getFiles}. This will filter all
 * files that do not match the run mode and any fallback scripts.</li>
 * <li>Start a new history entry to store your results using {@link #createHistoryEntry()
 * createHistoryEntry}. This store a new run with in-progress state.</li>
 * <li>Execute your files one by one with {@link #execute(String) execute}</li>
 * <li>Store each script run in history using
 * {@link #storeExecutionInHistory(HistoryEntry, ExecutionResult) storeExecutionInHistory}</li>
 * <li>Mark the run as done by closing the history with {@link #finishHistoryEntry(HistoryEntry)
 * finishHistoryEntry}</li>
 * </ol>
 *
 * @author Roland Gruber
 */
@ProviderType
public interface AecuService {

    /**
     * Prefix in repository where ad-hoc AECU scripts are located.
     */
    @Deprecated
    public static final String AECU_VAR_PATH_PREFIX = "/var/groovyconsole/scripts/aecu";

    /**
     * Prefix in repository where install hook AECU scripts are located.
     */
    public static final String AECU_CONF_PATH_PREFIX = "/conf/groovyconsole/scripts/aecu";

    /**
     * Prefix in repository where install hook AECU scripts are located.
     */
    public static final String AECU_APPS_PATH_PREFIX = "/apps/aecu-scripts";

    /**
     * Returns the AECU version.
     *
     * @return version
     */
    String getVersion();

    /**
     * Returns a list of files that can be executed in the given path.
     *
     * @param path file or folder
     * @return list of files that are executable
     * @throws AecuException error finding files (e.g. invalid path)
     */
    List<String> getFiles(String path) throws AecuException;

    /**
     * Checks if the folder matches the system's run modes if specified in folder name.
     *
     * @param name resource name
     * @return matches run modes
     */
    boolean matchesRunmodes(String name);

    /**
     * Checks if the name is a valid script.
     *
     * @param name file name
     * @return is valid
     */
    boolean isValidScriptName(String name);

    /**
     * Executes the script at the given position.
     *
     * @param path path of script
     * @return execution result
     * @throws AecuException error during execution
     */
    ExecutionResult execute(String path) throws AecuException;

    /**
     * Starts a new history entry.
     *
     * @return history entry
     * @throws AecuException error setting up entry
     */
    HistoryEntry createHistoryEntry() throws AecuException;

    /**
     * Stores an execution run in existing history.
     *
     * @param history history entry
     * @param result  script execution result
     * @return updated history
     * @throws AecuException error inserting history entry
     */
    HistoryEntry storeExecutionInHistory(HistoryEntry history, ExecutionResult result) throws AecuException;

    /**
     * Finishes the history entry.
     *
     * @param history open history entry
     * @return history entry
     * @throws AecuException error saving state
     */
    HistoryEntry finishHistoryEntry(HistoryEntry history) throws AecuException;

    /**
     * Returns the last history entries. The search starts at the newest entry.
     *
     * @param startIndex start reading at this index (first is 0)
     * @param count      number of entries to read
     * @return history entries (newest first)
     * @throws AecuException error reading history
     */
    List<HistoryEntry> getHistory(int startIndex, int count) throws AecuException;

    /**
     * Executes the script(s) at the given position and taking install hook history into account.
     *
     * @param path path of script/folder
     * @return execution result
     * @throws AecuException error during execution
     */
    HistoryEntry executeWithInstallHookHistory(String path) throws AecuException;

}
