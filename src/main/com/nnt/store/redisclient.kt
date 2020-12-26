package com.nnt.store

import com.nnt.core.logger
import redis.clients.jedis.*
import redis.clients.jedis.params.GeoRadiusParam
import redis.clients.jedis.params.SetParams
import redis.clients.jedis.params.ZAddParams
import redis.clients.jedis.params.ZIncrByParams

enum class RedisValueType(value: Int) {
    NONE(0),
    STRING(1),
    LIST(2),
    SET(3),
    ZSET(4),
    HASH(5)
}

interface RedisClientOperations {

    // 健康检查
    fun ping(message: String? = null): Boolean

    // 断开当前链接
    fun quit()

    // 登录
    fun auth(password: String, user: String? = null): Boolean

    // 获得服务器信息
    fun info(section: String? = null): String?

    // 基础命令
    fun set(key: String, value: Any, params: SetParams? = null): Boolean
    fun get(key: String, def: String? = null): String?
    fun exists(key: String): Boolean
    fun persist(key: String): Boolean
    fun type(key: String): RedisValueType
    fun expire(key: String, seconds: Int): Boolean
    fun pexpire(key: String, milliseconds: Long): Boolean
    fun expireAt(key: String, unixTime: Long): Boolean
    fun pexpireAt(key: String, millisecondsTimestamp: Long): Boolean
    fun ttl(key: String): Long
    fun pttl(key: String): Long
    fun getset(key: String, value: Any): String?
    fun setnx(key: String, value: Any): Boolean
    fun setex(key: String, seconds: Int, value: Any): Boolean
    fun psetex(key: String, milliseconds: Long, value: Any): Boolean
    fun decr(key: String, decrement: Long? = null): Long
    fun incr(key: String, increment: Long): Long
    fun incr(key: String, increment: Double): Double
    fun incr(key: String): Long

    // hash
    fun hset(key: String, field: String, value: Any)
    fun hset(key: String, hash: MutableMap<String, String>)
    fun hget(key: String, field: String): String?
    fun hsetnx(key: String, field: String, value: Any): Boolean
    fun hmset(key: String, hash: MutableMap<String, String>): Boolean
    fun hmget(key: String, vararg fields: String): MutableList<String>
    fun hincr(key: String, field: String, value: Long): Long
    fun hincr(key: String, field: String, value: Double): Double
    fun hexists(key: String, field: String): Boolean
    fun hdel(key: String, vararg fields: String)
    fun hlen(key: String): Long
    fun hkeys(key: String): MutableSet<String>
    fun hvals(key: String): MutableList<String>
    fun hgetall(key: String): MutableMap<String, String>

    // list
    fun rpush(key: String, vararg strings: String): Long
    fun lpush(key: String, vararg strings: String): Long
    fun llen(key: String): Long
    fun lrange(key: String, start: Long, stop: Long): MutableList<String>
    fun ltrim(key: String, start: Long, stop: Long): Boolean
    fun lindex(key: String, index: Long): String
    fun lset(key: String, index: Long, value: Any): Boolean
    fun lrem(key: String, count: Long, value: Any): Long
    fun lpop(key: String): String
    fun rpop(key: String): String

    // set
    fun sadd(key: String, vararg members: String): Long
    fun smembers(key: String): MutableSet<String>
    fun srem(key: String, vararg members: String): Long
    fun spop(key: String): String
    fun spop(key: String, count: Long): MutableSet<String>
    fun scard(key: String): Long
    fun sismember(key: String, member: String): Boolean
    fun srandmember(key: String): String
    fun srandmember(key: String, count: Int): MutableList<String>

    // ordered-set
    fun zadd(key: String, score: Double, member: String, params: ZAddParams? = null): Long
    fun zadd(key: String, scoreMembers: MutableMap<String, Double>, params: ZAddParams? = null): Long
    fun zrange(key: String, start: Long, stop: Long): MutableSet<String>
    fun zrem(key: String, vararg members: String): Long
    fun zincr(key: String, increment: Double, member: String, params: ZIncrByParams? = null): Double
    fun zrank(key: String, member: String): Long
    fun zrevrank(key: String, member: String): Long
    fun zrevrange(key: String, start: Long, stop: Long): MutableSet<String>
    fun zrangeWithScores(key: String, start: Long, stop: Long): MutableSet<Tuple>
    fun zrevrangeWithScores(key: String, start: Long, stop: Long): MutableSet<Tuple>
    fun zcard(key: String): Long
    fun zscore(key: String, member: String?): Double
    fun zpopmax(key: String): Tuple
    fun zpopmax(key: String, count: Int): MutableSet<Tuple>
    fun zpopmin(key: String): Tuple
    fun zpopmin(key: String, count: Int): MutableSet<Tuple>
    fun sort(key: String): MutableList<String>
    fun sort(key: String, sortingParameters: SortingParams?): MutableList<String>
    fun sort(key: String, sortingParameters: SortingParams?, dstkey: String): Long
    fun sort(key: String, dstkey: String): Long
    fun zcount(key: String, min: Double, max: Double): Long
    fun zcount(key: String, min: String?, max: String?): Long
    fun zrangeByScore(key: String, min: Double, max: Double): MutableSet<String>
    fun zrangeByScore(key: String, min: String?, max: String?): MutableSet<String>
    fun zrangeByScore(key: String, min: Double, max: Double, offset: Int, count: Int): MutableSet<String>
    fun zrangeByScore(key: String, min: String?, max: String?, offset: Int, count: Int): MutableSet<String>
    fun zrevrangeByScore(key: String, max: Double, min: Double): MutableSet<String>
    fun zrevrangeByScore(key: String, max: String?, min: String?): MutableSet<String>
    fun zrevrangeByScore(key: String, max: Double, min: Double, offset: Int, count: Int): MutableSet<String>
    fun zrevrangeByScore(
        key: String,
        max: String?,
        min: String?,
        offset: Int,
        count: Int,
    ): MutableSet<String>

    fun zrangeByScoreWithScores(key: String, min: Double, max: Double): MutableSet<Tuple>
    fun zrangeByScoreWithScores(key: String, min: String?, max: String?): MutableSet<Tuple>
    fun zrangeByScoreWithScores(
        key: String,
        min: Double,
        max: Double,
        offset: Int,
        count: Int,
    ): MutableSet<Tuple>

    fun zrangeByScoreWithScores(
        key: String,
        min: String?,
        max: String?,
        offset: Int,
        count: Int,
    ): MutableSet<Tuple>

    fun zrevrangeByScoreWithScores(key: String, max: Double, min: Double): MutableSet<Tuple>
    fun zrevrangeByScoreWithScores(
        key: String,
        max: Double,
        min: Double,
        offset: Int,
        count: Int,
    ): MutableSet<Tuple>

    fun zrevrangeByScoreWithScores(
        key: String,
        max: String?,
        min: String?,
        offset: Int,
        count: Int,
    ): MutableSet<Tuple>

    fun zrevrangeByScoreWithScores(key: String, max: String?, min: String?): MutableSet<Tuple>
    fun zremrangeByRank(key: String, start: Long, stop: Long): Long
    fun zremrangeByScore(key: String, min: Double, max: Double): Long
    fun zremrangeByScore(key: String, min: String?, max: String?): Long
    fun zlexcount(key: String, min: String?, max: String?): Long
    fun zrangeByLex(key: String, min: String?, max: String?): MutableSet<String>
    fun zrangeByLex(key: String, min: String?, max: String?, offset: Int, count: Int): MutableSet<String>
    fun zrevrangeByLex(key: String, max: String?, min: String?): MutableSet<String>
    fun zrevrangeByLex(key: String, max: String?, min: String?, offset: Int, count: Int): MutableSet<String>
    fun zremrangeByLex(key: String, min: String?, max: String?): Long
    fun linsert(key: String, where: ListPosition?, pivot: String?, value: String?): Long
    fun lpushx(key: String, vararg string: String?): Long
    fun rpushx(key: String, vararg string: String?): Long
    fun del(key: String): Long
    fun unlink(key: String): Long
    fun echo(string: String?): String
    fun move(key: String, dbIndex: Int): Long
    fun bitcount(key: String): Long
    fun bitcount(key: String, start: Long, end: Long): Long
    fun pfadd(key: String, vararg elements: String?): Long
    fun pfcount(key: String): Long

    // geo
    fun geoadd(key: String, longitude: Double, latitude: Double, member: String): Long
    fun geoadd(key: String, memberCoordinateMap: MutableMap<String, GeoCoordinate>): Long
    fun geodist(key: String, member1: String, member2: String, unit: GeoUnit?): Double
    fun geohash(key: String, vararg members: String): MutableList<String>
    fun geopos(key: String, vararg members: String): MutableList<GeoCoordinate>

    fun georadius(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?,
    ): MutableList<GeoRadiusResponse>

    fun georadiusReadonly(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?,
    ): MutableList<GeoRadiusResponse>

    fun georadiusByMember(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?,
    ): MutableList<GeoRadiusResponse>

    fun georadiusByMemberReadonly(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?,
    ): MutableList<GeoRadiusResponse>

    // iterator
    fun hscan(key: String, cursor: String?): ScanResult<MutableMap.MutableEntry<String, String>>
    fun hscan(
        key: String,
        cursor: String?,
        params: ScanParams?,
    ): ScanResult<MutableMap.MutableEntry<String, String>>

    fun sscan(key: String, cursor: String?): ScanResult<String>
    fun sscan(key: String, cursor: String?, params: ScanParams?): ScanResult<String>
    fun zscan(key: String, cursor: String?): ScanResult<Tuple>
    fun zscan(key: String, cursor: String?, params: ScanParams?): ScanResult<Tuple>
    fun bitfield(key: String, vararg arguments: String?): MutableList<Long>
    fun bitfieldReadonly(key: String, vararg arguments: String?): MutableList<Long>
    fun hstrlen(key: String, field: String?): Long
    fun blpop(timeout: Int, key: String): MutableList<String>
    fun brpop(timeout: Int, key: String): MutableList<String>
    fun rename(oldkey: String, newkey: String): String
    fun renamenx(oldkey: String, newkey: String): Long
    fun rpoplpush(srckey: String, dstkey: String): String
    fun smove(srckey: String, dstkey: String, member: String?): Long

    // close
    fun close()
}

class RedisClient(jedis: Jedis, kv: KvRedis) : RedisClientOperations {

    private val _hdl = jedis
    private var prefix: String? = kv.prefix

    private fun safeKey(key: String): String {
        if (prefix == null)
            return key
        return "${prefix}${key}"
    }

    override fun ping(message: String?): Boolean {
        if (message == null) {
            return _hdl.ping() == "PONG"
        }
        return _hdl.ping(message) == message
    }

    override fun quit() {
        _hdl.quit()
    }

    override fun auth(password: String, user: String?): Boolean {
        try {
            val res: String
            if (user == null) {
                res = _hdl.auth(user, password)
            } else {
                res = _hdl.auth(password)
            }
            if (res == "OK") {
                return true
            }
            logger.error(res)
            return false
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return false
    }

    override fun info(section: String?): String? {
        try {
            if (section == null)
                return _hdl.info()
            return _hdl.info(section)
        } catch (err: Throwable) {
            logger.exception(err)
        }
        return null
    }

    override fun set(key: String, value: Any, params: SetParams?): Boolean {
        val res: String
        if (params != null) {
            res = _hdl.set(safeKey(key), value.toString(), params)
        } else {
            res = _hdl.set(safeKey(key), value.toString())
        }
        return res == "OK"
    }

    override fun get(key: String, def: String?): String? {
        return _hdl.get(safeKey(key))
    }

    override fun exists(key: String): Boolean {
        return _hdl.exists(safeKey(key))
    }

    override fun persist(key: String): Boolean {
        return _hdl.persist(safeKey(key)) == 1L
    }

    override fun type(key: String): RedisValueType {
        val typ = _hdl.type(safeKey(key))
        when (typ) {
            "none" -> {
                return RedisValueType.NONE
            }
            "string" -> {
                return RedisValueType.STRING
            }
            "list" -> {
                return RedisValueType.LIST
            }
            "set" -> {
                return RedisValueType.SET
            }
            "zset" -> {
                return RedisValueType.ZSET
            }
            "hash" -> {
                return RedisValueType.HASH
            }
        }
        return RedisValueType.NONE
    }

    override fun expire(key: String, seconds: Int): Boolean {
        return _hdl.expire(safeKey(key), seconds) == 1L
    }

    override fun pexpire(key: String, milliseconds: Long): Boolean {
        return _hdl.pexpire(safeKey(key), milliseconds) == 1L
    }

    override fun expireAt(key: String, unixTime: Long): Boolean {
        return _hdl.expireAt(safeKey(key), unixTime) == 1L
    }

    override fun pexpireAt(key: String, millisecondsTimestamp: Long): Boolean {
        return _hdl.pexpireAt(safeKey(key), millisecondsTimestamp) == 1L
    }

    override fun ttl(key: String): Long {
        return _hdl.ttl(safeKey(key))
    }

    override fun pttl(key: String): Long {
        return _hdl.pttl(safeKey(key))
    }

    override fun getset(key: String, value: Any): String? {
        return _hdl.getSet(safeKey(key), value.toString())
    }

    override fun setnx(key: String, value: Any): Boolean {
        return _hdl.setnx(safeKey(key), value.toString()) == 1L
    }

    override fun setex(key: String, seconds: Int, value: Any): Boolean {
        val res = _hdl.setex(safeKey(key), seconds, value.toString())
        return res == "OK"
    }

    override fun psetex(key: String, milliseconds: Long, value: Any): Boolean {
        val res = _hdl.psetex(safeKey(key), milliseconds, value.toString())
        return res == "OK"
    }

    override fun decr(key: String, decrement: Long?): Long {
        if (decrement == null) {
            return _hdl.decr(safeKey(key))
        }
        return _hdl.decrBy(safeKey(key), decrement)
    }

    override fun incr(key: String, increment: Long): Long {
        return _hdl.incrBy(safeKey(key), increment)
    }

    override fun incr(key: String, increment: Double): Double {
        return _hdl.incrByFloat(safeKey(key), increment)
    }

    override fun incr(key: String): Long {
        return _hdl.incr(safeKey(key))
    }

    override fun hset(key: String, field: String, value: Any) {
        _hdl.hset(safeKey(key), field, value.toString())
    }

    override fun hset(key: String, hash: MutableMap<String, String>) {
        _hdl.hset(safeKey(key), hash)
    }

    override fun hget(key: String, field: String): String? {
        return _hdl.hget(safeKey(key), field)
    }

    override fun hsetnx(key: String, field: String, value: Any): Boolean {
        val res = _hdl.hsetnx(safeKey(key), field, value.toString())
        return res == 1L
    }

    override fun hmset(key: String, hash: MutableMap<String, String>): Boolean {
        val res = _hdl.hmset(safeKey(key), hash)
        return res == "OK"
    }

    override fun hmget(key: String, vararg fields: String): MutableList<String> {
        return _hdl.hmget(safeKey(key), *fields)
    }

    override fun hincr(key: String, field: String, value: Long): Long {
        return _hdl.hincrBy(safeKey(key), field, value)
    }

    override fun hincr(key: String, field: String, value: Double): Double {
        return _hdl.hincrByFloat(safeKey(key), field, value)
    }

    override fun hexists(key: String, field: String): Boolean {
        return _hdl.hexists(safeKey(key), field)
    }

    override fun hdel(key: String, vararg fields: String) {
        _hdl.hdel(safeKey(key), *fields)
    }

    override fun hlen(key: String): Long {
        return _hdl.hlen(safeKey(key))
    }

    override fun hkeys(key: String): MutableSet<String> {
        return _hdl.hkeys(safeKey(key))
    }

    override fun hvals(key: String): MutableList<String> {
        return _hdl.hvals(safeKey(key))
    }

    override fun hgetall(key: String): MutableMap<String, String> {
        return _hdl.hgetAll(safeKey(key))
    }

    override fun rpush(key: String, vararg strings: String): Long {
        return _hdl.rpush(safeKey(key), *strings)
    }

    override fun lpush(key: String, vararg strings: String): Long {
        return _hdl.lpush(safeKey(key), *strings)
    }

    override fun llen(key: String): Long {
        return _hdl.llen(safeKey(key))
    }

    override fun lrange(key: String, start: Long, stop: Long): MutableList<String> {
        return _hdl.lrange(safeKey(key), start, stop)
    }

    override fun ltrim(key: String, start: Long, stop: Long): Boolean {
        val res = _hdl.ltrim(safeKey(key), start, stop)
        return res == "OK"
    }

    override fun lindex(key: String, index: Long): String {
        return _hdl.lindex(safeKey(key), index)
    }

    override fun lset(key: String, index: Long, value: Any): Boolean {
        val res = _hdl.lset(safeKey(key), index, value.toString())
        return res == "OK"
    }

    override fun lrem(key: String, count: Long, value: Any): Long {
        return _hdl.lrem(safeKey(key), count, value.toString())
    }

    override fun lpop(key: String): String {
        return _hdl.lpop(safeKey(key))
    }

    override fun rpop(key: String): String {
        return _hdl.rpop(safeKey(key))
    }

    override fun sadd(key: String, vararg members: String): Long {
        return _hdl.sadd(safeKey(key), *members)
    }

    override fun smembers(key: String): MutableSet<String> {
        return _hdl.smembers(safeKey(key))
    }

    override fun srem(key: String, vararg members: String): Long {
        return _hdl.srem(safeKey(key), *members)
    }

    override fun spop(key: String): String {
        return _hdl.spop(safeKey(key))
    }

    override fun spop(key: String, count: Long): MutableSet<String> {
        return _hdl.spop(safeKey(key), count)
    }

    override fun scard(key: String): Long {
        return _hdl.scard(safeKey(key))
    }

    override fun sismember(key: String, member: String): Boolean {
        return _hdl.sismember(safeKey(key), member)
    }

    override fun srandmember(key: String): String {
        return _hdl.srandmember(safeKey(key))
    }

    override fun srandmember(key: String, count: Int): MutableList<String> {
        return _hdl.srandmember(safeKey(key), count)
    }

    override fun zadd(key: String, score: Double, member: String, params: ZAddParams?): Long {
        val res: Long
        if (params == null) {
            res = _hdl.zadd(safeKey(key), score, member)
        } else {
            res = _hdl.zadd(safeKey(key), score, member, params)
        }
        return res
    }

    override fun zadd(key: String, scoreMembers: MutableMap<String, Double>, params: ZAddParams?): Long {
        val res: Long
        if (params == null) {
            res = _hdl.zadd(safeKey(key), scoreMembers)
        } else {
            res = _hdl.zadd(safeKey(key), scoreMembers, params)
        }
        return res
    }

    override fun zrange(key: String, start: Long, stop: Long): MutableSet<String> {
        return _hdl.zrange(safeKey(key), start, stop)
    }

    override fun zrem(key: String, vararg members: String): Long {
        return _hdl.zrem(safeKey(key), *members)
    }

    override fun zincr(key: String, increment: Double, member: String, params: ZIncrByParams?): Double {
        val res: Double
        if (params == null) {
            res = _hdl.zincrby(safeKey(key), increment, member)
        } else {
            res = _hdl.zincrby(safeKey(key), increment, member, params)
        }
        return res
    }

    override fun zrank(key: String, member: String): Long {
        return _hdl.zrank(safeKey(key), member)
    }

    override fun zrevrank(key: String, member: String): Long {
        return _hdl.zrevrank(safeKey(key), member)
    }

    override fun zrevrange(key: String, start: Long, stop: Long): MutableSet<String> {
        return _hdl.zrevrange(safeKey(key), start, stop)
    }

    override fun zrangeWithScores(key: String, start: Long, stop: Long): MutableSet<Tuple> {
        return _hdl.zrangeWithScores(safeKey(key), start, stop)
    }

    override fun zrevrangeWithScores(key: String, start: Long, stop: Long): MutableSet<Tuple> {
        return _hdl.zrevrangeWithScores(safeKey(key), start, stop)
    }

    override fun zcard(key: String): Long {
        return _hdl.zcard(safeKey(key))
    }

    override fun zscore(key: String, member: String?): Double {
        return _hdl.zscore(safeKey(key), member)
    }

    override fun zpopmax(key: String): Tuple {
        return _hdl.zpopmax(safeKey(key))
    }

    override fun zpopmax(key: String, count: Int): MutableSet<Tuple> {
        return _hdl.zpopmax(safeKey(key), count)
    }

    override fun zpopmin(key: String): Tuple {
        return _hdl.zpopmin(safeKey(key))
    }

    override fun zpopmin(key: String, count: Int): MutableSet<Tuple> {
        return _hdl.zpopmin(safeKey(key), count)
    }

    override fun sort(key: String): MutableList<String> {
        return _hdl.sort(safeKey(key))
    }

    override fun sort(key: String, sortingParameters: SortingParams?): MutableList<String> {
        return _hdl.sort(safeKey(key), sortingParameters)
    }

    override fun sort(key: String, sortingParameters: SortingParams?, dstkey: String): Long {
        return _hdl.sort(safeKey(key), sortingParameters, dstkey)
    }

    override fun sort(key: String, dstkey: String): Long {
        return _hdl.sort(safeKey(key), dstkey)
    }

    override fun zcount(key: String, min: Double, max: Double): Long {
        return _hdl.zcount(safeKey(key), min, max)
    }

    override fun zcount(key: String, min: String?, max: String?): Long {
        return _hdl.zcount(safeKey(key), min, max)
    }

    override fun zrangeByScore(key: String, min: Double, max: Double): MutableSet<String> {
        return _hdl.zrangeByScore(safeKey(key), min, max)
    }

    override fun zrangeByScore(key: String, min: String?, max: String?): MutableSet<String> {
        return _hdl.zrangeByScore(safeKey(key), min, max)
    }

    override fun zrangeByScore(key: String, min: Double, max: Double, offset: Int, count: Int): MutableSet<String> {
        return _hdl.zrangeByScore(safeKey(key), min, max, offset, count)
    }

    override fun zrangeByScore(key: String, min: String?, max: String?, offset: Int, count: Int): MutableSet<String> {
        return _hdl.zrangeByScore(safeKey(key), min, max, offset, count)
    }

    override fun zrevrangeByScore(key: String, max: Double, min: Double): MutableSet<String> {
        return _hdl.zrevrangeByScore(safeKey(key), max, min)
    }

    override fun zrevrangeByScore(key: String, max: String?, min: String?): MutableSet<String> {
        return _hdl.zrevrangeByScore(safeKey(key), max, min)
    }

    override fun zrevrangeByScore(key: String, max: Double, min: Double, offset: Int, count: Int): MutableSet<String> {
        return _hdl.zrevrangeByScore(safeKey(key), max, min, offset, count)
    }

    override fun zrevrangeByScore(
        key: String,
        max: String?,
        min: String?,
        offset: Int,
        count: Int,
    ): MutableSet<String> {
        return _hdl.zrevrangeByScore(safeKey(key), max, min, offset, count)
    }

    override fun zrangeByScoreWithScores(key: String, min: Double, max: Double): MutableSet<Tuple> {
        return _hdl.zrangeByScoreWithScores(safeKey(key), min, max)
    }

    override fun zrangeByScoreWithScores(key: String, min: String?, max: String?): MutableSet<Tuple> {
        return _hdl.zrangeByScoreWithScores(safeKey(key), min, max)
    }

    override fun zrangeByScoreWithScores(
        key: String,
        min: Double,
        max: Double,
        offset: Int,
        count: Int,
    ): MutableSet<Tuple> {
        return _hdl.zrangeByScoreWithScores(safeKey(key), min, max, offset, count)
    }

    override fun zrangeByScoreWithScores(
        key: String,
        min: String?,
        max: String?,
        offset: Int,
        count: Int,
    ): MutableSet<Tuple> {
        return _hdl.zrangeByScoreWithScores(safeKey(key), min, max, offset, count)
    }

    override fun zrevrangeByScoreWithScores(key: String, max: Double, min: Double): MutableSet<Tuple> {
        return _hdl.zrevrangeByScoreWithScores(safeKey(key), max, min)
    }

    override fun zrevrangeByScoreWithScores(
        key: String,
        max: Double,
        min: Double,
        offset: Int,
        count: Int,
    ): MutableSet<Tuple> {
        return _hdl.zrevrangeByScoreWithScores(safeKey(key), max, min, offset, count)
    }

    override fun zrevrangeByScoreWithScores(
        key: String,
        max: String?,
        min: String?,
        offset: Int,
        count: Int,
    ): MutableSet<Tuple> {
        return _hdl.zrevrangeByScoreWithScores(safeKey(key), max, min, offset, count)
    }

    override fun zrevrangeByScoreWithScores(key: String, max: String?, min: String?): MutableSet<Tuple> {
        return _hdl.zrevrangeByScoreWithScores(safeKey(key), max, min)
    }

    override fun zremrangeByRank(key: String, start: Long, stop: Long): Long {
        return _hdl.zremrangeByRank(safeKey(key), start, stop)
    }

    override fun zremrangeByScore(key: String, min: Double, max: Double): Long {
        return _hdl.zremrangeByScore(safeKey(key), min, max)
    }

    override fun zremrangeByScore(key: String, min: String?, max: String?): Long {
        return _hdl.zremrangeByScore(safeKey(key), min, max)
    }

    override fun zlexcount(key: String, min: String?, max: String?): Long {
        return _hdl.zlexcount(safeKey(key), min, max)
    }

    override fun zrangeByLex(key: String, min: String?, max: String?): MutableSet<String> {
        return _hdl.zrangeByLex(safeKey(key), min, max)
    }

    override fun zrangeByLex(key: String, min: String?, max: String?, offset: Int, count: Int): MutableSet<String> {
        return _hdl.zrangeByLex(safeKey(key), min, max, offset, count)
    }

    override fun zrevrangeByLex(key: String, max: String?, min: String?): MutableSet<String> {
        return _hdl.zrevrangeByLex(safeKey(key), max, min)
    }

    override fun zrevrangeByLex(key: String, max: String?, min: String?, offset: Int, count: Int): MutableSet<String> {
        return _hdl.zrevrangeByLex(safeKey(key), max, min, offset, count)
    }

    override fun zremrangeByLex(key: String, min: String?, max: String?): Long {
        return _hdl.zremrangeByLex(safeKey(key), min, max)
    }

    override fun linsert(key: String, where: ListPosition?, pivot: String?, value: String?): Long {
        return _hdl.linsert(safeKey(key), where, pivot, value)
    }

    override fun lpushx(key: String, vararg string: String?): Long {
        return _hdl.lpushx(safeKey(key), *string)
    }

    override fun rpushx(key: String, vararg string: String?): Long {
        return _hdl.rpushx(safeKey(key), *string)
    }

    override fun del(key: String): Long {
        return _hdl.del(safeKey(key))
    }

    override fun unlink(key: String): Long {
        return _hdl.unlink(safeKey(key))
    }

    override fun echo(string: String?): String {
        return _hdl.echo(string)
    }

    override fun move(key: String, dbIndex: Int): Long {
        return _hdl.move(safeKey(key), dbIndex)
    }

    override fun bitcount(key: String): Long {
        return _hdl.bitcount(safeKey(key))
    }

    override fun bitcount(key: String, start: Long, end: Long): Long {
        return _hdl.bitcount(safeKey(key), start, end)
    }

    override fun pfadd(key: String, vararg elements: String?): Long {
        return _hdl.pfadd(safeKey(key), *elements)
    }

    override fun pfcount(key: String): Long {
        return _hdl.pfcount(safeKey(key))
    }

    override fun geoadd(key: String, longitude: Double, latitude: Double, member: String): Long {
        try {
            return _hdl.geoadd(safeKey(key), longitude, latitude, member)
        } catch (err: Throwable) {
            // logger.exception(err)
            logger.error(err)
        }
        return 0
    }

    override fun geoadd(key: String, memberCoordinateMap: MutableMap<String, GeoCoordinate>): Long {
        try {
            return _hdl.geoadd(safeKey(key), memberCoordinateMap)
        } catch (err: Throwable) {
            // logger.exception(err)
            logger.error(err)
        }
        return 0
    }

    override fun geodist(key: String, member1: String, member2: String, unit: GeoUnit?): Double {
        if (unit == null)
            return _hdl.geodist(safeKey(key), member1, member2)
        return _hdl.geodist(safeKey(key), member1, member2, unit)
    }

    override fun geohash(key: String, vararg members: String): MutableList<String> {
        return _hdl.geohash(safeKey(key), *members)
    }

    override fun geopos(key: String, vararg members: String): MutableList<GeoCoordinate> {
        return _hdl.geopos(safeKey(key), *members)
    }

    override fun georadius(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?,
    ): MutableList<GeoRadiusResponse> {
        if (param == null)
            return _hdl.georadius(safeKey(key), longitude, latitude, radius, unit)
        return _hdl.georadius(safeKey(key), longitude, latitude, radius, unit, param)
    }

    override fun georadiusReadonly(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?,
    ): MutableList<GeoRadiusResponse> {
        if (param == null)
            return _hdl.georadiusReadonly(safeKey(key), longitude, latitude, radius, unit)
        return _hdl.georadiusReadonly(safeKey(key), longitude, latitude, radius, unit, param)
    }

    override fun georadiusByMember(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?,
    ): MutableList<GeoRadiusResponse> {
        if (param == null)
            return _hdl.georadiusByMember(safeKey(key), member, radius, unit)
        return _hdl.georadiusByMember(safeKey(key), member, radius, unit, param)
    }

    override fun georadiusByMemberReadonly(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?,
    ): MutableList<GeoRadiusResponse> {
        if (param == null)
            return _hdl.georadiusByMemberReadonly(safeKey(key), member, radius, unit)
        return _hdl.georadiusByMemberReadonly(safeKey(key), member, radius, unit, param)
    }

    override fun hscan(key: String, cursor: String?): ScanResult<MutableMap.MutableEntry<String, String>> {
        return _hdl.hscan(safeKey(key), cursor)
    }

    override fun hscan(
        key: String,
        cursor: String?,
        params: ScanParams?,
    ): ScanResult<MutableMap.MutableEntry<String, String>> {
        return _hdl.hscan(safeKey(key), cursor, params)
    }

    override fun sscan(key: String, cursor: String?): ScanResult<String> {
        return _hdl.sscan(safeKey(key), cursor)
    }

    override fun sscan(key: String, cursor: String?, params: ScanParams?): ScanResult<String> {
        return _hdl.sscan(safeKey(key), cursor, params)
    }

    override fun zscan(key: String, cursor: String?): ScanResult<Tuple> {
        return _hdl.zscan(safeKey(key), cursor)
    }

    override fun zscan(key: String, cursor: String?, params: ScanParams?): ScanResult<Tuple> {
        return _hdl.zscan(safeKey(key), cursor, params)
    }

    override fun bitfield(key: String, vararg arguments: String?): MutableList<Long> {
        return _hdl.bitfield(safeKey(key), *arguments)
    }

    override fun bitfieldReadonly(key: String, vararg arguments: String?): MutableList<Long> {
        return _hdl.bitfieldReadonly(safeKey(key), *arguments)
    }

    override fun hstrlen(key: String, field: String?): Long {
        return _hdl.hstrlen(safeKey(key), field)
    }

    override fun blpop(timeout: Int, key: String): MutableList<String> {
        return _hdl.blpop(timeout, safeKey(key))
    }

    override fun brpop(timeout: Int, key: String): MutableList<String> {
        return _hdl.brpop(timeout, safeKey(key))
    }

    override fun rename(oldkey: String, newkey: String): String {
        return _hdl.rename(safeKey(oldkey), safeKey(newkey))
    }

    override fun renamenx(oldkey: String, newkey: String): Long {
        return _hdl.renamenx(safeKey(oldkey), safeKey(newkey))
    }

    override fun rpoplpush(srckey: String, dstkey: String): String {
        return _hdl.rpoplpush(safeKey(srckey), safeKey(dstkey))
    }

    override fun smove(srckey: String, dstkey: String, member: String?): Long {
        return _hdl.smove(safeKey(srckey), safeKey(dstkey), member)
    }

    override fun close() {
        _hdl.close()
    }
}