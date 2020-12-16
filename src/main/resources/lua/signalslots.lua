local NS = {}
local socket = require('socket')

------------------------------------------------------------------------
local DateTime = {}
NS.DateTime = DateTime
function DateTime:Now()
    return socket.gettime() * 1e6
end

function DateTime:Current()
    return os.time()
end

local function _define_class(cls)
    local function fn_new()
        local r = {}
        setmetatable(r, { __index = cls })
        return r
    end

    local function fn_dispose()
        -- PASS
    end

    cls.new = fn_new
    cls.init = fn_new
    cls.dispose = fn_dispose
    cls.fin = fn_dispose

    return cls
end

local Array = {
    _count = 0,
    _arr = nil
}
_define_class(Array)

function Array:new()
    local r = self.init()
    r._arr = {}
    return r
end

function Array:clone()
    local r = Array:new()
    for i = 1, self._count do
        table.insert(r._arr, self._arr[i])
    end
    r._count = self._count
    return r
end

function Array:pushs(arr)
    for i = 1, arr:size() do
        table.insert(self._arr, arr:at(i))
    end
    self._count = self._count + arr:size()
    return self
end

function Array:push(val)
    table.insert(self._arr, val)
    self._count = self._count + 1
    return self
end

function Array:size()
    return self._count
end

function Array:clear()
    self._arr = {}
    self._count = 0
    return self
end

function Array:at(idx)
    return self._arr[idx]
end

function Array:set(val, pos)
    table.insert(self._arr, pos, val)
    return self
end

function Array:has(val)
    for i = 1, self._count do
        if (self._arr[i] == val) then
            return true
        end
    end
    return false
end

function Array:index(val, def)
    if (def == nil) then
        def = -1
    end
    for i = 1, self._count do
        if (self._arr[i] == val) then
            return i
        end
    end
    return def
end

function Array:remove_objects(fn_filter)
    local kps = {}
    local rms = Array:new()
    for i = 1, self._count do
        if (not fn_filter(self._arr[i], i)) then
            table.insert(kps, self._arr[i])
        else
            rms:push(self._arr[i])
        end
    end
    self._arr = kps
    self._count = self._count - rms:size()
    return rms
end

function Array:query_object(fn_query, def)
    for i = 1, self._count do
        if (fn_query(self._arr[i], i)) then
            return self._arr[i]
        end
    end
    return def
end

local Set = {
    _count = 0,
    _arr = nil
}
_define_class(Set)

function Set:new()
    local r = self.init()
    r._arr = {}
    return r
end

function Set:push(val)
    if (not self:has(val)) then
        table.insert(self._arr, val)
        self._count = self._count + 1
    end
    return self
end

function Set:remove(val)
    local idx = self:index(val)
    if (idx ~= -1) then
        table.remove(self._arr, idx)
        self._count = self._count - 1
        return true
    end
    return false
end

function Set:has(val)
    for i = 1, self._count do
        if (self._arr[i] == val) then
            return true
        end
    end
    return false
end

function Set:index(val, def)
    if (def == nil) then
        def = -1
    end
    for i = 1, self._count do
        if (self._arr[i] == val) then
            return i
        end
    end
    return def
end

function Set:size()
    return self._count
end

function Set:at(idx)
    return self._arr[idx]
end

function Set:clear()
    self._arr = {}
    self._count = 0
    return self
end

local Map = {
    _count = 0,
    _keys = nil,
    _vals = nil
}
_define_class(Map)

function Map:new()
    local r = self.init()
    r._keys = Set:new()
    r._vals = Array:new()
    return r
end

function Map:has(k)
    return self._keys:has(k)
end

function Map:value(k, def)
    local fnd = self._keys:index(k)
    if (fnd == -1) then
        return def
    end
    return self._vals:at(fnd)
end

function Map:set(k, v)
    local fnd = self._keys:index(k)
    if (fnd == -1) then
        self._keys:push(k)
        self._vals:push(v)
        self._count = self._count + 1
    else
        self._vals:set(v, fnd)
    end
    return self
end

function Map:clear()
    self._keys:clear()
    self._vals:clear()
    self._count = 0
    return self
end

function Map:size()
    return self._count
end

function Map:at(idx)
    return self._vals:at(idx), self._keys:at(idx)
end

-----------------------------------------------------------------------
-- 用于穿透整个emit流程的对象
local SlotTunnel = {
    veto = false, -- 是否请求中断了emit过程
    payload = nil, -- 附加数据
}
_define_class(SlotTunnel)

-- 插槽对象
local Slot = {
    cb = nil, -- 回调
    target = nil, -- 回调的上下文
    sender = nil, -- 激发者
    data = nil, -- 数据
    tunnel = nil, -- 穿透用的数据
    payload = nil, -- connect 时附加的数据
    signal = nil, -- 信号源
    eps = 0, -- 激发频率限制 (emits per second)
    _epstms = nil,
    _veto = false, -- 是否中断掉信号调用树
    count = nil, -- 调用几次自动解绑，默认为 null，不使用概设定
    emitedCount = 0,
}
_define_class(Slot)

-- 激发信号
-- @data 附带的数据，激发后自动解除引用
function Slot:emit(data, tunnel)
    if (self.eps > 0) then
        local now = DateTime:Now()
        if (self._epstms == nil) then
            self._epstms = now
        else
            local el = now - self._epstms
            if ((1000 / el) > self.eps) then
                return
            else
                self._epstms = now
            end
        end
    end

    self.data = data
    self.tunnel = tunnel

    self:_doEmit()
end

function Slot:_doEmit()
    if (self.target) then
        if (self.cb) then
            self.cb(self)
        end
    elseif (self.cb) then
        self.cb(self)
    end

    self.data = nil
    self.tunnel = nil
    self.emitedCount = self.emitedCount + 1
end

local Slots = {
    slots = nil, -- 保存所有插槽
    owner = nil, -- 所有者，会传递到 Slot 的 sender
    signal = nil, -- 信号源
    _block = 0, -- 阻塞信号 @note emit被阻塞的信号将不会有任何作用
}
_define_class(Slots)

function Slots:new()
    local r = self.init()
    r.slots = Array:new()
    return r
end

function Slots:dispose()
    self.fin()
    self:clear()
    self.owner = nil
end

-- 清空连接
function Slots:clear()
    for i = 1, self.slots:size() do
        self.slots:at(i):dispose()
    end
    self.slots:clear()
end

function Slots:block()
    self._block = self._block + 1
end

function Slots:unblock()
    self._block = self._block - 1
end

-- 是否已经阻塞
function Slots:isblocked()
    return self._block ~= 0
end

-- 添加一个插槽
function Slots:add(s)
    self.slots:push(s)
end

-- 对所有插槽激发信号
-- @note 返回被移除的插槽的对象
function Slots:emit(data, tunnel)
    if (self:isblocked()) then
        return nil
    end
    local ids = nil
    local slots = self.slots:clone()
    for i = 1, slots:size() do
        local o = slots:at(i)
        repeat
            if (o.count ~= nil and o.emitedCount >= o.count) then
                break -- 激活数控制
            end

            -- 激发信号
            o.signal = self.signal
            o.sender = self.owner
            o:emit(data, tunnel)

            -- 控制激活数
            if (o.count ~= nil and o.emitedCount >= o.count) then
                if (ids == nil) then
                    ids = Array:new()
                end
                ids:push(i)
                break
            end
        until (true)

        if (o.veto) then
            break
        end
    end

    -- 删除用完的slot
    if (ids ~= nil) then
        local r = Set:new()
        function filter(e, idx)
            return not ids:has(idx)
        end
        local rms = self.slots:remove_objects(filter)
        for i = 1, rms:size() do
            local o = rms:at(i)
            if (o.target) then
                r:push(o.target)
            end
            -- 释放
            o:dispose()
        end
        return r
    end

    return nil
end

function Slots:disconnect(cb, target)
    function filter(e, idx)
        if (cb and e.cb ~= cb) then
            return false
        end
        if (e.target == target) then
            e:dispose()
            return true
        end
        return false
    end
    local rms = self.slots:remove_objects(filter)
    return rms:size() ~= 0
end

function Slots:find_connected_function(cb, target)
    function query(e, idx)
        return e.cb == cb and e.target == target
    end
    return self.slots:query_object(query)
end

function Slots:is_connected(target)
    function test(e, idx)
        return e.target == target
    end
    return self.slots:query_object(test) ~= nil
end

local Signals = {
    owner = nil, -- 信号的主体
    _slots = nil,
    __invtargets = nil, --  反向登记，当自身 dispose 时，需要和对方断开
}
_define_class(Signals)

function Signals:new(owner)
    if (owner == nil) then
        print("signals 对象必须通过 Object 实例化")
        return nil
    end

    local r = self.init()
    r.owner = owner
    r._slots = Map:new()
    r.__invtargets = Set:new()
    return r
end

function Signals:dispose()
    -- 反向断开连接
    for i = 1, self.__invtargets:size() do
        local o = self.__invtargets:at(i)
        if (o.owner and o.owner._signals) then
            o.owner._signals:disconnect_of_target(self.owner, false)
        end
    end
    self.__invtargets:clear()

    -- 清理信号，不能直接用clear的原因是clear不会断开对于owner的引用
    for i = 1, self._slots:size() do
        local o, k = self._slots:at(i)
        if (o) then
            o:dispose()
        end
    end
    self._slots:clear()

    self.owner = nil
end

function Signals:clear()
    -- 清空反向的连接
    for i = 1, self.__invtargets:size() do
        local o = self.__invtargets:at(i)
        if (o.owner and o.owner._signals) then
            o.owner._signals:disconnect_of_target(self.owner, false)
        end
    end
    self.__invtargets:clear()

    -- 清空slot的连接
    for i = 1, self._slots:size() do
        local o, k = self._slots:at(i)
        if (o) then
            o:clear()
        end
    end
end

-- 注册信号
function Signals:register(sig)
    if (sig == nil) then
        print("不能注册一个空信号")
        return false
    end

    if (self._slots:has(sig)) then
        return false
    end

    self._slots:set(sig, nil)
    return true
end

function Signals:_avaslots(sig)
    local ss = self._slots:value(sig, -1)
    if (ss == -1) then
        print("对象信号 " .. sig .. " 不存在")
        return nil
    end

    if (ss == nil) then
        ss = Slots:new()
        ss.signal = sig
        ss.owner = self.owner
        self._slots:set(sig, ss)
    end

    return ss
end

-- 只连接一次
function Signals:once(sig, cb, target)
    local r = self:connect(sig, cb, target)
    r.cout = 1
    return r
end

-- 连接信号插槽
function Signals:connect(sig, cb, target)
    local ss = self:_avaslots(sig)
    if (ss == nil) then
        print("对象信号 " .. sig .. " 不存在")
        return nil
    end

    local s = ss:find_connected_function(cb, target)
    if (s ~= nil) then
        return s
    end

    s = Slot:new()
    s.cb = cb
    s.target = target
    ss:add(s)

    self:__inv_connect(target)
    return s
end

-- 该信号是否存在连接上的插槽
function Signals:is_connected(sig)
    local ss = self:_avaslots(sig)
    return ss ~= nil and ss.slots:size() ~= 0
end

-- 激发信号
function Signals:emit(sig, data, tunnel)
    local ss = self._slots:value(sig, -1)
    if (ss ~= -1) then
        if (ss) then
            local targets = ss:emit(data, tunnel)
            if (targets) then
                -- 收集所有被移除的target，并断开反向连接
                for i = 1, targets:size() do
                    local target = targets:at(i)
                    if (not self:is_connected_of_target(target)) then
                        self:__inv_disconnect(target)
                    end
                end
            end
        end
    else
        print("对象信号 " .. sig .. " 不存在")
    end
end

-- 断开连接
function Signals:disconnect_of_target(target, inv)
    if (inv == nil) then
        inv = true
    end

    for i = 1, self._slots:size() do
        local ss = self._slots:at(i)
        if (ss) then
            ss:disconnect(nil, target)
        end
    end

    if (inv) then
        self.__inv_disconnect(target)
    end
end

-- 断开连接
function Signals:disconnect(sig, cb, target)
    local ss = self._slots:value(sig)
    if (ss == nil) then
        return
    end

    if (cb == nil and target == nil) then
        -- 清除sig的所有插槽，自动断开反向引用
        local targets = Set:new()
        for i = 1, ss.slots:size() do
            local o = ss.slots:at(i)
            if (o.target) then
                targets:push(o.target)
            end
            o:dispose()
        end
        ss.slots:clear()
        for i = 1, targets:size() do
            local tgt = targets:at(i)
            if (not self:is_connected_of_target(tgt)) then
                self:__inv_disconnect(tgt)
            end
        end
    end
end

function Signals:is_connected_of_target(target)
    for i = 1, self._slots:size() do
        local ss = self._slots:at(i)
        if (ss:is_connected(target)) then
            return true
        end
    end
    return false
end

-- 阻塞一个信号，将不响应激发
function Signals:block(sig)
    local ss = self._slots:value(sig)
    if (ss) then
        ss:block()
    end
end

function Signals:unblock(sig)
    local ss = self._slots:value(sig)
    if (ss) then
        ss:unblock()
    end
end

function Signals:isblocked(sig)
    local ss = self._slots:value(sig)
    if (ss) then
        return ss:isblocked()
    end
    return false
end

function Signals:__inv_connect(tgt)
    if (tgt == nil or tgt._signals == nil) then
        return
    end
    if (tgt._signals == self) then
        return
    end
    tgt._signals.__invtargets:push(self)
end

function Signals:__inv_disconnect(tgt)
    if (tgt == nil or tgt._signals == nil) then
        return
    end
    if (tgt._signals == self) then
        return
    end
    tgt._signals.__invtargets:remove(self)
end

local Object = {
    _signals = nil, -- 信号对象
    _count = 1,
}
NS.Object = _define_class(Object)

function Object:Declare(cls)
    for k, v in pairs(Object) do
        cls[k] = v
    end
    _define_class(cls)
end

function Object:signals()
    if (self._signals == nil) then
        self._signals = Signals:new(self)
    end
    return self._signals
end

function Object:grab()
    self._count = self._count + 1
end

function Object:drop()
    self._count = self._count - 1
    if (self._count == 0) then
        if (self._signals) then
            self._signals:dispose()
            self._signals = nil
        end
        return true
    end
    return false
end

-- 常用信号
NS.kSignalRemoved = "::nnt::removed"
NS.kSignalStarting = "::nnt::starting"
NS.kSignalStarted = "::nnt::started"
NS.kSignalStopping = "::nnt::stopping"
NS.kSignalStopped = "::nnt::stopped"
NS.kSignalAction = "::nnt::action"
NS.kSignalDone = "::nnt::done"

return NS
