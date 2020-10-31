package com.nnt.store

import redis.clients.jedis.*
import redis.clients.jedis.params.GeoRadiusParam
import redis.clients.jedis.params.SetParams
import redis.clients.jedis.params.ZAddParams
import redis.clients.jedis.params.ZIncrByParams

interface RedisClientOperations {
    fun ping(message: String): String
    fun ping(): String
    fun quit(): String
    fun auth(password: String): String
    fun auth(user: String, password: String): String
    fun info(): String
    fun info(section: String): String
    fun set(key: String, value: String, params: SetParams? = null): String
    fun get(key: String): String
    fun exists(key: String): Boolean
    fun persist(key: String): Long
    fun type(key: String): String
    fun expire(key: String, seconds: Int): Long
    fun pexpire(key: String, milliseconds: Long): Long
    fun expireAt(key: String, unixTime: Long): Long
    fun pexpireAt(key: String, millisecondsTimestamp: Long): Long
    fun ttl(key: String): Long
    fun pttl(key: String): Long
    fun touch(key: String): Long
    fun setbit(key: String, offset: Long, value: Boolean): Boolean
    fun setbit(key: String, offset: Long, value: String): Boolean
    fun getbit(key: String, offset: Long): Boolean
    fun setrange(key: String, offset: Long, value: String?): Long
    fun getrange(key: String, startOffset: Long, endOffset: Long): String
    fun getSet(key: String, value: String?): String
    fun setnx(key: String, value: String?): Long
    fun setex(key: String, seconds: Int, value: String?): String
    fun psetex(key: String, milliseconds: Long, value: String?): String
    fun decrBy(key: String, decrement: Long): Long
    fun decr(key: String): Long
    fun incrBy(key: String, increment: Long): Long
    fun incrByFloat(key: String, increment: Double): Double
    fun incr(key: String): Long
    fun append(key: String, value: String?): Long
    fun substr(key: String, start: Int, end: Int): String
    fun hset(key: String, field: String?, value: String?): Long
    fun hset(key: String, hash: MutableMap<String, String>?): Long
    fun hget(key: String, field: String?): String
    fun hsetnx(key: String, field: String?, value: String?): Long
    fun hmset(key: String, hash: MutableMap<String, String>?): String
    fun hmget(key: String, vararg fields: String?): MutableList<String>
    fun hincrBy(key: String, field: String?, value: Long): Long
    fun hincrByFloat(key: String, field: String?, value: Double): Double
    fun hexists(key: String, field: String?): Boolean
    fun hdel(key: String, vararg fields: String?): Long
    fun hlen(key: String): Long
    fun hkeys(key: String): MutableSet<String>
    fun hvals(key: String): MutableList<String>
    fun hgetAll(key: String): MutableMap<String, String>
    fun rpush(key: String, vararg strings: String?): Long
    fun lpush(key: String, vararg strings: String?): Long
    fun llen(key: String): Long
    fun lrange(key: String, start: Long, stop: Long): MutableList<String>
    fun ltrim(key: String, start: Long, stop: Long): String
    fun lindex(key: String, index: Long): String
    fun lset(key: String, index: Long, value: String?): String
    fun lrem(key: String, count: Long, value: String?): Long
    fun lpop(key: String): String
    fun rpop(key: String): String
    fun sadd(key: String, vararg members: String?): Long
    fun smembers(key: String): MutableSet<String>
    fun srem(key: String, vararg members: String?): Long
    fun spop(key: String): String
    fun spop(key: String, count: Long): MutableSet<String>
    fun scard(key: String): Long
    fun sismember(key: String, member: String?): Boolean
    fun srandmember(key: String): String
    fun srandmember(key: String, count: Int): MutableList<String>
    fun strlen(key: String): Long
    fun zadd(key: String, score: Double, member: String?): Long
    fun zadd(key: String, score: Double, member: String?, params: ZAddParams?): Long
    fun zadd(key: String, scoreMembers: MutableMap<String, Double>?): Long
    fun zadd(key: String, scoreMembers: MutableMap<String, Double>?, params: ZAddParams?): Long
    fun zrange(key: String, start: Long, stop: Long): MutableSet<String>
    fun zrem(key: String, vararg members: String?): Long
    fun zincrby(key: String, increment: Double, member: String?): Double
    fun zincrby(key: String, increment: Double, member: String?, params: ZIncrByParams?): Double
    fun zrank(key: String, member: String?): Long
    fun zrevrank(key: String, member: String?): Long
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
        count: Int
    ): MutableSet<String>

    fun zrangeByScoreWithScores(key: String, min: Double, max: Double): MutableSet<Tuple>
    fun zrangeByScoreWithScores(key: String, min: String?, max: String?): MutableSet<Tuple>
    fun zrangeByScoreWithScores(
        key: String,
        min: Double,
        max: Double,
        offset: Int,
        count: Int
    ): MutableSet<Tuple>

    fun zrangeByScoreWithScores(
        key: String,
        min: String?,
        max: String?,
        offset: Int,
        count: Int
    ): MutableSet<Tuple>

    fun zrevrangeByScoreWithScores(key: String, max: Double, min: Double): MutableSet<Tuple>
    fun zrevrangeByScoreWithScores(
        key: String,
        max: Double,
        min: Double,
        offset: Int,
        count: Int
    ): MutableSet<Tuple>

    fun zrevrangeByScoreWithScores(
        key: String,
        max: String?,
        min: String?,
        offset: Int,
        count: Int
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
    fun geoadd(key: String, longitude: Double, latitude: Double, member: String): Long
    fun geoadd(key: String, memberCoordinateMap: MutableMap<String, GeoCoordinate>? = null): Long
    fun geodist(key: String, member1: String, member2: String): Double
    fun geodist(key: String, member1: String, member2: String, unit: GeoUnit?): Double
    fun geohash(key: String, vararg members: String?): MutableList<String>
    fun geopos(key: String, vararg members: String?): MutableList<GeoCoordinate>
    fun georadius(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse>

    fun georadius(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse>

    fun georadiusReadonly(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse>

    fun georadiusReadonly(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse>

    fun georadiusByMember(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse>

    fun georadiusByMember(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse>

    fun georadiusByMemberReadonly(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse>

    fun georadiusByMemberReadonly(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse>

    fun hscan(key: String, cursor: String?): ScanResult<MutableMap.MutableEntry<String, String>>
    fun hscan(
        key: String,
        cursor: String?,
        params: ScanParams?
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

    override fun ping(message: String): String {
        return _hdl.ping(message)
    }

    override fun ping(): String {
        return _hdl.ping()
    }

    override fun quit(): String {
        return _hdl.quit()
    }

    override fun auth(password: String): String {
        return _hdl.auth(password)
    }

    override fun auth(user: String, password: String): String {
        return _hdl.auth(user, password)
    }

    override fun info(): String {
        return _hdl.info()
    }

    override fun info(section: String): String {
        return _hdl.info(section)
    }

    override fun set(key: String, value: String, params: SetParams?): String {
        if (params != null)
            return _hdl.set(safeKey(key), value, params)
        return _hdl.set(safeKey(key), value)
    }

    override fun get(key: String): String {
        return _hdl.get(safeKey(key))
    }

    override fun exists(key: String): Boolean {
        return _hdl.exists(safeKey(key))
    }

    override fun persist(key: String): Long {
        return _hdl.persist(safeKey(key))
    }

    override fun type(key: String): String {
        return _hdl.type(safeKey(key))
    }

    override fun expire(key: String, seconds: Int): Long {
        return _hdl.expire(safeKey(key), seconds)
    }

    override fun pexpire(key: String, milliseconds: Long): Long {
        return _hdl.pexpire(safeKey(key), milliseconds)
    }

    override fun expireAt(key: String, unixTime: Long): Long {
        return _hdl.expireAt(safeKey(key), unixTime)
    }

    override fun pexpireAt(key: String, millisecondsTimestamp: Long): Long {
        return _hdl.pexpireAt(safeKey(key), millisecondsTimestamp)
    }

    override fun ttl(key: String): Long {
        return _hdl.ttl(safeKey(key))
    }

    override fun pttl(key: String): Long {
        return _hdl.pttl(safeKey(key))
    }

    override fun touch(key: String): Long {
        return _hdl.touch(safeKey(key))
    }

    override fun setbit(key: String, offset: Long, value: Boolean): Boolean {
        return _hdl.setbit(safeKey(key), offset, value)
    }

    override fun setbit(key: String, offset: Long, value: String): Boolean {
        return _hdl.setbit(safeKey(key), offset, value)
    }

    override fun getbit(key: String, offset: Long): Boolean {
        return _hdl.getbit(safeKey(key), offset)
    }

    override fun setrange(key: String, offset: Long, value: String?): Long {
        return _hdl.setrange(safeKey(key), offset, value)
    }

    override fun getrange(key: String, startOffset: Long, endOffset: Long): String {
        return _hdl.getrange(safeKey(key), startOffset, endOffset)
    }

    override fun getSet(key: String, value: String?): String {
        return _hdl.getSet(safeKey(key), value)
    }

    override fun setnx(key: String, value: String?): Long {
        return _hdl.setnx(safeKey(key), value)
    }

    override fun setex(key: String, seconds: Int, value: String?): String {
        return _hdl.setex(safeKey(key), seconds, value)
    }

    override fun psetex(key: String, milliseconds: Long, value: String?): String {
        return _hdl.psetex(safeKey(key), milliseconds, value)
    }

    override fun decrBy(key: String, decrement: Long): Long {
        return _hdl.decrBy(safeKey(key), decrement)
    }

    override fun decr(key: String): Long {
        return _hdl.decr(safeKey(key))
    }

    override fun incrBy(key: String, increment: Long): Long {
        return _hdl.incrBy(safeKey(key), increment)
    }

    override fun incrByFloat(key: String, increment: Double): Double {
        return _hdl.incrByFloat(safeKey(key), increment)
    }

    override fun incr(key: String): Long {
        return _hdl.incr(safeKey(key))
    }

    override fun append(key: String, value: String?): Long {
        return _hdl.append(safeKey(key), value)
    }

    override fun substr(key: String, start: Int, end: Int): String {
        return _hdl.substr(safeKey(key), start, end)
    }

    override fun hset(key: String, field: String?, value: String?): Long {
        return _hdl.hset(safeKey(key), field, value)
    }

    override fun hset(key: String, hash: MutableMap<String, String>?): Long {
        return _hdl.hset(safeKey(key), hash)
    }

    override fun hget(key: String, field: String?): String {
        return _hdl.hget(safeKey(key), field)
    }

    override fun hsetnx(key: String, field: String?, value: String?): Long {
        return _hdl.hsetnx(safeKey(key), field, value)
    }

    override fun hmset(key: String, hash: MutableMap<String, String>?): String {
        return _hdl.hmset(safeKey(key), hash)
    }

    override fun hmget(key: String, vararg fields: String?): MutableList<String> {
        return _hdl.hmget(safeKey(key), *fields)
    }

    override fun hincrBy(key: String, field: String?, value: Long): Long {
        return _hdl.hincrBy(safeKey(key), field, value)
    }

    override fun hincrByFloat(key: String, field: String?, value: Double): Double {
        return _hdl.hincrByFloat(safeKey(key), field, value)
    }

    override fun hexists(key: String, field: String?): Boolean {
        return _hdl.hexists(safeKey(key), field)
    }

    override fun hdel(key: String, vararg fields: String?): Long {
        return _hdl.hdel(safeKey(key), *fields)
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

    override fun hgetAll(key: String): MutableMap<String, String> {
        return _hdl.hgetAll(safeKey(key))
    }

    override fun rpush(key: String, vararg strings: String?): Long {
        return _hdl.rpush(safeKey(key), *strings)
    }

    override fun lpush(key: String, vararg strings: String?): Long {
        return _hdl.lpush(safeKey(key), *strings)
    }

    override fun llen(key: String): Long {
        return _hdl.llen(safeKey(key))
    }

    override fun lrange(key: String, start: Long, stop: Long): MutableList<String> {
        return _hdl.lrange(safeKey(key), start, stop)
    }

    override fun ltrim(key: String, start: Long, stop: Long): String {
        return _hdl.ltrim(safeKey(key), start, stop)
    }

    override fun lindex(key: String, index: Long): String {
        return _hdl.lindex(safeKey(key), index)
    }

    override fun lset(key: String, index: Long, value: String?): String {
        return _hdl.lset(safeKey(key), index, value)
    }

    override fun lrem(key: String, count: Long, value: String?): Long {
        return _hdl.lrem(safeKey(key), count, value)
    }

    override fun lpop(key: String): String {
        return _hdl.lpop(safeKey(key))
    }

    override fun rpop(key: String): String {
        return _hdl.rpop(safeKey(key))
    }

    override fun sadd(key: String, vararg members: String?): Long {
        return _hdl.sadd(safeKey(key), *members)
    }

    override fun smembers(key: String): MutableSet<String> {
        return _hdl.smembers(safeKey(key))
    }

    override fun srem(key: String, vararg members: String?): Long {
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

    override fun sismember(key: String, member: String?): Boolean {
        return _hdl.sismember(safeKey(key), member)
    }

    override fun srandmember(key: String): String {
        return _hdl.srandmember(safeKey(key))
    }

    override fun srandmember(key: String, count: Int): MutableList<String> {
        return _hdl.srandmember(safeKey(key), count)
    }

    override fun strlen(key: String): Long {
        return _hdl.strlen(safeKey(key))
    }

    override fun zadd(key: String, score: Double, member: String?): Long {
        return _hdl.zadd(safeKey(key), score, member)
    }

    override fun zadd(key: String, score: Double, member: String?, params: ZAddParams?): Long {
        return _hdl.zadd(safeKey(key), score, member, params)
    }

    override fun zadd(key: String, scoreMembers: MutableMap<String, Double>?): Long {
        return _hdl.zadd(safeKey(key), scoreMembers)
    }

    override fun zadd(key: String, scoreMembers: MutableMap<String, Double>?, params: ZAddParams?): Long {
        return _hdl.zadd(safeKey(key), scoreMembers, params)
    }

    override fun zrange(key: String, start: Long, stop: Long): MutableSet<String> {
        return _hdl.zrange(safeKey(key), start, stop)
    }

    override fun zrem(key: String, vararg members: String?): Long {
        return _hdl.zrem(safeKey(key), *members)
    }

    override fun zincrby(key: String, increment: Double, member: String?): Double {
        return _hdl.zincrby(safeKey(key), increment, member)
    }

    override fun zincrby(key: String, increment: Double, member: String?, params: ZIncrByParams?): Double {
        return _hdl.zincrby(safeKey(key), increment, member, params)
    }

    override fun zrank(key: String, member: String?): Long {
        return _hdl.zrank(safeKey(key), member)
    }

    override fun zrevrank(key: String, member: String?): Long {
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
        count: Int
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
        count: Int
    ): MutableSet<Tuple> {
        return _hdl.zrangeByScoreWithScores(safeKey(key), min, max, offset, count)
    }

    override fun zrangeByScoreWithScores(
        key: String,
        min: String?,
        max: String?,
        offset: Int,
        count: Int
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
        count: Int
    ): MutableSet<Tuple> {
        return _hdl.zrevrangeByScoreWithScores(safeKey(key), max, min, offset, count)
    }

    override fun zrevrangeByScoreWithScores(
        key: String,
        max: String?,
        min: String?,
        offset: Int,
        count: Int
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
        return _hdl.geoadd(safeKey(key), longitude, latitude, member)
    }

    override fun geoadd(key: String, memberCoordinateMap: MutableMap<String, GeoCoordinate>?): Long {
        return _hdl.geoadd(safeKey(key), memberCoordinateMap)
    }

    override fun geodist(key: String, member1: String, member2: String): Double {
        return _hdl.geodist(safeKey(key), member1, member2)
    }

    override fun geodist(key: String, member1: String, member2: String, unit: GeoUnit?): Double {
        return _hdl.geodist(safeKey(key), member1, member2, unit)
    }

    override fun geohash(key: String, vararg members: String?): MutableList<String> {
        return _hdl.geohash(safeKey(key), *members)
    }

    override fun geopos(key: String, vararg members: String?): MutableList<GeoCoordinate> {
        return _hdl.geopos(safeKey(key), *members)
    }

    override fun georadius(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadius(safeKey(key), longitude, latitude, radius, unit)
    }

    override fun georadius(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadius(safeKey(key), longitude, latitude, radius, unit, param)
    }

    override fun georadiusReadonly(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusReadonly(safeKey(key), longitude, latitude, radius, unit)
    }

    override fun georadiusReadonly(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusReadonly(safeKey(key), longitude, latitude, radius, unit, param)
    }

    override fun georadiusByMember(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusByMember(safeKey(key), member, radius, unit)
    }

    override fun georadiusByMember(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusByMember(safeKey(key), member, radius, unit, param)
    }

    override fun georadiusByMemberReadonly(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusByMemberReadonly(safeKey(key), member, radius, unit)
    }

    override fun georadiusByMemberReadonly(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusByMemberReadonly(safeKey(key), member, radius, unit, param)
    }

    override fun hscan(key: String, cursor: String?): ScanResult<MutableMap.MutableEntry<String, String>> {
        return _hdl.hscan(safeKey(key), cursor)
    }

    override fun hscan(
        key: String,
        cursor: String?,
        params: ScanParams?
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