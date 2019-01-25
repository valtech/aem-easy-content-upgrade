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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests GroovyConsoleResponse
 * 
 * @author Roland Gruber
 */
public class GroovyConsoleResponseTest {

    private static final String TEST = "testval";

    private GroovyConsoleResponse response;

    @Before
    public void setup() {
        response = new GroovyConsoleResponse();
    }

    @Test
    public void setters() throws IOException {
        response.addCookie(null);
        response.sendError(0);
        response.sendError(0, TEST);
        response.sendRedirect(TEST);
        response.setDateHeader(TEST, 0);
        response.addDateHeader(TEST, 0);
        response.setHeader(TEST, TEST);
        response.addHeader(TEST, TEST);
        response.setIntHeader(TEST, 0);
        response.addIntHeader(TEST, 0);
        response.setStatus(0);
        response.setStatus(0, TEST);
        response.setCharacterEncoding(TEST);
        response.setContentLength(0);
        response.setContentType(TEST);
        response.setBufferSize(0);
        response.flushBuffer();
        response.resetBuffer();
        response.reset();
        response.setLocale(null);
    }

    @Test
    public void getters() throws IOException {
        assertFalse(response.containsHeader(null));
        assertEquals(TEST, response.encodeURL(TEST));
        assertEquals(TEST, response.encodeUrl(TEST));
        assertEquals(TEST, response.encodeRedirectURL(TEST));
        assertEquals(TEST, response.encodeRedirectUrl(TEST));
        assertEquals(200, response.getStatus());
        assertNull(response.getHeader(TEST));
        assertTrue(response.getHeaders(TEST).isEmpty());
        assertTrue(response.getHeaderNames().isEmpty());
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertNull(response.getContentType());
        assertNull(response.getOutputStream());
        assertNull(response.getWriter());
        assertEquals(0, response.getBufferSize());
        assertFalse(response.isCommitted());
        assertNull(response.getLocale());
        assertNull(response.adaptTo(null));
    }

}
