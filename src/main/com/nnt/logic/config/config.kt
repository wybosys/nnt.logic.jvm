package com.nnt.logic.config

import com.nnt.logic.core.JsonObject
import com.nnt.logic.core.logger
import com.nnt.logic.manager.App
import com.nnt.logic.manager.Config

fun NodeIsEnable(node: JsonObject): Boolean {
    if (node["enable"] == null)
        return true
    if (!node["enable"].isTextual) {
        logger.warn("enable节点数据类型不是string")
        return false
    }
    val conds = node["enable"].asText().split(",")
    // 找到一个满足的即为满足
    val fnd = conds.firstOrNull() { e: String ->
        if (e.isEmpty())
            return false

        if (e == "debug")
            return Config.DEBUG;
        // 仅--develop打开
        if (e == "develop")
            return Config.DEVELOP;
        // 仅--publish打开
        if (e == "publish")
            return Config.PUBLISH;
        // 仅--distribution打开
        if (e == "distribution")
            return Config.DISTRIBUTION;
        // 处于publish或distribution打开
        if (e == "release")
            return Config.PUBLISH || Config.DISTRIBUTION;
        // 运行在devops容器中
        if (e == "devops")
            return Config.DEVOPS;
        // 容器内网测试版
        if (e == "devops-develop" || e == "devopsdevelop")
            return Config.DEVOPS_DEVELOP;
        // 容器发布版本
        if (e == "devops-release" || e == "devopsrelease")
            return Config.DEVOPS_RELEASE;
        // 本地运行
        if (e == "local")
            return Config.LOCAL;

        val args = App.args
        if (args.indexOf("--${e}") != -1)
            return true;

        return false
    }
    return fnd != null
}