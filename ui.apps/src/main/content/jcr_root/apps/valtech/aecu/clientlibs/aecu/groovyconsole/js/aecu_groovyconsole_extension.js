$(document).ready(function () {

    if ($("#bindings").length != 0 && $("#bindings ul li.aecu-binding").length == 0) {
        // TODO url to some public available javadoc
        $("#bindings ul").append("<li class='aecu-binding'>aecu - de.valtech.aecu.core.groovy.console.bindings.SimpleContentUpdate</li>");
    }

    if ($("#imports").length != 0 && $("#bindings ul li.aecu-import").length == 0) {
        // TODO url to some public available javadoc
        $("#imports ul").append("<li class='aecu-import'>de.valtech.aecu.core.groovy.console.bindings.filters</li>");
    }

});