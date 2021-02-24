/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
/**
 * block the UI until the report page is fully loaded
 */

var blockUI = function (message) {
    $.blockUI({
        message: '<h2>' + message + '</h2>',
        css: {
            width: '40%',
            left: '30%',
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff'
        }
    });
};

(function () {
    blockUI('Report generating. This may take over a minute: thank you for your patience.');

    // block UI while page is loading
    $(document).ready(function() {
        $(window).load(function() {
            setTimeout(function() {$.unblockUI();}, 90000)
        });
    });
})();
