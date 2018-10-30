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
package de.valtech.aecu.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests GroovyConsoleRequest
 * 
 * @author Roland Gruber
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class GroovyConsoleRequestTest {

    @Mock
    private ResourceResolver resolver;

    private GroovyConsoleRequest request;

    @Before
    public void setup() {
        request = new GroovyConsoleRequest(resolver);
    }

    @Test
    public void getResourceResolver() {
        assertEquals(resolver, request.getResourceResolver());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeAttribute() {
        request.removeAttribute("");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setAttribute() {
        request.setAttribute(null, "");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setCharacterEncoding() throws UnsupportedEncodingException {
        request.setCharacterEncoding("");
    }

    @Test
    public void emptyMethods() throws IOException, ServletException {
        assertNull(request.getAuthType());
        assertNull(request.getContextPath());
        assertNull(request.getCookies());
        assertEquals(0, request.getDateHeader(""));
        assertNull(request.getHeader(""));
        assertNull(request.getHeaderNames());
        assertNull(request.getHeaders(""));
        assertEquals(0, request.getIntHeader(""));
        assertNull(request.getMethod());
        assertNull(request.getPathInfo());
        assertNull(request.getPathTranslated());
        assertNull(request.getQueryString());
        assertNull(request.getRemoteUser());
        assertNull(request.getRequestURI());
        assertNull(request.getRequestURL());
        assertNull(request.getRequestedSessionId());
        assertNull(request.getServletPath());
        assertNull(request.getSession());
        assertNull(request.getSession(true));
        assertFalse(request.isRequestedSessionIdFromCookie());
        assertFalse(request.isRequestedSessionIdFromURL());
        assertFalse(request.isRequestedSessionIdFromUrl());
        assertFalse(request.isRequestedSessionIdValid());
        assertFalse(request.isUserInRole(""));
        assertNull(request.getAttribute(""));
        assertNull(request.getAttributeNames());
        assertNull(request.getCharacterEncoding());
        assertEquals(0, request.getContentLength());
        assertNull(request.getContentType());
        assertNull(request.getInputStream());
        assertNull(request.getLocalAddr());
        assertNull(request.getLocalName());
        assertEquals(0, request.getLocalPort());
        assertNull(request.getLocale());
        assertNull(request.getLocales());
        assertNull(request.getParameter(""));
        assertNull(request.getParameterMap());
        assertNull(request.getParameterNames());
        assertNull(request.getParameterValues(""));
        assertNull(request.getProtocol());
        assertNull(request.getReader());
        assertNull(request.getRealPath(""));
        assertNull(request.getRemoteAddr());
        assertNull(request.getRemoteHost());
        assertEquals(0, request.getRemotePort());
        assertNull(request.getRequestDispatcher(""));
        assertNull(request.getScheme());
        assertNull(request.getServerName());
        assertEquals(0, request.getServerPort());
        assertFalse(request.isSecure());
        assertNull(request.adaptTo(null));
        assertNull(request.getCookie(""));
        assertNull(request.getRequestDispatcher((Resource) null));
        assertNull(request.getRequestDispatcher("", null));
        assertNull(request.getRequestDispatcher((Resource) null, null));
        assertNull(request.getRequestParameter(""));
        assertNull(request.getRequestParameterList());
        assertNull(request.getRequestParameterMap());
        assertNull(request.getRequestParameters(""));
        assertNull(request.getRequestPathInfo());
        assertNull(request.getRequestProgressTracker());
        assertNull(request.getResource());
        assertNull(request.getResourceBundle(null));
        assertNull(request.getResourceBundle("", null));
        assertNull(request.getResponseContentType());
        assertNull(request.getResponseContentTypes());
        assertFalse(request.authenticate(null));
        request.login(null, null);
        request.logout();
        assertNull(request.getParts());
        assertNull(request.getPart(null));
        assertNull(request.getServletContext());
        assertNull(request.startAsync());
        assertNull(request.startAsync(null, null));
        assertFalse(request.isAsyncStarted());
        assertFalse(request.isAsyncSupported());
        assertNull(request.getAsyncContext());
        assertNull(request.getDispatcherType());
    }

}
