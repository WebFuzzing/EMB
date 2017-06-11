package org.javiermf.features;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {

        String dbPort = System.getProperty("h2.tcp.dbport");
        if(dbPort != null) {
            Server.createTcpServer("-tcp","-tcpAllowOthers","-tcpPort",dbPort).start();
        }

        SpringApplication.run(Application.class, args);
    }

}
