package com.nnt.store

import com.nnt.core.JsonObject
import com.nnt.core.logger
import com.nnt.core.toValue
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.*
import org.springframework.jdbc.datasource.SingleConnectionDataSource
import org.springframework.jdbc.support.KeyHolder
import org.springframework.jdbc.support.rowset.SqlRowSet
import java.sql.Connection
import javax.sql.DataSource
import kotlin.reflect.KClass

typealias JdbcProperties = HikariConfig

open class Jdbc : AbstractDbms() {

    var url: String = ""
    var user: String = ""
    var pwd: String = ""
    var driver: String = ""

    override fun config(cfg: JsonObject): Boolean {
        if (!super.config(cfg))
            return false

        if (!cfg.has("url")) {
            logger.fatal("${id} 没有配置url")
            return false
        }
        url = cfg["url"]!!.asString()

        if (!cfg.has("driver")) {
            logger.fatal("${id} 没有配置driver")
            return false
        }
        driver = cfg["driver"]!!.asString()

        if (cfg.has("user"))
            user = cfg["user"]!!.asString()
        if (cfg.has("pwd"))
            pwd = cfg["pwd"]!!.asString()

        return true
    }

    private lateinit var _dsfac: DataSource

    protected open fun propertiesForJdbc(): JdbcProperties {
        val props = DefaultJdbcProperties()
        props.driverClassName = driver
        props.jdbcUrl = url
        if (!user.isEmpty()) {
            props.username = user
            if (!pwd.isEmpty())
                props.password = pwd
        }
        return props
    }

    override fun open() {
        val props = propertiesForJdbc()
        _dsfac = HikariDataSource(props)
        logger.info("打开 ${id}@jdbc")
    }

    override fun close() {
        // pass
    }

    fun acquire(): JdbcSession {
        val tpl = JdbcTemplate(_dsfac)
        return JdbcSession(tpl)
    }

    override fun acquireSession(): ISession {
        return acquire()
    }

    // 直接执行sql语句返回原始数据类型
    fun execute(
        proc: (tpl: JdbcTemplate, conn: Connection) -> Unit,
    ): Boolean {
        var r = true
        var conn: Connection? = null
        try {
            conn = _dsfac.connection
            val tpl = JdbcTemplate(SingleConnectionDataSource(conn, true))
            proc(tpl, conn)
        } catch (err: Throwable) {
            logger.exception(err)
            r = false
        } finally {
            conn?.close()
        }
        return r
    }

    companion object {

        fun DefaultJdbcProperties(): JdbcProperties {
            val props = JdbcProperties()
            props.poolName = "nnt.logic"
            props.minimumIdle = 0
            props.maximumPoolSize = 512
            props.connectionTestQuery = "select 1"
            return props
        }
    }

}

// jdbc业务对象
open class JdbcSession : ISession {

    constructor(tpl: JdbcTemplate) {
        _tpl = tpl
    }

    protected constructor() {
        // 有些session需要特殊情况发生时才初始化tpl
    }

    // 不使用 JdbcOperations by tpl 的写法是因为会造成编译器warnning
    protected var _tpl: JdbcTemplate? = null

    protected open fun tpl(): JdbcTemplate {
        synchronized(this) {
            return _tpl!!
        }
    }

    override open fun close() {
        // pass
    }

    override open fun commit() {
        // pass
    }

    override fun rollback() {
        // pass
    }

    protected fun finalize() {
        close()
    }

    // 代理

    open fun <T> query(sql: String, rse: ResultSetExtractor<T>): T? {
        try {
            return tpl().query(sql, rse)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun query(sql: String, rch: RowCallbackHandler): Boolean {
        try {
            tpl().query(sql, rch)
            return true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return false
    }

    open fun <T> query(sql: String, rowMapper: RowMapper<T>): List<T> {
        try {
            return tpl().query(sql, rowMapper)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return listOf()
    }

    open fun <T : Any> queryForList(sql: String, elementType: KClass<T>): List<T> {
        try {
            return tpl().queryForList(sql, elementType.java)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return listOf()
    }

    open fun queryForList(sql: String): List<Map<String, Any>> {
        try {
            return tpl().queryForList(sql)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return listOf()
    }

    open fun <T> query(sql: String, pss: PreparedStatementSetter, rse: ResultSetExtractor<T>): T? {
        try {
            return tpl().query(sql, pss, rse)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T> query(sql: String, args: Array<Any>, argTypes: IntArray, rse: ResultSetExtractor<T>): T? {
        try {
            return tpl().query(sql, args, argTypes, rse)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T> query(sql: String, args: Array<Any>, rse: ResultSetExtractor<T>): T? {
        try {
            return tpl().query(sql, args, rse)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T> query(sql: String, rse: ResultSetExtractor<T>, vararg args: Any): T? {
        try {
            return tpl().query(sql, rse, *args)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun query(psc: PreparedStatementCreator, rch: RowCallbackHandler): Boolean {
        try {
            tpl().query(psc, rch)
            return true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return false
    }

    open fun query(sql: String, pss: PreparedStatementSetter, rch: RowCallbackHandler): Boolean {
        try {
            tpl().query(sql, pss, rch)
            return true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return false
    }

    open fun query(sql: String, args: Array<Any>, argTypes: IntArray, rch: RowCallbackHandler): Boolean {
        try {
            tpl().query(sql, args, argTypes, rch)
            return true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return false
    }

    open fun query(sql: String, args: Array<Any>, rch: RowCallbackHandler): Boolean {
        try {
            tpl().query(sql, args, rch)
            return true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return false
    }

    open fun query(sql: String, rch: RowCallbackHandler, vararg args: Any): Boolean {
        try {
            tpl().query(sql, rch, *args)
            return true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return false
    }

    open fun <T> query(psc: PreparedStatementCreator, rowMapper: RowMapper<T>): List<T> {
        try {
            return tpl().query(psc, rowMapper)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return listOf()
    }

    open fun <T> query(sql: String, pss: PreparedStatementSetter, rowMapper: RowMapper<T>): List<T> {
        try {
            return tpl().query(sql, pss, rowMapper)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return listOf()
    }

    open fun <T> query(sql: String, args: Array<Any>, argTypes: IntArray, rowMapper: RowMapper<T>): List<T> {
        try {
            return tpl().query(sql, args, argTypes, rowMapper)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return listOf()
    }

    open fun <T> query(sql: String, args: Array<Any>, rowMapper: RowMapper<T>): List<T> {
        try {
            return tpl().query(sql, args, rowMapper)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return listOf()
    }

    open fun <T> query(sql: String, rowMapper: RowMapper<T>, vararg args: Any): List<T> {
        try {
            return tpl().query(sql, rowMapper, *args)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return listOf()
    }

    open fun <T> queryForObject(sql: String, rowMapper: RowMapper<T>): T? {
        try {
            return tpl().queryForObject(sql, rowMapper)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T : Any> queryForObject(sql: String, requiredType: KClass<T>): T? {
        try {
            return tpl().queryForObject(sql, requiredType.java)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T> queryForObject(sql: String, args: Array<Any>, argTypes: IntArray, rowMapper: RowMapper<T>): T? {
        try {
            return tpl().queryForObject(sql, args, argTypes, rowMapper)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T> queryForObject(sql: String, args: Array<Any>, rowMapper: RowMapper<T>): T? {
        try {
            return tpl().queryForObject(sql, args, rowMapper)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T> queryForObject(sql: String, rowMapper: RowMapper<T>, vararg args: Any): T? {
        try {
            return tpl().queryForObject(sql, rowMapper, *args)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T : Any> queryForObject(sql: String, args: Array<Any>, argTypes: IntArray, requiredType: KClass<T>): T? {
        try {
            return tpl().queryForObject(sql, args, argTypes, requiredType.java)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T : Any> queryForObject(sql: String, args: Array<Any>, requiredType: KClass<T>): T? {
        try {
            return tpl().queryForObject(sql, args, requiredType.java)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T : Any> queryForObject(sql: String, requiredType: KClass<T>, vararg args: Any): T? {
        try {
            return tpl().queryForObject(sql, requiredType.java, *args)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun queryForMap(sql: String): Map<String, Any>? {
        try {
            return tpl().queryForMap(sql)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun queryForMap(sql: String, args: Array<Any>, argTypes: IntArray): Map<String, Any>? {
        try {
            return tpl().queryForMap(sql, args, argTypes)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun queryForMap(sql: String, vararg args: Any): Map<String, Any>? {
        try {
            return tpl().queryForMap(sql, *args)
        } catch (err: EmptyResultDataAccessException) {
            // pass
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T : Any> queryForList(sql: String, args: Array<Any>, elementType: KClass<T>): List<T> {
        try {
            val ti = GetTableInfo(elementType)
            if (ti == null) {
                return tpl().queryForList(sql, toValue(args), elementType.java)
            }

            // 模型类型
            val r = tpl().queryForList(sql, *toValue(args))
            return r.map {
                val t = elementType.constructors.first().call()
                Fill(t, it, ti)
                t
            }
        } catch (err: Throwable) {
            // pass
        }
        return listOf()
    }

    open fun <T : Any> queryForList(sql: String, elementType: KClass<T>, vararg args: Any): List<T> {
        try {
            val ti = GetTableInfo(elementType)
            if (ti == null) {
                return tpl().queryForList(sql, elementType.java, *toValue(args))
            }

            // 模型类型
            val r = tpl().queryForList(sql, *toValue(args))
            return r.map {
                val t = elementType.constructors.first().call()
                Fill(t, it, ti)
                t
            }
        } catch (err: Throwable) {
            // pass
        }
        return listOf()
    }

    open fun queryForList(sql: String, args: Array<Any>, argTypes: IntArray): List<Map<String, Any>> {
        try {
            return tpl().queryForList(sql, args, argTypes)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return listOf()
    }

    open fun queryForList(sql: String, vararg args: Any): List<Map<String, Any>> {
        try {
            return tpl().queryForList(sql, *args)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return listOf()
    }

    open fun queryForRowSet(sql: String): SqlRowSet? {
        try {
            return tpl().queryForRowSet(sql)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun queryForRowSet(sql: String, args: Array<Any>, argTypes: IntArray): SqlRowSet? {
        try {
            return tpl().queryForRowSet(sql, args, argTypes)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun queryForRowSet(sql: String, vararg args: Any): SqlRowSet? {
        try {
            return tpl().queryForRowSet(sql, *args)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun update(sql: String): Int {
        try {
            return tpl().update(sql)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return 0
    }

    open fun update(psc: PreparedStatementCreator): Int {
        try {
            return tpl().update(psc)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return 0
    }

    open fun update(psc: PreparedStatementCreator, generatedKeyHolder: KeyHolder): Int {
        try {
            return tpl().update(psc, generatedKeyHolder)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return 0
    }

    open fun update(sql: String, pss: PreparedStatementSetter): Int {
        try {
            return tpl().update(sql, pss)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return 0
    }

    open fun update(sql: String, args: Array<Any>, argTypes: IntArray): Int {
        try {
            return tpl().update(sql, args, argTypes)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return 0
    }

    open fun update(sql: String, vararg args: Any?): Int {
        try {
            return tpl().update(sql, *args)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return 0
    }

    open fun batchUpdate(vararg sql: String): IntArray {
        try {
            return tpl().batchUpdate(*sql)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return intArrayOf()
    }

    open fun batchUpdate(sql: String, pss: BatchPreparedStatementSetter): IntArray {
        try {
            return tpl().batchUpdate(sql, pss)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return intArrayOf()
    }

    open fun batchUpdate(sql: String, batchArgs: List<Array<Any>>): IntArray {
        try {
            return tpl().batchUpdate(sql, batchArgs)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return intArrayOf()
    }

    open fun batchUpdate(sql: String, batchArgs: List<Array<Any>>, argTypes: IntArray): IntArray {
        try {
            return tpl().batchUpdate(sql, batchArgs, argTypes)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return intArrayOf()
    }

    open fun <T> batchUpdate(
        sql: String,
        batchArgs: Collection<T>,
        batchSize: Int,
        pss: ParameterizedPreparedStatementSetter<T>,
    ): Array<IntArray> {
        try {
            return tpl().batchUpdate(sql, batchArgs, batchSize, pss)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return arrayOf()
    }

    open fun <T> execute(action: ConnectionCallback<T>): T? {
        try {
            return tpl().execute(action)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T> execute(action: StatementCallback<T>): T? {
        try {
            return tpl().execute(action)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun execute(sql: String): Boolean {
        try {
            tpl().execute(sql)
            return true
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return false
    }

    open fun <T> execute(csc: CallableStatementCreator, action: CallableStatementCallback<T>): T? {
        try {
            return tpl().execute(csc, action)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun <T> execute(callString: String, action: CallableStatementCallback<T>): T? {
        try {
            return tpl().execute(callString, action)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    open fun call(csc: CallableStatementCreator, declaredParameters: List<SqlParameter>): Map<String, Any> {
        try {
            return tpl().call(csc, declaredParameters)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return mapOf()
    }
}