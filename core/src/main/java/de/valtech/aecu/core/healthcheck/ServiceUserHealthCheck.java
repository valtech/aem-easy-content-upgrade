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

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.hc.api.HealthCheck;
import org.apache.sling.hc.api.Result;
import org.apache.sling.hc.util.FormattingResultLog;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;

/**
 * Checks if the internal service user is ok.
 * 
 * @author Roland Gruber
 */
@Component(
    immediate = true,
    service = HealthCheck.class,
    property = {
        HealthCheck.TAGS + "=aecu",
        HealthCheck.NAME + "=AECU Service User",
        HealthCheck.MBEAN_NAME + "=aecuServiceUserHCmBean"
    }
)
public class ServiceUserHealthCheck implements HealthCheck {
    
    @Reference
    private ServiceResourceResolverService resolverService;

    @Override
    public Result execute() {
        final FormattingResultLog resultLog = new FormattingResultLog();
        try (ResourceResolver resolver = resolverService.getServiceResourceResolver()) {
            resultLog.info("Ok");
        } catch (LoginException e) {
            resultLog.critical("Unable to open service resource resolver {}", e.getMessage());
        }
        return new Result(resultLog);
    }

}
