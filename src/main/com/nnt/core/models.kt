package com.nnt.core

const val PAGED_LIMIT = 5000

@model()
class Null {}

// 需要登陆的空模型
@model([auth])
class AuthedNull {}

// Rest服务下ClientSDK的心跳更新
@model()
class RestUpdate {

    @integer(1, [output], "心跳间隔")
    var heartbeatTime: Int = 3

    @json(2, [output])
    var models: JsonObject? = null
}

// 具有顺序性的分页基类模型
@model()
open class SeqPaged {

    @integer(1, [input, output, optional], "排序依赖的最大数值")
    var last: Int = -1

    @integer(2, [input, optional], "一次拉取多少个")
    var limit: Int = 10

    @integer(3, [output], "数据总数")
    var total: Int = 0

    val skips: Int
        get() {
            return last + limit
        }
}

// 基于页码的分页数据模型
@model()
open class NumPaged {

    @integer(1, [input, output, optional], "请求的页码")
    var page: Int = 0

    @integer(2, [input, optional], "单页多少条数据")
    var limit: Int = 10

    @integer(3, [output], "数据总数")
    var total: Int = 0

    val skips: Int
        get() {
            return page * limit
        }

    val overflow: Boolean
        get() {
            return total > PAGED_LIMIT
        }
}
