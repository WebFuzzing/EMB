/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the MIT License (MIT) which accompanies this
 * distribution, and is available at https://opensource.org/licenses/MIT
 * 
 * Contributors: Development Gateway - initial API and implementation
 ******************************************************************************/
// invoked before the window is unloaded
// we display a confirmation box, if the box is OK, we allow the user
// to leave the page and at the same time unlock editing
// browser leaves the tab
$(window).on('unload', function (e) {
    $.ajax({
        type : 'GET',
        async : false,
        timeout : 3000,
        url : '${callbackUrl}',
        data : '${args}'
    });
});
