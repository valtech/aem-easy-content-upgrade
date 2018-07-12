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
package de.valtech.aecu.core.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

/**
 * @author Bryan Chavez
 */
public class BaseServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -5240930544859160292L;

    protected static final String ERROR_MESSAGE_INTERNAL_SERVER = "Internal Server Error";


    protected void setNoCache(HttpServletResponse response) {
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");
    }

    protected void writeResult(SlingHttpServletResponse response, String json, int status) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(status);
        response.getWriter().write(json);
    }

    protected void writeResult(SlingHttpServletResponse response, String json) throws IOException {
        writeResult(response, json, HttpServletResponse.SC_OK);
    }

    protected void sendInternalServerError(SlingHttpServletResponse response) throws IOException {
        writeResult(response, ERROR_MESSAGE_INTERNAL_SERVER, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    protected boolean validateParameter(String param) {
        return param != null && StringUtils.isNotEmpty(param);
    }

}
