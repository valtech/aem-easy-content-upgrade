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

(function ($, ns, channel, window) {
    "use strict";

    AECU.History = {};

    /**
     * Select the accordion item based on the GET parameter.
     */
    AECU.History.selectAccordion = function () {
        var search = window.location.search;
        if (search && search.includes('aecuScriptPath')) {
            search = search.substring(1);
            var params = search.split('&');
            for (var i = 0; i < params.length; i++) {
                var param = params[i];
                var parts = param.split('=');

                if (parts[0] == 'aecuScriptPath') {
                    var name = parts[1];
                    var item = $("coral-accordion-item[data-path='" + name + "']");
                    item[0].selected = true;
                }
            }
        }
    };

    /**
     * Initial actions
     */
    Coral.commons.ready(function () {
        AECU.History.selectAccordion();
    });

})(window.jQuery, Granite.author, jQuery(document), this);