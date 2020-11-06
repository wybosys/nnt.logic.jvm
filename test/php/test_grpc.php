#!/usr/bin/env php
<?php

use Com\Test\Dubbo\TestClient;
use Google\Protobuf\GPBEmpty;
use Google\Protobuf\StringValue;
use Grpc\Channel;

require dirname(__FILE__) . '/vendor/autoload.php';

define("HOST", "localhost:8093");

function println($msg)
{
    echo "${msg}\n";
}

function test($idx)
{
    println("test ${idx}");

    $chann = new Channel(HOST, []);
    $stub = new TestClient(null, null, $chann);

    // 测试hello
    [$reply, $status] = $stub->hello(new GPBEmpty())->wait();
    println("收到 {$reply->getMessage()}");

    # 测试echoo
    $req = new StringValue();
    $req->setValue("test echoo");
    [$reply, $status] = $stub->echoo($req)->wait();
    println("收到数据 " . $reply->getId() . " " . $reply->getOutput());

    # 修改
    # response.id = 5555
    $reply->setOutput("modified");
    [$reply, $status] = $stub->update_echoo($reply)->wait();
    if ($reply->getValue())
        println("修改成功");
    else
        println("修改失败");

    # 查询
    [$reply, $status] = $stub->echoos(new GPBEmpty())->wait();
    println("收到 " . count($reply->getItem()) . " 条数据");
    foreach ($reply->getItem() as $e) {
        println($e->getId() . " " . $e->getOutput());
    }

    # 清空echoo
    [$reply, $status] = $stub->clear_echoo(new GPBEmpty())->wait();
    println("清空 {$reply->getValue()} 条数据");
}

for ($i = 0; $i < 100; ++$i)
    test($i);