package org.grpc.ncs;

import io.grpc.stub.StreamObserver;
import org.grpc.ncs.generated.*;

/**
 * created by manzhang on 2021/10/22
 */
public class NcsServiceImplBaseImpl extends NcsServiceGrpc.NcsServiceImplBase {
    @Override
    public void checkTriangle(TriangleRequest request, StreamObserver<DtoRequest> responseObserver) {
        super.checkTriangle(request, responseObserver);
    }

    @Override
    public void bessj(BessjRequest request, StreamObserver<DtoRequest> responseObserver) {
        super.bessj(request, responseObserver);
    }

    @Override
    public void expint(ExpintRequest request, StreamObserver<DtoRequest> responseObserver) {
        super.expint(request, responseObserver);
    }

    @Override
    public void fisher(FisherRequest request, StreamObserver<DtoRequest> responseObserver) {
        super.fisher(request, responseObserver);
    }

    @Override
    public void gammq(GammqRequest request, StreamObserver<DtoRequest> responseObserver) {
        super.gammq(request, responseObserver);
    }

    @Override
    public void remainder(RemainderRequest request, StreamObserver<DtoRequest> responseObserver) {
        super.remainder(request, responseObserver);
    }
}
