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


import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.core.history.HistoryUtil;

/**
 * @author Bryan Chavez
 */

@Component(immediate = true, service = {Servlet.class}, property = {"sling.servlet.paths=/bin/public/valtech/aecu/execute",
        "sling.servlet.extensions=json", "sling.servlet.methods=GET"})
public class ExecutionServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ExecutionServlet.class);

    protected static final String ERROR_MESSAGE_MANDATORY =
            "ExecutionServlet :: Make sure your are sending the correct parameters.";

    @Reference
    AecuService aecuService;


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        this.setNoCache(response);

        String historyEntryAction = request.getParameter("historyEntryAction");
        String aecuScriptPath = request.getParameter("aecuScriptPath");
        if (!this.validateParameter(aecuScriptPath) || !this.validateParameter(historyEntryAction)) {
            writeResult(response, ERROR_MESSAGE_MANDATORY);
            return;
        }

        try {
            HistoryEntry historyEntry = this.getHistoryEntry(request, response, historyEntryAction);
            ExecutionResult executionResult = aecuService.execute(aecuScriptPath);
            aecuService.storeExecutionInHistory(historyEntry, executionResult);
            this.finishHistoryEntry(historyEntry, historyEntryAction);
            writeResult(response, this.prepareJson(executionResult.isSuccess(), historyEntry.getRepositoryPath()));

        } catch (AecuException e) {
            this.sendInternalServerError(response);
        }

    }

    protected HistoryEntry getHistoryEntry(SlingHttpServletRequest request, SlingHttpServletResponse response,
            String historyEntryAction) throws AecuException, IOException {

        HistoryEntry historyEntry;

        switch (historyEntryAction.toLowerCase()) {
            case "use":
            case "close":
                // Used for "use" and "close"
                String historyEntryPath = request.getParameter("historyEntryPath");
                if (!this.validateParameter(historyEntryPath)) {
                    writeResult(response, ERROR_MESSAGE_MANDATORY);
                    return null;
                }

                ResourceResolver resolver = request.getResourceResolver();
                HistoryUtil historyUtil = new HistoryUtil();
                historyEntry = historyUtil.readHistoryEntry(resolver.getResource(historyEntryPath));
                break;
            default:
                // Used for "single" and "create"
                historyEntry = aecuService.createHistoryEntry();
                break;
        }

        return historyEntry;
    }

    protected HistoryEntry finishHistoryEntry(HistoryEntry historyEntry, String historyEntryAction) throws AecuException {

        switch (historyEntryAction.toLowerCase()) {
            case "single":
            case "close":
                // Used for "single" and "close"
                aecuService.finishHistoryEntry(historyEntry);
                break;
        }

        return historyEntry;

    }

    /**
     * This method builds the JSON String for the response. Eg: {"success":
     * true,"historyEntryPath":"/var/aecu/2018/6/13/152892696338961314"}
     *
     * @param status           success or fail
     * @param historyEntryPath path to history node
     * @return json String
     */
    protected String prepareJson(boolean status, String historyEntryPath) {
        JsonObject json = new JsonObject();
        json.addProperty("success", status);
        json.addProperty("historyEntryPath", historyEntryPath);
        return json.toString();
    }

}
