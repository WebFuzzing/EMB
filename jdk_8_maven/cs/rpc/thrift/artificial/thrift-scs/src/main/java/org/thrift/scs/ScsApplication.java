package org.thrift.scs;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thrift.scs.client.ScsService;
import org.thrift.scs.service.ScsServiceImpl;


/**
 * created by manzhang on 2021/10/23
 */
@Configuration
@SpringBootApplication
public class ScsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScsApplication.class, args);
    }

    @Bean
    public TProtocolFactory tProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }

    @Bean
    public ServletRegistrationBean ncsServlet(TProtocolFactory protocolFactory, ScsServiceImpl service) {
        TServlet tServlet =  new TServlet(new ScsService.Processor<>(service), protocolFactory);
        return new ServletRegistrationBean(tServlet, "/scs");
    }
}
