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
package org.devgateway.toolkit.web.spring;

import org.devgateway.toolkit.persistence.repository.AdminSettingsRepository;
import org.devgateway.toolkit.persistence.spring.CustomJPAUserDetailsService;
import org.devgateway.toolkit.web.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

/**
 * @author mpostelnicu This configures the spring security for the Web project.
 *         An
 */

@Configuration
@Order(2) // this loads the security config after the forms security (if you use
// them overlayed, it must pick that one first)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@PropertySource("classpath:allowedApiEndpoints.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    protected CustomJPAUserDetailsService customJPAUserDetailsService;

    @Autowired
    protected AdminSettingsRepository adminSettingsRepository;

    @Value("${allowedApiEndpoints}")
    private String[] allowedApiEndpoints;

    @Value("${roleHierarchy}")
    private String roleHierarchyStringRepresentation;


    @Bean
    public HttpSessionSecurityContextRepository httpSessionSecurityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityContextPersistenceFilter securityContextPersistenceFilter() {

        SecurityContextPersistenceFilter securityContextPersistenceFilter = new SecurityContextPersistenceFilter(
                httpSessionSecurityContextRepository());
        return securityContextPersistenceFilter;
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/", "/home", "/v2/api-docs/**", "/swagger-ui.html**", "/webjars/**", "/images/**",
                "/configuration/**", "/swagger-resources/**", "/dashboard", "/languages/**", "/isAuthenticated",
                "/wicket/resource/**/*.ttf", "/wicket/resource/**/*.woff", "/corruption-risk",
                SecurityUtil.getDisabledApiSecurity(adminSettingsRepository) ? "/api/**" : "/",
                "/wicket/resource/**/*.woff2", "/wicket/resource/**/*.css.map"
        ).antMatchers(allowedApiEndpoints);

    }


    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .expressionHandler(webExpressionHandler()) // inject role hierarchy
                .anyRequest().authenticated().and().formLogin().
                loginPage("/login").
                permitAll().and().requestCache().and()
                .logout().permitAll().and().sessionManagement().and().csrf().disable();
        http.addFilter(securityContextPersistenceFilter());
    }

    /**
     * Instantiates {@see DefaultWebSecurityExpressionHandler} and assigns to it role hierarchy.
     *
     * @return
     */
    private SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy());
        return handler;
    }

    /**
     * Enable hierarchical roles. This bean can be used to extract all effective roles.
     */
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(roleHierarchyStringRepresentation);
        return roleHierarchy;
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        // we use standard password encoder for all passwords
        StandardPasswordEncoder spe = new StandardPasswordEncoder();
        auth.userDetailsService(customJPAUserDetailsService).passwordEncoder(spe);
    }

}
