package org.thrift.ncs;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thrift.ncs.client.NcsService;
import org.thrift.ncs.client.Dto;

/**
 * created by manzhang on 2021/10/21
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = NcsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NcsApplicationTest {

    @Autowired
    protected TProtocolFactory protocolFactory;

    @Value("${local.server.port}")
    int port;

    protected NcsService.Client client;

    @BeforeEach
    public void setUp() throws Exception {
        TTransport transport = new THttpClient("http://localhost:"+port+"/ncs");

        TProtocol protocol = protocolFactory.getProtocol(transport);

        client = new NcsService.Client(protocol);
    }

    @Test
    public void testTriangle() throws Exception {
        Dto dto = client.checkTriangle(3, 4,5);
        assertEquals(1, dto.resultAsInt);
    }

}
