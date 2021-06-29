// GENERATED CODE -- DO NOT EDIT!

'use strict';
var grpc = require('grpc');
var dubbo_test_pb = require('../dubbo/test_pb.js');
var google_protobuf_empty_pb = require('google-protobuf/google/protobuf/empty_pb.js');
var google_protobuf_wrappers_pb = require('google-protobuf/google/protobuf/wrappers_pb.js');
var dao_pb = require('../dao_pb.js');

function serialize_com_test_Echoo(arg) {
  if (!(arg instanceof dao_pb.Echoo)) {
    throw new Error('Expected argument of type com.test.Echoo');
  }
  return new Buffer(arg.serializeBinary());
}

function deserialize_com_test_Echoo(buffer_arg) {
  return dao_pb.Echoo.deserializeBinary(new Uint8Array(buffer_arg));
}

function serialize_com_test_Echoos(arg) {
  if (!(arg instanceof dao_pb.Echoos)) {
    throw new Error('Expected argument of type com.test.Echoos');
  }
  return new Buffer(arg.serializeBinary());
}

function deserialize_com_test_Echoos(buffer_arg) {
  return dao_pb.Echoos.deserializeBinary(new Uint8Array(buffer_arg));
}

function serialize_com_test_dubbo_ReqTestEcho(arg) {
  if (!(arg instanceof dubbo_test_pb.ReqTestEcho)) {
    throw new Error('Expected argument of type com.test.dubbo.ReqTestEcho');
  }
  return new Buffer(arg.serializeBinary());
}

function deserialize_com_test_dubbo_ReqTestEcho(buffer_arg) {
  return dubbo_test_pb.ReqTestEcho.deserializeBinary(new Uint8Array(buffer_arg));
}

function serialize_com_test_dubbo_RspTestEcho(arg) {
  if (!(arg instanceof dubbo_test_pb.RspTestEcho)) {
    throw new Error('Expected argument of type com.test.dubbo.RspTestEcho');
  }
  return new Buffer(arg.serializeBinary());
}

function deserialize_com_test_dubbo_RspTestEcho(buffer_arg) {
  return dubbo_test_pb.RspTestEcho.deserializeBinary(new Uint8Array(buffer_arg));
}

function serialize_com_test_dubbo_TestReply(arg) {
  if (!(arg instanceof dubbo_test_pb.TestReply)) {
    throw new Error('Expected argument of type com.test.dubbo.TestReply');
  }
  return new Buffer(arg.serializeBinary());
}

function deserialize_com_test_dubbo_TestReply(buffer_arg) {
  return dubbo_test_pb.TestReply.deserializeBinary(new Uint8Array(buffer_arg));
}

function serialize_google_protobuf_BoolValue(arg) {
  if (!(arg instanceof google_protobuf_wrappers_pb.BoolValue)) {
    throw new Error('Expected argument of type google.protobuf.BoolValue');
  }
  return new Buffer(arg.serializeBinary());
}

function deserialize_google_protobuf_BoolValue(buffer_arg) {
  return google_protobuf_wrappers_pb.BoolValue.deserializeBinary(new Uint8Array(buffer_arg));
}

function serialize_google_protobuf_Empty(arg) {
  if (!(arg instanceof google_protobuf_empty_pb.Empty)) {
    throw new Error('Expected argument of type google.protobuf.Empty');
  }
  return new Buffer(arg.serializeBinary());
}

function deserialize_google_protobuf_Empty(buffer_arg) {
  return google_protobuf_empty_pb.Empty.deserializeBinary(new Uint8Array(buffer_arg));
}

function serialize_google_protobuf_Int32Value(arg) {
  if (!(arg instanceof google_protobuf_wrappers_pb.Int32Value)) {
    throw new Error('Expected argument of type google.protobuf.Int32Value');
  }
  return new Buffer(arg.serializeBinary());
}

function deserialize_google_protobuf_Int32Value(buffer_arg) {
  return google_protobuf_wrappers_pb.Int32Value.deserializeBinary(new Uint8Array(buffer_arg));
}

function serialize_google_protobuf_StringValue(arg) {
  if (!(arg instanceof google_protobuf_wrappers_pb.StringValue)) {
    throw new Error('Expected argument of type google.protobuf.StringValue');
  }
  return new Buffer(arg.serializeBinary());
}

function deserialize_google_protobuf_StringValue(buffer_arg) {
  return google_protobuf_wrappers_pb.StringValue.deserializeBinary(new Uint8Array(buffer_arg));
}


// 测试hello的协议
var TestService = exports.TestService = {
  // 返回欢迎
  hello: {
    path: '/com.test.dubbo.Test/hello',
    requestStream: false,
    responseStream: false,
    requestType: google_protobuf_empty_pb.Empty,
    responseType: dubbo_test_pb.TestReply,
    requestSerialize: serialize_google_protobuf_Empty,
    requestDeserialize: deserialize_google_protobuf_Empty,
    responseSerialize: serialize_com_test_dubbo_TestReply,
    responseDeserialize: deserialize_com_test_dubbo_TestReply,
  },
  // 普通测试echo
  echo: {
    path: '/com.test.dubbo.Test/echo',
    requestStream: false,
    responseStream: false,
    requestType: dubbo_test_pb.ReqTestEcho,
    responseType: dubbo_test_pb.RspTestEcho,
    requestSerialize: serialize_com_test_dubbo_ReqTestEcho,
    requestDeserialize: deserialize_com_test_dubbo_ReqTestEcho,
    responseSerialize: serialize_com_test_dubbo_RspTestEcho,
    responseDeserialize: deserialize_com_test_dubbo_RspTestEcho,
  },
  // 数据库添加一个echoo
  echoo: {
    path: '/com.test.dubbo.Test/echoo',
    requestStream: false,
    responseStream: false,
    requestType: google_protobuf_wrappers_pb.StringValue,
    responseType: dao_pb.Echoo,
    requestSerialize: serialize_google_protobuf_StringValue,
    requestDeserialize: deserialize_google_protobuf_StringValue,
    responseSerialize: serialize_com_test_Echoo,
    responseDeserialize: deserialize_com_test_Echoo,
  },
  // 列表
  echoos: {
    path: '/com.test.dubbo.Test/echoos',
    requestStream: false,
    responseStream: false,
    requestType: google_protobuf_empty_pb.Empty,
    responseType: dao_pb.Echoos,
    requestSerialize: serialize_google_protobuf_Empty,
    requestDeserialize: deserialize_google_protobuf_Empty,
    responseSerialize: serialize_com_test_Echoos,
    responseDeserialize: deserialize_com_test_Echoos,
  },
  // 清空echoo
  echooclear: {
    path: '/com.test.dubbo.Test/echooclear',
    requestStream: false,
    responseStream: false,
    requestType: google_protobuf_empty_pb.Empty,
    responseType: google_protobuf_wrappers_pb.Int32Value,
    requestSerialize: serialize_google_protobuf_Empty,
    requestDeserialize: deserialize_google_protobuf_Empty,
    responseSerialize: serialize_google_protobuf_Int32Value,
    responseDeserialize: deserialize_google_protobuf_Int32Value,
  },
  // 更新
  echooupdate: {
    path: '/com.test.dubbo.Test/echooupdate',
    requestStream: false,
    responseStream: false,
    requestType: dao_pb.Echoo,
    responseType: google_protobuf_wrappers_pb.BoolValue,
    requestSerialize: serialize_com_test_Echoo,
    requestDeserialize: deserialize_com_test_Echoo,
    responseSerialize: serialize_google_protobuf_BoolValue,
    responseDeserialize: deserialize_google_protobuf_BoolValue,
  },
  // 测试返回失败
  error: {
    path: '/com.test.dubbo.Test/error',
    requestStream: false,
    responseStream: false,
    requestType: google_protobuf_empty_pb.Empty,
    responseType: google_protobuf_empty_pb.Empty,
    requestSerialize: serialize_google_protobuf_Empty,
    requestDeserialize: deserialize_google_protobuf_Empty,
    responseSerialize: serialize_google_protobuf_Empty,
    responseDeserialize: deserialize_google_protobuf_Empty,
  },
};

exports.TestClient = grpc.makeGenericClientConstructor(TestService);
var Test1Service = exports.Test1Service = {
  // 普通测试echo
  echo: {
    path: '/com.test.dubbo.Test1/echo',
    requestStream: false,
    responseStream: false,
    requestType: dubbo_test_pb.ReqTestEcho,
    responseType: dubbo_test_pb.RspTestEcho,
    requestSerialize: serialize_com_test_dubbo_ReqTestEcho,
    requestDeserialize: deserialize_com_test_dubbo_ReqTestEcho,
    responseSerialize: serialize_com_test_dubbo_RspTestEcho,
    responseDeserialize: deserialize_com_test_dubbo_RspTestEcho,
  },
};

exports.Test1Client = grpc.makeGenericClientConstructor(Test1Service);
