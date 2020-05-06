/*
 * Copyright 2018 - 2020 Valtech GmbH
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

import com.google.gson.JsonObject;

import de.valtech.aecu.api.service.AecuException;
import de.valtech.aecu.api.service.AecuService;
import de.valtech.aecu.api.service.ExecutionResult;
import de.valtech.aecu.api.service.ExecutionState;
import de.valtech.aecu.api.service.HistoryEntry;
import de.valtech.aecu.core.history.HistoryUtil;
import de.valtech.aecu.core.security.AccessValidationService;

/**
 * @author Bryan Chavez
 */

@Component(immediate = true, service = {Servlet.class}, property = {"sling.servlet.paths=/bin/public/valtech/aecu/execute",
        "sling.servlet.extensions=json", "sling.servlet.methods=GET"})
public class ExecutionServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    protected static final String ERROR_MESSAGE_MANDATORY =
            "ExecutionServlet :: Make sure your are sending the correct parameters.";

    @Reference
    private transient AecuService aecuService;

    @Reference
    private transient HistoryUtil historyUtil;

    @Reference
    private transient AccessValidationService accessValidationService;


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        if (!accessValidationService.canExecute(request)) {
            return;
        }

        this.setNoCache(response);

        String historyEntryAction = request.getParameter("historyEntryAction");
        String aecuScriptPath = request.getParameter("aecuScriptPath");
        String skipExecution = request.getParameter("skipExecution");
        boolean skip = "true".equals(skipExecution);
        if (!this.validateParameter(aecuScriptPath) || !this.validateParameter(historyEntryAction)) {
            writeResult(response, ERROR_MESSAGE_MANDATORY);
            return;
        }

        try {
            HistoryEntry historyEntry = this.getHistoryEntry(request, response, historyEntryAction);
            ExecutionResult executionResult;
            if (skip) {
                executionResult = new ExecutionResult(ExecutionState.SKIPPED, null, null, null, null, aecuScriptPath);
            } else {
                executionResult = aecuService.execute(aecuScriptPath);
            }
            aecuService.storeExecutionInHistory(historyEntry, executionResult);
            this.finishHistoryEntry(historyEntry, historyEntryAction);
            writeResult(response, this.prepareJson(executionResult, historyEntry.getRepositoryPath()));

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
        String action = historyEntryAction.toLowerCase();
        if ("single".equals(action) || "close".equals(action)) {
            aecuService.finishHistoryEntry(historyEntry);
        }
        return historyEntry;
    }

    /**
     * This method builds the JSON String for the response. Eg: {"success":
     * true,"historyEntryPath":"/var/aecu/2018/6/13/152892696338961314"}
     *
     * @param executionResult  result
     * @param historyEntryPath path to history node
     * @return json String
     */
    protected String prepareJson(ExecutionResult executionResult, String historyEntryPath) {
        JsonObject json = new JsonObject();
        json.addProperty("state", executionResult.getState().name());
        json.addProperty("historyEntryPath", historyEntryPath);
        ExecutionResult fallbackExecutionResult = executionResult.getFallbackResult();
        if (fallbackExecutionResult != null) {
            json.addProperty("fallbackState", fallbackExecutionResult.getState().name());
        }
        return json.toString();
    }

}
