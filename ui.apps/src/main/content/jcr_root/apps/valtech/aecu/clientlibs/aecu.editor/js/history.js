(function($, ns, channel, window) {
	"use strict";
	
	AECU.History = {};
	
	/**
	 * Select the accordion item based on the GET parameter.
	 */
	AECU.History.selectAccordion = function() {
		var search = window.location.search;
		if (search && search.includes('aecuScriptPath')) {
			search = search.substring(1);
			var params = search.split('&');
			for (var i = 0; i < params.length; i++) {
				var param = params[i];
				var parts = param.split('=');
				if (parts[0] == 'aecuScriptPath') {
					var name = parts[1];
					var item = jQuery("coral-accordion-item[data-path='" + name + "']");
					Coral.commons.ready(item[0], function() {
						item[0].selected = true;
					});
				}
			}
		}
	};
	
	/**
	 * Initial actions
	 */
	$(document).ready(function() {
		AECU.History.selectAccordion();
	});
	
})(jQuery, Granite.author, jQuery(document), this);
