#!/usr/bin/env python3

import asyncio
from dubbo.test_pb2_grpc import TestStub
from dubbo.test_pb2 import ReqTestEcho
from google.protobuf.empty_pb2 import Empty
from google.protobuf.wrappers_pb2 import StringValue

import grpc

# 当前启动的测试服务
host = "localhost:8093"

# 链接服务器
channel = grpc.insecure_channel(host)
stub = TestStub(channel)


async def test(idx):
    print("test %d" % idx)

    # 测试hello
    response = stub.hello(Empty())
    print("收到数据 %s" % response.message)

    # 测试echo
    req = ReqTestEcho()
    req.input = "hello"
    response = stub.echo(req)
    print("收到数据 %s" % (response.output))

    # 测试echoo
    response = stub.echoo(StringValue(value="test echoo"))
    print("收到数据 %d %s" % (response.id, response.output))

    # 修改
    # response.id = 5555
    response.output = "modified"
    response = stub.update_echoo(response)
    print("修改成功" if response.value else "修改失败")

    # 查询
    response = stub.echoos(Empty())
    print("收到 %d 条数据" % len(response.item))
    for e in response.item:
        print("%d %s" % (e.id, e.output))

    # 清空echoo
    response = stub.clear_echoo(Empty())
    print("清空 %d 条数据" % response.value)


async def test_co():
    await asyncio.wait([test(i) for i in range(100)])


loop = asyncio.get_event_loop()
loop.run_until_complete(test_co())
loop.close()
