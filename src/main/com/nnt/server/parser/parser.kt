package com.nnt.server.parser

import com.nnt.core.FieldOption
import com.nnt.core.STATUS
import com.nnt.core.logger
import kotlin.reflect.KClass

/**
 * Paser负责将不同协议传输的数据回写刀模型中，根据不同的协议，params有时为json，有时是字节流
 */
abstract class AbstractParser {

    // 检查模型和输入数据的匹配情况，返回status的错误码
    abstract fun checkInput(proto: KClass<*>, params: Map<String, *>): STATUS

    // 根据属性定义解码数据
    abstract fun decodeField(fp: FieldOption, value: Any?, input: Boolean, output: Boolean): Any

    // 将数据从参数集写入模型
    abstract fun fill(mdl: Any, params: Map<String, *>, input: Boolean, output: Boolean)

}

private val parsers = mutableMapOf<String, AbstractParser>()

fun RegisterParser(name: String, parser: AbstractParser) {
    if (parsers.containsKey(name)) {
        logger.fatal("重复注册解析器 $name")
        return
    }
    parsers[name] = parser
}

fun FindParser(name: String): AbstractParser {
    return parsers[name] ?: parsers["jsobj"]!!
}
