package de.valtech.aecu.startuphook;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.api.service.HistoryEntry;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;


@Component(service=JobConsumer.class, property= {
    JobConsumer.PROPERTY_TOPICS + "=" + AecuStartupJobConsumer.JOB_TOPIC
})
public class AecuStartupJobConsumer implements JobConsumer {

    protected static final String JOB_TOPIC = "de/valtech/aecu/cloud/AecuStartupJobTopic";

    private static final Logger LOGGER = LoggerFactory.getLogger(AecuStartupJobConsumer.class);
    
    @Reference
    private AecuService aecuService;


    public JobResult process(final Job job) {
        try {
            LOGGER.info("AECU migration started");
            HistoryEntry result = aecuService.executeWithInstallHookHistory(AecuService.AECU_APPS_PATH_PREFIX);
            LOGGER.info("AECU migration finished with result "+result.getResult());
            return JobResult.OK;
        } catch (AecuException ae) {
            LOGGER.error("Error while executing AECU migration", ae);
            // Do not retry job, hence status CANCEL (=failed permanently) and not FAILED (=can be retried)
            return JobResult.CANCEL;
        }
    }
}