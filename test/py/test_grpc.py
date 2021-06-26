#!/usr/bin/env python3
import argparse
import asyncio

import grpc
from google.protobuf.empty_pb2 import Empty
from google.protobuf.wrappers_pb2 import StringValue

from dubbo.test_pb2 import ReqTestEcho
from dubbo.test_pb2_grpc import TestStub, Test1Stub

# 当前启动的测试服务
host = "localhost:8093"

# 链接服务器
channel = grpc.insecure_channel(host)
stub = TestStub(channel)
stub1 = Test1Stub(channel)


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

    req = ReqTestEcho()
    req.input = "test1:hello"
    response = stub1.echo(req)
    print("收到数据 %s" % (response.output))

    # 测试echoo
    response = stub.echoo(StringValue(value="test echoo"))
    print("收到数据 %d %s" % (response.id, response.output))

    # 修改
    # response.id = 5555
    response.output = "modified"
    response = stub.echooupdate(response)
    print("修改成功" if response.value else "修改失败")

    # 查询
    response = stub.echoos(Empty())
    print("收到 %d 条数据" % len(response.item))
    for e in response.item:
        print("%d %s" % (e.id, e.output))

    # 清空echoo
    response = stub.echooclear(Empty())
    print("清空 %d 条数据" % response.value)

    # 测试返回错误
    try:
        stub.error(Empty())
    except grpc.RpcError as e:
        print(e.code(), e.details())


if __name__ == '__main__':
    args = argparse.ArgumentParser()
    args.add_argument('-n', '--ncnt', default=1, help='数量')
    args = args.parse_args()


    async def test_co():
        await asyncio.wait([test(i) for i in range(args.ncnt)])


    loop = asyncio.get_event_loop()
    loop.run_until_complete(test_co())
    loop.close()
