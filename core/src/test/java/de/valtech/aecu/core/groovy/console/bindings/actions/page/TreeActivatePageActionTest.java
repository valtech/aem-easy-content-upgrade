/*
 * Copyright 2019 - 2022 Valtech GmbH
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;

import javax.jcr.Session;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Tests TreeActivatePageAction
 * 
 * @author Roland Gruber
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TreeActivatePageActionTest {

    private static final String PATH = "path";
    private static final String SUBPATH = "subpath";

    @Mock
    private PageManager pageManager;

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

    @Mock
    private Page page;

    @Mock
    private Page subPage;

    private TreeActivatePageAction action;

    @BeforeEach
    public void setup() {
        when(context.getPageManager()).thenReturn(pageManager);
        when(context.getReplicator()).thenReturn(replicator);
        when(context.getResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(pageManager.getContainingPage(resource)).thenReturn(page);
        when(resource.getPath()).thenReturn(PATH);
        when(page.getPath()).thenReturn(PATH);
        when(subPage.getPath()).thenReturn(SUBPATH);
        Iterator<Page> subpages = Arrays.asList(subPage).iterator();
        when(page.listChildren()).thenReturn(subpages);
        Iterator<Page> subsubpages = mock(Iterator.class);
        when(subPage.listChildren()).thenReturn(subsubpages);
        this.action = new TreeActivatePageAction(false, context);
    }

    @Test
    public void doAction_pageNotFound() throws PersistenceException, AecuException {
        when(pageManager.getContainingPage(resource)).thenReturn(null);

        String result = action.doAction(resource);

        assertTrue(result.contains("Unable to find a page"));
    }

    @Test
    public void doAction_replicatorException() throws PersistenceException, ReplicationException, AecuException {
        doThrow(ReplicationException.class).when(replicator).replicate(session, ReplicationActionType.ACTIVATE, PATH);

        assertThrows(PersistenceException.class, () -> action.doAction(resource));
    }

    @Test
    public void doAction_success() throws PersistenceException, ReplicationException, AecuException {
        String result = action.doAction(resource);

        assertTrue(result.contains("Replicated page tree"));
        assertTrue(result.contains("2 pages"));
        verify(replicator, times(1)).replicate(session, ReplicationActionType.ACTIVATE, PATH);
        verify(replicator, times(1)).replicate(session, ReplicationActionType.ACTIVATE, SUBPATH);
    }

    @Test
    public void doAction_successDeactivatedSubpage() throws PersistenceException, ReplicationException, AecuException {
        ReplicationStatus replicationStatus = mock(ReplicationStatus.class);
        when(replicationStatus.isDeactivated()).thenReturn(true);
        when(subPage.adaptTo(ReplicationStatus.class)).thenReturn(replicationStatus);
        this.action = new TreeActivatePageAction(true, context);

        String result = action.doAction(resource);

        assertTrue(result.contains("Replicated page tree"));
        assertTrue(result.contains("1 pages"));
        verify(replicator, times(1)).replicate(session, ReplicationActionType.ACTIVATE, PATH);
        verify(replicator, never()).replicate(session, ReplicationActionType.ACTIVATE, SUBPATH);
    }

}
