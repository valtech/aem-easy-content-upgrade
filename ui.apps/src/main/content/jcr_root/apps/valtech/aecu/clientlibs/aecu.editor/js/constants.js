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

AECU.Constants = {};

AECU.Constants.Executor = {
    servletPath : "/bin/public/valtech/aecu/execute.json?aecuScriptPath={0}&historyEntryAction={1}&historyEntryPath={2}",
    historyPath : "/apps/valtech/aecu/tools/history/details.html?entry={0}&aecuScriptPath={1}"
}

AECU.Constants.Executor.Status = {
    ready : {icon: "helpCircle", className: ""},
    inProgress : {icon: "playCircle", className: "icon-color-inprogress"},
    fail : {icon: "closeCircle", className: "icon-color-fail"},
    pending : {icon: "pending", className: "icon-color-inprogress"},
    executed: {icon: "checkCircle", className: "icon-color-ok"},
    internalError: {icon: "sentimentNegative", className: "icon-color-fail"}
}

AECU.Constants.Executor.HistoryEntryActions = {
    single : "single",
    create : "create",
    use : "use",
    close : "close"
}