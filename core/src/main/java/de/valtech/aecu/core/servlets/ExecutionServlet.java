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

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import de.valtech.aecu.core.history.HistoryUtil;
import de.valtech.aecu.service.AecuException;
import de.valtech.aecu.service.AecuService;
import de.valtech.aecu.service.ExecutionResult;
import de.valtech.aecu.service.HistoryEntry;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * @author Bryan Chavez
 */

@Component(immediate = true,
service = {Servlet.class},
property = {
        "sling.servlet.paths=/bin/public/valtech/aecu/execute",
        "sling.servlet.extensions=json",
        "sling.servlet.methods=GET"
})
public class ExecutionServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ExecutionServlet.class);

    protected static final String ERROR_MESSAGE_MANDATORY = "ExecutionServlet :: Make sure your are sending the correct parameters.";

    @Reference
    AecuService aecuService;


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        this.setNoCache(response);

        String historyEntryAction = request.getParameter("historyEntryAction");
        String aecuScriptPath = request.getParameter("aecuScriptPath");
        if(!this.validateParameter(aecuScriptPath) || !this.validateParameter(historyEntryAction)){
            this.writeResult(response, ERROR_MESSAGE_MANDATORY);
            return;
        }

        try {
            HistoryEntry historyEntry = this.getHistoryEntry(request, response, historyEntryAction);
            ExecutionResult executionResult = aecuService.execute(aecuScriptPath);
            aecuService.storeExecutionInHistory(historyEntry, executionResult);
            this.finishHistoryEntry(historyEntry,historyEntryAction);
            response.getWriter().write(this.prepareJson(executionResult.isSuccess(),historyEntry.getRepositoryPath()));

        }catch (AecuException e){
            this.sendInternalServerError(response);
        }

    }

    protected HistoryEntry getHistoryEntry(SlingHttpServletRequest request, SlingHttpServletResponse response, String historyEntryAction)
            throws AecuException, IOException{

        HistoryEntry historyEntry;

        switch (historyEntryAction.toLowerCase()) {
            case "use":
            case "close":
                //Used for "use" and "close"
                String historyEntryPath = request.getParameter("historyEntryPath");
                if(!this.validateParameter(historyEntryPath)){
                    this.writeResult(response, ERROR_MESSAGE_MANDATORY);
                    return null;
                }

                ResourceResolver resolver = request.getResourceResolver();
                HistoryUtil historyUtil = new HistoryUtil();
                historyEntry = historyUtil.readHistoryEntry(resolver.getResource(historyEntryPath));
                break;
            default:
                //Used for "single" and "create"
                historyEntry = aecuService.createHistoryEntry();
                break;
        }

        return historyEntry;
    }

    protected HistoryEntry finishHistoryEntry(HistoryEntry historyEntry, String historyEntryAction) throws AecuException{

        switch (historyEntryAction.toLowerCase()) {
            case "single":
            case "close":
                //Used for "single" and "close"
                aecuService.finishHistoryEntry(historyEntry);
                break;
        }

        return historyEntry;

    }

    /**
     * This method builds the JSON String for the response.
     * Eg: {"success": true,"historyEntryPath":"/var/aecu/2018/6/13/152892696338961314"}
     *
     * @param status
     * @param historyEntryPath
     * @return json String
     */
    protected String prepareJson (boolean status, String historyEntryPath) {
        JsonObject json = new JsonObject();
        json.addProperty("success",status);
        json.addProperty("historyEntryPath", historyEntryPath);
        return json.toString();
    }

}