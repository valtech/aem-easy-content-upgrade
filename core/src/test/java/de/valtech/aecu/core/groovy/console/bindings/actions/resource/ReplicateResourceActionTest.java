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
package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jcr.Session;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;

import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Tests ReplicateResourceAction
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class ReplicateResourceActionTest {

    private static final String PATH = "path";

    @Mock
    private Replicator replicator;

    @Mock
    private BindingContext context;

    @Mock
    private Resource resource;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Session session;

    private ReplicateResourceAction action;

    @Before
    public void setup() {
        when(context.getReplicator()).thenReturn(replicator);
        when(context.getResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        this.action = new ReplicateResourceAction(true, context);
        when(resource.getPath()).thenReturn(PATH);
    }

    @Test(expected = PersistenceException.class)
    public void doAction_replicatorException() throws PersistenceException, ReplicationException {
        doThrow(ReplicationException.class).when(replicator).replicate(session, ReplicationActionType.ACTIVATE, PATH);

        action.doAction(resource);
    }

    @Test
    public void doAction_success() throws PersistenceException, ReplicationException {
        String result = action.doAction(resource);

        assertTrue(result.contains("Replicated resource"));
        verify(replicator, times(1)).replicate(session, ReplicationActionType.ACTIVATE, PATH);
    }

    @Test
    public void doAction_successDeactivate() throws PersistenceException, ReplicationException {
        this.action = new ReplicateResourceAction(false, context);

        String result = action.doAction(resource);

        assertTrue(result.contains("Replicated resource"));
        verify(replicator, times(1)).replicate(session, ReplicationActionType.DEACTIVATE, PATH);
    }

    @Test
    public void doAction_successDryRun() throws PersistenceException, ReplicationException {
        when(context.isDryRun()).thenReturn(true);

        String result = action.doAction(resource);

        assertTrue(result.contains("Replicated resource"));
        verify(replicator, never()).replicate(session, ReplicationActionType.ACTIVATE, PATH);
    }

}
