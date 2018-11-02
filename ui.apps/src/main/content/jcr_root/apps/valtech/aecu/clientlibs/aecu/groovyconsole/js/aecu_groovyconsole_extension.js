$(document).ready(function () {

    if ($("#bindings").length != 0 && $("#bindings ul li:contains(\"aecu\")").length == 0) {
        $("#bindings ul").append(
        		"<li class='aecu-binding'>"
        		+ "<a target=\"_blank\" href=\"https://github.com/valtech/aem-easy-content-upgrade\">aecu</a> - "
        		+ "<a target=\"_blank\" href=\"https://valtech.github.io/aem-easy-content-upgrade/\">de.valtech.aecu.core.groovy.console.bindings.SimpleContentUpdate</a>"
        		+ "</li>"
        );
    }

    if ($("#imports").length != 0 && $("#imports ul li:contains(\"aecu\")").length == 0) {
        $("#imports ul").append("<li class='aecu-import'><a target=\"_blank\" href=\"https://valtech.github.io/aem-easy-content-upgrade/\">de.valtech.aecu.api.groovy.console.bindings.filters</a></li>");
    }

});