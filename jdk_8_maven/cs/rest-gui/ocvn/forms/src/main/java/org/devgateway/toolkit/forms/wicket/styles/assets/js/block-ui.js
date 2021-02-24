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
 * block the UI until the page is fully loaded
 */

var blockUI = function (message) {
    $.blockUI({
        message: '<h1>' + message + '</h1>',
        css: {
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
    blockUI('Please wait...');

    // block UI while page is loading
    $(document).ready(function() {
        $(window).load(function() {
            $.unblockUI();
        });
    });
})();