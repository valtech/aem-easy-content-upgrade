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
package de.valtech.aecu.core.model.history;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import de.valtech.aecu.service.HistoryEntry;

/**
 * Model class for a single history item.
 * 
 * @author Roland Gruber
 */
@Model(adaptables = Resource.class)
public class HistoryDataItem {
    
    private final DateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    
    @SlingObject
    private Resource resource;
    
    private HistoryEntry history = null;
    
    @PostConstruct
    public void setup() {
        history = resource.adaptTo(ValueMap.class).get(HistoryDataSource.ATTR_HISTORY, HistoryEntry.class);
    }
    
    /**
     * Returns the date of the run.
     * 
     * @return date
     */
    public String getDate() {
        return format.format(history.getEnd());
    }
    
    /**
     * Returns the result of the run.
     * 
     * @return result
     */
    public String getStatus() {
        return history.getResult().name();
    }

    /**
     * Returns the path of the run.
     * 
     * @return path
     */
    public String getPath() {
        return history.getRepositoryPath();
    }

}
