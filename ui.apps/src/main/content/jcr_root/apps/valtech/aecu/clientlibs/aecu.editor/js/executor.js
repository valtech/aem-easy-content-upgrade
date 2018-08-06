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

AECU.Executor = {};

AECU.Executor.historyEntryPath;

AECU.Executor.doGET = function (getProps) {
    /* ADD HERE YOUR COMMON EXECUTOR AJAX PROPERTIES AND
    MERGE THEM WITH getPros */
    var props = {
        async: false
    }
    var executorGetProps = $.extend({}, props, getProps);
    AECU.RequestHandler.GET(executorGetProps);
}

AECU.Executor.executeAll = function (tableRows) {
    AECU.Executor.changeAllStatus(AECU.Constants.Executor.Status.pending);
    var historyEntryAction;
    for (var i = 0, length = tableRows.length; i < length; i++) {
        if (i == 0) {
            historyEntryAction = AECU.Constants.Executor.HistoryEntryActions.create;
        } else if (i == tableRows.length - 1) {
            historyEntryAction = AECU.Constants.Executor.HistoryEntryActions.close;
        } else {
            historyEntryAction = AECU.Constants.Executor.HistoryEntryActions.use;
        }
        AECU.Executor.execute(tableRows[i], historyEntryAction, AECU.Executor.historyEntryPath);
    }
    AECU.Executor.disableButton();
    AECU.Executor.historyEntryPath = undefined;
}

AECU.Executor.execute = function (row, historyEntryAction, historyEntryPath) {
    this.doGET({
                   url: AECU.Constants.Executor.servletPath.format(row.dataset.aecuExecuteScript, historyEntryAction,
                                                                   historyEntryPath),
                   beforeSend: function () {
                       AECU.Executor.changeRowStatus(row, AECU.Constants.Executor.Status.inProgress);
                       AECU.Executor.disableButton(row);
                   },
                   success: function (json) {
                       AECU.Executor.historyEntryPath = json.historyEntryPath;
                       AECU.Executor.addHistoryLink(row, json.historyEntryPath);
                       if (json.success) {
                           AECU.Executor.changeRowStatus(row, AECU.Constants.Executor.Status.executed);
                       } else {
                           AECU.Executor.changeRowStatus(row, AECU.Constants.Executor.Status.fail);
                       }
                   },
                   error: function (jqXHR, textStatus, errorThrown) {
                       AECU.Executor.changeRowStatus(row, AECU.Constants.Executor.Status.internalError);
                   }
               });
}

AECU.Executor.addHistoryLink = function (row, historyEntryPath) {
    var historyLink = $(row).find("[data-aecu-execute-script-history]")[0];
    historyLink.href = AECU.Constants.Executor.historyPath.format(historyEntryPath, row.dataset.aecuExecuteScript);
    historyLink.text = "Show details";
}

AECU.Executor.changeRowStatus = function (row, value) {
    AECU.Executor.changeStatus($(row).find("[data-aecu-execute-script-status]"), value);
    AECU.Executor.changeScriptColor($(row).find("[data-aecu-execute-script-path]"), value);
    if (value === AECU.Constants.Executor.Status.inProgress) {
    	var historyLink = $(row).find("[data-aecu-execute-script-history]")[0];
    	historyLink.text = ""
    }
}

AECU.Executor.changeAllStatus = function (value) {
    AECU.Executor.changeStatus($("[data-aecu-execute-script-status]"), value);
    AECU.Executor.changeScriptColor($("[data-aecu-execute-script-path]"), value);
}

AECU.Executor.changeStatus = function (items, value) {
    var icon = value.icon;
    var title = value.text;
    var className = value.className;
    var iconTags = items.children("coral-icon");
    iconTags.each(function () {
        this.set('icon', icon);
        this.set('title', title);
        this._syncDOM();
    });
    iconTags.removeClass('icon-color-inprogress');
    iconTags.addClass(className);
}

AECU.Executor.changeScriptColor = function (items, value) {
    var className = value.className;
    items.removeClass('icon-color-inprogress');
    items.addClass(className);
}

AECU.Executor.disableButton = function (row) {
    if (row != undefined) {
        $(row).find("[data-aecu-execute-script-button]").prop('disabled', true);
    } else {
        /* The only button not in a row. */
        $("#aecu-execute-button-all").prop('disabled', true);
    }
}

$(document).ready(function () {

    /* Disable executeAll button is there are no scripts displayed. */
    if ($('[data-aecu-execute-script]').length == 0) {
        AECU.Executor.disableButton();
    }

    /* Event for executing all scrips displayed in screen. */
    $("#aecu-execute-button-all").on('click', function (e) {
        var tableRows = $('[data-aecu-execute-script]');
        if (tableRows.length > 0) {
            AECU.Executor.executeAll(tableRows);
        }
    });

    /* Event for each row (script) displayed in screen. */
    $('[data-aecu-execute-script-button]').on('click', function (event) {
        AECU.Executor.disableButton();
        AECU.Executor.execute(
            this.closest('[data-aecu-execute-script]'),
            AECU.Constants.Executor.HistoryEntryActions.single, null);
    });

    /* open rail tab */
    var button = $('coral-cyclebutton');
    var panel =  $('coral-panel');
    if (button) {
        Coral.commons.ready(button[0], function () {
        	if (panel) {
        		Coral.commons.ready(panel[0], function () {
        			var selected = panel.attr('aria-selected');
                    if (!selected || (selected == "false")) {
                        button.find('button').click();            	
                    }        			
        		});
        	}
        });
    }

});