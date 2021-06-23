// package: com.test.dubbo
// file: dubbo/test.proto

/* tslint:disable */
/* eslint-disable */

import * as grpc from "grpc";
import * as dubbo_test_pb from "../dubbo/test_pb";
import * as google_protobuf_empty_pb from "google-protobuf/google/protobuf/empty_pb";
import * as google_protobuf_wrappers_pb from "google-protobuf/google/protobuf/wrappers_pb";
import * as dao_pb from "../dao_pb";

interface ITestService extends grpc.ServiceDefinition<grpc.UntypedServiceImplementation> {
    hello: ITestService_Ihello;
    echo: ITestService_Iecho;
    echoo: ITestService_Iechoo;
    echoos: ITestService_Iechoos;
    echooclear: ITestService_Iechooclear;
    echooupdate: ITestService_Iechooupdate;
}

interface ITestService_Ihello extends grpc.MethodDefinition<google_protobuf_empty_pb.Empty, dubbo_test_pb.TestReply> {
    path: "/com.test.dubbo.Test/hello";
    requestStream: false;
    responseStream: false;
    requestSerialize: grpc.serialize<google_protobuf_empty_pb.Empty>;
    requestDeserialize: grpc.deserialize<google_protobuf_empty_pb.Empty>;
    responseSerialize: grpc.serialize<dubbo_test_pb.TestReply>;
    responseDeserialize: grpc.deserialize<dubbo_test_pb.TestReply>;
}
interface ITestService_Iecho extends grpc.MethodDefinition<dubbo_test_pb.ReqTestEcho, dubbo_test_pb.RspTestEcho> {
    path: "/com.test.dubbo.Test/echo";
    requestStream: false;
    responseStream: false;
    requestSerialize: grpc.serialize<dubbo_test_pb.ReqTestEcho>;
    requestDeserialize: grpc.deserialize<dubbo_test_pb.ReqTestEcho>;
    responseSerialize: grpc.serialize<dubbo_test_pb.RspTestEcho>;
    responseDeserialize: grpc.deserialize<dubbo_test_pb.RspTestEcho>;
}
interface ITestService_Iechoo extends grpc.MethodDefinition<google_protobuf_wrappers_pb.StringValue, dao_pb.Echoo> {
    path: "/com.test.dubbo.Test/echoo";
    requestStream: false;
    responseStream: false;
    requestSerialize: grpc.serialize<google_protobuf_wrappers_pb.StringValue>;
    requestDeserialize: grpc.deserialize<google_protobuf_wrappers_pb.StringValue>;
    responseSerialize: grpc.serialize<dao_pb.Echoo>;
    responseDeserialize: grpc.deserialize<dao_pb.Echoo>;
}
interface ITestService_Iechoos extends grpc.MethodDefinition<google_protobuf_empty_pb.Empty, dao_pb.Echoos> {
    path: "/com.test.dubbo.Test/echoos";
    requestStream: false;
    responseStream: false;
    requestSerialize: grpc.serialize<google_protobuf_empty_pb.Empty>;
    requestDeserialize: grpc.deserialize<google_protobuf_empty_pb.Empty>;
    responseSerialize: grpc.serialize<dao_pb.Echoos>;
    responseDeserialize: grpc.deserialize<dao_pb.Echoos>;
}
interface ITestService_Iechooclear extends grpc.MethodDefinition<google_protobuf_empty_pb.Empty, google_protobuf_wrappers_pb.Int32Value> {
    path: "/com.test.dubbo.Test/echooclear";
    requestStream: false;
    responseStream: false;
    requestSerialize: grpc.serialize<google_protobuf_empty_pb.Empty>;
    requestDeserialize: grpc.deserialize<google_protobuf_empty_pb.Empty>;
    responseSerialize: grpc.serialize<google_protobuf_wrappers_pb.Int32Value>;
    responseDeserialize: grpc.deserialize<google_protobuf_wrappers_pb.Int32Value>;
}
interface ITestService_Iechooupdate extends grpc.MethodDefinition<dao_pb.Echoo, google_protobuf_wrappers_pb.BoolValue> {
    path: "/com.test.dubbo.Test/echooupdate";
    requestStream: false;
    responseStream: false;
    requestSerialize: grpc.serialize<dao_pb.Echoo>;
    requestDeserialize: grpc.deserialize<dao_pb.Echoo>;
    responseSerialize: grpc.serialize<google_protobuf_wrappers_pb.BoolValue>;
    responseDeserialize: grpc.deserialize<google_protobuf_wrappers_pb.BoolValue>;
}

export const TestService: ITestService;

export interface ITestServer {
    hello: grpc.handleUnaryCall<google_protobuf_empty_pb.Empty, dubbo_test_pb.TestReply>;
    echo: grpc.handleUnaryCall<dubbo_test_pb.ReqTestEcho, dubbo_test_pb.RspTestEcho>;
    echoo: grpc.handleUnaryCall<google_protobuf_wrappers_pb.StringValue, dao_pb.Echoo>;
    echoos: grpc.handleUnaryCall<google_protobuf_empty_pb.Empty, dao_pb.Echoos>;
    echooclear: grpc.handleUnaryCall<google_protobuf_empty_pb.Empty, google_protobuf_wrappers_pb.Int32Value>;
    echooupdate: grpc.handleUnaryCall<dao_pb.Echoo, google_protobuf_wrappers_pb.BoolValue>;
}

export interface ITestClient {
    hello(request: google_protobuf_empty_pb.Empty, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.TestReply) => void): grpc.ClientUnaryCall;
    hello(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.TestReply) => void): grpc.ClientUnaryCall;
    hello(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.TestReply) => void): grpc.ClientUnaryCall;
    echo(request: dubbo_test_pb.ReqTestEcho, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
    echo(request: dubbo_test_pb.ReqTestEcho, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
    echo(request: dubbo_test_pb.ReqTestEcho, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
    echoo(request: google_protobuf_wrappers_pb.StringValue, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoo) => void): grpc.ClientUnaryCall;
    echoo(request: google_protobuf_wrappers_pb.StringValue, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoo) => void): grpc.ClientUnaryCall;
    echoo(request: google_protobuf_wrappers_pb.StringValue, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoo) => void): grpc.ClientUnaryCall;
    echoos(request: google_protobuf_empty_pb.Empty, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoos) => void): grpc.ClientUnaryCall;
    echoos(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoos) => void): grpc.ClientUnaryCall;
    echoos(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoos) => void): grpc.ClientUnaryCall;
    echooclear(request: google_protobuf_empty_pb.Empty, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.Int32Value) => void): grpc.ClientUnaryCall;
    echooclear(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.Int32Value) => void): grpc.ClientUnaryCall;
    echooclear(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.Int32Value) => void): grpc.ClientUnaryCall;
    echooupdate(request: dao_pb.Echoo, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.BoolValue) => void): grpc.ClientUnaryCall;
    echooupdate(request: dao_pb.Echoo, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.BoolValue) => void): grpc.ClientUnaryCall;
    echooupdate(request: dao_pb.Echoo, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.BoolValue) => void): grpc.ClientUnaryCall;
}

export class TestClient extends grpc.Client implements ITestClient {
    constructor(address: string, credentials: grpc.ChannelCredentials, options?: object);
    public hello(request: google_protobuf_empty_pb.Empty, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.TestReply) => void): grpc.ClientUnaryCall;
    public hello(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.TestReply) => void): grpc.ClientUnaryCall;
    public hello(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.TestReply) => void): grpc.ClientUnaryCall;
    public echo(request: dubbo_test_pb.ReqTestEcho, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
    public echo(request: dubbo_test_pb.ReqTestEcho, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
    public echo(request: dubbo_test_pb.ReqTestEcho, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
    public echoo(request: google_protobuf_wrappers_pb.StringValue, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoo) => void): grpc.ClientUnaryCall;
    public echoo(request: google_protobuf_wrappers_pb.StringValue, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoo) => void): grpc.ClientUnaryCall;
    public echoo(request: google_protobuf_wrappers_pb.StringValue, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoo) => void): grpc.ClientUnaryCall;
    public echoos(request: google_protobuf_empty_pb.Empty, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoos) => void): grpc.ClientUnaryCall;
    public echoos(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoos) => void): grpc.ClientUnaryCall;
    public echoos(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: dao_pb.Echoos) => void): grpc.ClientUnaryCall;
    public echooclear(request: google_protobuf_empty_pb.Empty, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.Int32Value) => void): grpc.ClientUnaryCall;
    public echooclear(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.Int32Value) => void): grpc.ClientUnaryCall;
    public echooclear(request: google_protobuf_empty_pb.Empty, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.Int32Value) => void): grpc.ClientUnaryCall;
    public echooupdate(request: dao_pb.Echoo, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.BoolValue) => void): grpc.ClientUnaryCall;
    public echooupdate(request: dao_pb.Echoo, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.BoolValue) => void): grpc.ClientUnaryCall;
    public echooupdate(request: dao_pb.Echoo, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: google_protobuf_wrappers_pb.BoolValue) => void): grpc.ClientUnaryCall;
}

interface ITest1Service extends grpc.ServiceDefinition<grpc.UntypedServiceImplementation> {
    echo: ITest1Service_Iecho;
}

interface ITest1Service_Iecho extends grpc.MethodDefinition<dubbo_test_pb.ReqTestEcho, dubbo_test_pb.RspTestEcho> {
    path: "/com.test.dubbo.Test1/echo";
    requestStream: false;
    responseStream: false;
    requestSerialize: grpc.serialize<dubbo_test_pb.ReqTestEcho>;
    requestDeserialize: grpc.deserialize<dubbo_test_pb.ReqTestEcho>;
    responseSerialize: grpc.serialize<dubbo_test_pb.RspTestEcho>;
    responseDeserialize: grpc.deserialize<dubbo_test_pb.RspTestEcho>;
}

export const Test1Service: ITest1Service;

export interface ITest1Server {
    echo: grpc.handleUnaryCall<dubbo_test_pb.ReqTestEcho, dubbo_test_pb.RspTestEcho>;
}

export interface ITest1Client {
    echo(request: dubbo_test_pb.ReqTestEcho, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
    echo(request: dubbo_test_pb.ReqTestEcho, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
    echo(request: dubbo_test_pb.ReqTestEcho, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
}

export class Test1Client extends grpc.Client implements ITest1Client {
    constructor(address: string, credentials: grpc.ChannelCredentials, options?: object);
    public echo(request: dubbo_test_pb.ReqTestEcho, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
    public echo(request: dubbo_test_pb.ReqTestEcho, metadata: grpc.Metadata, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
    public echo(request: dubbo_test_pb.ReqTestEcho, metadata: grpc.Metadata, options: Partial<grpc.CallOptions>, callback: (error: grpc.ServiceError | null, response: dubbo_test_pb.RspTestEcho) => void): grpc.ClientUnaryCall;
}
