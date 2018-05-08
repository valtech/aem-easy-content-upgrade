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
package de.valtech.aecu.core.jmx;

import java.util.List;

import com.adobe.granite.jmx.annotation.Description;
import com.adobe.granite.jmx.annotation.Name;

import de.valtech.aecu.service.AecuException;

/**
 * JMX service interface.
 * 
 * @author Roland Gruber
 */
@Description("AEM Easy Content Upgrade")
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
     * Executes the script at the given position.
     * 
     * @param path path of script
     * @return execution result
     * @throws AecuException error during execution
     */
    String execute(@Name("Path") @Description("Path to file that should be executed") String path) throws AecuException;

}
