package org.grpc.scs;

import io.grpc.stub.StreamObserver;
import org.grpc.scs.generated.*;
import org.grpc.scs.imp.*;

/**
 * created by manzhang on 2021/10/23
 */
public class ScsServiceImplBaseImpl extends ScsServiceGrpc.ScsServiceImplBase {

    @Override
    public void calc(CalcRequest request, StreamObserver<DtoResponse> responseObserver) {
        String res = Calc.subject(request.getOp(), request.getArg1(), request.getArg2());
        DtoResponse response = DtoResponse.newBuilder().setValue(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void cookie(CookieRequest request, StreamObserver<DtoResponse> responseObserver) {
        String res = Cookie.subject(request.getName(), request.getVal(), request.getSite());
        DtoResponse response = DtoResponse.newBuilder().setValue(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void costfuns(CostfunsRequest request, StreamObserver<DtoResponse> responseObserver) {
        String res = Costfuns.subject(request.getI(), request.getS());
        DtoResponse response = DtoResponse.newBuilder().setValue(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void dateParse(DateParseRequest request, StreamObserver<DtoResponse> responseObserver) {
        String res = DateParse.subject(request.getDayname(), request.getMonthname());
        DtoResponse response = DtoResponse.newBuilder().setValue(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void fileSuffix(FileSuffixRequest request, StreamObserver<DtoResponse> responseObserver) {
        String res = FileSuffix.subject(request.getDirectory(), request.getFile());
        DtoResponse response = DtoResponse.newBuilder().setValue(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void notyPevar(NotyPevarRequest request, StreamObserver<DtoResponse> responseObserver) {
        String res = NotyPevar.subject(request.getI(), request.getS());
        DtoResponse response = DtoResponse.newBuilder().setValue(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void ordered4(Ordered4Request request, StreamObserver<DtoResponse> responseObserver) {
        String res = Ordered4.subject(request.getW(), request.getX(), request.getZ(), request.getY());
        DtoResponse response = DtoResponse.newBuilder().setValue(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void pat(PatRequest request, StreamObserver<DtoResponse> responseObserver) {
        String res = Pat.subject(request.getTxt(), request.getPat());
        DtoResponse response = DtoResponse.newBuilder().setValue(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void regex(RegexRequest request, StreamObserver<DtoResponse> responseObserver) {
        String res = Regex.subject(request.getTxt());
        DtoResponse response = DtoResponse.newBuilder().setValue(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void text2txt(Text2txtRequest request, StreamObserver<DtoResponse> responseObserver) {
        String res = Text2Txt.subject(request.getWord1(), request.getWord2(), request.getWord3());
        DtoResponse response = DtoResponse.newBuilder().setValue(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void title(TitleRequest request, StreamObserver<DtoResponse> responseObserver) {
        String res = Title.subject(request.getSex(), request.getTitle());
        DtoResponse response = DtoResponse.newBuilder().setValue(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
