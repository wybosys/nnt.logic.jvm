#!/usr/bin/env python3

import grpc
from dubbo.test_pb2_grpc import TestStub
from google.protobuf.empty_pb2 import Empty
from google.protobuf.wrappers_pb2 import StringValue

# 当前启动的测试服务
host = "localhost:8092"

# 链接服务器
channel = grpc.insecure_channel(host)
stub = TestStub(channel)

# 测试hello
response = stub.hello(Empty())
print("收到数据 %s" % response.message)

# 测试echoo
response = stub.echoo(StringValue(value="test echoo"))
print("收到数据 %d %s" % (response.id, response.output))

# 修改
# response.id = 5555
response.output = "modified"
response = stub.update_echoo(response)
print("修改成功" if response.value else "修改失败")

# 清空echoo
response = stub.clear_echoo(Empty())
print("清空 %d 条数据" % response.value)
