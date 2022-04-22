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
package de.valtech.aecu.core.util.runtime;

import javax.jcr.Node;
import javax.jcr.Session;

import com.day.cq.commons.jcr.JcrConstants;

/**
 * Checks if a composite node store is in place (using approach from AC Tool).
 */
public final class RuntimeHelper {

    private static final String LIBS = "/libs";

    private RuntimeHelper() {
        // static methods only
    }

    /**
     * If a user has permissions on "/" but does not have the capability to create nodes under
     * "/libs" then it is assumed to be a composite node store.
     * 
     * @param session session
     * @return is composite node store
     * @see <a href="https://issues.apache.org/jira/browse/OAK-6563">OAK-6563</a>
     */
    public static boolean isCompositeNodeStore(Session session) {
        try {
            Node readOnlyCandidate = session.getNode(LIBS);
            boolean hasPermission = session.hasPermission("/", Session.ACTION_SET_PROPERTY);
            if (!hasPermission) {
                return false;
            }
            return !session.hasCapability("addNode", readOnlyCandidate, new String[] {JcrConstants.NT_FOLDER});
        } catch (Exception e) {
            throw new IllegalStateException("Unable to check if session is uses a composite node store", e);
        }
    }

}

