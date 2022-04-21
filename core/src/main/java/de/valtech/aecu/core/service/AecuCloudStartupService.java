/*
 * Copyright 2022 Bart Senn and Valtech GmbH
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
package de.valtech.aecu.core.service;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;
import de.valtech.aecu.core.util.runtime.RuntimeHelper;

/**
 * Service that executes the AECU migration if the node store type is composite (AEM Cloud).
 */
@Component(service = AecuCloudStartupService.class, immediate = true, name = "AECU migration service")
public class AecuCloudStartupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AecuCloudStartupService.class);

    @Reference
    private AecuService aecuService;
    @Reference
    private ServiceResourceResolverService resourceResolverService;

    @Activate
    public void activate() {
        ResourceResolver resourceResolver = getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);
        boolean isCompositeNodeStore = RuntimeHelper.isCompositeNodeStore(session);
        if (isCompositeNodeStore) {
            startAecuMigration();
        }
    }

    /**
     * Starts the AECU migration
     */
    void startAecuMigration() {
        try {
            LOGGER.info("AECU migration started");
            aecuService.executeWithInstallHookHistory(AecuService.AECU_APPS_PATH_PREFIX);
            LOGGER.info("AECU migration finished");
        } catch (AecuException ae) {
            LOGGER.error("Error while executing AECU migration", ae);
        }
    }

    /**
     * Returns the resource resolver to be used
     * 
     * @return the resource resolver
     */
    private ResourceResolver getResourceResolver() {
        try {
            return resourceResolverService.getAdminResourceResolver();
        } catch (LoginException le) {
            throw new IllegalStateException("Error while logging in", le);
        }
    }

}

