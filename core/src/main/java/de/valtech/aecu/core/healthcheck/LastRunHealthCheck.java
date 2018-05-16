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
package de.valtech.aecu.core.healthcheck;

import java.util.List;

import org.apache.sling.hc.api.HealthCheck;
import org.apache.sling.hc.api.Result;
import org.apache.sling.hc.util.FormattingResultLog;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.valtech.aecu.service.AecuException;
import de.valtech.aecu.service.AecuService;
import de.valtech.aecu.service.HistoryEntry;

/**
 * Checks if the last script run was ok.
 * 
 * @author Roland Gruber
 */
@Component(
    immediate = true,
    service = HealthCheck.class,
    property = {
        HealthCheck.TAGS + "=aecu",
        HealthCheck.NAME + "=AECU Last Run",
        HealthCheck.MBEAN_NAME + "=aecuLastRunHCmBean"
    }
)
public class LastRunHealthCheck implements HealthCheck {
    
    @Reference
    private AecuService aecuService;

    @Override
    public Result execute() {
        final FormattingResultLog resultLog = new FormattingResultLog();
        try {
            List<HistoryEntry> history = aecuService.getHistory(0, 1);
            if (history.isEmpty()) {
                resultLog.info("No runs found");
            }
            else {
                HistoryEntry entry = history.get(0);
                switch (entry.getResult()) {
                    case FAILURE:
                        resultLog.critical("Last execution failed");
                        break;
                    case SUCCESS:
                        resultLog.info("Last run was successful");
                        break;
                    case UNKNOWN:
                        resultLog.warn("Last execution is still running");
                        break;
                }
            }
        } catch (AecuException e) {
            resultLog.critical(e.getMessage());
        }
        return new Result(resultLog);
    }

}
