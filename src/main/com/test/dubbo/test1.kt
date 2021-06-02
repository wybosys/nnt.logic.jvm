package com.test.dubbo

import com.nnt.core.logger
import io.grpc.stub.StreamObserver

interface ITest1 : DubboTest1Grpc.ITest1 {

}

class Test1 : ITest1, DubboTest1Grpc.Test1ImplBase() {

    // grpc协议下必须实现该函数
    fun setProxiedImpl(impl: ITest1) {
        super.setProxiedImpl(impl)
    }

    override fun echo(request: TestOuterClass.ReqTestEcho, responseObserver: StreamObserver<TestOuterClass.RspTestEcho>) {
        logger.info("调用 test1::grpc-echo")

        val rsp = TestOuterClass.RspTestEcho.newBuilder()
            .setOutput(request.input)
            .build()
        responseObserver.onNext(rsp)
        responseObserver.onCompleted()
    }
}