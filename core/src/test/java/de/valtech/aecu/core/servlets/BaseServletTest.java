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
package de.valtech.aecu.core.servlets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests BaseServlet
 * 
 * @author Roland Gruber
 */
@RunWith(MockitoJUnitRunner.class)
public class BaseServletTest {

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private PrintWriter writer;

    private BaseServlet servlet;

    @Before
    public void setup() throws IOException {
        servlet = new BaseServlet();
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void setNoCache() {
        servlet.setNoCache(response);

        verify(response, times(3)).setHeader(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void writeResult_ok() throws IOException {
        servlet.writeResult(response, "test");

        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(writer, times(1)).write("test");
    }

    @Test
    public void writeResult_status() throws IOException {
        servlet.writeResult(response, "test", HttpServletResponse.SC_BAD_GATEWAY);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_GATEWAY);
        verify(writer, times(1)).write("test");
    }

    @Test
    public void sendInternalServerError() throws IOException {
        servlet.sendInternalServerError(response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void validateParameter() {
        assertFalse(servlet.validateParameter(null));
        assertFalse(servlet.validateParameter(""));
        assertTrue(servlet.validateParameter("123"));
    }

}
