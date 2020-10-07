#!/usr/bin/env python3

import grpc
from test_pb2_grpc import TestStub
from google.protobuf.empty_pb2 import Empty

# 当前启动的测试服务
host = "localhost:8092"

# 链接服务器
channel = grpc.insecure_channel(host)

# 构造数据
stub = TestStub(channel)
response = stub.hello(Empty())
print("收到数据 %s" % response.message)
