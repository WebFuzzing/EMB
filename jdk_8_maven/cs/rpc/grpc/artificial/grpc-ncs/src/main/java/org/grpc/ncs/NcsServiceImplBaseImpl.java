package org.grpc.ncs;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.grpc.ncs.generated.*;
import org.grpc.ncs.imp.*;

/**
 * created by manzhang on 2021/10/22
 *
 * grpc status https://grpc.github.io/grpc/core/md_doc_statuscodes.html
 */
public class NcsServiceImplBaseImpl extends NcsServiceGrpc.NcsServiceImplBase {
    @Override
    public void checkTriangle(TriangleRequest request, StreamObserver<DtoResponse> responseObserver) {
        int result = TriangleClassification.classify(request.getA(), request.getB(), request.getC());
        DtoResponse response = DtoResponse.newBuilder().setResultAsInt(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void bessj(BessjRequest request, StreamObserver<DtoResponse> responseObserver) {
        if (request.getN() <=2 || request.getN() >= 1000){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }else{
            double result = (new Bessj()).bessj(request.getN(), request.getX());
            DtoResponse response = DtoResponse.newBuilder().setResultAsDouble(result).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void expint(ExpintRequest request, StreamObserver<DtoResponse> responseObserver) {
        try {
            double result = Expint.exe(request.getN(), request.getX());
            DtoResponse response = DtoResponse.newBuilder().setResultAsDouble(result).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch (RuntimeException e){
            responseObserver.onError(e);
        }
    }

    @Override
    public void fisher(FisherRequest request, StreamObserver<DtoResponse> responseObserver) {

        if(request.getM() > 1000 || request.getN() > 1000){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
            return;
        }

        try{
            double result = Fisher.exe(request.getM(), request.getN(), request.getX());
            DtoResponse response = DtoResponse.newBuilder().setResultAsDouble(result).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch (RuntimeException e){
            responseObserver.onError(e);
        }


    }

    @Override
    public void gammq(GammqRequest request, StreamObserver<DtoResponse> responseObserver) {
        try{
            double result = (new Gammq()).exe(request.getA(), request.getX());
            DtoResponse response = DtoResponse.newBuilder().setResultAsDouble(result).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch (RuntimeException e){
            responseObserver.onError(e);
        }
    }

    @Override
    public void remainder(RemainderRequest request, StreamObserver<DtoResponse> responseObserver) {
        int lim = 10_000;
        if(request.getA() > lim || request.getA() < -lim || request.getB() > lim || request.getB() < -lim){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
            return;
        }

        int result = Remainder.exe(request.getA(), request.getB());
        DtoResponse response = DtoResponse.newBuilder().setResultAsInt(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
