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
package org.devgateway.toolkit.web.security;

import java.security.Principal;
import java.util.List;
import org.devgateway.toolkit.persistence.dao.AdminSettings;
import org.devgateway.toolkit.persistence.dao.Person;
import org.devgateway.toolkit.persistence.dao.categories.Role;
import org.devgateway.toolkit.persistence.repository.AdminSettingsRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {

    }

    /**
     * returns the principal object. In our case the principal should be
     * {@link Person}
     * 
     * @return the principal or null
     * @see Principal
     */
    public static Person getCurrentAuthenticatedPerson() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        final Object principal = authentication.getPrincipal();
        if (principal instanceof Person) {
            return (Person) principal;
        }
        return null;
    }


    public static Boolean getDisabledApiSecurity(AdminSettingsRepository adminSettingsRepository) {
        List<AdminSettings> all = adminSettingsRepository.findAll();
        if (all == null || all.size() == 0) {
            return false;
        }
        if (all.size() > 1) {
            throw new RuntimeException("Multiple admin settings found! Only one or zero allowed!");
        }

        if (all.get(0).getDisableApiSecurity() == null) {
            return false;
        }

        return all.get(0).getDisableApiSecurity();
    }


    /**
     * Returns true if the user has ROLE_ADMIN
     * 
     * @param p
     * @return
     */
    public static boolean isUserAdmin(final Person p) {
        if (p == null || p.getRoles() == null) {
            return false;
        }
        for (Role r : p.getRoles()) {
            if (r.getAuthority().equalsIgnoreCase(SecurityConstants.Roles.ROLE_ADMIN)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCurrentUserAdmin() {
        Person p = getCurrentAuthenticatedPerson();
        return isUserAdmin(p);
    }

}