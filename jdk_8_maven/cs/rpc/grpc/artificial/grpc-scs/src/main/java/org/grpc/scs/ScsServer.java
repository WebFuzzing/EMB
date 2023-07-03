package org.grpc.scs;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * created by manzhang on 2021/10/23
 *
 * based on https://github.com/grpc/grpc-java/blob/master/examples/src/main/java/io/grpc/examples/routeguide/RouteGuideServer.java
 * also check https://denuwanhimangahettiarachchi.medium.com/interservice-communication-using-grpc-in-spring-boot-microservice-ecosystem-57e3047f27d
 */
public class ScsServer {

    private static final Logger logger = Logger.getLogger(ScsServer.class.getName());

    private final int port;
    private final Server server;

    public ScsServer(int port) {
        this(ServerBuilder.forPort(port), port);
    }

    public ScsServer(ServerBuilder<?> serverBuilder, int port) {
        this.port = port;
        server = serverBuilder.addService(new ScsServiceImplBaseImpl())
                .build();
    }

    public void start() throws IOException {
        server.start();
        logger.info("ScsServer started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    ScsServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        int sutPort = 8980;
        if (args.length == 1){
            try{
                sutPort = Integer.parseInt(args[0]);
            }catch (Exception NumberFormatException){

            }
        }

        ScsServer server = new ScsServer(sutPort);
        server.start();
        server.blockUntilShutdown();
    }
}
