package org.grpc.ncs.generated;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.41.0)",
    comments = "Source: ncs.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class NcsServiceGrpc {

  private NcsServiceGrpc() {}

  public static final String SERVICE_NAME = "NcsService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<TriangleRequest,
      DtoRequest> getCheckTriangleMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "checkTriangle",
      requestType = TriangleRequest.class,
      responseType = DtoRequest.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<TriangleRequest,
      DtoRequest> getCheckTriangleMethod() {
    io.grpc.MethodDescriptor<TriangleRequest, DtoRequest> getCheckTriangleMethod;
    if ((getCheckTriangleMethod = NcsServiceGrpc.getCheckTriangleMethod) == null) {
      synchronized (NcsServiceGrpc.class) {
        if ((getCheckTriangleMethod = NcsServiceGrpc.getCheckTriangleMethod) == null) {
          NcsServiceGrpc.getCheckTriangleMethod = getCheckTriangleMethod =
              io.grpc.MethodDescriptor.<TriangleRequest, DtoRequest>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "checkTriangle"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TriangleRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DtoRequest.getDefaultInstance()))
              .setSchemaDescriptor(new NcsServiceMethodDescriptorSupplier("checkTriangle"))
              .build();
        }
      }
    }
    return getCheckTriangleMethod;
  }

  private static volatile io.grpc.MethodDescriptor<BessjRequest,
      DtoRequest> getBessjMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "bessj",
      requestType = BessjRequest.class,
      responseType = DtoRequest.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<BessjRequest,
      DtoRequest> getBessjMethod() {
    io.grpc.MethodDescriptor<BessjRequest, DtoRequest> getBessjMethod;
    if ((getBessjMethod = NcsServiceGrpc.getBessjMethod) == null) {
      synchronized (NcsServiceGrpc.class) {
        if ((getBessjMethod = NcsServiceGrpc.getBessjMethod) == null) {
          NcsServiceGrpc.getBessjMethod = getBessjMethod =
              io.grpc.MethodDescriptor.<BessjRequest, DtoRequest>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "bessj"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  BessjRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DtoRequest.getDefaultInstance()))
              .setSchemaDescriptor(new NcsServiceMethodDescriptorSupplier("bessj"))
              .build();
        }
      }
    }
    return getBessjMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ExpintRequest,
      DtoRequest> getExpintMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "expint",
      requestType = ExpintRequest.class,
      responseType = DtoRequest.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ExpintRequest,
      DtoRequest> getExpintMethod() {
    io.grpc.MethodDescriptor<ExpintRequest, DtoRequest> getExpintMethod;
    if ((getExpintMethod = NcsServiceGrpc.getExpintMethod) == null) {
      synchronized (NcsServiceGrpc.class) {
        if ((getExpintMethod = NcsServiceGrpc.getExpintMethod) == null) {
          NcsServiceGrpc.getExpintMethod = getExpintMethod =
              io.grpc.MethodDescriptor.<ExpintRequest, DtoRequest>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "expint"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ExpintRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DtoRequest.getDefaultInstance()))
              .setSchemaDescriptor(new NcsServiceMethodDescriptorSupplier("expint"))
              .build();
        }
      }
    }
    return getExpintMethod;
  }

  private static volatile io.grpc.MethodDescriptor<FisherRequest,
      DtoRequest> getFisherMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "fisher",
      requestType = FisherRequest.class,
      responseType = DtoRequest.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<FisherRequest,
      DtoRequest> getFisherMethod() {
    io.grpc.MethodDescriptor<FisherRequest, DtoRequest> getFisherMethod;
    if ((getFisherMethod = NcsServiceGrpc.getFisherMethod) == null) {
      synchronized (NcsServiceGrpc.class) {
        if ((getFisherMethod = NcsServiceGrpc.getFisherMethod) == null) {
          NcsServiceGrpc.getFisherMethod = getFisherMethod =
              io.grpc.MethodDescriptor.<FisherRequest, DtoRequest>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "fisher"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FisherRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DtoRequest.getDefaultInstance()))
              .setSchemaDescriptor(new NcsServiceMethodDescriptorSupplier("fisher"))
              .build();
        }
      }
    }
    return getFisherMethod;
  }

  private static volatile io.grpc.MethodDescriptor<GammqRequest,
      DtoRequest> getGammqMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "gammq",
      requestType = GammqRequest.class,
      responseType = DtoRequest.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<GammqRequest,
      DtoRequest> getGammqMethod() {
    io.grpc.MethodDescriptor<GammqRequest, DtoRequest> getGammqMethod;
    if ((getGammqMethod = NcsServiceGrpc.getGammqMethod) == null) {
      synchronized (NcsServiceGrpc.class) {
        if ((getGammqMethod = NcsServiceGrpc.getGammqMethod) == null) {
          NcsServiceGrpc.getGammqMethod = getGammqMethod =
              io.grpc.MethodDescriptor.<GammqRequest, DtoRequest>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "gammq"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  GammqRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DtoRequest.getDefaultInstance()))
              .setSchemaDescriptor(new NcsServiceMethodDescriptorSupplier("gammq"))
              .build();
        }
      }
    }
    return getGammqMethod;
  }

  private static volatile io.grpc.MethodDescriptor<RemainderRequest,
      DtoRequest> getRemainderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "remainder",
      requestType = RemainderRequest.class,
      responseType = DtoRequest.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<RemainderRequest,
      DtoRequest> getRemainderMethod() {
    io.grpc.MethodDescriptor<RemainderRequest, DtoRequest> getRemainderMethod;
    if ((getRemainderMethod = NcsServiceGrpc.getRemainderMethod) == null) {
      synchronized (NcsServiceGrpc.class) {
        if ((getRemainderMethod = NcsServiceGrpc.getRemainderMethod) == null) {
          NcsServiceGrpc.getRemainderMethod = getRemainderMethod =
              io.grpc.MethodDescriptor.<RemainderRequest, DtoRequest>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "remainder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RemainderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DtoRequest.getDefaultInstance()))
              .setSchemaDescriptor(new NcsServiceMethodDescriptorSupplier("remainder"))
              .build();
        }
      }
    }
    return getRemainderMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NcsServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NcsServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NcsServiceStub>() {
        @Override
        public NcsServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NcsServiceStub(channel, callOptions);
        }
      };
    return NcsServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NcsServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NcsServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NcsServiceBlockingStub>() {
        @Override
        public NcsServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NcsServiceBlockingStub(channel, callOptions);
        }
      };
    return NcsServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NcsServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NcsServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NcsServiceFutureStub>() {
        @Override
        public NcsServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NcsServiceFutureStub(channel, callOptions);
        }
      };
    return NcsServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class NcsServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void checkTriangle(TriangleRequest request,
                              io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckTriangleMethod(), responseObserver);
    }

    /**
     */
    public void bessj(BessjRequest request,
                      io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getBessjMethod(), responseObserver);
    }

    /**
     */
    public void expint(ExpintRequest request,
                       io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExpintMethod(), responseObserver);
    }

    /**
     */
    public void fisher(FisherRequest request,
                       io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFisherMethod(), responseObserver);
    }

    /**
     */
    public void gammq(GammqRequest request,
                      io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGammqMethod(), responseObserver);
    }

    /**
     */
    public void remainder(RemainderRequest request,
                          io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRemainderMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCheckTriangleMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                TriangleRequest,
                DtoRequest>(
                  this, METHODID_CHECK_TRIANGLE)))
          .addMethod(
            getBessjMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                BessjRequest,
                DtoRequest>(
                  this, METHODID_BESSJ)))
          .addMethod(
            getExpintMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                ExpintRequest,
                DtoRequest>(
                  this, METHODID_EXPINT)))
          .addMethod(
            getFisherMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                FisherRequest,
                DtoRequest>(
                  this, METHODID_FISHER)))
          .addMethod(
            getGammqMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                GammqRequest,
                DtoRequest>(
                  this, METHODID_GAMMQ)))
          .addMethod(
            getRemainderMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                RemainderRequest,
                DtoRequest>(
                  this, METHODID_REMAINDER)))
          .build();
    }
  }

  /**
   */
  public static final class NcsServiceStub extends io.grpc.stub.AbstractAsyncStub<NcsServiceStub> {
    private NcsServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected NcsServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NcsServiceStub(channel, callOptions);
    }

    /**
     */
    public void checkTriangle(TriangleRequest request,
                              io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckTriangleMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void bessj(BessjRequest request,
                      io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getBessjMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void expint(ExpintRequest request,
                       io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExpintMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fisher(FisherRequest request,
                       io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFisherMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void gammq(GammqRequest request,
                      io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGammqMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void remainder(RemainderRequest request,
                          io.grpc.stub.StreamObserver<DtoRequest> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRemainderMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class NcsServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<NcsServiceBlockingStub> {
    private NcsServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected NcsServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NcsServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public DtoRequest checkTriangle(TriangleRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckTriangleMethod(), getCallOptions(), request);
    }

    /**
     */
    public DtoRequest bessj(BessjRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getBessjMethod(), getCallOptions(), request);
    }

    /**
     */
    public DtoRequest expint(ExpintRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExpintMethod(), getCallOptions(), request);
    }

    /**
     */
    public DtoRequest fisher(FisherRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFisherMethod(), getCallOptions(), request);
    }

    /**
     */
    public DtoRequest gammq(GammqRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGammqMethod(), getCallOptions(), request);
    }

    /**
     */
    public DtoRequest remainder(RemainderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRemainderMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class NcsServiceFutureStub extends io.grpc.stub.AbstractFutureStub<NcsServiceFutureStub> {
    private NcsServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected NcsServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NcsServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DtoRequest> checkTriangle(
        TriangleRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckTriangleMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DtoRequest> bessj(
        BessjRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getBessjMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DtoRequest> expint(
        ExpintRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExpintMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DtoRequest> fisher(
        FisherRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFisherMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DtoRequest> gammq(
        GammqRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGammqMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DtoRequest> remainder(
        RemainderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRemainderMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CHECK_TRIANGLE = 0;
  private static final int METHODID_BESSJ = 1;
  private static final int METHODID_EXPINT = 2;
  private static final int METHODID_FISHER = 3;
  private static final int METHODID_GAMMQ = 4;
  private static final int METHODID_REMAINDER = 5;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final NcsServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(NcsServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CHECK_TRIANGLE:
          serviceImpl.checkTriangle((TriangleRequest) request,
              (io.grpc.stub.StreamObserver<DtoRequest>) responseObserver);
          break;
        case METHODID_BESSJ:
          serviceImpl.bessj((BessjRequest) request,
              (io.grpc.stub.StreamObserver<DtoRequest>) responseObserver);
          break;
        case METHODID_EXPINT:
          serviceImpl.expint((ExpintRequest) request,
              (io.grpc.stub.StreamObserver<DtoRequest>) responseObserver);
          break;
        case METHODID_FISHER:
          serviceImpl.fisher((FisherRequest) request,
              (io.grpc.stub.StreamObserver<DtoRequest>) responseObserver);
          break;
        case METHODID_GAMMQ:
          serviceImpl.gammq((GammqRequest) request,
              (io.grpc.stub.StreamObserver<DtoRequest>) responseObserver);
          break;
        case METHODID_REMAINDER:
          serviceImpl.remainder((RemainderRequest) request,
              (io.grpc.stub.StreamObserver<DtoRequest>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class NcsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    NcsServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Ncs.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("NcsService");
    }
  }

  private static final class NcsServiceFileDescriptorSupplier
      extends NcsServiceBaseDescriptorSupplier {
    NcsServiceFileDescriptorSupplier() {}
  }

  private static final class NcsServiceMethodDescriptorSupplier
      extends NcsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    NcsServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (NcsServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NcsServiceFileDescriptorSupplier())
              .addMethod(getCheckTriangleMethod())
              .addMethod(getBessjMethod())
              .addMethod(getExpintMethod())
              .addMethod(getFisherMethod())
              .addMethod(getGammqMethod())
              .addMethod(getRemainderMethod())
              .build();
        }
      }
    }
    return result;
  }
}
