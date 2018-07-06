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
package de.valtech.aecu.service;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Service interface for AECU.
 * 
 * @author Roland Gruber
 */
@ProviderType
public interface AecuService {
    
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
     * @param result script execution result
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
     * @param count number of entries to read
     * @return history entries (newest first)
     * @throws AecuException error reading history
     */
    List<HistoryEntry> getHistory(int startIndex, int count) throws AecuException;

}
