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

AECU.Executor.doGET = function (getProps) {
    /* ADD HERE YOUR COMMON EXECUTOR AJAX PROPERTIES AND
    MERGE THEM WITH getPros */
    var props = {
        async: true
    }
    var executorGetProps = $.extend({}, props, getProps);
    AECU.RequestHandler.GET(executorGetProps);
}

AECU.Executor.executeAll = function(tableRows, historyEntryAction, historyEntryPath){
    if(tableRows.length == 0) return;

    if(tableRows.length == 1) historyEntryAction = AECU.Constants.Executor.HistoryEntryActions.close;

    AECU.Executor.execute(tableRows[0],historyEntryAction,historyEntryPath).then(function success(newhistoryEntryPath){
        AECU.Executor.executeAll(tableRows.slice(1), AECU.Constants.Executor.HistoryEntryActions.use, newhistoryEntryPath);
    }, function error(){
        AECU.Executor.changeRowStatus(tableRows,AECU.Constants.Executor.Status.internalError);
    });
}

AECU.Executor.executeAllSkip = 'false';

AECU.Executor.execute = function (row, historyEntryAction, historyEntryPath) {
    var deferred = $.Deferred()
    this.doGET({
       url: AECU.Constants.Executor.servletPath.format(row.dataset.aecuExecuteScript, historyEntryAction, historyEntryPath, AECU.Executor.executeAllSkip),
       beforeSend: function () {
           AECU.Executor.changeRowStatus(row, AECU.Constants.Executor.Status.inProgress);
           AECU.Executor.disableButton(row);
           AECU.Executor.disableExecuteAllButton();
       },
       success: function (json) {
           AECU.Executor.addHistoryLink(row, json.historyEntryPath);
           if (json.state == 'SUCCESS') {
               AECU.Executor.changeRowStatus(row, AECU.Constants.Executor.Status.executed);
           } else if (json.state == 'SKIPPED') {
        	   AECU.Executor.changeRowStatus(row, AECU.Constants.Executor.Status.skip);
           } else {
               AECU.Executor.changeRowStatus(row, AECU.Constants.Executor.Status.fail);
               AECU.Executor.executeAllSkip = 'true';
               if(json.fallbackState){
                   AECU.Executor.addFallbackText(row);
               }
           }
           deferred.resolve(json.historyEntryPath);
       },
       error: function (jqXHR, textStatus, errorThrown) {
           AECU.Executor.changeRowStatus(row, AECU.Constants.Executor.Status.internalError);
           deferred.reject();
       }
   });
    return deferred.promise();
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
    });
    iconTags.removeClass('icon-color-inprogress');
    iconTags.addClass(className);
}

AECU.Executor.changeScriptColor = function (items, value) {
    var className = value.className;
    items.removeClass('icon-color-inprogress');
    items.addClass(className);
}

AECU.Executor.addFallbackText = function (row){ 
    $(row).find("[data-aecu-execute-script-path]").append(AECU.Constants.Executor.Status.fallback.element);
}

AECU.Executor.disableButton = function (row) {
    $(row).find("[data-aecu-execute-script-button]").prop('disabled', true);
}

AECU.Executor.disableExecuteAllButton = function () {
    $("#aecu-execute-button-all").prop('disabled', true);
}

AECU.Executor.disableAllButtons = function (row) {
    AECU.Executor.disableExecuteAllButton();
    AECU.Executor.disableButton($('[data-aecu-execute-script]'));
}

$(document).ready(function () {

    /* Disable executeAll button is there is one or no scripts displayed. */
    var tableRows = $('[data-aecu-execute-script]');
    if (tableRows.length == 0 || tableRows.length == 1) {
        AECU.Executor.disableExecuteAllButton();
    }

    /* Event for executing all scrips displayed in screen. */
    $("#aecu-execute-button-all").on('click', function () {
        var tableRows = $('[data-aecu-execute-script]');
        if (tableRows.length > 0) {
            AECU.Executor.disableAllButtons()
            AECU.Executor.changeAllStatus(AECU.Constants.Executor.Status.pending);
            AECU.Executor.executeAllSkip = 'false';
            AECU.Executor.executeAll(tableRows, AECU.Constants.Executor.HistoryEntryActions.create);
        }
    });

    /* Event for each row (script) displayed in screen. */
    $('[data-aecu-execute-script-button]').on('click', function (event) {
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
                        setTimeout(function() {button.find('[tabindex="-1"]').click();}, 300);
                    }        			
        		});
        	}
        });
    }

});