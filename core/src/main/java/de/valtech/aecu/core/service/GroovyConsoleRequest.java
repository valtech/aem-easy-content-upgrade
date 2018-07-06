/*
 *  Copyright 2018 Valtech GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package de.valtech.aecu.core.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.request.RequestProgressTracker;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Dummy request that is used to execute the script.
 * 
 * @author Roland Gruber
 */
public class GroovyConsoleRequest implements SlingHttpServletRequest {
    
    private ResourceResolver resolver;
    
    /**
     * Constructor
     * 
     * @param resolver resource resolver
     */
    public GroovyConsoleRequest(ResourceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return null;
    }

    @Override
    public long getDateHeader(String arg0) {
        return 0;
    }

    @Override
    public String getHeader(String arg0) {
        return null;
    }

    @Override
    public Enumeration getHeaderNames() {
        return null;
    }

    @Override
    public Enumeration getHeaders(String arg0) {
        return null;
    }

    @Override
    public int getIntHeader(String arg0) {
        return 0;
    }

    @Override
    public String getMethod() {
        return null;
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return null;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean arg0) {
        return null;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isUserInRole(String arg0) {
        return false;
    }

    @Override
    public Object getAttribute(String arg0) {
        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration getLocales() {
        return null;
    }

    @Override
    public String getParameter(String arg0) {
        return null;
    }

    @Override
    public Map getParameterMap() {
        return null;
    }

    @Override
    public Enumeration getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String arg0) {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRealPath(String arg0) {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String arg0) {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public void removeAttribute(String arg0) {
    }

    @Override
    public void setAttribute(String arg0, Object arg1) {
    }

    @Override
    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> arg0) {
        return null;
    }

    @Override
    public Cookie getCookie(String arg0) {
        return null;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(Resource arg0) {
        return null;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String arg0, RequestDispatcherOptions arg1) {
        return null;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(Resource arg0, RequestDispatcherOptions arg1) {
        return null;
    }

    @Override
    public RequestParameter getRequestParameter(String arg0) {
        return null;
    }

    @Override
    public List<RequestParameter> getRequestParameterList() {
        return null;
    }

    @Override
    public RequestParameterMap getRequestParameterMap() {
        return null;
    }

    @Override
    public RequestParameter[] getRequestParameters(String arg0) {
        return null;
    }

    @Override
    public RequestPathInfo getRequestPathInfo() {
        return null;
    }

    @Override
    public RequestProgressTracker getRequestProgressTracker() {
        return null;
    }

    @Override
    public Resource getResource() {
        return null;
    }

    @Override
    public ResourceBundle getResourceBundle(Locale arg0) {
        return null;
    }

    @Override
    public ResourceBundle getResourceBundle(String arg0, Locale arg1) {
        return null;
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return resolver;
    }

    @Override
    public String getResponseContentType() {
        return null;
    }

    @Override
    public Enumeration<String> getResponseContentTypes() {
        return null;
    }

}
