<?php
// GENERATED CODE -- DO NOT EDIT!

namespace Com\Test\Dubbo;

/**
 * 测试hello的协议
 */
class TestClient extends \Grpc\BaseStub {

    /**
     * @param string $hostname hostname
     * @param array $opts channel options
     * @param \Grpc\Channel $channel (optional) re-use channel object
     */
    public function __construct($hostname, $opts, $channel = null) {
        parent::__construct($hostname, $opts, $channel);
    }

    /**
     * 返回欢迎
     * @param \Google\Protobuf\GPBEmpty $argument input argument
     * @param array $metadata metadata
     * @param array $options call options
     */
    public function hello(\Google\Protobuf\GPBEmpty $argument,
      $metadata = [], $options = []) {
        return $this->_simpleRequest('/com.test.dubbo.Test/hello',
        $argument,
        ['\Com\Test\Dubbo\TestReply', 'decode'],
        $metadata, $options);
    }

    /**
     * 普通测试echo
     * @param \Com\Test\Dubbo\ReqTestEcho $argument input argument
     * @param array $metadata metadata
     * @param array $options call options
     */
    public function echo(\Com\Test\Dubbo\ReqTestEcho $argument,
      $metadata = [], $options = []) {
        return $this->_simpleRequest('/com.test.dubbo.Test/echo',
        $argument,
        ['\Com\Test\Dubbo\RspTestEcho', 'decode'],
        $metadata, $options);
    }

    /**
     * 数据库添加一个echoo
     * @param \Google\Protobuf\StringValue $argument input argument
     * @param array $metadata metadata
     * @param array $options call options
     */
    public function echoo(\Google\Protobuf\StringValue $argument,
      $metadata = [], $options = []) {
        return $this->_simpleRequest('/com.test.dubbo.Test/echoo',
        $argument,
        ['\Com\Test\Echoo', 'decode'],
        $metadata, $options);
    }

    /**
     * 列表
     * @param \Google\Protobuf\GPBEmpty $argument input argument
     * @param array $metadata metadata
     * @param array $options call options
     */
    public function echoos(\Google\Protobuf\GPBEmpty $argument,
      $metadata = [], $options = []) {
        return $this->_simpleRequest('/com.test.dubbo.Test/echoos',
        $argument,
        ['\Com\Test\Echoos', 'decode'],
        $metadata, $options);
    }

    /**
     * 清空echoo
     * @param \Google\Protobuf\GPBEmpty $argument input argument
     * @param array $metadata metadata
     * @param array $options call options
     */
    public function clear_echoo(\Google\Protobuf\GPBEmpty $argument,
      $metadata = [], $options = []) {
        return $this->_simpleRequest('/com.test.dubbo.Test/clear_echoo',
        $argument,
        ['\Google\Protobuf\Int32Value', 'decode'],
        $metadata, $options);
    }

    /**
     * 更新
     * @param \Com\Test\Echoo $argument input argument
     * @param array $metadata metadata
     * @param array $options call options
     */
    public function update_echoo(\Com\Test\Echoo $argument,
      $metadata = [], $options = []) {
        return $this->_simpleRequest('/com.test.dubbo.Test/update_echoo',
        $argument,
        ['\Google\Protobuf\BoolValue', 'decode'],
        $metadata, $options);
    }

}
