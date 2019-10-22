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
 * file upload autosubmit form
 */
(function () {
    // listen for the onchange event
    $('body').on('change.bs.fileinput', 'input[type=file]', function (event) {
        event.stopPropagation();
        // find the file submit button and trigger 'click' event
        var fileSubmit = $(this).closest('.input-group-btn').find('.fileinput-upload-button');
        setTimeout(function() {fileSubmit.trigger( 'click' );}, 1000);

        // add loading image
        $(this).closest('.file-input').find('.file-preview').addClass('loading');

        // disable the upload button
        $(this).closest('.file-input').find('.fileinput-upload-button').prop("disabled", true);
    });
})();
