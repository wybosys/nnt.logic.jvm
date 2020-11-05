package com.nnt.logger

import com.nnt.core.*
import com.nnt.manager.Config
import org.apache.log4j.*
import org.apache.log4j.Level

class Log4j : AbstractLogger() {

    var dir = URI("logs")
    var name = "app.log"
    var pattern = "[%d{dd/MM/yy hh:mm:ss:sss z}{TIMEZONE}] %p: %m%n"
    private lateinit var _logger: Logger

    override fun config(cfg: JsonObject): Boolean {
        if (!super.config(cfg))
            return false

        if (!cfg.has("name")) {
            println("${id} 没有配置日志名称")
            return false
        }
        name = cfg["name"]!!.asString()

        if (!cfg.has("dir")) {
            println("${id} 没有配置日志保存的目录")
            return false
        }
        dir = URI(cfg["dir"]!!.asString())
        if (!File(dir).exists()) {
            File(dir).mkdirs()
        }

        if (cfg.has("pattern")) {
            pattern = cfg["pattern"]!!.asString()
        }
        pattern = pattern.replace("{TIMEZONE}", "{${Config.TIMEZONE}}")

        val size = ByteUnit()
        var daily = false
        if (!cfg.has("roll")) {
            println("${id} 没有配置roll参数")
            return false
        }

        val nroll = cfg["roll"]!!
        if (nroll.has("size")) {
            val n = nroll["size"]!!
            if (n.isInteger()) {
                size.bytes = n.asInteger()
            } else {
                size.fromstr(n.asString())
            }
        }
        if (nroll.has("daily")) {
            daily = nroll["daily"]!!.asBoolean()
        }
        if (size.bytes == 0L && !daily) {
            println("${id} roll参数配置失败")
        }

        lateinit var apd: WriterAppender
        if (size.bytes > 0L) {
            val file = RollingFileAppender()
            file.file = "${dir.path}/${name}.log"
            file.immediateFlush = true
            file.append = true
            file.maximumFileSize = size.bytes
            file.maxBackupIndex = 1024
            apd = file
        } else {
            val file = DailyRollingFileAppender()
            file.file = "${dir.path}/${name}.log"
            file.immediateFlush = true
            file.append = true
            file.datePattern = "'.'yyyy-MM-dd"
            apd = file
        }

        val sl = EnhancedPatternLayout()
        sl.conversionPattern = pattern
        apd.layout = sl
        apd.name = id
        apd.activateOptions()

        // 注册
        BasicConfigurator.configure(apd)

        // 获取
        _logger = Logger.getLogger(id)!!
        _logger.level = Level.ALL // loggers层控制级别

        return true
    }

    override fun log(msg: String, status: STATUS?) {
        _logger.debug(msg)
    }

    override fun warn(msg: String, status: STATUS?) {
        _logger.warn(msg)
    }

    override fun info(msg: String, status: STATUS?) {
        _logger.info(msg)
    }

    override fun fatal(msg: String, status: STATUS?) {
        _logger.fatal(msg)
    }

    override fun exception(msg: String, status: STATUS?) {
        _logger.error(msg)
    }

}
