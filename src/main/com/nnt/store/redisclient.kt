package com.nnt.store

import redis.clients.jedis.*
import redis.clients.jedis.params.GeoRadiusParam
import redis.clients.jedis.params.SetParams
import redis.clients.jedis.params.ZAddParams
import redis.clients.jedis.params.ZIncrByParams

class RedisClient(val jedis: Jedis, val kv: KvRedis) {

    private val _hdl = jedis
    private var prefix: String? = kv.prefix

    private fun safeKey(key: String): String {
        if (prefix == null)
            return key
        return "${prefix}:${key}"
    }

    fun ping(message: String): String {
        return _hdl.ping(message)
    }

    fun ping(): String {
        return _hdl.ping()
    }

    fun quit(): String {
        return _hdl.quit()
    }

    fun auth(password: String): String {
        return _hdl.auth(password)
    }

    fun auth(user: String, password: String): String {
        return _hdl.auth(user, password)
    }

    fun info(): String {
        return _hdl.info()
    }

    fun info(section: String): String {
        return _hdl.info(section)
    }

    fun set(key: String, value: String, params: SetParams? = null): String {
        if (params != null)
            return _hdl.set(safeKey(key), value, params)
        return _hdl.set(safeKey(key), value)
    }

    fun get(key: String): String {
        return _hdl.get(safeKey(key))
    }

    fun exists(key: String): Boolean {
        return _hdl.exists(safeKey(key))
    }

    fun persist(key: String): Long {
        return _hdl.persist(safeKey(key))
    }

    fun type(key: String): String {
        return _hdl.type(safeKey(key))
    }

    fun expire(key: String, seconds: Int): Long {
        return _hdl.expire(safeKey(key), seconds)
    }

    fun pexpire(key: String, milliseconds: Long): Long {
        return _hdl.pexpire(safeKey(key), milliseconds)
    }

    fun expireAt(key: String, unixTime: Long): Long {
        return _hdl.expireAt(safeKey(key), unixTime)
    }

    fun pexpireAt(key: String, millisecondsTimestamp: Long): Long {
        return _hdl.pexpireAt(safeKey(key), millisecondsTimestamp)
    }

    fun ttl(key: String): Long {
        return _hdl.ttl(safeKey(key))
    }

    fun pttl(key: String): Long {
        return _hdl.pttl(safeKey(key))
    }

    fun touch(key: String): Long {
        return _hdl.touch(safeKey(key))
    }

    fun setbit(key: String, offset: Long, value: Boolean): Boolean {
        return _hdl.setbit(safeKey(key), offset, value)
    }

    fun setbit(key: String, offset: Long, value: String): Boolean {
        return _hdl.setbit(safeKey(key), offset, value)
    }

    fun getbit(key: String, offset: Long): Boolean {
        return _hdl.getbit(safeKey(key), offset)
    }

    fun setrange(key: String, offset: Long, value: String?): Long {
        return _hdl.setrange(safeKey(key), offset, value)
    }

    fun getrange(key: String, startOffset: Long, endOffset: Long): String {
        return _hdl.getrange(safeKey(key), startOffset, endOffset)
    }

    fun getSet(key: String, value: String?): String {
        return _hdl.getSet(safeKey(key), value)
    }

    fun setnx(key: String, value: String?): Long {
        return _hdl.setnx(safeKey(key), value)
    }

    fun setex(key: String, seconds: Int, value: String?): String {
        return _hdl.setex(safeKey(key), seconds, value)
    }

    fun psetex(key: String, milliseconds: Long, value: String?): String {
        return _hdl.psetex(safeKey(key), milliseconds, value)
    }

    fun decrBy(key: String, decrement: Long): Long {
        return _hdl.decrBy(safeKey(key), decrement)
    }

    fun decr(key: String): Long {
        return _hdl.decr(safeKey(key))
    }

    fun incrBy(key: String, increment: Long): Long {
        return _hdl.incrBy(safeKey(key), increment)
    }

    fun incrByFloat(key: String, increment: Double): Double {
        return _hdl.incrByFloat(safeKey(key), increment)
    }

    fun incr(key: String): Long {
        return _hdl.incr(safeKey(key))
    }

    fun append(key: String, value: String?): Long {
        return _hdl.append(safeKey(key), value)
    }

    fun substr(key: String, start: Int, end: Int): String {
        return _hdl.substr(safeKey(key), start, end)
    }

    fun hset(key: String, field: String?, value: String?): Long {
        return _hdl.hset(safeKey(key), field, value)
    }

    fun hset(key: String, hash: MutableMap<String, String>?): Long {
        return _hdl.hset(safeKey(key), hash)
    }

    fun hget(key: String, field: String?): String {
        return _hdl.hget(safeKey(key), field)
    }

    fun hsetnx(key: String, field: String?, value: String?): Long {
        return _hdl.hsetnx(safeKey(key), field, value)
    }

    fun hmset(key: String, hash: MutableMap<String, String>?): String {
        return _hdl.hmset(safeKey(key), hash)
    }

    fun hmget(key: String, vararg fields: String?): MutableList<String> {
        return _hdl.hmget(safeKey(key), *fields)
    }

    fun hincrBy(key: String, field: String?, value: Long): Long {
        return _hdl.hincrBy(safeKey(key), field, value)
    }

    fun hincrByFloat(key: String, field: String?, value: Double): Double {
        return _hdl.hincrByFloat(safeKey(key), field, value)
    }

    fun hexists(key: String, field: String?): Boolean {
        return _hdl.hexists(safeKey(key), field)
    }

    fun hdel(key: String, vararg fields: String?): Long {
        return _hdl.hdel(safeKey(key), *fields)
    }

    fun hlen(key: String): Long {
        return _hdl.hlen(safeKey(key))
    }

    fun hkeys(key: String): MutableSet<String> {
        return _hdl.hkeys(safeKey(key))
    }

    fun hvals(key: String): MutableList<String> {
        return _hdl.hvals(safeKey(key))
    }

    fun hgetAll(key: String): MutableMap<String, String> {
        return _hdl.hgetAll(safeKey(key))
    }

    fun rpush(key: String, vararg strings: String?): Long {
        return _hdl.rpush(safeKey(key), *strings)
    }

    fun lpush(key: String, vararg strings: String?): Long {
        return _hdl.lpush(safeKey(key), *strings)
    }

    fun llen(key: String): Long {
        return _hdl.llen(safeKey(key))
    }

    fun lrange(key: String, start: Long, stop: Long): MutableList<String> {
        return _hdl.lrange(safeKey(key), start, stop)
    }

    fun ltrim(key: String, start: Long, stop: Long): String {
        return _hdl.ltrim(safeKey(key), start, stop)
    }

    fun lindex(key: String, index: Long): String {
        return _hdl.lindex(safeKey(key), index)
    }

    fun lset(key: String, index: Long, value: String?): String {
        return _hdl.lset(safeKey(key), index, value)
    }

    fun lrem(key: String, count: Long, value: String?): Long {
        return _hdl.lrem(safeKey(key), count, value)
    }

    fun lpop(key: String): String {
        return _hdl.lpop(safeKey(key))
    }

    fun rpop(key: String): String {
        return _hdl.rpop(safeKey(key))
    }

    fun sadd(key: String, vararg members: String?): Long {
        return _hdl.sadd(safeKey(key), *members)
    }

    fun smembers(key: String): MutableSet<String> {
        return _hdl.smembers(safeKey(key))
    }

    fun srem(key: String, vararg members: String?): Long {
        return _hdl.srem(safeKey(key), *members)
    }

    fun spop(key: String): String {
        return _hdl.spop(safeKey(key))
    }

    fun spop(key: String, count: Long): MutableSet<String> {
        return _hdl.spop(safeKey(key), count)
    }

    fun scard(key: String): Long {
        return _hdl.scard(safeKey(key))
    }

    fun sismember(key: String, member: String?): Boolean {
        return _hdl.sismember(safeKey(key), member)
    }

    fun srandmember(key: String): String {
        return _hdl.srandmember(safeKey(key))
    }

    fun srandmember(key: String, count: Int): MutableList<String> {
        return _hdl.srandmember(safeKey(key), count)
    }

    fun strlen(key: String): Long {
        return _hdl.strlen(safeKey(key))
    }

    fun zadd(key: String, score: Double, member: String?): Long {
        return _hdl.zadd(safeKey(key), score, member)
    }

    fun zadd(key: String, score: Double, member: String?, params: ZAddParams?): Long {
        return _hdl.zadd(safeKey(key), score, member, params)
    }

    fun zadd(key: String, scoreMembers: MutableMap<String, Double>?): Long {
        return _hdl.zadd(safeKey(key), scoreMembers)
    }

    fun zadd(key: String, scoreMembers: MutableMap<String, Double>?, params: ZAddParams?): Long {
        return _hdl.zadd(safeKey(key), scoreMembers, params)
    }

    fun zrange(key: String, start: Long, stop: Long): MutableSet<String> {
        return _hdl.zrange(safeKey(key), start, stop)
    }

    fun zrem(key: String, vararg members: String?): Long {
        return _hdl.zrem(safeKey(key), *members)
    }

    fun zincrby(key: String, increment: Double, member: String?): Double {
        return _hdl.zincrby(safeKey(key), increment, member)
    }

    fun zincrby(key: String, increment: Double, member: String?, params: ZIncrByParams?): Double {
        return _hdl.zincrby(safeKey(key), increment, member, params)
    }

    fun zrank(key: String, member: String?): Long {
        return _hdl.zrank(safeKey(key), member)
    }

    fun zrevrank(key: String, member: String?): Long {
        return _hdl.zrevrank(safeKey(key), member)
    }

    fun zrevrange(key: String, start: Long, stop: Long): MutableSet<String> {
        return _hdl.zrevrange(safeKey(key), start, stop)
    }

    fun zrangeWithScores(key: String, start: Long, stop: Long): MutableSet<Tuple> {
        return _hdl.zrangeWithScores(safeKey(key), start, stop)
    }

    fun zrevrangeWithScores(key: String, start: Long, stop: Long): MutableSet<Tuple> {
        return _hdl.zrevrangeWithScores(safeKey(key), start, stop)
    }

    fun zcard(key: String): Long {
        return _hdl.zcard(safeKey(key))
    }

    fun zscore(key: String, member: String?): Double {
        return _hdl.zscore(safeKey(key), member)
    }

    fun zpopmax(key: String): Tuple {
        return _hdl.zpopmax(safeKey(key))
    }

    fun zpopmax(key: String, count: Int): MutableSet<Tuple> {
        return _hdl.zpopmax(safeKey(key), count)
    }

    fun zpopmin(key: String): Tuple {
        return _hdl.zpopmin(safeKey(key))
    }

    fun zpopmin(key: String, count: Int): MutableSet<Tuple> {
        return _hdl.zpopmin(safeKey(key), count)
    }

    fun sort(key: String): MutableList<String> {
        return _hdl.sort(safeKey(key))
    }

    fun sort(key: String, sortingParameters: SortingParams?): MutableList<String> {
        return _hdl.sort(safeKey(key), sortingParameters)
    }

    fun sort(key: String, sortingParameters: SortingParams?, dstkey: String): Long {
        return _hdl.sort(safeKey(key), sortingParameters, dstkey)
    }

    fun sort(key: String, dstkey: String): Long {
        return _hdl.sort(safeKey(key), dstkey)
    }

    fun zcount(key: String, min: Double, max: Double): Long {
        return _hdl.zcount(safeKey(key), min, max)
    }

    fun zcount(key: String, min: String?, max: String?): Long {
        return _hdl.zcount(safeKey(key), min, max)
    }

    fun zrangeByScore(key: String, min: Double, max: Double): MutableSet<String> {
        return _hdl.zrangeByScore(safeKey(key), min, max)
    }

    fun zrangeByScore(key: String, min: String?, max: String?): MutableSet<String> {
        return _hdl.zrangeByScore(safeKey(key), min, max)
    }

    fun zrangeByScore(key: String, min: Double, max: Double, offset: Int, count: Int): MutableSet<String> {
        return _hdl.zrangeByScore(safeKey(key), min, max, offset, count)
    }

    fun zrangeByScore(key: String, min: String?, max: String?, offset: Int, count: Int): MutableSet<String> {
        return _hdl.zrangeByScore(safeKey(key), min, max, offset, count)
    }

    fun zrevrangeByScore(key: String, max: Double, min: Double): MutableSet<String> {
        return _hdl.zrevrangeByScore(safeKey(key), max, min)
    }

    fun zrevrangeByScore(key: String, max: String?, min: String?): MutableSet<String> {
        return _hdl.zrevrangeByScore(safeKey(key), max, min)
    }

    fun zrevrangeByScore(key: String, max: Double, min: Double, offset: Int, count: Int): MutableSet<String> {
        return _hdl.zrevrangeByScore(safeKey(key), max, min, offset, count)
    }

    fun zrevrangeByScore(
        key: String,
        max: String?,
        min: String?,
        offset: Int,
        count: Int
    ): MutableSet<String> {
        return _hdl.zrevrangeByScore(safeKey(key), max, min, offset, count)
    }

    fun zrangeByScoreWithScores(key: String, min: Double, max: Double): MutableSet<Tuple> {
        return _hdl.zrangeByScoreWithScores(safeKey(key), min, max)
    }

    fun zrangeByScoreWithScores(key: String, min: String?, max: String?): MutableSet<Tuple> {
        return _hdl.zrangeByScoreWithScores(safeKey(key), min, max)
    }

    fun zrangeByScoreWithScores(
        key: String,
        min: Double,
        max: Double,
        offset: Int,
        count: Int
    ): MutableSet<Tuple> {
        return _hdl.zrangeByScoreWithScores(safeKey(key), min, max, offset, count)
    }

    fun zrangeByScoreWithScores(
        key: String,
        min: String?,
        max: String?,
        offset: Int,
        count: Int
    ): MutableSet<Tuple> {
        return _hdl.zrangeByScoreWithScores(safeKey(key), min, max, offset, count)
    }

    fun zrevrangeByScoreWithScores(key: String, max: Double, min: Double): MutableSet<Tuple> {
        return _hdl.zrevrangeByScoreWithScores(safeKey(key), max, min)
    }

    fun zrevrangeByScoreWithScores(
        key: String,
        max: Double,
        min: Double,
        offset: Int,
        count: Int
    ): MutableSet<Tuple> {
        return _hdl.zrevrangeByScoreWithScores(safeKey(key), max, min, offset, count)
    }

    fun zrevrangeByScoreWithScores(
        key: String,
        max: String?,
        min: String?,
        offset: Int,
        count: Int
    ): MutableSet<Tuple> {
        return _hdl.zrevrangeByScoreWithScores(safeKey(key), max, min, offset, count)
    }

    fun zrevrangeByScoreWithScores(key: String, max: String?, min: String?): MutableSet<Tuple> {
        return _hdl.zrevrangeByScoreWithScores(safeKey(key), max, min)
    }

    fun zremrangeByRank(key: String, start: Long, stop: Long): Long {
        return _hdl.zremrangeByRank(safeKey(key), start, stop)
    }

    fun zremrangeByScore(key: String, min: Double, max: Double): Long {
        return _hdl.zremrangeByScore(safeKey(key), min, max)
    }

    fun zremrangeByScore(key: String, min: String?, max: String?): Long {
        return _hdl.zremrangeByScore(safeKey(key), min, max)
    }

    fun zlexcount(key: String, min: String?, max: String?): Long {
        return _hdl.zlexcount(safeKey(key), min, max)
    }

    fun zrangeByLex(key: String, min: String?, max: String?): MutableSet<String> {
        return _hdl.zrangeByLex(safeKey(key), min, max)
    }

    fun zrangeByLex(key: String, min: String?, max: String?, offset: Int, count: Int): MutableSet<String> {
        return _hdl.zrangeByLex(safeKey(key), min, max, offset, count)
    }

    fun zrevrangeByLex(key: String, max: String?, min: String?): MutableSet<String> {
        return _hdl.zrevrangeByLex(safeKey(key), max, min)
    }

    fun zrevrangeByLex(key: String, max: String?, min: String?, offset: Int, count: Int): MutableSet<String> {
        return _hdl.zrevrangeByLex(safeKey(key), max, min, offset, count)
    }

    fun zremrangeByLex(key: String, min: String?, max: String?): Long {
        return _hdl.zremrangeByLex(safeKey(key), min, max)
    }

    fun linsert(key: String, where: ListPosition?, pivot: String?, value: String?): Long {
        return _hdl.linsert(safeKey(key), where, pivot, value)
    }

    fun lpushx(key: String, vararg string: String?): Long {
        return _hdl.lpushx(safeKey(key), *string)
    }

    fun rpushx(key: String, vararg string: String?): Long {
        return _hdl.rpushx(safeKey(key), *string)
    }

    fun del(key: String): Long {
        return _hdl.del(safeKey(key))
    }

    fun unlink(key: String): Long {
        return _hdl.unlink(safeKey(key))
    }

    fun echo(string: String?): String {
        return _hdl.echo(string)
    }

    fun move(key: String, dbIndex: Int): Long {
        return _hdl.move(safeKey(key), dbIndex)
    }

    fun bitcount(key: String): Long {
        return _hdl.bitcount(safeKey(key))
    }

    fun bitcount(key: String, start: Long, end: Long): Long {
        return _hdl.bitcount(safeKey(key), start, end)
    }

    fun pfadd(key: String, vararg elements: String?): Long {
        return _hdl.pfadd(safeKey(key), *elements)
    }

    fun pfcount(key: String): Long {
        return _hdl.pfcount(safeKey(key))
    }

    fun geoadd(key: String, longitude: Double, latitude: Double, member: String?): Long {
        return _hdl.geoadd(safeKey(key), longitude, latitude, member)
    }

    fun geoadd(key: String, memberCoordinateMap: MutableMap<String, GeoCoordinate>?): Long {
        return _hdl.geoadd(safeKey(key), memberCoordinateMap)
    }

    fun geodist(key: String, member1: String?, member2: String?): Double {
        return _hdl.geodist(safeKey(key), member1, member2)
    }

    fun geodist(key: String, member1: String?, member2: String?, unit: GeoUnit?): Double {
        return _hdl.geodist(safeKey(key), member1, member2, unit)
    }

    fun geohash(key: String, vararg members: String?): MutableList<String> {
        return _hdl.geohash(safeKey(key), *members)
    }

    fun geopos(key: String, vararg members: String?): MutableList<GeoCoordinate> {
        return _hdl.geopos(safeKey(key), *members)
    }

    fun georadius(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadius(safeKey(key), longitude, latitude, radius, unit)
    }

    fun georadius(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadius(safeKey(key), longitude, latitude, radius, unit, param)
    }

    fun georadiusReadonly(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusReadonly(safeKey(key), longitude, latitude, radius, unit)
    }

    fun georadiusReadonly(
        key: String,
        longitude: Double,
        latitude: Double,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusReadonly(safeKey(key), longitude, latitude, radius, unit, param)
    }

    fun georadiusByMember(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusByMember(safeKey(key), member, radius, unit)
    }

    fun georadiusByMember(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusByMember(safeKey(key), member, radius, unit, param)
    }

    fun georadiusByMemberReadonly(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusByMemberReadonly(safeKey(key), member, radius, unit)
    }

    fun georadiusByMemberReadonly(
        key: String,
        member: String?,
        radius: Double,
        unit: GeoUnit?,
        param: GeoRadiusParam?
    ): MutableList<GeoRadiusResponse> {
        return _hdl.georadiusByMemberReadonly(safeKey(key), member, radius, unit, param)
    }

    fun hscan(key: String, cursor: String?): ScanResult<MutableMap.MutableEntry<String, String>> {
        return _hdl.hscan(safeKey(key), cursor)
    }

    fun hscan(
        key: String,
        cursor: String?,
        params: ScanParams?
    ): ScanResult<MutableMap.MutableEntry<String, String>> {
        return _hdl.hscan(safeKey(key), cursor, params)
    }

    fun sscan(key: String, cursor: String?): ScanResult<String> {
        return _hdl.sscan(safeKey(key), cursor)
    }

    fun sscan(key: String, cursor: String?, params: ScanParams?): ScanResult<String> {
        return _hdl.sscan(safeKey(key), cursor, params)
    }

    fun zscan(key: String, cursor: String?): ScanResult<Tuple> {
        return _hdl.zscan(safeKey(key), cursor)
    }

    fun zscan(key: String, cursor: String?, params: ScanParams?): ScanResult<Tuple> {
        return _hdl.zscan(safeKey(key), cursor, params)
    }

    fun bitfield(key: String, vararg arguments: String?): MutableList<Long> {
        return _hdl.bitfield(safeKey(key), *arguments)
    }

    fun bitfieldReadonly(key: String, vararg arguments: String?): MutableList<Long> {
        return _hdl.bitfieldReadonly(safeKey(key), *arguments)
    }

    fun hstrlen(key: String, field: String?): Long {
        return _hdl.hstrlen(safeKey(key), field)
    }

    fun xadd(key: String, id: StreamEntryID?, hash: MutableMap<String, String>?): StreamEntryID {
        return _hdl.xadd(safeKey(key), id, hash)
    }

    fun xadd(
        key: String,
        id: StreamEntryID?,
        hash: MutableMap<String, String>?,
        maxLen: Long,
        approximateLength: Boolean
    ): StreamEntryID {
        return _hdl.xadd(safeKey(key), id, hash, maxLen, approximateLength)
    }

    fun xlen(key: String): Long {
        return _hdl.xlen(safeKey(key))
    }

    fun xrange(
        key: String,
        start: StreamEntryID?,
        end: StreamEntryID?,
        count: Int
    ): MutableList<StreamEntry> {
        return _hdl.xrange(safeKey(key), start, end, count)
    }

    fun xrevrange(
        key: String,
        end: StreamEntryID?,
        start: StreamEntryID?,
        count: Int
    ): MutableList<StreamEntry> {
        return _hdl.xrevrange(safeKey(key), end, start, count)
    }

    fun xack(key: String, group: String?, vararg ids: StreamEntryID?): Long {
        return _hdl.xack(safeKey(key), group, *ids)
    }

    fun xgroupCreate(key: String, groupname: String?, id: StreamEntryID?, makeStream: Boolean): String {
        return _hdl.xgroupCreate(safeKey(key), groupname, id, makeStream)
    }

    fun xgroupSetID(key: String, groupname: String?, id: StreamEntryID?): String {
        return _hdl.xgroupSetID(safeKey(key), groupname, id)
    }

    fun xgroupDestroy(key: String, groupname: String?): Long {
        return _hdl.xgroupDestroy(safeKey(key), groupname)
    }

    fun xgroupDelConsumer(key: String, groupname: String?, consumerName: String?): Long {
        return _hdl.xgroupDelConsumer(safeKey(key), groupname, consumerName)
    }

    fun xdel(key: String, vararg ids: StreamEntryID?): Long {
        return _hdl.xdel(safeKey(key), *ids)
    }

    fun xtrim(key: String, maxLen: Long, approximateLength: Boolean): Long {
        return _hdl.xtrim(safeKey(key), maxLen, approximateLength)
    }

    fun xpending(
        key: String,
        groupname: String?,
        start: StreamEntryID?,
        end: StreamEntryID?,
        count: Int,
        consumername: String?
    ): MutableList<StreamPendingEntry> {
        return _hdl.xpending(safeKey(key), groupname, start, end, count, consumername)
    }

    fun xclaim(
        key: String,
        group: String?,
        consumername: String?,
        minIdleTime: Long,
        newIdleTime: Long,
        retries: Int,
        force: Boolean,
        vararg ids: StreamEntryID?
    ): MutableList<StreamEntry> {
        return _hdl.xclaim(safeKey(key), group, consumername, minIdleTime, newIdleTime, retries, force, *ids)
    }

    fun xinfoStream(key: String): StreamInfo {
        return _hdl.xinfoStream(safeKey(key))
    }

    fun xinfoGroup(key: String): MutableList<StreamGroupInfo> {
        return _hdl.xinfoGroup(safeKey(key))
    }

    fun xinfoConsumers(key: String, group: String?): MutableList<StreamConsumersInfo> {
        return _hdl.xinfoConsumers(safeKey(key), group)
    }

    fun blpop(timeout: Int, key: String): MutableList<String> {
        return _hdl.blpop(timeout, safeKey(key))
    }

    fun brpop(timeout: Int, key: String): MutableList<String> {
        return _hdl.brpop(timeout, safeKey(key))
    }

    fun rename(oldkey: String, newkey: String): String {
        return _hdl.rename(safeKey(oldkey), safeKey(newkey))
    }

    fun renamenx(oldkey: String, newkey: String): Long {
        return _hdl.renamenx(safeKey(oldkey), safeKey(newkey))
    }

    fun rpoplpush(srckey: String, dstkey: String): String {
        return _hdl.rpoplpush(safeKey(srckey), safeKey(dstkey))
    }

    fun smove(srckey: String, dstkey: String, member: String?): Long {
        return _hdl.smove(safeKey(srckey), safeKey(dstkey), member)
    }

    fun close() {
        _hdl.close()
    }
}