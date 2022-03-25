package de.valtech.aecu.core.listeners;


import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.core.jmx.AecuServiceMBean;
import de.valtech.aecu.core.serviceuser.ServiceResourceResolverService;
import java.util.Collections;
import java.util.List;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ResourceChangeListener} implementation that listens for a specific, configurable path
 * to trigger the AECU migration without using install hooks.
 */
@Component(
        immediate = true,
        service = ResourceChangeListener.class,
        property = {
                ResourceChangeListener.PATHS + "=" + AecuMigrationTrigger.TRIGGER_LOCATION,
                ResourceChangeListener.CHANGES + "=ADDED"
        }
)
@Designate(ocd = AecuMigrationTrigger.Config.class, factory = true)
public class AecuMigrationTrigger implements ResourceChangeListener {

    @ObjectClassDefinition(
            name = "AECU migration trigger configuration",
            description = "AECU migration trigger configuration"
    )
    public @interface Config {
        @AttributeDefinition(
                name = "Trigger config",
                description = "Trigger configuration"
        )
        String triggerConfig() default "migration-trigger";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AecuMigrationTrigger.class);

    static final String TRIGGER_LOCATION = "/conf/aecu/trigger";
    private static final String TRIGGER_TYPE = JcrConstants.NT_UNSTRUCTURED;

    @Reference
    private AecuServiceMBean aecuServiceMBean;
    @Reference
    private ServiceResourceResolverService resourceResolverService;
    private Resource triggerResource;

    private String triggerConfig;

    @Activate
    public void activate(final Config config) {
        triggerConfig = config.triggerConfig();
        LOGGER.info("AECU migration trigger listener is active...");

        //do an initial check for existing trigger
        try (ResourceResolver resourceResolver = getResourceResolver()) {
            LOGGER.info("Looking for existing trigger...");
            if (getTriggerResource(resourceResolver) != null) {
                LOGGER.info("Existing trigger found!");
                startAecuMigration(resourceResolver);
            }
        }
    }

    @Override
    public void onChange(List<ResourceChange> changes) {
        try(ResourceResolver resourceResolver = getResourceResolver()) {
            changes.stream()
                    .filter(change -> isMigrationTrigger(change, resourceResolver))
                    .findAny()
                    .ifPresent(change -> {
                        startAecuMigration(resourceResolver);
                    });
        }
    }

    /**
     * Starts the AECU migration
     * @param resourceResolver the resource resolver to use
     */
    void startAecuMigration(ResourceResolver resourceResolver) {
        try {
            LOGGER.info("AECU migration started");
            List<String> migrationScripts = aecuServiceMBean.getFiles(AecuService.AECU_CONF_PATH_PREFIX);
            if (!migrationScripts.isEmpty()) {
                migrationScripts.forEach(this::executeScript);
                LOGGER.info("AECU migration finished");
            } else {
                LOGGER.info("No AECU groovy scripts to execute");
            }
        } catch(AecuException ae) {
            LOGGER.error("Error while executing AECU migration", ae);
        } finally {
            deleteTrigger(resourceResolver);
        }
    }

    /**
     * Executes a script cleanly (handles possible exception)
     * @param scriptPath the path to the script to execute
     */
    private void executeScript(String scriptPath) {
        try {
            aecuServiceMBean.executeWithHistory(scriptPath);
        } catch(AecuException ae) {
            LOGGER.error("Error when executing script " + scriptPath, ae);
        }
    }

    /**
     * Checks if a changed resource is a trigger to start the AECU migration
     * @param change the changed resource
     * @param resourceResolver the resource resolver to use
     * @return <code>true</code> if migration should start, <code>false</code> if not
     */
    boolean isMigrationTrigger(ResourceChange change, ResourceResolver resourceResolver) {
        if(buildTriggerPath().equals(change.getPath())
                && ResourceChange.ChangeType.ADDED.equals(change.getType())) {
            Resource triggerResource = getTriggerResource(resourceResolver);
            return triggerResource != null && TRIGGER_TYPE.equals(triggerResource.getResourceType());
        }
        return false;
    }

    private String buildTriggerPath() {
        return TRIGGER_LOCATION + "/" + triggerConfig;
    }

    /**
     * Deletes the trigger resource from the JCR.
     * @param resourceResolver the resource reoslver used to delete the trigger resource
     */
    void deleteTrigger(ResourceResolver resourceResolver) {
        try {
            if(triggerResource != null) {
                resourceResolver.delete(triggerResource);
                resourceResolver.commit();
                triggerResource = null;
            }
        } catch(PersistenceException pe) {
            throw new RuntimeException("Could not delete AECU migration-trigger", pe);
        }
    }

    /**
     * Returns the trigger resource from the JCR
     * @return the trigger resource or <code>null</code> if the used {@link ResourceResolver} is null
     *          or the resource can't be found
     * @see #getResourceResolver()
     */
    Resource getTriggerResource(ResourceResolver resourceResolver) {
        if(triggerResource == null) {
            triggerResource = resourceResolver.getResource(buildTriggerPath());
        }
        return triggerResource;
    }

    /**
     * Returns the resource resolver to be used
     * @return the resource resolver
     */
    private ResourceResolver getResourceResolver() {
        try {
            return resourceResolverService.getContentMigratorResourceResolver();
        } catch(LoginException le) {
            throw new RuntimeException("Error while logging in", le);
        }
    }

}

