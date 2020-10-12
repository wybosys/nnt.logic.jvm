#!/usr/bin/env python3

# flink测试使用的kafka数据源
# 定时给kafka投放数据

import kafka
import kafka.admin
import json
import time

TOPIC = "nnt-logic-test"
HOST = "localhost:9092"

cli = kafka.KafkaClient(
    bootstrap_servers=HOST
)

produ = kafka.KafkaProducer(
    bootstrap_servers=HOST
)

admin = kafka.KafkaAdminClient(
    bootstrap_servers=HOST
)


def IsTopicExists(topic):
    tps = admin.list_topics()
    return topic in tps


def CreateTopic(topic):
    tp = kafka.admin.NewTopic(topic, 3, 1)
    admin.create_topics([tp])


def Send(topic, msg):
    produ.send(topic, msg.encode('utf-8'))


def test():
    if not IsTopicExists(TOPIC):
        print("自动创建测试topic: %s" % TOPIC)
        CreateTopic(TOPIC)
    id = 0
    while True:
        msg = json.dumps({id: id, "data": "TEST"})
        Send(TOPIC, msg)
        time.sleep(1)


if __name__ == "__main__":
    test()
