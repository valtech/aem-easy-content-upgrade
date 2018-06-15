$(document).ready(function () {
	
	AECU.History = {};
	
	/**
	 * Select the accordion item based on the GET parameter.
	 */
	AECU.History.selectAccordion = function() {
		var search = window.location.search;
		if (search && search.includes('aecuScriptPath')) {
			search = search.substring(1);
			params = search.split('&');
			for (var i = 0; i < params.length; i++) {
				var param = params[i];
				var parts = param.split('=');
				if (parts[0] == 'aecuScriptPath') {
					var name = parts[1];
					var item = jQuery("coral-accordion-item[data-path='" + name + "']");
					item[0].selected = true;
				}
			}
		}
	};
	
	
	AECU.History.selectAccordion();
	
});
