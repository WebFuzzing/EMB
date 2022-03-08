package org.grpc.ncs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.grpc.ncs.generated.DtoResponse;
import org.grpc.ncs.generated.NcsServiceGrpc;
import org.grpc.ncs.generated.TriangleRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * created by manzhang on 2021/10/23
 */
public class NcsServerTest {

    NcsServer server;

    final int port = 9090;

    ManagedChannel channel;
    NcsServiceGrpc.NcsServiceBlockingStub stub;


    @BeforeEach
    public void start() throws IOException {
        server = new NcsServer(port);
        server.start();

        channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build();
        stub = NcsServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    public void stop() throws InterruptedException {
        channel.shutdown();
        server.stop();
    }

    @Test
    public void testTriangle(){
        DtoResponse dto = stub.checkTriangle(TriangleRequest.newBuilder().setA(3).setB(4).setC(5).build());
        assertEquals(1, dto.getResultAsInt());
    }


}
