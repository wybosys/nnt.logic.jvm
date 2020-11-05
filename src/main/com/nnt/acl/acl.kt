package com.nnt.acl

import com.nnt.core.use

// 访问控制
class AcEntity {

    // 权限判断
    var ignore: Boolean = false

    // 错误检查
    var errorchk: Boolean = true

    // 配额检查
    var quota: Boolean = true

}

val ACROOT = use(AcEntity()) {
    it.ignore = true
    it.errorchk = false
    it.quota = false
}
