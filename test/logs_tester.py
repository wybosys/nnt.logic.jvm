#!/usr/bin/env python3

import kafka

import kafka
import json
import time

TOPIC = "xaas-logs"
HOST = "xaas-kafka:9092,xaas-kafka:9093,xaas-kafka:9094"

cli = kafka.KafkaConsumer(
    TOPIC,
    bootstrap_servers=HOST
)


def test():
    for msg in cli:
        print(msg.value.decode('utf-8'))


if __name__ == "__main__":
    test()
