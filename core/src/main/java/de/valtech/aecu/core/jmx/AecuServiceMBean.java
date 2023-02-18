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
package de.valtech.aecu.core.jmx;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

import com.adobe.granite.jmx.annotation.Description;
import com.adobe.granite.jmx.annotation.Name;

import de.valtech.aecu.api.service.AecuException;

/**
 * JMX service interface.
 *
 * @author Roland Gruber
 */
@Description("AEM Easy Content Upgrade")
@ProviderType
public interface AecuServiceMBean {

    /**
     * Returns the AECU version.
     *
     * @return version
     */
    @Description("Version")
    public String getVersion();

    /**
     * Returns a list of files that can be executed in the given path.
     *
     * @param path file or folder
     * @return list of files that are executable
     * @throws AecuException error finding files (e.g. invalid path)
     */
    @Description("Returns a list of files that can be executed in the given path")
    List<String> getFiles(@Name("Path") @Description("File or folder") String path) throws AecuException;

    /**
     * Executes the script(s) at the given position.
     *
     * @param path path of script/folder
     * @return execution result
     * @throws AecuException error during execution
     */
    @Description("Executes a single file or all files of a folder structure")
    String execute(@Name("Path") @Description("Path to file/folder that should be executed") String path) throws AecuException;

    /**
     * Executes the script(s) at the given position and taking install hook history into account.
     *
     * @param path path of script/folder
     * @return execution result
     * @throws AecuException error during execution
     */
    @Description("Executes a single file or all files of a folder structure. Additionally, the install hook history will be checked if scripts need to be run. History will also be updated for executed scripts.")
    String executeWithHistory(@Name("Path") @Description("Path to file/folder that should be executed") String path)
            throws AecuException;

    /**
     * Returns history entries.
     *
     * @param start start index (0 is last run)
     * @param count number of entries to return
     * @return history entries
     * @throws AecuException error reading history
     */
    @Description("Returns the last history entries")
    String getHistory(@Name("Start index") int start, @Name("Count") int count) throws AecuException;

    /**
     * Returns history entries.
     *
     * @param data json data used in the script context
     * @return history entries
     * @throws AecuException error reading history
     */
    @Description("Executes all groovyscripts under /apps. Additionally you can pass data to the script context")
    String executeAllScripts(@Name("data") String data) throws AecuException;

}
