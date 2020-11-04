package com.nnt.server

import com.nnt.core.*
import com.nnt.server.parser.AbstractParser
import com.nnt.server.render.AbstractRender
import java.time.LocalDateTime

const val RESPONSE_SID = "X-NntLogic-SessionId"

enum class DeviceType(val type: Int) {
    UNKNOWN(0),
    IOS(1),
    ANDROID(2),
}

class TransactionInfo {

    // 客户端代码
    var agent: String = "" // 全小写
    var ua: String = "" // 原始ua

    // 访问的主机
    var host: String = ""
    var origin: String = ""

    // 客户端的地址
    var addr: String = ""

    // 来源
    var referer: String = ""
    var path: String = ""

    private var _deviceType: DeviceType? = null

    // 设备机型
    val deviceType: DeviceType
        get() {
            if (this._deviceType != null)
                return this._deviceType!!
            if (this.agent.indexOf("iphone") != -1) {
                this._deviceType = DeviceType.IOS;
            } else if (this.agent.indexOf("ipad") != -1) {
                this._deviceType = DeviceType.IOS;
            } else if (this.agent.indexOf("android") != -1) {
                this._deviceType = DeviceType.ANDROID;
            } else {
                this._deviceType = DeviceType.UNKNOWN;
            }
            return this._deviceType!!
        }

}

class TransactionSubmitOption {

    // 仅输出模型
    var model: Boolean = false

    // 直接输出数据
    var raw: Boolean = false

    // 输出的类型
    var type: String? = null
}

abstract class Transaction {

    init {
        waitTimeout()
    }

    // 返回事务用来区分客户端的id，通常业务中实现为sid
    abstract fun sessionId(): String?

    // 获得同意个sid之下的客户端的id，和sid结合起来保证唯一性，即 sid.{cid}
    open fun clientId(): String? {
        return params["_cid"] as String?
    }

    // 是否是新连接上的客户端(包括客户端重启)
    open fun newOneClient(): Boolean {
        return params["_noc"] == "1"
    }

    // 动作
    private var _action: String = ""
    var action: String
        get() {
            return _action
        }
        set(act: String) {
            _action = act
            val p = _action.split(".")
            if (p.size != 2) {
                router = "null"
                call = "null"
            } else {
                router = p[0].toLowerCase()
                call = p[1].toLowerCase()
            }
        }

    // 映射到router的执行器中
    var router: String = ""
    var call: String = ""

    // 参数
    var params = mapOf<String, Any?>()

    // 执行的结果
    var status = STATUS.UNKNOWN

    // 错误信息
    var message: String? = null

    // 输出和输入的model
    var model: Any? = null

    // 基于哪个服务器运行
    var server: AbstractServer? = null

    // 是否需要压缩
    var gzip: Boolean = false

    // 是否已经压缩
    var compressed: Boolean = false

    // 是否暴露接口（通常只有登录会设置为true)
    var expose: Boolean = false

    // 此次的时间
    val time = LocalDateTime.now()

    // 恢复到model, 返回错误码
    open fun modelize(r: IRouter): STATUS {
        // 恢复模型
        val ap = FindAction(r, this.call)
        if (ap == null)
            return STATUS.ACTION_NOT_FOUND;
        expose = ap.expose

        // 获得模型类
        val clz = ap.clazz

        // 检查输入参数
        val sta = parser.checkInput(clz, params)
        if (sta != STATUS.OK)
            return sta

        // 填入数据到模型
        model = clz.constructors.first().call()
        try {
            parser.fill(model!!, params, true, false)
        } catch (err: Throwable) {
            model = null
            logger.fatal(err.toString())
            return STATUS.MODEL_ERROR
        }

        return STATUS.OK
    }

    // 恢复上下文，涉及到数据的恢复，所以是异步模式
    open suspend fun collect() {
        // pass
    }

    // 验证
    open fun needAuth(): Boolean {
        // return IsNeedAuth(model)
        return true
    }

    // 是否已经授权
    abstract fun auth(): Boolean

    // 同步模式会自动提交，异步模式需要手动提交
    var implSubmit: (opt: TransactionSubmitOption?) -> Unit = {}

    private var _submited: Boolean = false
    private var _submited_timeout: Boolean = false

    open suspend fun submit(opt: TransactionSubmitOption? = null) {

    }

    // 当提交的时候修改
    var hookSubmit: suspend () -> Unit = {}

    // 输出文件
    var implOutput: (type: String, obj: Any) -> Unit = { type, obj -> }
    private var _outputed: Boolean = false

    open fun output(type: String, obj: Any) {

    }

    protected open fun waitTimeout() {

    }

    // 部分api本来时间就很长，所以存在自定义timeout的需求
    fun timeout(seconds: Seconds) {

    }

    private fun _cbTimeout() {

    }

    // 超时定时器
    private lateinit var _timeout: DelayHandler

    // 运行在console中
    var console: Boolean = false

    // 带上此次请求事务的参数实例化一个模型
    // 通常业务层中会对params增加一些数据，来满足trans对auth、context的需求，如果直接new对象的化，就没办法加入这些数据
    /*
    open fun <T> instance(cls: Class<T>): T {

    }
     */

    // 环境信息
    val info = TransactionInfo()

    // 是否把sid返回客户端
    var responseSessionId: Boolean = false

    // 静默模式，不输出回调
    var quiet: Boolean = false

    // 用来解析传入数据
    lateinit var parser: AbstractParser

    // 用来构建输出
    lateinit var render: AbstractRender
}

class EmptyTransaction : Transaction() {

    override fun waitTimeout() {
        // pass 不进行计时
    }

    override fun sessionId(): String? {
        return null
    }

    override fun auth(): Boolean {
        return false
    }

}