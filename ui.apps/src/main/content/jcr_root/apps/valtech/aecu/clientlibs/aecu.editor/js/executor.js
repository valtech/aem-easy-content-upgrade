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

AECU.Executor = {};


AECU.Executor.historyEntryPath;


AECU.Executor.doGET = function(getProps){
    /* ADD HERE YOUR COMMON EXECUTOR AJAX PROPERTIES AND
    MERGE THEM WITH getPros */
    var props = {
        async: false
    }
    var executorGetProps = $.extend({},props, getProps);
    AECU.RequestHandler.GET(executorGetProps);
}


AECU.Executor.executeAll = function(tableRows) {
    AECU.Executor.changeAllStatus(AECU.Constants.Executor.Status.pending);
    var historyEntryAction;
    for (var i = 0, length = tableRows.length; i < length; i++) {
        if(i == 0){
            historyEntryAction = AECU.Constants.Executor.HistoryEntryActions.create;
        }else if(i == tableRows.length-1){
            historyEntryAction = AECU.Constants.Executor.HistoryEntryActions.close;
        }else{
            historyEntryAction = AECU.Constants.Executor.HistoryEntryActions.use;
        }
        AECU.Executor.execute(tableRows[i],historyEntryAction,AECU.Executor.historyEntryPath);
    }

    AECU.Executor.historyEntryPath = undefined;
}


AECU.Executor.execute = function(row,historyEntryAction,historyEntryPath) {
    this.doGET({
        url : AECU.Constants.Executor.servletPath.format(row.dataset.aecuExecuteScript,historyEntryAction,historyEntryPath),
        beforeSend: function(){
            AECU.Executor.changeRowStatus(row,AECU.Constants.Executor.Status.inProgress);
            AECU.Executor.disableButton(row);
        },
        success: function( json ) {
            AECU.Executor.historyEntryPath = json.historyEntryPath;
            AECU.Executor.addHistoryLink(row,json.historyEntryPath);
            if(json.success){
                AECU.Executor.changeRowStatus(row,AECU.Constants.Executor.Status.executed);
            }else{
                AECU.Executor.changeRowStatus(row,AECU.Constants.Executor.Status.fail);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            AECU.Executor.changeRowStatus(row,AECU.Constants.Executor.Status.internalError);
        }
    });
}

AECU.Executor.addHistoryLink = function(row, historyEntryPath){
    var historyLink = $(row).find("[data-aecu-execute-script-history]")[0];
    historyLink.href = AECU.Constants.Executor.historyPath.format(historyEntryPath, row.dataset.aecuExecuteScript);
    historyLink.text = "Go to history";
}

AECU.Executor.changeRowStatus = function(row, value){
	AECU.Executor.changeStatus($(row).find("[data-aecu-execute-script-status]"), value);
}


AECU.Executor.changeAllStatus = function(value){
	AECU.Executor.changeStatus($("[data-aecu-execute-script-status]"), value);
}

AECU.Executor.changeStatus = function(items, value){
	var icon = value.icon;
	var className = value.className;
	var iconTags = items.children("coral-icon");
	iconTags.each(function() {
		this.set('icon', icon)
	});
	iconTags.removeClass('icon-color-inprogress');
	iconTags.addClass(className);
}

AECU.Executor.disableButton = function(row){
    if(row != undefined){
        $(row).find("[data-aecu-execute-script-button]").prop('disabled', true);
    }else{
        /* The only button not in a row. */
        $("#aecu-execute-button-all").prop('disabled', true);
    }
}



$(document).ready(function(){

    /* Disable executeAll button is there are no scripts displayed. */
    if($('[data-aecu-execute-script]').length == 0){
        AECU.Executor.disableButton();
    }

    /* Event for executing all scrips displayed in screen. */
    $("#aecu-execute-button-all").on('click', function(e) {
        var tableRows = $('[data-aecu-execute-script]');
        if(tableRows.length > 0){
            AECU.Executor.executeAll(tableRows);
        }
    });

    /* Event for each row (script) displayed in screen. */
    $('[data-aecu-execute-script-button]').on('click', function(event) {
        AECU.Executor.execute(
            this.closest('[data-aecu-execute-script]'),
            AECU.Constants.Executor.HistoryEntryActions.single,null);
    });

});