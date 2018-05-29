package de.valtech.aecu.core.installhook;

import de.valtech.aecu.service.AecuException;
import de.valtech.aecu.service.AecuService;

import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.vault.fs.api.ProgressTrackerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Collects groovy script paths to execute.
 */
public class AecuTrackerListener implements ProgressTrackerListener {

    private static final Logger LOG = LoggerFactory.getLogger(AecuTrackerListener.class);

    private static final String SCRIPT_PARENT_PATH = "/etc/groovyconsole/scripts/aecu/";

    private final ProgressTrackerListener originalListener;
    private final AecuService aecuService;
    private final Set<String> actions;
    private final Set<String> paths;

    public AecuTrackerListener(ProgressTrackerListener originalListener, AecuService aecuService, String... actions) {
        this.originalListener = originalListener;
        this.aecuService = aecuService;
        this.actions = new HashSet<>(Arrays.asList(actions));
        this.paths = new HashSet<>();
    }

    public Set<String> getGroovyScriptPaths() {
        return Collections.unmodifiableSet(paths);
    }

    @Override
    public void onMessage(Mode mode, String action, String path) {
        originalListener.onMessage(mode, action, path);

        if (!actions.contains(action)) {
            LOG.debug("Skipping {} due to non matching action {}", path, action);
            return;
        }
        // TODO: not the best idea to couple the detection of project roots to the structure
        if (path.startsWith(SCRIPT_PARENT_PATH)) {
            String pathToUse = Text.getAbsoluteParent(path, 4);
            // grouping items by first sub folder below SCRIPT_PARENT_PATH
            LOG.info("Found matching path {}, using parent {}", path, pathToUse);
            try {
                paths.addAll(aecuService.getFiles(pathToUse));
            } catch (AecuException e) {
                // TODO re-throw?
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void onError(Mode mode, String action, Exception e) {
        originalListener.onError(mode, action, e);
    }
}
