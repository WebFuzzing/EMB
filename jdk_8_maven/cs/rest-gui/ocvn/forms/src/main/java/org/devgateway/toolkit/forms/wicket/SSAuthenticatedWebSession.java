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
package org.devgateway.toolkit.forms.wicket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;

import java.util.Collection;

/**
 * AuthenticatedWebSession implementation using Spring Security.
 *
 * Based on:
 * https://cwiki.apache.org/confluence/display/WICKET/Spring+Security+and+Wicket-auth-roles
 *
 * @author Marcin Zajączkowski, 2011-02-05
 */
public class SSAuthenticatedWebSession extends AuthenticatedWebSession {

    private static final long serialVersionUID = 7496424885650965870L;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private AuthenticationException ae;

    @SpringBean(required = false)
    private RememberMeServices rememberMeServices;

    @SpringBean
    private AuthenticationManager authenticationManager;

    @SpringBean
    private RoleHierarchy roleHierarchy;

    // @SpringBean
    // private SessionRegistry sessionRegistry;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.Session#replaceSession()
     */
    @Override
    public void replaceSession() {
        // do nothing here, this breaks spring security in wicket 6.19
    }

    private void ensureDependenciesNotNull() {
        if (authenticationManager == null) {
            throw new IllegalStateException("An authenticationManager is required.");
        }
    }

    public SSAuthenticatedWebSession(final Request request) {
        super(request);
        Injector.get().inject(this);
        ensureDependenciesNotNull();
        if (authenticationManager == null) {
            throw new IllegalStateException("Injection of AuthenticationManager failed.");
        }

    }

    public static SSAuthenticatedWebSession getSSAuthenticatedWebSession() {
        return (SSAuthenticatedWebSession) Session.get();
    }

    @Override
    public boolean authenticate(final String username, final String password) {
        boolean authenticated;
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            // SecurityContextHolder.getContext());
            authenticated = authentication.isAuthenticated();

            if (authenticated && rememberMeServices != null) {
                rememberMeServices.loginSuccess(
                        (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest(),
                        (HttpServletResponse) RequestCycle.get().getResponse().getContainerResponse(), authentication);
            }

        } catch (AuthenticationException e) {
            this.setAe(e);
            log.warn("User '{}' failed to login. Reason: {}", username, e.getMessage());
            authenticated = false;
        }
        return authenticated;
    }

    // FIXME: MZA: Modification of returning object - it would be better if
    // roles were returned
    @Override
    public Roles getRoles() {
        Roles roles = new Roles();
        getRolesIfSignedIn(roles);
        return roles;
    }

    /**
     * Gets the Spring roles and dumps them into Wicket's {@link Roles} object,
     * only if the user is signed in
     * 
     * @see {@link #isSignedIn()}
     * @see #addRolesFromAuthentication(Roles, Authentication)
     * @param roles
     */
    private void getRolesIfSignedIn(final Roles roles) {
        if (isSignedIn()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            addRolesFromAuthentication(roles, authentication);
        }
    }

    /**
     * Adds effective roles to Wicket {@link Roles} object. It does so by
     * getting authorities from {@link Authentication#getAuthorities()} and
     * building effective roles list by taking in account role hierarchy.
     *
     * @param roles
     * @param authentication
     */
    private void addRolesFromAuthentication(final Roles roles, final Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : roleHierarchy.getReachableGrantedAuthorities(authorities)) {
            roles.add(authority.getAuthority());
        }
    }

    public AuthenticationException getAe() {
        return ae;
    }

    public void setAe(final AuthenticationException ae) {
        this.ae = ae;
    }

}
