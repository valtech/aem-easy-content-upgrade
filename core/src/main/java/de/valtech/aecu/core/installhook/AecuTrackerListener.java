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
package de.valtech.aecu.core.installhook;

import de.valtech.aecu.service.AecuService;

import org.apache.jackrabbit.vault.fs.api.ProgressTrackerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Collects groovy script paths to potentially execute based on the given actions.
 */
public class AecuTrackerListener implements ProgressTrackerListener {

    private static final Logger LOG = LoggerFactory.getLogger(AecuTrackerListener.class);

    private static final Set<String> ACTIONS = new HashSet<>(Arrays.asList("A", "M"));

    private final ProgressTrackerListener originalListener;
    private final AecuService aecuService;
    private final List<String> paths;

    /**
     * Constructor.
     * @param originalListener the original ProgressTrackerListener.
     * @param aecuService an AecuService instance.
     */
    public AecuTrackerListener(ProgressTrackerListener originalListener, AecuService aecuService) {
        this.originalListener = originalListener;
        this.aecuService = aecuService;
        this.paths = new LinkedList<>();
    }

    /**
     * Returns an unmodifiable list of the modified or added paths encountered during the installation phase.
     * @return a list of modified or added paths, can be empty.
     */
    @Nonnull
    public List<String> getModifiedOrAddedPaths() {
        return Collections.unmodifiableList(paths);
    }

    @Override
    public void onMessage(Mode mode, String action, String path) {
        originalListener.onMessage(mode, action, path);

        if (!ACTIONS.contains(action)) {
            LOG.debug("Skipping {} due to non matching action {}", path, action);
            return;
        }

        if (aecuService.isValidScriptName(path)) {
            LOG.debug("Found valid script path {}", path);
            paths.add(path);
        }
    }

    @Override
    public void onError(Mode mode, String action, Exception e) {
        originalListener.onError(mode, action, e);
    }
}
