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
package de.valtech.aecu.startuphook;

import java.util.Collection;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentConfigurationDTO;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.service.component.runtime.dto.SatisfiedReferenceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icfolson.aem.groovy.console.api.BindingExtensionProvider;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;

/**
 * Service that executes the AECU migration if the node store type is composite (AEM Cloud).
 */
@Component(service = AecuCloudStartupService.class, immediate = true, name = "AECU cloud startup hook")
public class AecuCloudStartupService {

    private static final String STAR_IMPORT_EXTENSION_PROVIDER = "StarImportExtensionProvider";
    private static final String BINDING_EXTENSION_PROVIDER = "BindingExtensionProvider";
    private static final String DEFAULT_EXTENSION_SERVICE =
            "com.icfolson.aem.groovy.console.extension.impl.DefaultExtensionService";

    private static final Logger LOGGER = LoggerFactory.getLogger(AecuCloudStartupService.class);

    private static final int WAIT_PERIOD = 10;
    private static final int WAIT_INTERVALS = 30;

    @Reference
    private AecuService aecuService;
    @Reference
    private ServiceResourceResolverService resourceResolverService;
    @Reference
    private ServiceComponentRuntime serviceComponentRuntime;

    @Activate
    public void checkAndRunMigration() {
        ResourceResolver resourceResolver = getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);
        boolean isCompositeNodeStore = RuntimeHelper.isCompositeNodeStore(session);
        if (!isCompositeNodeStore) {
            try {
                if (!waitForServices()) {
                    LOGGER.error("Groovy extension services seem to be not bound");
                    throw new IllegalStateException("Groovy extension services seem to be not bound");
                }
                Thread.sleep(1000L * WAIT_PERIOD);
                startAecuMigration();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted", e);
            }
        }
    }

    /**
     * Waits till Groovy Console took up our services.
     * 
     * @return services are ok
     * @throws InterruptedException sleep failed
     */
    protected boolean waitForServices() throws InterruptedException {
        for (int i = 0; i < WAIT_INTERVALS; i++) {
            if (servicesAreOk()) {
                return true;
            }
            Thread.sleep(1000L * WAIT_PERIOD);
            LOGGER.debug("Services not yet injected, waiting");
        }
        return false;
    }

    /**
     * Checks if our services are already injected.
     */
    private boolean servicesAreOk() {
        Bundle bundle = FrameworkUtil.getBundle(BindingExtensionProvider.class);
        ComponentDescriptionDTO componentDescription =
                serviceComponentRuntime.getComponentDescriptionDTO(bundle, DEFAULT_EXTENSION_SERVICE);
        if ((componentDescription == null) || !serviceComponentRuntime.isComponentEnabled(componentDescription)) {
            return false;
        }
        Collection<ComponentConfigurationDTO> componentConfigurations =
                serviceComponentRuntime.getComponentConfigurationDTOs(componentDescription);
        int satisfied = 0;
        for (ComponentConfigurationDTO componentConfiguration : componentConfigurations) {
            for (SatisfiedReferenceDTO satisfiedReference : componentConfiguration.satisfiedReferences) {
                if ((BINDING_EXTENSION_PROVIDER.equals(satisfiedReference.name)
                        || STAR_IMPORT_EXTENSION_PROVIDER.equals(satisfiedReference.name))
                        && (satisfiedReference.boundServices.length >= 2)) {
                    satisfied++;
                }
            }
        }
        return satisfied == 2;
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

