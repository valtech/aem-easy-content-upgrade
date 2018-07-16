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

import de.valtech.aecu.service.AecuException;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.value.DateValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Execution history for groovy scripts executed during {@link AecuInstallHook} invocation.
 */
public class HookExecutionHistory {

    private static final Logger LOG = LoggerFactory.getLogger(HookExecutionHistory.class);

    private static final String HISTORY_BASE_PATH = "/var/aecu-installhook";

    private static final String PN_EXECUTED = "executed";

    private final Node hookHistory;

    /**
     * Constructor.
     * @param session a session with write permissons on {@value HISTORY_BASE_PATH}.
     * @param groovyScriptPath the groovy script to instantiate the execution history for.
     * @throws AecuException in case the call to JcrUtils.getOrCreateByPath fails.
     */
    public HookExecutionHistory(Session session, String groovyScriptPath) throws AecuException {
        try {
            hookHistory = JcrUtils.getOrCreateByPath(HISTORY_BASE_PATH + groovyScriptPath, false, JcrConstants.NT_UNSTRUCTURED,
                    JcrConstants.NT_UNSTRUCTURED, session, true);
        } catch (RepositoryException e) {
            throw new AecuException("Error getting or creating node at " + HISTORY_BASE_PATH + groovyScriptPath, e);
        }
    }

    /**
     * Returns if the script has been executed before. This is determined by checking existence of the property
     * {@value PN_EXECUTED} on the history node.
     * @return true if it has been executed previously, false otherwise.
     */
    public boolean hasBeenExecutedBefore() {
        boolean hasBeenExecuted = false;
        try {
            hasBeenExecuted = hookHistory.hasProperty(PN_EXECUTED);
        } catch (RepositoryException e) {
            LOG.error(e.getMessage(), e);
        }
        return hasBeenExecuted;
    }

    /**
     * Sets {@value PN_EXECUTED} on the history node to the current date.
     * @throws AecuException in case the property could not be saved.
     */
    public void setExecuted() throws AecuException {
        try {
            hookHistory.setProperty(PN_EXECUTED, new DateValue(Calendar.getInstance()));
            hookHistory.getSession().save();
        } catch (RepositoryException e) {
            throw new AecuException("Could not set property " + PN_EXECUTED, e);
        }
    }

}
