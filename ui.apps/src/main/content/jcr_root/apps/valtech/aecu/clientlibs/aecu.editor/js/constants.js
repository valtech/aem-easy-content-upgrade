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

AECU.Constants = {};

AECU.Constants.Executor = {
    servletPath: "/bin/public/valtech/aecu/execute.json?aecuScriptPath={0}&historyEntryAction={1}&historyEntryPath={2}",
    historyPath: "/apps/valtech/aecu/tools/history/details.html?entry={0}&aecuScriptPath={1}"
}

AECU.Constants.Executor.Status = {
    ready: {
        icon: "helpCircle",
        className: "",
        text: "Ready to run"
    },
    inProgress: {
        icon: "playCircle",
        className: "icon-color-inprogress",
        text: "In progress"
    },
    fail: {
        icon: "closeCircle",
        className: "icon-color-fail",
        text: "Failed"
    },
    pending: {
        icon: "pending",
        className: "icon-color-inprogress",
        text: "Pending"
    },
    executed: {
        icon: "checkCircle",
        className: "icon-color-ok",
        text: "Ok"
    },
    internalError: {
        icon: "sentimentNegative",
        className: "icon-color-fail",
        text: "Internal error"
    },
    fallback: {
        element: '&nbsp<span class="icon-color-ok">(Fallback executed)</span>',
    }
}

AECU.Constants.Executor.HistoryEntryActions = {
    single: "single",
    create: "create",
    use: "use",
    close: "close"
}