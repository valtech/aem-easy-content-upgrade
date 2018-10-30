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
package de.valtech.aecu.core.groovy.console.bindings.actions.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Mock for servlet response to get e.g. response status.
 * 
 * @author Roland Gruber
 *
 */
public class MockHttpServletResponse implements HttpServletResponse {

    private String contentType;
    private String characterEncoding;
    private int status = HttpServletResponse.SC_OK;
    private Locale locale;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private PrintWriter printWriter = new PrintWriter(out);

    /**
     * Returns the output stream.
     * 
     * @return output
     */
    public ByteArrayOutputStream getOutput() {
        return out;
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        characterEncoding = charset;
    }

    @Override
    public void setContentLength(int len) {}

    @Override
    public void setContentType(String type) {
        contentType = type;
    }

    @Override
    public void setBufferSize(int size) {}

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {}

    @Override
    public void resetBuffer() {}

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {}

    @Override
    public void setLocale(Locale loc) {
        locale = loc;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void addCookie(Cookie cookie) {}

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        status = sc;
    }

    @Override
    public void sendError(int sc) throws IOException {
        status = sc;
    }

    @Override
    public void sendRedirect(String location) throws IOException {}

    @Override
    public void setDateHeader(String name, long date) {}

    @Override
    public void addDateHeader(String name, long date) {}

    @Override
    public void setHeader(String name, String value) {}

    @Override
    public void addHeader(String name, String value) {}

    @Override
    public void setIntHeader(String name, int value) {}

    @Override
    public void addIntHeader(String name, int value) {}

    @Override
    public void setStatus(int sc) {
        status = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        status = sc;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

}
