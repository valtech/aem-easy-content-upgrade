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
        HealthCheck.NAME + "=AECU Self Check",
        HealthCheck.MBEAN_NAME + "=aecuSelfCheckHCmBean"
    }
)
public class SelfCheckHealthCheck implements HealthCheck {
    
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
