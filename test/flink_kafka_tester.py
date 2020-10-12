#!/usr/bin/env python3

# flink测试使用的kafka数据源
# 定时吃掉kafka投放数据

import kafka
import json
import time

TOPIC = "nnt-logic-test"
HOST = "localhost:9092"

cli = kafka.KafkaConsumer(
    TOPIC,
    bootstrap_servers=HOST
)


def test():
    for msg in cli:
        print(msg.value.decode('utf-8'))


if __name__ == "__main__":
    test()
