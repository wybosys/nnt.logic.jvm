package com.nnt.core

// 定义内部的错误码
// <0的代表系统级错误，>0代表成功，但是需要额外处理，=0代表完全成功
enum class STATUS(val value: Int) {

    UNKNOWN(-1000),
    EXCEPTION(-999), // 遇到了未处理的异常
    ROUTER_NOT_FOUND(-998), // 没有找到路由
    CONTEXT_LOST(-997), // 上下文丢失
    MODEL_ERROR(-996), // 恢复模型失败
    PARAMETER_NOT_MATCH(-995), // 参数不符合要求
    NEED_AUTH(-994), // 需要登陆
    TYPE_MISMATCH(-993), // 参数类型错误
    FILESYSTEM_FAILED(-992), // 文件系统失败
    FILE_NOT_FOUND(-991), // 文件不存在
    ARCHITECT_DISMATCH(-990), // 代码不符合标准架构
    SERVER_NOT_FOUND(-989), // 没有找到服务器
    LENGTH_OVERFLOW(-988), // 长度超过限制
    TARGET_NOT_FOUND(-987), // 目标对象没有找到
    PERMISSION_FAILED(-986), // 没有权限
    WAIT_IMPLEMENTION(-985), // 等待实现
    ACTION_NOT_FOUND(-984), // 没有找到动作
    TARGET_EXISTS(-983), // 已经存在
    STATE_FAILED(-982), // 状态错误
    UPLOAD_FAILED(-981), // 上传失败
    MASK_WORD(-980), // 有敏感词
    SELF_ACTION(-979), // 针对自己进行操作
    PASS_FAILED(-978), // 验证码匹配失败
    OVERFLOW(-977), // 数据溢出
    AUTH_EXPIRED(-976), // 授权过期
    SIGNATURE_ERROR(-975), // 签名错误
    FORMAT_ERROR(-974),  // 返回的数据格式错误
    CONFIG_ERROR(-973), // 配置错误
    PRIVILEGE_ERROR(-972), // 权限错误
    LIMIT(-971), // 受到限制
    PAGED_OVERFLOW(-970), // 超出分页数据的处理能力
    NEED_ITEMS(-969), // 需要额外物品
    DECODE_ERROR(-968), // 解码失败
    ENCODE_ERROR(-967), // 编码失败

    IM_CHECK_FAILED(-899), // IM检查输入的参数失败
    IM_NO_RELEATION(-898), // IM检查双方不存在关系

    SOCK_WRONG_PORTOCOL(-860), // SOCKET请求了错误的通讯协议
    SOCK_AUTH_TIMEOUT(-859), // 因为连接后长期没有登录，所以服务端主动断开了链接
    SOCK_SERVER_CLOSED(-858), // 服务器关闭

    SECURITY_FAILED(-6), // 检测到安全问题
    THIRD_FAILED(-5), // 第三方出错
    MULTIDEVICE(-4), // 多端登陆
    HFDENY(-3), // 高频调用被拒绝（之前的访问还没有结束) high frequency deny
    TIMEOUT(-2), // 超时
    FAILED(-1), // 一般失败
    OK(0), // 成功
}

fun TO_STATUS(value: Int): STATUS {
    val r =
        when (value) {

            STATUS.EXCEPTION.value -> STATUS.EXCEPTION
            STATUS.ROUTER_NOT_FOUND.value -> STATUS.ROUTER_NOT_FOUND
            STATUS.CONTEXT_LOST.value -> STATUS.CONTEXT_LOST
            STATUS.MODEL_ERROR.value -> STATUS.MODEL_ERROR
            STATUS.PARAMETER_NOT_MATCH.value -> STATUS.PARAMETER_NOT_MATCH
            STATUS.NEED_AUTH.value -> STATUS.NEED_AUTH
            STATUS.TYPE_MISMATCH.value -> STATUS.TYPE_MISMATCH
            STATUS.FILESYSTEM_FAILED.value -> STATUS.FILESYSTEM_FAILED
            STATUS.FILE_NOT_FOUND.value -> STATUS.FILE_NOT_FOUND
            STATUS.ARCHITECT_DISMATCH.value -> STATUS.ARCHITECT_DISMATCH
            STATUS.SERVER_NOT_FOUND.value -> STATUS.SERVER_NOT_FOUND
            STATUS.LENGTH_OVERFLOW.value -> STATUS.LENGTH_OVERFLOW
            STATUS.TARGET_NOT_FOUND.value -> STATUS.TARGET_NOT_FOUND
            STATUS.PERMISSION_FAILED.value -> STATUS.PERMISSION_FAILED
            STATUS.WAIT_IMPLEMENTION.value -> STATUS.WAIT_IMPLEMENTION
            STATUS.ACTION_NOT_FOUND.value -> STATUS.ACTION_NOT_FOUND
            STATUS.TARGET_EXISTS.value -> STATUS.TARGET_EXISTS
            STATUS.STATE_FAILED.value -> STATUS.STATE_FAILED
            STATUS.UPLOAD_FAILED.value -> STATUS.UPLOAD_FAILED
            STATUS.MASK_WORD.value -> STATUS.MASK_WORD
            STATUS.SELF_ACTION.value -> STATUS.SELF_ACTION
            STATUS.PASS_FAILED.value -> STATUS.PASS_FAILED
            STATUS.OVERFLOW.value -> STATUS.OVERFLOW
            STATUS.AUTH_EXPIRED.value -> STATUS.AUTH_EXPIRED
            STATUS.SIGNATURE_ERROR.value -> STATUS.SIGNATURE_ERROR
            STATUS.FORMAT_ERROR.value -> STATUS.FORMAT_ERROR
            STATUS.CONFIG_ERROR.value -> STATUS.CONFIG_ERROR
            STATUS.PRIVILEGE_ERROR.value -> STATUS.PRIVILEGE_ERROR
            STATUS.LIMIT.value -> STATUS.LIMIT
            STATUS.PAGED_OVERFLOW.value -> STATUS.PAGED_OVERFLOW
            STATUS.NEED_ITEMS.value -> STATUS.NEED_ITEMS
            STATUS.DECODE_ERROR.value -> STATUS.DECODE_ERROR
            STATUS.ENCODE_ERROR.value -> STATUS.ENCODE_ERROR

            STATUS.IM_CHECK_FAILED.value -> STATUS.IM_CHECK_FAILED
            STATUS.IM_NO_RELEATION.value -> STATUS.IM_NO_RELEATION

            STATUS.SOCK_WRONG_PORTOCOL.value -> STATUS.SOCK_WRONG_PORTOCOL
            STATUS.SOCK_AUTH_TIMEOUT.value -> STATUS.SOCK_AUTH_TIMEOUT
            STATUS.SOCK_SERVER_CLOSED.value -> STATUS.SOCK_SERVER_CLOSED

            STATUS.SECURITY_FAILED.value -> STATUS.SECURITY_FAILED
            STATUS.THIRD_FAILED.value -> STATUS.THIRD_FAILED
            STATUS.MULTIDEVICE.value -> STATUS.MULTIDEVICE
            STATUS.HFDENY.value -> STATUS.HFDENY
            STATUS.TIMEOUT.value -> STATUS.TIMEOUT
            STATUS.FAILED.value -> STATUS.FAILED
            STATUS.OK.value -> STATUS.OK

            else -> STATUS.UNKNOWN
        }
    return r
}