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
package de.valtech.aecu.core.maintenance;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobExecutionContext;
import org.apache.sling.event.jobs.consumer.JobExecutionResult;
import org.apache.sling.event.jobs.consumer.JobExecutor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import com.adobe.granite.maintenance.MaintenanceConstants;

/**
 * Purges old entries from the history.
 * 
 * @author Roland Gruber
 */
@Component(
    property = {
        MaintenanceConstants.PROPERTY_TASK_NAME + "=AECUPurgeHistory",
        MaintenanceConstants.PROPERTY_TASK_TITLE + "=AECU Purge History",
        JobExecutor.PROPERTY_TOPICS + "=" + MaintenanceConstants.TASK_TOPIC_PREFIX + "AECUPurgeHistory",
    }
)
@Designate(ocd = PurgeHistoryConfiguration.class)
public class PurgeHistoryTask implements JobExecutor {
    
    private PurgeHistoryConfiguration config;

    /**
     * Activates the service.
     * 
     * @param config configuration
     */
    public void activate(PurgeHistoryConfiguration config) {
        this.config = config;
    }

    @Override
    public JobExecutionResult process(Job job, JobExecutionContext context) {
        // TODO Auto-generated method stub
        return context.result().message("Done").succeeded();
    }

}
