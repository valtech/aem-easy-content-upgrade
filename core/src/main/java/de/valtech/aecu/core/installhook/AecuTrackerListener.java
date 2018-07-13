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
