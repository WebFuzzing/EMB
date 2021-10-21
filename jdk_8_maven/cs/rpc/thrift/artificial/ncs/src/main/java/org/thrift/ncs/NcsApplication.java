package org.thrift.ncs;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;


/**
 * created by manzhang on 2021/10/21
 */
@Configuration
@SpringBootApplication
public class NcsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NcsApplication.class, args);
    }

    @Bean
    public TProtocolFactory tProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }

    @Bean
    public ServletRegistrationBean ncsServlet(TProtocolFactory protocolFactory, NcsServiceImpl service) {
        TServlet tServlet =  new TServlet(new NcsService.Processor<>(service), protocolFactory);
        return new ServletRegistrationBean(tServlet, "/ncs");
    }
}
