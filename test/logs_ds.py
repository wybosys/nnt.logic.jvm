#!/usr/bin/env python3

import kafka
import json
import time
import os
import datetime

# 循环给xaas-logs容器投递测试用日志数据

LOGS_DIR = "/storage/xaas-logs/logs"
LOGS_FILE = "%s/nnt-logic-logs-ds.log" % LOGS_DIR
MSGID = 0


def one():
    log = {
        "app": -1,
        "cmd": -1,
        "data": {
            "id": MSGID
        }
    }
    tm = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S,%f')[:-3]
    level = "test"
    msg = "[xaas %s] %s: %s\n" % (tm, level, json.dumps(log))
    open(LOGS_FILE, "a").write(msg)


def main():
    if not os.path.isdir(LOGS_DIR):
        print("请先启动 xaas-logs 容器")
        return

    global MSGID
    while True:
        one()
        time.sleep(1)
        MSGID += 1


if __name__ == "__main__":
    main()
