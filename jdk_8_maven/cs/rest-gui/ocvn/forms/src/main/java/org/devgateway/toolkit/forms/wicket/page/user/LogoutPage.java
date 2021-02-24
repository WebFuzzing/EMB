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
 * 
 */
package org.devgateway.toolkit.forms.wicket.page.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.web.authentication.RememberMeServices;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author mpostelnicu
 *
 */
@MountPath("/preLogout")
public class LogoutPage extends RedirectPage {
    private static final long serialVersionUID = 1L;

    @SpringBean(required = false)
    private RememberMeServices rememberMeServices;

    public LogoutPage() {
        super("/logout");

        if (rememberMeServices != null) {
            rememberMeServices.loginFail((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest(),
                    (HttpServletResponse) RequestCycle.get().getResponse().getContainerResponse());
        }

    }
}
