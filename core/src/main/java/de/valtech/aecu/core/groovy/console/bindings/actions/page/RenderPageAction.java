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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.engine.SlingRequestProcessor;

import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.core.groovy.console.bindings.actions.Action;
import de.valtech.aecu.core.groovy.console.bindings.actions.util.MockHttpServletResponse;
import de.valtech.aecu.core.groovy.console.bindings.impl.BindingContext;

/**
 * Checks the rendering of a page of a given resource.
 * 
 * @author Roland Gruber
 */
public class RenderPageAction implements Action {

    private BindingContext context;
    private int statusCode;
    private String textPresent;
    private String textNotPresent;

    /**
     * Constructor
     * 
     * @param context binding context
     */
    public RenderPageAction(BindingContext context, int statusCode, String textPresent, String textNotPresent) {
        this.context = context;
        this.statusCode = statusCode;
        this.textPresent = textPresent;
        this.textNotPresent = textNotPresent;
    }

    @Override
    public String doAction(Resource resource) throws PersistenceException, AecuException {
        Page page = context.getPageManager().getContainingPage(resource);
        if (page == null) {
            return "Unable to find a page for resource " + resource.getPath();
        }
        String successMessage = "Correct page rendering for " + page.getPath();
        RequestResponseFactory requestResponseFactory = context.getRequestResponseFactory();
        SlingRequestProcessor requestProcessor = context.getSlingRequestProcessor();
        String requestPath = page.getPath() + ".html";
        HttpServletRequest req = requestResponseFactory.createRequest(HttpConstants.METHOD_GET, requestPath);
        WCMMode.DISABLED.toRequest(req);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        try {
            requestProcessor.processRequest(req, resp, context.getResolver());
            if (resp.getStatus() != statusCode) {
                throw new AecuException(requestPath + " returned " + resp.getStatus() + " instead of " + statusCode);
            }
            String html = resp.getOutput().toString();
            out.close();
            if (StringUtils.isNotBlank(textPresent) && !html.contains(textPresent)) {
                throw new AecuException(requestPath + " did not include " + textPresent);
            }
            if (StringUtils.isNotBlank(textNotPresent) && html.contains(textNotPresent)) {
                throw new AecuException(requestPath + " did include " + textNotPresent);
            }
        } catch (ServletException | IOException e) {
            throw new PersistenceException("Unable to render " + requestPath);
        }
        return successMessage;
    }

}
