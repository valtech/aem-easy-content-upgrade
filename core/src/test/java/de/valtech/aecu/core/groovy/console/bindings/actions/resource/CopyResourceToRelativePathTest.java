/*
 * Copyright 2020 Valtech GmbH
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

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test CopyResourceToRelativePathTest
 * @author Roxana Muresan
 */
@RunWith(MockitoJUnitRunner.class)
public class CopyResourceToRelativePathTest {

    private static final String SOURCE_PATH = "/content/source";
    private static final String SOURCE_NAME = "source";
    private static final String DESTINATION_PARENT_PATH = "/content/to/relative";

    @Mock
    private BindingContext contextMock;
    @Mock
    private ResourceResolver resourceResolverMock;
    @Mock
    private Resource resourceMock;
    @Mock
    private Resource destinationParentResourceMock;
    @Mock
    private PageManager pageManagerMock;
    @Mock
    private Session sessionMock;
    @Mock
    private Workspace workspaceMock;
    @Mock
    private ValueMap valueMapMock;

    @Before
    public void setup() {
        when(contextMock.getResolver()).thenReturn(resourceResolverMock);
        when(contextMock.isDryRun()).thenReturn(false);
        when(resourceResolverMock.getResource(eq(resourceMock), anyString())).thenReturn(destinationParentResourceMock);
        when(resourceMock.getPath()).thenReturn(SOURCE_PATH);
        when(resourceMock.getName()).thenReturn(SOURCE_NAME);
        when(resourceMock.getValueMap()).thenReturn(valueMapMock);
        when(destinationParentResourceMock.getPath()).thenReturn(DESTINATION_PARENT_PATH);
        when(resourceResolverMock.adaptTo(eq(PageManager.class))).thenReturn(pageManagerMock);
        when(resourceResolverMock.adaptTo(eq(Session.class))).thenReturn(sessionMock);
        when(sessionMock.getWorkspace()).thenReturn(workspaceMock);
    }

    @Test
    public void testDoAction_copyResource_differentPath_withNewName() throws PersistenceException, RepositoryException {
        testDoAction_onResource("newName", DESTINATION_PARENT_PATH + "/newName");
    }

    @Test
    public void testDoAction_copyResource_differentPath_noNewName() throws PersistenceException, RepositoryException {
        testDoAction_onResource(null, DESTINATION_PARENT_PATH + "/" +  SOURCE_NAME);
    }

    @Test
    public void testDoAction_copyResource_samePath_withNewName() throws PersistenceException, RepositoryException {
        when(destinationParentResourceMock.getPath()).thenReturn("/content");
        testDoAction_onResource("newName", "/content/newName");
    }

    @Test
    public void testDoAction_copyResource_dryRun() throws PersistenceException, RepositoryException {
        CopyResourceToRelativePath copyAction = new CopyResourceToRelativePath("to/relative", "newName", contextMock);
        when(valueMapMock.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn(JcrConstants.NT_UNSTRUCTURED);
        when(contextMock.isDryRun()).thenReturn(true);

        copyAction.doAction(resourceMock);

        verify(workspaceMock, never()).copy(anyString(), anyString());
    }

    private void testDoAction_onResource(String newName, String expectedDestinationPath) throws PersistenceException, RepositoryException {
        CopyResourceToRelativePath copyAction = new CopyResourceToRelativePath("to/relative", newName, contextMock);
        when(valueMapMock.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn(JcrConstants.NT_UNSTRUCTURED);

        copyAction.doAction(resourceMock);

        verify(workspaceMock, times(1)).copy(eq(SOURCE_PATH), eq(expectedDestinationPath));
    }

    @Test
    public void testDoAction_copyPage_differentPath_withNewName() throws WCMException, PersistenceException {
        testDoAction_onPage("newName", DESTINATION_PARENT_PATH + "/newName");
    }

    @Test
    public void testDoAction_copyPage_differentPath_noNewName() throws WCMException, PersistenceException {
        testDoAction_onPage(null, DESTINATION_PARENT_PATH + "/" + SOURCE_NAME);
    }

    @Test
    public void testDoAction_copyPage_samePath_withNewName() throws WCMException, PersistenceException {
        when(destinationParentResourceMock.getPath()).thenReturn("/content");
        testDoAction_onPage("newName", "/content/newName");
    }

    private void testDoAction_onPage(String newName, String expectedDestinationPath) throws PersistenceException, WCMException {
        CopyResourceToRelativePath copyAction = new CopyResourceToRelativePath("to/relative", newName, contextMock);
        when(valueMapMock.get(JcrConstants.JCR_PRIMARYTYPE, String.class)).thenReturn(NameConstants.NT_PAGE);

        copyAction.doAction(resourceMock);

        verify(pageManagerMock, times(1)).copy(eq(resourceMock), eq(expectedDestinationPath), eq(null), eq(false), eq(false), eq(false));
    }
}
