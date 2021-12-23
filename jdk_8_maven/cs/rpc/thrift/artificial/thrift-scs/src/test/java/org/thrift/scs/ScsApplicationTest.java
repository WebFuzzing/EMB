package org.thrift.scs;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thrift.scs.client.ScsService;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * created by manzhang on 2021/10/23
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ScsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScsApplicationTest {

    @Autowired
    protected TProtocolFactory protocolFactory;

    @Value("${local.server.port}")
    int port;

    protected ScsService.Client client;

    @BeforeEach
    public void setUp() throws Exception {
        TTransport transport = new THttpClient("http://localhost:"+port+"/scs");

        TProtocol protocol = protocolFactory.getProtocol(transport);

        client = new ScsService.Client(protocol);
    }

    @Test
    public void testCalc() throws Exception {
        String value = client.calc("plus", 1, 2);
        assertEquals("3.0", value);
    }

}
