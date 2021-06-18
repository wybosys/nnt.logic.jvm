#!/usr/bin/env python3

import argparse
import os
import re
import subprocess
from typing import List

# 使用python生成
# python3 -m grpc_tools.protoc --python_out=py --grpc_python_out=py -I../src/main/proto dubbo/test.proto dao.proto

# 使用protoc生成
# protoc --proto_path=../src/main/proto --plugin=protoc-gen-grpc= /usr/bin/grpc_python_plugin --python_out=py --grpc_out=py dubbo/test.proto dao.proto

# 保存所有proto的目录
PROTO_DIR = "../src/main/proto"
PROTO_ALLOW = [re.compile("\.proto$")]
PROTO_DENY = [re.compile("^[~#]")]


def checkpat(inp, pats):
    for pat in pats:
        if pat.findall(inp):
            return True
    return False


def listall(dir, allows, denys):
    ret = []
    """扫描所有文件"""
    for each in os.listdir(dir):
        tgt = "%s/%s" % (dir, each)
        if os.path.isfile(tgt):
            if not checkpat(each, allows):
                continue
            if checkpat(each, denys):
                continue
            ret.append(tgt)
        else:
            ret += listall(tgt, allows, denys)
    return ret

# 构造生成的脚本
#CMDS = []
# CMD_PYTHON = [
#    "protoc --proto_path="+PROTO_DIR,
#    "--plugin=protoc-gen-grpc=/usr/bin/grpc_python_plugin",
#    "--python_out=py --grpc_out=py",
#    ' '.join(PROTOS)
# ]
#cmd = ' '.join(CMD_PYTHON)
# CMDS.append(cmd)

# CMD_PHP = [
#    "protoc --proto_path="+PROTO_DIR,
#    "--plugin=protoc-gen-grpc=/usr/bin/grpc_php_plugin",
#    "--php_out=php --grpc_out=php",
#    ' '.join(PROTOS)
# ]
#cmd = ' '.join(CMD_PHP)
# CMDS.append(cmd)

# 生成php自动加载
#CMDS.append("cd php && composer dump-autoload && cd -")

# 生成
# for cmd in CMDS:
# print(cmd)
#    os.system(cmd)


def FindTool(cmd: str) -> str:
    (sta, out) = subprocess.getstatusoutput('which %s' % cmd)
    if sta == 0:
        return out
    return None


def GenPy(protos: List[str]):
    gr = "python3 -m grpc_tools.protoc --python_out=py --grpc_python_out=py -I../src/main/proto %s" % (
        ' '.join(protos))
    (sta, out) = subprocess.getstatusoutput(gr)
    if sta != 0:
        raise Exception(out)


def GenPHP(protos: List[str]):
    plugin = FindTool('grpc_php_plugin')
    if not plugin:
        raise Exception('没有找到 grpc_php_plugin')
    gr = "protoc --proto_path=../src/main/proto --plugin=protoc-gen-grpc=%s --php_out=php --grpc_out=php %s" % (
        plugin, ' '.join(protos))
    (sta, out) = subprocess.getstatusoutput(gr)
    if sta != 0:
        raise Exception(out)


if __name__ == '__main__':
    args = argparse.ArgumentParser()
    args.add_argument('type', choices=['py', 'php', 'all'])
    args = args.parse_args()

    protos = listall(PROTO_DIR, PROTO_ALLOW, PROTO_DENY)
    print('生成 %s' % ' '.join(protos))

    if args.type == 'py':
        GenPy(protos)
    elif args.type == 'php':
        GenPHP(protos)
    else:
        GenPy(protos)
        GenPHP(protos)
