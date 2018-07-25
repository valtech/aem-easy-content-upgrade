$(document).ready(function () {

    if ($("#bindings").length != 0 && $("#bindings ul li.aecu-binding").length == 0) {
        $("#bindings ul").append("<li class='aecu-binding'>aecu - <a target=\"_blank\" href=\"https://github.com/valtech/aem-easy-content-upgrade\">de.valtech.aecu.core.groovy.console.bindings.SimpleContentUpdate</a></li>");
    }

    if ($("#imports").length != 0 && $("#bindings ul li.aecu-import").length == 0) {
        $("#imports ul").append("<li class='aecu-import'><a target=\"_blank\" href=\"https://valtech.github.io/aem-easy-content-upgrade/\">de.valtech.aecu.core.groovy.console.bindings.filters</a></li>");
    }

});