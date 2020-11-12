package com.nnt.store

import com.nnt.core.Null
import com.nnt.core.ToType
import com.nnt.core.logger
import com.nnt.core.toJsonObject
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

annotation class coltype(
    val type: KClass<*>
)

annotation class colenum(
    val name: String
)

annotation class coljson(
    val name: String,
    val type: KClass<*> = Null::class
)

class ColumnInfo {

    // 字段名
    var name: String? = null

    // 类型
    var string: Boolean = false
    var integer: Boolean = false
    var decimal: Boolean = false
    var boolean: Boolean = false
    var timestamp: Boolean = false
    var enum: Boolean = false
    var valtype: KClass<*>? = null
    var json: Boolean = false

    // 设置属性
    lateinit var property: KProperty<*>

    // 二级对象属性
    var typeproperty: KProperty<*>? = null
}

class ColumnInfos {

    // 使用名称作为映射的字段表
    private val _namedColumns = mutableMapOf<String, ColumnInfo>()

    // 字段表(有些字段定义为类型，但是数据库中栏位是平铺处理)
    private val _columns = mutableSetOf<ColumnInfo>()

    val namedColumns: Map<String, ColumnInfo> get() = _namedColumns
    val columns: Set<ColumnInfo> get() = _columns

    fun clear() {
        _namedColumns.clear()
        _columns.clear()
    }

    fun merge(r: ColumnInfos) {
        r._namedColumns.forEach {
            _namedColumns[it.key] = it.value
        }
        r._columns.forEach {
            _columns.add(it)
        }
    }

    fun add(ci: ColumnInfo) {
        if (ci.name != null) {
            _namedColumns[ci.name!!] = ci
            _columns.add(ci)
        } else if (ci.valtype != null) {
            val cis = UpdateTableColumnInfos(ci.valtype!!)
            // 合并二级属性
            cis._namedColumns.forEach { name, nci ->
                _namedColumns[name] = nci
            }
            cis._columns.forEach {
                // 有没有名字的都会出现在这里，绑定下二级prop
                it.valtype = ci.valtype
                it.typeproperty = ci.typeproperty

                _columns.add(it)
            }
        } else {
            logger.fatal("遇到未知的栏目定义")
        }
    }
}

class TableInfo {

    // 数据表名称
    var name: String = ""

    // 继承的类
    var parent: KClass<*>? = null

    // 字段
    var columns = ColumnInfos()

    // 字段名
    val allfields: String
        get() {
            return columns.namedColumns.keys.joinToString(",")
        }
}

private val _tables = mutableMapOf<KClass<*>, TableInfo?>()

fun IsTable(clz: KClass<*>): Boolean {
    return GetTableInfo(clz) != null
}

fun UpdateTableColumnInfos(clz: KClass<*>): ColumnInfos {
    val cols = ColumnInfos()

    // 数据表不支持继承
    clz.memberProperties.forEach { prop ->
        prop.annotations.forEach {
            when (it) {
                is colstring -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.string = true
                    t.property = prop
                    cols.add(t)
                }
                is colinteger -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.integer = true
                    t.property = prop
                    cols.add(t)
                }
                is coldecimal -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.decimal = true
                    t.property = prop
                    cols.add(t)
                }
                is colboolean -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.boolean = true
                    t.property = prop
                    cols.add(t)
                }
                is coltimestamp -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.timestamp = true
                    t.property = prop
                    cols.add(t)
                }
                is colenum -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.enum = true
                    t.property = prop
                    cols.add(t)
                }
                is coljson -> {
                    val t = ColumnInfo()
                    t.name = it.name
                    t.json = true
                    t.property = prop
                    if (it.type != Null::class)
                        t.valtype = it.type
                    cols.add(t)
                }
                is coltype -> {
                    val t = ColumnInfo()
                    t.valtype = it.type
                    t.typeproperty = prop
                    cols.add(t)
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
    ti.columns.namedColumns.forEach { name, fp ->
        if (!data.contains(name))
            return@forEach

        if (fp.property !is KMutableProperty<*>) {
            logger.warn("mdl参数 ${name} 只读")
            return@forEach
        }

        // 如果是二级
        if (fp.typeproperty != null) {
            // 获得一下当前对象上二级对象是否存在，不存在需要实例化
            var ref = fp.typeproperty!!.getter.call(mdl)
            if (ref == null) {
                if (fp.typeproperty !is KMutableProperty<*>) {
                    logger.warn("mdl参数 ${fp.typeproperty!!.name} 只读")
                    return@forEach
                }

                ref = fp.valtype!!.constructors.first().call()
                (fp.typeproperty as KMutableProperty<*>).setter.call(mdl, ref)
            }

            // 转换到定义的类型
            val v = ToType(data[name], fp.property.returnType)
            (fp.property as KMutableProperty<*>).setter.call(ref, v)
        } else {
            if (fp.json) {
                val p = data[name]
                if (p is String) {
                    val v = toJsonObject(p)?.flat()
                    (fp.property as KMutableProperty<*>).setter.call(mdl, v)
                }
            } else {
                // 转换到定义的类型
                val v = ToType(data[name], fp.property.returnType)
                (fp.property as KMutableProperty<*>).setter.call(mdl, v)
            }
        }
    }
}