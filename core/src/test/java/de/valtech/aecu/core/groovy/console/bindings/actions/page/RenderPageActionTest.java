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
package de.valtech.aecu.core.groovy.console.bindings.actions.page;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.core.groovy.console.bindings.actions.util.MockHttpServletResponse;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Tests RenderPageAction
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class RenderPageActionTest {

    @Mock
    private BindingContext context;

    @Mock
    private PageManager pageManager;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private RequestResponseFactory requestResponseFactory;

    @Mock
    private SlingRequestProcessor requestProcessor;

    @Mock
    private Page page;

    @Mock
    private Resource resource;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ByteArrayOutputStream out;

    private RenderPageAction actionSpy;

    @Before
    public void setup() {
        when(context.getPageManager()).thenReturn(pageManager);
        when(context.getResolver()).thenReturn(resolver);
        when(context.getRequestResponseFactory()).thenReturn(requestResponseFactory);
        when(context.getSlingRequestProcessor()).thenReturn(requestProcessor);
        when(pageManager.getContainingPage(resource)).thenReturn(page);
        when(requestResponseFactory.createRequest(Mockito.anyString(), Mockito.anyString())).thenReturn(request);
        actionSpy = spy(new RenderPageAction(context, 200, "test", "789"));
        MockHttpServletResponse response = spy(new MockHttpServletResponse());
        when(actionSpy.createResponse()).thenReturn(response);
        when(response.getOutput()).thenReturn(out);
        when(out.toString()).thenReturn("test123");
    }

    @Test
    public void doAction() throws PersistenceException, AecuException {
        RenderPageAction action = new RenderPageAction(context, 200, null, null);
        String result = action.doAction(resource);

        assertTrue(result.contains("Correct"));
    }

    @Test(expected = AecuException.class)
    public void doAction_noPage() throws PersistenceException, AecuException {
        when(pageManager.getContainingPage(resource)).thenReturn(null);

        RenderPageAction action = new RenderPageAction(context, 200, null, null);
        action.doAction(resource);
    }

    @Test(expected = AecuException.class)
    public void doAction_wrongStatus() throws PersistenceException, AecuException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(500);
        when(actionSpy.createResponse()).thenReturn(response);

        actionSpy.doAction(resource);
    }

    @Test
    public void doAction_textPresent() throws AecuException, IOException {
        actionSpy.doAction(resource);
    }

    @Test(expected = AecuException.class)
    public void doAction_textPresentDoesNotMatch() throws AecuException, IOException {
        when(out.toString()).thenReturn("1111");

        actionSpy.doAction(resource);
    }

    @Test(expected = AecuException.class)
    public void doAction_textNotPresentDoesNotMatch() throws AecuException, IOException {
        when(out.toString()).thenReturn("test789");

        actionSpy.doAction(resource);
    }

}
