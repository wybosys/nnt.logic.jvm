#!/usr/bin/env python3

import time
import threading
from dubbo.test_pb2_grpc import TestStub
from google.protobuf.empty_pb2 import Empty
from google.protobuf.wrappers_pb2 import StringValue

import grpc

# 当前启动的测试服务
host = "localhost:8093"


def test():
    # 链接服务器
    channel = grpc.insecure_channel(host)
    stub = TestStub(channel)

    for i in range(0, 50):
        beg = time.time_ns()

        # 测试hello
        response = stub.hello(Empty())
        # print("收到数据 %s" % response.message)

        end = time.time_ns()
        cost = end - beg
        print("消耗 %f ms" % (cost / 1000000))


if __name__ == "__main__":
    while (1):
        for j in range(0, 50):
            threading.Thread(target=test).start()
        time.sleep(1)
