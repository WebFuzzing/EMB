package org.grpc.ncs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.grpc.scs.ScsServer;
import org.grpc.scs.generated.CalcRequest;
import org.grpc.scs.generated.DtoResponse;
import org.grpc.scs.generated.ScsServiceGrpc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * created by manzhang on 2021/10/23
 */
public class ScsServerTest {

    ScsServer server;

    final int port = 9090;

    ManagedChannel channel;
    ScsServiceGrpc.ScsServiceBlockingStub stub;


    @BeforeEach
    public void start() throws IOException {
        server = new ScsServer(port);
        server.start();

        channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build();
        stub = ScsServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    public void stop() throws InterruptedException {
        channel.shutdown();
        server.stop();
    }

    @Test
    public void testCalc(){
        DtoResponse dto = stub.calc(CalcRequest.newBuilder().setOp("plus").setArg1(1).setArg2(2).build());
        assertEquals("3.0", dto.getValue());
    }


}
