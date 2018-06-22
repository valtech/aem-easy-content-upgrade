/*
 *  Copyright 2018 Valtech GmbH
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>
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
        writeResult(response,json,HttpServletResponse.SC_OK);
    }

    protected void sendInternalServerError(SlingHttpServletResponse response) throws IOException {
        writeResult(response,ERROR_MESSAGE_INTERNAL_SERVER, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    protected boolean validateParameter(String param){
        return param != null  && StringUtils.isNotEmpty(param);
    }

}
