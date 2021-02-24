package org.devgateway.toolkit.web;

import java.util.Arrays;

import org.devgateway.toolkit.persistence.dao.Person;
import org.devgateway.toolkit.persistence.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Stub {@link UserDetailsService} used for testing purposes. Not to be used on
 * anything else!
 * 
 * @author mpostelnicu
 *
 */
@Profile({"integration", "shadow-integration"})
@Configuration
public class TestUserDetailsConfiguration {

    @Autowired
    private PersonRepository personRepository;

    @Bean("testUserDetailsAdminProcuringEntity")
    public UserDetailsService testUserDetailsAdminProcuringEntity() {

        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Person person = new Person();
                person.setUsername(username);
                person.setPassword("idontcare");
                person.setAuthorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_PROCURING_ENTITY"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")));
                return personRepository.save(person);
            }
        };
    }

}