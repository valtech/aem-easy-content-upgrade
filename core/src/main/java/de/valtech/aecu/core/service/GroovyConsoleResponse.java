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
package de.valtech.aecu.core.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletResponse;

/**
 * Dummy response to execute the scripts.
 * 
 * @author Roland Gruber
 *
 */
public class GroovyConsoleResponse implements SlingHttpServletResponse {

    @Override
    public void addCookie(Cookie cookie) {
        // not used
    }

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public String encodeURL(String url) {
        return url;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return url;
    }

    @Override
    public String encodeUrl(String url) {
        return url;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return url;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        // not used
    }

    @Override
    public void sendError(int sc) throws IOException {
        // not used
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        // not used
    }

    @Override
    public void setDateHeader(String name, long date) {
        // not used
    }

    @Override
    public void addDateHeader(String name, long date) {
        // not used
    }

    @Override
    public void setHeader(String name, String value) {
        // not used
    }

    @Override
    public void addHeader(String name, String value) {
        // not used
    }

    @Override
    public void setIntHeader(String name, int value) {
        // not used
    }

    @Override
    public void addIntHeader(String name, int value) {
        // not used
    }

    @Override
    public void setStatus(int sc) {
        // not used
    }

    @Override
    public void setStatus(int sc, String sm) {
        // not used
    }

    @Override
    public int getStatus() {
        return 200;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return Collections.emptyList();
    }

    @Override
    public String getCharacterEncoding() {
        return "UTF-8";
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        // not used
    }

    @Override
    public void setContentLength(int len) {
        // not used
    }

    @Override
    public void setContentType(String type) {
        // not used
    }

    @Override
    public void setBufferSize(int size) {
        // not used
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        // not used
    }

    @Override
    public void resetBuffer() {
        // not used
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {
        // not used
    }

    @Override
    public void setLocale(Locale loc) {
        // not used
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> arg0) {
        return null;
    }

}
