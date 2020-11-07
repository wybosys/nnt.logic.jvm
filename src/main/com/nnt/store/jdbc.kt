package com.nnt.store

import com.alibaba.druid.pool.DruidDataSourceFactory
import com.nnt.core.JsonObject
import com.nnt.core.logger
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.*
import org.springframework.jdbc.datasource.SingleConnectionDataSource
import org.springframework.jdbc.support.KeyHolder
import org.springframework.jdbc.support.rowset.SqlRowSet
import java.sql.Connection
import java.util.*
import javax.sql.DataSource
import kotlin.reflect.KClass

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

    protected open fun propertiesForJdbc(): Properties {
        val props = Properties()
        props.setProperty("driverClassName", driver)
        props.setProperty("url", url)
        if (!user.isEmpty()) {
            props.setProperty("username", user)
            if (!pwd.isEmpty())
                props.setProperty("password", pwd)
        }

        // 设置连接数
        props.setProperty("initialSize", "0")
        props.setProperty("minIdle", "0")
        props.setProperty("maxActive", "512")

        return props
    }

    override fun open() {
        val props = propertiesForJdbc()
        _dsfac = DruidDataSourceFactory.createDataSource(props)
        logger.info("打开 ${id}@jdbc")
    }

    override fun close() {
        // pass
    }

    fun acquire(): JdbcSession {
        val conn = _dsfac.connection
        val tpl = JdbcTemplate(SingleConnectionDataSource(conn, true))
        return JdbcSession(conn, tpl)
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
            logger.exception(err.localizedMessage)
            r = false
        } finally {
            conn?.close()
        }
        return r
    }

}

// jdbc业务对象
open class JdbcSession(conn: Connection, tpl: JdbcTemplate) : ISession {

    // 不使用 JdbcOperations by tpl 的写法是因为会造成编译器warnning

    private val _conn = conn
    private val _tpl = tpl
    private var _closed = false

    override open fun close() {
        if (!_closed) {
            _conn.close()
            _closed = true
        }
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

    @Throws(DataAccessException::class)
    open fun <T> execute(action: ConnectionCallback<T>): T {
        return _tpl.execute(action)
    }

    @Throws(DataAccessException::class)
    open fun <T> execute(action: StatementCallback<T>): T {
        return _tpl.execute(action)
    }

    @Throws(DataAccessException::class)
    open fun execute(sql: String) {
        _tpl.execute(sql)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(sql: String, rse: ResultSetExtractor<T>): T {
        return _tpl.query(sql, rse)
    }

    @Throws(DataAccessException::class)
    open fun query(sql: String, rch: RowCallbackHandler) {
        _tpl.query(sql, rch)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(sql: String, rowMapper: RowMapper<T>): List<T> {
        return _tpl.query(sql, rowMapper)
    }

    @Throws(DataAccessException::class)
    open fun <T> queryForObject(sql: String, rowMapper: RowMapper<T>): T {
        return _tpl.queryForObject(sql, rowMapper)
    }

    @Throws(DataAccessException::class)
    open fun <T : Any> queryForObject(sql: String, requiredType: KClass<T>): T? {
        return _tpl.queryForObject(sql, requiredType.java)
    }

    @Throws(DataAccessException::class)
    open fun queryForMap(sql: String): Map<String, Any> {
        return _tpl.queryForMap(sql)
    }

    @Throws(DataAccessException::class)
    open fun <T : Any> queryForList(sql: String, elementType: KClass<T>): List<T> {
        return _tpl.queryForList(sql, elementType.java)
    }

    @Throws(DataAccessException::class)
    open fun queryForList(sql: String): List<Map<String, Any>> {
        return _tpl.queryForList(sql)
    }

    @Throws(DataAccessException::class)
    open fun queryForRowSet(sql: String): SqlRowSet {
        return _tpl.queryForRowSet(sql)
    }

    @Throws(DataAccessException::class)
    open fun update(sql: String): Int {
        return _tpl.update(sql)
    }

    @Throws(DataAccessException::class)
    open fun batchUpdate(vararg sql: String): IntArray {
        return _tpl.batchUpdate(*sql)
    }

    @Throws(DataAccessException::class)
    open fun <T> execute(psc: PreparedStatementCreator, action: PreparedStatementCallback<T>): T {
        return _tpl.execute(psc, action)
    }

    @Throws(DataAccessException::class)
    open fun <T> execute(sql: String, action: PreparedStatementCallback<T>): T {
        return _tpl.execute(sql, action)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(psc: PreparedStatementCreator, rse: ResultSetExtractor<T>): T {
        return _tpl.query(psc, rse)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(sql: String, pss: PreparedStatementSetter, rse: ResultSetExtractor<T>): T {
        return _tpl.query(sql, pss, rse)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(sql: String, args: Array<Any>, argTypes: IntArray, rse: ResultSetExtractor<T>): T {
        return _tpl.query(sql, args, argTypes, rse)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(sql: String, args: Array<Any>, rse: ResultSetExtractor<T>): T {
        return _tpl.query(sql, args, rse)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(sql: String, rse: ResultSetExtractor<T>, vararg args: Any): T {
        return _tpl.query(sql, rse, *args)
    }

    @Throws(DataAccessException::class)
    open fun query(psc: PreparedStatementCreator, rch: RowCallbackHandler) {
        _tpl.query(psc, rch)
    }

    @Throws(DataAccessException::class)
    open fun query(sql: String, pss: PreparedStatementSetter, rch: RowCallbackHandler) {
        _tpl.query(sql, pss, rch)
    }

    @Throws(DataAccessException::class)
    open fun query(sql: String, args: Array<Any>, argTypes: IntArray, rch: RowCallbackHandler) {
        _tpl.query(sql, args, argTypes, rch)
    }

    @Throws(DataAccessException::class)
    open fun query(sql: String, args: Array<Any>, rch: RowCallbackHandler) {
        _tpl.query(sql, args, rch)
    }

    @Throws(DataAccessException::class)
    open fun query(sql: String, rch: RowCallbackHandler, vararg args: Any) {
        _tpl.query(sql, rch, *args)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(psc: PreparedStatementCreator, rowMapper: RowMapper<T>): List<T> {
        return _tpl.query(psc, rowMapper)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(sql: String, pss: PreparedStatementSetter, rowMapper: RowMapper<T>): List<T> {
        return _tpl.query(sql, pss, rowMapper)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(sql: String, args: Array<Any>, argTypes: IntArray, rowMapper: RowMapper<T>): List<T> {
        return _tpl.query(sql, args, argTypes, rowMapper)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(sql: String, args: Array<Any>, rowMapper: RowMapper<T>): List<T> {
        return _tpl.query(sql, args, rowMapper)
    }

    @Throws(DataAccessException::class)
    open fun <T> query(sql: String, rowMapper: RowMapper<T>, vararg args: Any): List<T> {
        return _tpl.query(sql, rowMapper, *args)
    }

    @Throws(DataAccessException::class)
    open fun <T> queryForObject(sql: String, args: Array<Any>, argTypes: IntArray, rowMapper: RowMapper<T>): T {
        return _tpl.queryForObject(sql, args, argTypes, rowMapper)
    }

    @Throws(DataAccessException::class)
    open fun <T> queryForObject(sql: String, args: Array<Any>, rowMapper: RowMapper<T>): T {
        return _tpl.queryForObject(sql, args, rowMapper)
    }

    @Throws(DataAccessException::class)
    open fun <T> queryForObject(sql: String, rowMapper: RowMapper<T>, vararg args: Any): T {
        return _tpl.queryForObject(sql, rowMapper, *args)
    }

    @Throws(DataAccessException::class)
    open fun <T : Any> queryForObject(sql: String, args: Array<Any>, argTypes: IntArray, requiredType: KClass<T>): T {
        return _tpl.queryForObject(sql, args, argTypes, requiredType.java)
    }

    @Throws(DataAccessException::class)
    open fun <T : Any> queryForObject(sql: String, args: Array<Any>, requiredType: KClass<T>): T? {
        return _tpl.queryForObject(sql, args, requiredType.java)
    }

    @Throws(DataAccessException::class)
    open fun <T : Any> queryForObject(sql: String, requiredType: KClass<T>, vararg args: Any): T? {
        return _tpl.queryForObject(sql, requiredType.java, *args)
    }

    @Throws(DataAccessException::class)
    open fun queryForMap(sql: String, args: Array<Any>, argTypes: IntArray): Map<String, Any> {
        return _tpl.queryForMap(sql, args, argTypes)
    }

    @Throws(DataAccessException::class)
    open fun queryForMap(sql: String, vararg args: Any): Map<String, Any> {
        return _tpl.queryForMap(sql, *args)
    }

    @Throws(DataAccessException::class)
    open fun <T : Any> queryForList(sql: String, args: Array<Any>, elementType: KClass<T>): List<T> {
        val ti = GetTableInfo(elementType)
        if (ti == null) {
            return _tpl.queryForList(sql, args, elementType.java)
        }

        // 模型类型
        val r = _tpl.queryForList(sql, *args)
        return r.map {
            val t = elementType.constructors.first().call()
            Fill(t, it, ti)
            t
        }
    }

    @Throws(DataAccessException::class)
    open fun <T : Any> queryForList(sql: String, elementType: KClass<T>, vararg args: Any): List<T> {
        val ti = GetTableInfo(elementType)
        if (ti == null) {
            return _tpl.queryForList(sql, elementType.java, *args)
        }

        // 模型类型
        val r = _tpl.queryForList(sql, *args)
        return r.map {
            val t = elementType.constructors.first().call()
            Fill(t, it, ti)
            t
        }
    }

    @Throws(DataAccessException::class)
    open fun queryForList(sql: String, args: Array<Any>, argTypes: IntArray): List<Map<String, Any>> {
        return _tpl.queryForList(sql, args, argTypes)
    }

    @Throws(DataAccessException::class)
    open fun queryForList(sql: String, vararg args: Any): List<Map<String, Any>> {
        return _tpl.queryForList(sql, *args)
    }

    @Throws(DataAccessException::class)
    open fun queryForRowSet(sql: String, args: Array<Any>, argTypes: IntArray): SqlRowSet {
        return _tpl.queryForRowSet(sql, args, argTypes)
    }

    @Throws(DataAccessException::class)
    open fun queryForRowSet(sql: String, vararg args: Any): SqlRowSet {
        return _tpl.queryForRowSet(sql, *args)
    }

    @Throws(DataAccessException::class)
    open fun update(psc: PreparedStatementCreator): Int {
        return _tpl.update(psc)
    }

    @Throws(DataAccessException::class)
    open fun update(psc: PreparedStatementCreator, generatedKeyHolder: KeyHolder): Int {
        return _tpl.update(psc, generatedKeyHolder)
    }

    @Throws(DataAccessException::class)
    open fun update(sql: String, pss: PreparedStatementSetter): Int {
        return _tpl.update(sql, pss)
    }

    @Throws(DataAccessException::class)
    open fun update(sql: String, args: Array<Any>, argTypes: IntArray): Int {
        return _tpl.update(sql, args, argTypes)
    }

    @Throws(DataAccessException::class)
    open fun update(sql: String, vararg args: Any): Int {
        return _tpl.update(sql, *args)
    }

    @Throws(DataAccessException::class)
    open fun batchUpdate(sql: String, pss: BatchPreparedStatementSetter): IntArray {
        return _tpl.batchUpdate(sql, pss)
    }

    @Throws(DataAccessException::class)
    open fun batchUpdate(sql: String, batchArgs: List<Array<Any>>): IntArray {
        return _tpl.batchUpdate(sql, batchArgs)
    }

    @Throws(DataAccessException::class)
    open fun batchUpdate(sql: String, batchArgs: List<Array<Any>>, argTypes: IntArray): IntArray {
        return _tpl.batchUpdate(sql, batchArgs, argTypes)
    }

    @Throws(DataAccessException::class)
    open fun <T> batchUpdate(
        sql: String,
        batchArgs: Collection<T>,
        batchSize: Int,
        pss: ParameterizedPreparedStatementSetter<T>,
    ): Array<IntArray> {
        return _tpl.batchUpdate(sql, batchArgs, batchSize, pss)
    }

    @Throws(DataAccessException::class)
    open fun <T> execute(csc: CallableStatementCreator, action: CallableStatementCallback<T>): T {
        return _tpl.execute(csc, action)
    }

    @Throws(DataAccessException::class)
    open fun <T> execute(callString: String, action: CallableStatementCallback<T>): T {
        return _tpl.execute(callString, action)
    }

    @Throws(DataAccessException::class)
    open fun call(csc: CallableStatementCreator, declaredParameters: List<SqlParameter>): Map<String, Any> {
        return _tpl.call(csc, declaredParameters)
    }

}