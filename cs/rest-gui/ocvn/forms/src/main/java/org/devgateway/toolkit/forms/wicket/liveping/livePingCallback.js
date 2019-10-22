/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the MIT License (MIT) which accompanies this
 * distribution, and is available at https://opensource.org/licenses/MIT
 * 
 * Contributors: Development Gateway - initial API and implementation
 ******************************************************************************/
// pings the wicket behavior every 60 seconds, only when the client is active
// and not idle
ifvisible.onEvery(30, function () {
    Wicket.Ajax.get({
        u : '${callbackUrl}&${args}'
    });
});