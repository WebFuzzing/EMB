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
package org.devgateway.toolkit.persistence.spring;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.derby.drda.NetworkServerControl;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * @author mpostelnicu
 *
 */
@Configuration
@EnableJpaAuditing
@PropertySource("classpath:/org/devgateway/toolkit/persistence/application.properties")
@Profile("!integration")
public class DatabaseConfiguration {

    @Value("${spring.datasource.username}")
    private String springDatasourceUsername;

    @Value("${spring.datasource.password}")
    private String springDatasourcePassword;

    @Value("${spring.datasource.url}")
    private String springDatasourceUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String springDatasourceDriverClassName;

    @Value("${spring.datasource.transaction-isolation}")
    private int springDatasourceTransactionIsolation;

    @Value("${spring.datasource.initial-size}")
    private int springDatasourceInitialSize;

    @Value("${spring.datasource.max-active}")
    private int springDatasourceMaxActive;

    @Value("${dg-toolkit.derby.port}")
    private int derbyPort;

    @Value("${dg-toolkit.datasource.jndi-name}")
    private String datasourceJndiName;

    protected static Logger logger = Logger.getLogger(DatabaseConfiguration.class);

    /**
     * This bean creates the JNDI tree and registers the
     * {@link javax.sql.DataSource} to this tree. This allows Pentaho Classic
     * Engine to use a {@link javax.sql.DataSource} ,in our case backed by a
     * connection pool instead of always opening up JDBC connections. Should
     * significantly improve performance of all classic reports. In PRD use
     * connection type=JNDI and name toolkitDS. To use it in PRD you need to add
     * the configuration to the local PRD. Edit
     * ~/.pentaho/simple-jndi/default.properties and add the following:
     * toolkitDS/type=javax.sql.DataSource
     * toolkitDS/driver=org.apache.derby.jdbc.ClientDriver toolkitDS/user=app
     * toolkitDS/password=app
     * toolkitDS/url=jdbc:derby://localhost//derby/toolkit
     * 
     * @return
     */
    @Bean
    public SimpleNamingContextBuilder jndiBuilder() {
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.bind(datasourceJndiName, dataSource());
        try {
            builder.activate();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return builder;
    }

    /**
     * Creates a {@link javax.sql.DataSource} based on Tomcat {@link DataSource}
     * 
     * @return
     */
    @Bean
    @DependsOn(value = { "derbyServer" })
    public DataSource dataSource() {
        PoolProperties pp = new PoolProperties();
        pp.setJmxEnabled(true);
        pp.setDefaultTransactionIsolation(springDatasourceTransactionIsolation);
        pp.setInitialSize(springDatasourceInitialSize);
        pp.setMaxActive(springDatasourceMaxActive);

        DataSource dataSource = new DataSource(pp);

        dataSource.setUrl(springDatasourceUrl);
        dataSource.setUsername(springDatasourceUsername);
        dataSource.setPassword(springDatasourcePassword);
        dataSource.setDriverClassName(springDatasourceDriverClassName);
        return dataSource;
    }

    /**
     * Graciously starts a Derby Database Server when the application starts up
     * 
     * @return
     * @throws Exception
     */
    @Bean(destroyMethod = "shutdown")
    public NetworkServerControl derbyServer() throws Exception {
        Properties p = System.getProperties();
        p.put("derby.storage.pageCacheSize", "30000");
        p.put("derby.language.maxMemoryPerTable", "20000");
        NetworkServerControl nsc = new NetworkServerControl(InetAddress.getByName("localhost"), derbyPort);
        nsc.start(new PrintWriter(java.lang.System.out, true));
        return nsc;
    }

}
