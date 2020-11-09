package com.nnt.store

import com.nnt.core.Null
import com.nnt.core.ToType
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

annotation class table(
    val name: String,
    val parent: KClass<*> = Null::class
)

annotation class colstring(
    val name: String
)

annotation class colinteger(
    val name: String
)

annotation class colboolean(
    val name: String
)

annotation class coldecimal(
    val name: String
)

annotation class coltimestamp(
    val name: String
)

class ColumnInfo {

    // 字段名
    lateinit var name: String

    // 类型
    var string: Boolean = false
    var integer: Boolean = false
    var decimal: Boolean = false
    var boolean: Boolean = false
    var timestamp: Boolean = false

    // 设置属性
    lateinit var property: KProperty<*>
}

typealias ColumnInfos = Map<String, ColumnInfo>

class TableInfo {

    // 数据表名称
    var name: String = ""

    // 继承的类
    var parent: KClass<*>? = null

    // 字段
    var columns: ColumnInfos = mapOf()

    // 字段名
    val allfields: String
        get() {
            return columns.keys.joinToString(",")
        }
}

private val _tables = mutableMapOf<KClass<*>, TableInfo?>()

fun IsTable(clz: KClass<*>): Boolean {
    return GetTableInfo(clz) != null
}

fun UpdateTableColumnInfos(clz: KClass<*>): ColumnInfos {
    val cols = mutableMapOf<String, ColumnInfo>()

    // 数据表不支持继承
    clz.memberProperties.forEach { prop ->
        prop.annotations.forEach {
            when (it) {
                is colstring -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.string = true
                    t.property = prop
                    cols[t.name] = t
                }
                is colinteger -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.integer = true
                    t.property = prop
                    cols[t.name] = t
                }
                is coldecimal -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.decimal = true
                    t.property = prop
                    cols[t.name] = t
                }
                is colboolean -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.boolean = true
                    t.property = prop
                    cols[t.name] = t
                }
                is coltimestamp -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.timestamp = true
                    t.property = prop
                    cols[t.name] = t
                }
            }
        }
    }

    return cols
}

fun UpdateTableInfo(clz: KClass<*>): TableInfo? {
    val decl_table = clz.annotations.firstOrNull {
        it is table
    } as table?
    if (decl_table == null) {
        _tables[clz] = null
        return null
    }

    val ti = TableInfo()
    ti.name = decl_table.name
    ti.parent = if (decl_table.parent == Null::class) null else decl_table.parent
    ti.columns = UpdateTableColumnInfos(clz)
    _tables[clz] = ti

    return ti
}

fun GetTableInfo(clz: KClass<*>): TableInfo? {
    if (_tables.contains(clz)) {
        return _tables[clz]
    }
    return UpdateTableInfo(clz)
}

fun Fill(mdl: Any, data: Map<String, Any>) {
    val ti = GetTableInfo(mdl.javaClass.kotlin)
    if (ti == null)
        return
    Fill(mdl, data, ti)
}

fun Fill(mdl: Any, data: Map<String, Any>, ti: TableInfo) {
    ti.columns.forEach { name, fp ->
        if (!data.contains(name))
            return@forEach
        if (fp.property !is KMutableProperty<*>)
            return@forEach
        (fp.property as KMutableProperty<*>).setter.call(mdl, ToType(data[name], fp.property.returnType))
    }
}