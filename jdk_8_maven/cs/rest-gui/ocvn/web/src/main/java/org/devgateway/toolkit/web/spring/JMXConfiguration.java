package org.devgateway.toolkit.web.spring;

import org.jminix.console.application.MiniConsoleApplication;
import org.jminix.console.servlet.SpringMiniConsoleServlet;
import org.jminix.server.WebSpringServerConnectionProvider;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JMXConfiguration {

    @Bean
    public WebSpringServerConnectionProvider jMiniXConnectionProvider() {
        return new WebSpringServerConnectionProvider();
    }

    @Bean
    public MiniConsoleApplication miniConsoleApplication() {
        MiniConsoleApplication mca = new MiniConsoleApplication();
        mca.setServerConnectionProvider(jMiniXConnectionProvider());
        return mca;
    }

    @Bean
    public ServletRegistrationBean jminiXServletRegistration(final MiniConsoleApplication miniConsoleApplication) {
        ServletRegistrationBean registration = new ServletRegistrationBean(new SpringMiniConsoleServlet());
        registration.addUrlMappings("/jminix/*");
        return registration;
    }
}
