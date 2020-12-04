package com.nnt.store

import com.nnt.store.reflect.TableInfo

typealias AffectedRows = Int

abstract class AbstractRdb : AbstractDbms() {

    // 获得实例对应的表信息
    abstract fun tables(): Map<String, TableInfo>

    // 获得指定表信息
    abstract fun table(name: String): TableInfo?

}
