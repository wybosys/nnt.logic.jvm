package com.nnt.signals

// 用于穿透整个emit流程的对象
class Tunnel {

    // 是否请求中断了emit过程
    var veto: Boolean = false

    // 附加数据
    var payload: Any? = null
}

typealias FnSlot = (s: Slot) -> Unit
typealias signal_t = String

// 获得当前时间
fun TimeCurrent(): Double {
    val tm = System.currentTimeMillis()
    return tm * 0.001
}

// 插槽对象
class Slot {

    // 处理调用
    var cb: FnSlot? = null

    // 携带数据
    var data: Any? = null

    // 穿透整个调用流程的数据
    var tunnel: Tunnel? = null

    // 信号源名称
    var signal: signal_t = ""

    // 激发频率限制 (emits per second)
    var eps: Int = 0

    // 激活信号的对象
    var sender: Object? = null

    // 是否中断掉信号调用树
    var veto: Boolean
        get() {
            return _veto
        }
        set(value) {
            _veto = value
            if (tunnel != null)
                tunnel!!.veto = value
        }

    // 调用几次自动解绑，默认为 0，不使用概设定
    var count: Int = 0
    var emitedCount: Int = 0

    // 激发信号 @data 附带的数据，激发后自动解除引用
    fun emit(data: Any?, tunnel: Tunnel?) {
        if (eps > 0) {
            val now = TimeCurrent()
            if (_epstm == 0.0) {
                _epstm = now
            } else {
                val el = now - _epstm
                //this._epstms = now; 注释以支持快速多次点击中可以按照频率命中一次，而不是全部都忽略掉
                if ((1000 / el) > eps)
                    return
                _epstm = now //命中一次后重置时间
            }
        }

        this.data = data
        this.tunnel = tunnel

        _doEmit()
    }

    private fun _doEmit() {
        if (cb != null) {
            cb!!(this)
        }

        // 清理
        data = null
        tunnel = null

        // 增加计数
        ++emitedCount
    }

    private var _epstm: Double = 0.0
    private var _veto: Boolean = false
}

// 插槽组
class Slots(signals: Signals) {

    private val _signals = signals

    // 信号源名称
    var signal: signal_t = ""

    // 隶属对象
    var owner: Object? = null

    // 清空连接
    fun clear() {
        _slots.clear()
    }

    // 阻塞所有插槽
    fun block() {
        ++_blk
    }

    // 解阻
    fun unblock() {
        --_blk
    }

    // 是否已经阻塞
    fun isblocked(): Boolean {
        return _blk != 0
    }

    // 添加一个插槽
    fun add(s: Slot) {
        _slots.add(s)
    }

    // 对所有插槽激发信号 @note 返回被移除的插槽的对象
    fun emit(data: Any?, tunnel: Tunnel?) {
        if (isblocked())
            return

        val snaps = _slots.toList()
        for (s in snaps) {
            if (s.count > 0 && (s.emitedCount >= s.count)) {
                _slots.remove(s)
                continue // 已经达到设置激活的数量
            }

            // 激发信号
            s.signal = signal
            s.sender = owner
            s.emit(data, tunnel)

            // 判断激活数是否达到设置
            if (s.count > 0 && (s.emitedCount >= s.count)) {
                _slots.remove(s)
            }

            // 阻断，停止执行
            if (s.veto)
                break
        }
    }

    // 移除
    fun disconnect(cb: FnSlot): Boolean {
        val iter = _slots.iterator()
        while (iter.hasNext()) {
            val e = iter.next()
            if (e.cb == cb) {
                iter.remove()
            }
        }
        return true
    }

    // 查找插槽
    fun find(cb: FnSlot): Slot? {
        val iter = _slots.iterator()
        while (iter.hasNext()) {
            val e = iter.next()
            if (e.cb == cb)
                return e
        }
        return null
    }

    // 已经连接的插槽数量
    fun size(): Int {
        return _slots.size
    }

    // 保存所有插槽
    private var _slots = mutableListOf<Slot>()

    // 阻塞信号计数器 @note emit被阻塞的信号将不会有任何作用
    private var _blk = 0

}

// 信号组
class Signals(
    // 信号的主体
    val owner: Object
) {

    // 清空
    fun clear() {
        _signals.clear()
    }

    // 注册信号
    fun register(sig: signal_t): Boolean {
        if (_signals.containsKey(sig))
            return false

        val ss = Slots(this)
        ss.signal = sig
        ss.owner = owner
        _signals[sig] = ss
        return true
    }

    // 返回指定信号的所有插槽
    fun find(sig: signal_t): Slots? {
        return _signals.get(sig)
    }

    // 只连接一次，调用后自动断开
    fun once(sig: signal_t, cb: FnSlot): Slot? {
        val r = connect(sig, cb)
        if (r != null)
            r.count = 1
        return r
    }

    // 连接信号插槽
    fun connect(sig: signal_t, cb: FnSlot): Slot? {
        if (!_signals.containsKey(sig)) {
            println("信号 ${sig} 不存在")
            return null
        }
        val ss = _signals[sig]!!

        // 判断是否已经连接
        var s = ss.find(cb)
        if (s != null)
            return s

        s = Slot()
        s.cb = cb
        ss.add(s)

        return s
    }

    // 该信号是否存在连接上的插槽
    fun isConnected(sig: signal_t): Boolean {
        val ss = _signals[sig]
        if (ss == null)
            return false
        return ss.size() > 0
    }

    // 激发信号
    fun emit(sig: signal_t, data: Any? = null, tunnel: Tunnel? = null) {
        val ss = _signals[sig]
        if (ss == null) {
            println("对象信号 ${sig} 不存在")
            return
        }

        ss.emit(data, tunnel)
    }

    // 断开连接
    fun disconnect(sig: signal_t, cb: FnSlot? = null) {
        val ss = _signals[sig]
        if (ss == null)
            return
        if (cb != null) {
            ss.disconnect(cb)
        } else {
            ss.clear()
        }
    }

    // 阻塞一个信号，将不响应激发
    fun block(sig: signal_t) {
        val ss = _signals[sig]
        ss?.block()
    }

    // 解阻
    fun unblock(sig: signal_t) {
        val ss = _signals[sig]
        ss?.unblock()
    }

    // 是否阻塞
    fun isblocked(sig: signal_t): Boolean {
        val ss = _signals[sig]
        return ss?.isblocked() ?: false
    }

    private val _signals = mutableMapOf<signal_t, Slots>()
}

// 基础对象，用于实现成员函数插槽
open class Object {

    protected var _signals: Signals? = null

    protected open fun _initSignals() {
        // pass
    }

    val signals: Signals
        get() {
            if (_signals == null) {
                synchronized(this) {
                    if (_signals == null) {
                        _signals = Signals(this)
                        _initSignals()
                    }
                }
            }
            return _signals!!
        }
}

// 定义常用信号
val kSignalChanged = "::nnt::changed"
val kSignalChanging = "::nnt::changing"
val kSignalStarting = "::nnt::starting"
val kSignalStarted = "::nnt::started"
val kSignalStopping = "::nnt::stopping"
val kSignalStopped = "::nnt::stopped"
val kSignalCompleting = "::nnt::completing"
val kSignalCompleted = "::nnt::completed"
val kSignalError = "::nnt::error"
val kSignalDone = "::nnt::done"
val kSignalNew = "::nnt::new"
val kSignalYes = "::nnt:yes"
val kSignalNo = "::nnt::no"
val kSignalCancel = "::nnt::cancel"