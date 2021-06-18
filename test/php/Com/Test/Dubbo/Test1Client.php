<?php
// GENERATED CODE -- DO NOT EDIT!

namespace Com\Test\Dubbo;

/**
 */
class Test1Client extends \Grpc\BaseStub {

    /**
     * @param string $hostname hostname
     * @param array $opts channel options
     * @param \Grpc\Channel $channel (optional) re-use channel object
     */
    public function __construct($hostname, $opts, $channel = null) {
        parent::__construct($hostname, $opts, $channel);
    }

    /**
     * 普通测试echo
     * @param \Com\Test\Dubbo\ReqTestEcho $argument input argument
     * @param array $metadata metadata
     * @param array $options call options
     * @return \Grpc\UnaryCall
     */
    public function echo(\Com\Test\Dubbo\ReqTestEcho $argument,
      $metadata = [], $options = []) {
        return $this->_simpleRequest('/com.test.dubbo.Test1/echo',
        $argument,
        ['\Com\Test\Dubbo\RspTestEcho', 'decode'],
        $metadata, $options);
    }

}
