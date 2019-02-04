/*
 * Copyright 2019 Valtech GmbH
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
package de.valtech.aecu.core.groovy.console.bindings.actions.page;

import java.util.Iterator;

import javax.jcr.Session;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Performs a tree activation.
 * 
 * @author Roland Gruber
 */
public class TreeActivatePageAction implements Action {

    private boolean skipDeactivated;
    private BindingContext context;
    private int count = 0;

    /**
     * Constructor
     * 
     * @param skipDeactivated skips deactivated pages
     * @param context         binding context
     */
    public TreeActivatePageAction(boolean skipDeactivated, BindingContext context) {
        this.skipDeactivated = skipDeactivated;
        this.context = context;
    }

    @Override
    public String doAction(Resource resource) throws PersistenceException, AecuException {
        Page page = context.getPageManager().getContainingPage(resource);
        if (page == null) {
            return "Unable to find a page for resource " + resource.getPath();
        }
        String skip = skipDeactivated ? "skipping" : "not skipping";
        try {
            performActivation(page);
        } catch (ReplicationException e) {
            throw new PersistenceException("Unable to replicate tree " + page.getPath(), e);
        }
        return "Replicated page tree " + page.getPath() + " " + skip + " deactivated pages (" + count + " pages)";
    }

    /**
     * Recursive activation of page.
     * 
     * @param page page
     * @throws ReplicationException error during replication
     */
    private void performActivation(Page page) throws ReplicationException {
        if (skipDeactivated) {
            ReplicationStatus status = page.adaptTo(ReplicationStatus.class);
            if ((status != null) && status.isDeactivated()) {
                return;
            }
        }
        activate(page);
        Iterator<Page> childIterator = page.listChildren();
        while (childIterator.hasNext()) {
            performActivation(childIterator.next());
        }
    }

    /**
     * Activates a single page.
     * 
     * @param page page
     * @throws ReplicationException error replicating page
     */
    private void activate(Page page) throws ReplicationException {
        count++;
        if (context.isDryRun()) {
            return;
        }
        Session session = context.getResolver().adaptTo(Session.class);
        context.getReplicator().replicate(session, ReplicationActionType.ACTIVATE, page.getPath());
    }

}
