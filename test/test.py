#!/usr/bin/env python3

import grpc
import test_pb2
import test_pb2_grpc

# 当前启动的测试服务
host = "localhost:8092"

# 链接服务器
channel = grpc.insecure_channel(host)

# 构造数据
stub = test_pb2_grpc.TestStub(channel)
response = stub.hello(test_pb2.TestReq())
print("收到数据 %s" % response.message)
