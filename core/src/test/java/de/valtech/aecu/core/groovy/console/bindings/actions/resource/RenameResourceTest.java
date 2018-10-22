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
package de.valtech.aecu.core.groovy.console.bindings.actions.resource;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jcr.ItemExistsException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests RenameResource
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class RenameResourceTest {

    private static final String NEW_NAME = "newName";

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Session session;

    @Mock
    private Resource resource;

    @Mock
    private Resource parent;

    @Before
    public void setup() {
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(resource.getName()).thenReturn("node");
        when(resource.getPath()).thenReturn("/parent/node");
        when(parent.getPath()).thenReturn("/parent");
        when(resource.getParent()).thenReturn(parent);
    }

    @Test
    public void doAction() throws PersistenceException, ItemExistsException, PathNotFoundException, VersionException,
            ConstraintViolationException, LockException, RepositoryException {
        RenameResource action = new RenameResource(resolver, NEW_NAME);

        action.doAction(resource);

        verify(session, times(1)).move("/parent/node", "/parent/" + NEW_NAME);
    }

    @Test
    public void doAction_noChange() throws PersistenceException, ItemExistsException, PathNotFoundException, VersionException,
            ConstraintViolationException, LockException, RepositoryException {
        when(resource.getName()).thenReturn(NEW_NAME);
        RenameResource action = new RenameResource(resolver, NEW_NAME);

        action.doAction(resource);

        verify(session, never()).move(Mockito.anyString(), Mockito.anyString());
    }

}
