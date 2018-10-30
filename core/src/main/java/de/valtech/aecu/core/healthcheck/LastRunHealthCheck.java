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
package de.valtech.aecu.core.healthcheck;

import java.util.List;

import org.apache.sling.hc.api.HealthCheck;
import org.apache.sling.hc.api.Result;
import org.apache.sling.hc.util.FormattingResultLog;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.api.service.HistoryEntry;

/**
 * Checks if the last script run was ok.
 *
 * @author Roland Gruber
 */
@Component(immediate = true, service = HealthCheck.class, property = {HealthCheck.TAGS + "=aecu",
        HealthCheck.NAME + "=AECU Last Run", HealthCheck.MBEAN_NAME + "=aecuLastRunHCmBean"})
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
            } else {
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
                    default:
                        resultLog.warn("Last execution is still ongoing");
                }
            }
        } catch (AecuException e) {
            resultLog.critical(e.getMessage());
        }
        return new Result(resultLog);
    }

}
