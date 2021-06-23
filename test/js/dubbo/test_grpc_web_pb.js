/**
 * @fileoverview gRPC-Web generated client stub for com.test.dubbo
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck



const grpc = {};
grpc.web = require('grpc-web');


var google_protobuf_empty_pb = require('google-protobuf/google/protobuf/empty_pb.js')

var google_protobuf_wrappers_pb = require('google-protobuf/google/protobuf/wrappers_pb.js')

var dao_pb = require('../dao_pb.js')
const proto = {};
proto.com = {};
proto.com.test = {};
proto.com.test.dubbo = require('./test_pb.js');

/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.com.test.dubbo.TestClient =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!grpc.web.GrpcWebClientBase} The client
   */
  this.client_ = new grpc.web.GrpcWebClientBase(options);

  /**
   * @private @const {string} The hostname
   */
  this.hostname_ = hostname;

};


/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.com.test.dubbo.TestPromiseClient =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!grpc.web.GrpcWebClientBase} The client
   */
  this.client_ = new grpc.web.GrpcWebClientBase(options);

  /**
   * @private @const {string} The hostname
   */
  this.hostname_ = hostname;

};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.google.protobuf.Empty,
 *   !proto.com.test.dubbo.TestReply>}
 */
const methodDescriptor_Test_hello = new grpc.web.MethodDescriptor(
  '/com.test.dubbo.Test/hello',
  grpc.web.MethodType.UNARY,
  google_protobuf_empty_pb.Empty,
  proto.com.test.dubbo.TestReply,
  /**
   * @param {!proto.google.protobuf.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.com.test.dubbo.TestReply.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.google.protobuf.Empty,
 *   !proto.com.test.dubbo.TestReply>}
 */
const methodInfo_Test_hello = new grpc.web.AbstractClientBase.MethodInfo(
  proto.com.test.dubbo.TestReply,
  /**
   * @param {!proto.google.protobuf.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.com.test.dubbo.TestReply.deserializeBinary
);


/**
 * @param {!proto.google.protobuf.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.com.test.dubbo.TestReply)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.com.test.dubbo.TestReply>|undefined}
 *     The XHR Node Readable Stream
 */
proto.com.test.dubbo.TestClient.prototype.hello =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/com.test.dubbo.Test/hello',
      request,
      metadata || {},
      methodDescriptor_Test_hello,
      callback);
};


/**
 * @param {!proto.google.protobuf.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.com.test.dubbo.TestReply>}
 *     Promise that resolves to the response
 */
proto.com.test.dubbo.TestPromiseClient.prototype.hello =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/com.test.dubbo.Test/hello',
      request,
      metadata || {},
      methodDescriptor_Test_hello);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.com.test.dubbo.ReqTestEcho,
 *   !proto.com.test.dubbo.RspTestEcho>}
 */
const methodDescriptor_Test_echo = new grpc.web.MethodDescriptor(
  '/com.test.dubbo.Test/echo',
  grpc.web.MethodType.UNARY,
  proto.com.test.dubbo.ReqTestEcho,
  proto.com.test.dubbo.RspTestEcho,
  /**
   * @param {!proto.com.test.dubbo.ReqTestEcho} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.com.test.dubbo.RspTestEcho.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.com.test.dubbo.ReqTestEcho,
 *   !proto.com.test.dubbo.RspTestEcho>}
 */
const methodInfo_Test_echo = new grpc.web.AbstractClientBase.MethodInfo(
  proto.com.test.dubbo.RspTestEcho,
  /**
   * @param {!proto.com.test.dubbo.ReqTestEcho} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.com.test.dubbo.RspTestEcho.deserializeBinary
);


/**
 * @param {!proto.com.test.dubbo.ReqTestEcho} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.com.test.dubbo.RspTestEcho)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.com.test.dubbo.RspTestEcho>|undefined}
 *     The XHR Node Readable Stream
 */
proto.com.test.dubbo.TestClient.prototype.echo =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/com.test.dubbo.Test/echo',
      request,
      metadata || {},
      methodDescriptor_Test_echo,
      callback);
};


/**
 * @param {!proto.com.test.dubbo.ReqTestEcho} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.com.test.dubbo.RspTestEcho>}
 *     Promise that resolves to the response
 */
proto.com.test.dubbo.TestPromiseClient.prototype.echo =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/com.test.dubbo.Test/echo',
      request,
      metadata || {},
      methodDescriptor_Test_echo);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.google.protobuf.StringValue,
 *   !proto.com.test.Echoo>}
 */
const methodDescriptor_Test_echoo = new grpc.web.MethodDescriptor(
  '/com.test.dubbo.Test/echoo',
  grpc.web.MethodType.UNARY,
  google_protobuf_wrappers_pb.StringValue,
  dao_pb.Echoo,
  /**
   * @param {!proto.google.protobuf.StringValue} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  dao_pb.Echoo.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.google.protobuf.StringValue,
 *   !proto.com.test.Echoo>}
 */
const methodInfo_Test_echoo = new grpc.web.AbstractClientBase.MethodInfo(
  dao_pb.Echoo,
  /**
   * @param {!proto.google.protobuf.StringValue} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  dao_pb.Echoo.deserializeBinary
);


/**
 * @param {!proto.google.protobuf.StringValue} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.com.test.Echoo)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.com.test.Echoo>|undefined}
 *     The XHR Node Readable Stream
 */
proto.com.test.dubbo.TestClient.prototype.echoo =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/com.test.dubbo.Test/echoo',
      request,
      metadata || {},
      methodDescriptor_Test_echoo,
      callback);
};


/**
 * @param {!proto.google.protobuf.StringValue} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.com.test.Echoo>}
 *     Promise that resolves to the response
 */
proto.com.test.dubbo.TestPromiseClient.prototype.echoo =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/com.test.dubbo.Test/echoo',
      request,
      metadata || {},
      methodDescriptor_Test_echoo);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.google.protobuf.Empty,
 *   !proto.com.test.Echoos>}
 */
const methodDescriptor_Test_echoos = new grpc.web.MethodDescriptor(
  '/com.test.dubbo.Test/echoos',
  grpc.web.MethodType.UNARY,
  google_protobuf_empty_pb.Empty,
  dao_pb.Echoos,
  /**
   * @param {!proto.google.protobuf.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  dao_pb.Echoos.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.google.protobuf.Empty,
 *   !proto.com.test.Echoos>}
 */
const methodInfo_Test_echoos = new grpc.web.AbstractClientBase.MethodInfo(
  dao_pb.Echoos,
  /**
   * @param {!proto.google.protobuf.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  dao_pb.Echoos.deserializeBinary
);


/**
 * @param {!proto.google.protobuf.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.com.test.Echoos)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.com.test.Echoos>|undefined}
 *     The XHR Node Readable Stream
 */
proto.com.test.dubbo.TestClient.prototype.echoos =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/com.test.dubbo.Test/echoos',
      request,
      metadata || {},
      methodDescriptor_Test_echoos,
      callback);
};


/**
 * @param {!proto.google.protobuf.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.com.test.Echoos>}
 *     Promise that resolves to the response
 */
proto.com.test.dubbo.TestPromiseClient.prototype.echoos =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/com.test.dubbo.Test/echoos',
      request,
      metadata || {},
      methodDescriptor_Test_echoos);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.google.protobuf.Empty,
 *   !proto.google.protobuf.Int32Value>}
 */
const methodDescriptor_Test_echooclear = new grpc.web.MethodDescriptor(
  '/com.test.dubbo.Test/echooclear',
  grpc.web.MethodType.UNARY,
  google_protobuf_empty_pb.Empty,
  google_protobuf_wrappers_pb.Int32Value,
  /**
   * @param {!proto.google.protobuf.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  google_protobuf_wrappers_pb.Int32Value.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.google.protobuf.Empty,
 *   !proto.google.protobuf.Int32Value>}
 */
const methodInfo_Test_echooclear = new grpc.web.AbstractClientBase.MethodInfo(
  google_protobuf_wrappers_pb.Int32Value,
  /**
   * @param {!proto.google.protobuf.Empty} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  google_protobuf_wrappers_pb.Int32Value.deserializeBinary
);


/**
 * @param {!proto.google.protobuf.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.google.protobuf.Int32Value)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.google.protobuf.Int32Value>|undefined}
 *     The XHR Node Readable Stream
 */
proto.com.test.dubbo.TestClient.prototype.echooclear =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/com.test.dubbo.Test/echooclear',
      request,
      metadata || {},
      methodDescriptor_Test_echooclear,
      callback);
};


/**
 * @param {!proto.google.protobuf.Empty} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.google.protobuf.Int32Value>}
 *     Promise that resolves to the response
 */
proto.com.test.dubbo.TestPromiseClient.prototype.echooclear =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/com.test.dubbo.Test/echooclear',
      request,
      metadata || {},
      methodDescriptor_Test_echooclear);
};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.com.test.Echoo,
 *   !proto.google.protobuf.BoolValue>}
 */
const methodDescriptor_Test_echooupdate = new grpc.web.MethodDescriptor(
  '/com.test.dubbo.Test/echooupdate',
  grpc.web.MethodType.UNARY,
  dao_pb.Echoo,
  google_protobuf_wrappers_pb.BoolValue,
  /**
   * @param {!proto.com.test.Echoo} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  google_protobuf_wrappers_pb.BoolValue.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.com.test.Echoo,
 *   !proto.google.protobuf.BoolValue>}
 */
const methodInfo_Test_echooupdate = new grpc.web.AbstractClientBase.MethodInfo(
  google_protobuf_wrappers_pb.BoolValue,
  /**
   * @param {!proto.com.test.Echoo} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  google_protobuf_wrappers_pb.BoolValue.deserializeBinary
);


/**
 * @param {!proto.com.test.Echoo} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.google.protobuf.BoolValue)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.google.protobuf.BoolValue>|undefined}
 *     The XHR Node Readable Stream
 */
proto.com.test.dubbo.TestClient.prototype.echooupdate =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/com.test.dubbo.Test/echooupdate',
      request,
      metadata || {},
      methodDescriptor_Test_echooupdate,
      callback);
};


/**
 * @param {!proto.com.test.Echoo} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.google.protobuf.BoolValue>}
 *     Promise that resolves to the response
 */
proto.com.test.dubbo.TestPromiseClient.prototype.echooupdate =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/com.test.dubbo.Test/echooupdate',
      request,
      metadata || {},
      methodDescriptor_Test_echooupdate);
};


/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.com.test.dubbo.Test1Client =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!grpc.web.GrpcWebClientBase} The client
   */
  this.client_ = new grpc.web.GrpcWebClientBase(options);

  /**
   * @private @const {string} The hostname
   */
  this.hostname_ = hostname;

};


/**
 * @param {string} hostname
 * @param {?Object} credentials
 * @param {?Object} options
 * @constructor
 * @struct
 * @final
 */
proto.com.test.dubbo.Test1PromiseClient =
    function(hostname, credentials, options) {
  if (!options) options = {};
  options['format'] = 'text';

  /**
   * @private @const {!grpc.web.GrpcWebClientBase} The client
   */
  this.client_ = new grpc.web.GrpcWebClientBase(options);

  /**
   * @private @const {string} The hostname
   */
  this.hostname_ = hostname;

};


/**
 * @const
 * @type {!grpc.web.MethodDescriptor<
 *   !proto.com.test.dubbo.ReqTestEcho,
 *   !proto.com.test.dubbo.RspTestEcho>}
 */
const methodDescriptor_Test1_echo = new grpc.web.MethodDescriptor(
  '/com.test.dubbo.Test1/echo',
  grpc.web.MethodType.UNARY,
  proto.com.test.dubbo.ReqTestEcho,
  proto.com.test.dubbo.RspTestEcho,
  /**
   * @param {!proto.com.test.dubbo.ReqTestEcho} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.com.test.dubbo.RspTestEcho.deserializeBinary
);


/**
 * @const
 * @type {!grpc.web.AbstractClientBase.MethodInfo<
 *   !proto.com.test.dubbo.ReqTestEcho,
 *   !proto.com.test.dubbo.RspTestEcho>}
 */
const methodInfo_Test1_echo = new grpc.web.AbstractClientBase.MethodInfo(
  proto.com.test.dubbo.RspTestEcho,
  /**
   * @param {!proto.com.test.dubbo.ReqTestEcho} request
   * @return {!Uint8Array}
   */
  function(request) {
    return request.serializeBinary();
  },
  proto.com.test.dubbo.RspTestEcho.deserializeBinary
);


/**
 * @param {!proto.com.test.dubbo.ReqTestEcho} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @param {function(?grpc.web.Error, ?proto.com.test.dubbo.RspTestEcho)}
 *     callback The callback function(error, response)
 * @return {!grpc.web.ClientReadableStream<!proto.com.test.dubbo.RspTestEcho>|undefined}
 *     The XHR Node Readable Stream
 */
proto.com.test.dubbo.Test1Client.prototype.echo =
    function(request, metadata, callback) {
  return this.client_.rpcCall(this.hostname_ +
      '/com.test.dubbo.Test1/echo',
      request,
      metadata || {},
      methodDescriptor_Test1_echo,
      callback);
};


/**
 * @param {!proto.com.test.dubbo.ReqTestEcho} request The
 *     request proto
 * @param {?Object<string, string>} metadata User defined
 *     call metadata
 * @return {!Promise<!proto.com.test.dubbo.RspTestEcho>}
 *     Promise that resolves to the response
 */
proto.com.test.dubbo.Test1PromiseClient.prototype.echo =
    function(request, metadata) {
  return this.client_.unaryCall(this.hostname_ +
      '/com.test.dubbo.Test1/echo',
      request,
      metadata || {},
      methodDescriptor_Test1_echo);
};


module.exports = proto.com.test.dubbo;

