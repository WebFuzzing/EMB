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
package org.devgateway.toolkit.persistence.spring;

import java.util.HashSet;
import java.util.Set;

import org.devgateway.toolkit.persistence.dao.Person;
import org.devgateway.toolkit.persistence.dao.categories.Role;
import org.devgateway.toolkit.persistence.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * {@link UserDetailsService} that uses JPA
 * 
 * @author mpostelnicu, krams
 * @see http
 *      ://krams915.blogspot.fi/2012/01/spring-security-31-implement_3065.html
 */
@Component
public class CustomJPAUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonRepository personRepository;

    /**
     * Returns a populated {@link UserDetails} object. The username is first
     * retrieved from the database and then mapped to a {@link UserDetails}
     * object. We are currently using the {@link User} implementation from
     * Spring
     */
    @Override
    public Person loadUserByUsername(final String username) throws UsernameNotFoundException {
        try {
            Person domainUser = personRepository.findByUsername(username);

            Set<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(domainUser);
            domainUser.setAuthorities(grantedAuthorities);
            return domainUser;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads {@link PersistedAuthority} objects from the
     * {@link org.devgateway.eudevfin.auth.common.domain.PersistedUser#getPersistedAuthorities()}
     * and also from the {@link PersistedUserGroup#getPersistedAuthorities()}
     * (only if the {@link User} belongs to only one {@link PersistedUserGroup})
     * and converts all {@link PersistedAuthority} objects to
     * {@link GrantedAuthority}.
     * 
     * @param domainUser
     * @return a {@link Set} containing the {@link GrantedAuthority}S
     */
    public static Set<GrantedAuthority> getGrantedAuthorities(final Person domainUser) {

        Set<GrantedAuthority> grantedAuth = new HashSet<GrantedAuthority>();

        // get user authorities
        for (Role authority : domainUser.getRoles()) {
            grantedAuth.add(new SimpleGrantedAuthority(authority.getAuthority()));
        }

        return grantedAuth;
    }
}
