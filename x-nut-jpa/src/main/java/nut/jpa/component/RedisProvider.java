package nut.jpa.component;

import cn.hutool.core.collection.CollUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Rain
 */
@Component
public class RedisProvider<K, V> {

    public KeyValue keyValue = new KeyValue();
    public Hash hash = new Hash();
    public List list = new List();
    public Set set = new Set();
    public SortedSet sortedSet = new SortedSet();

//    @Resource(name = "redisTemplatePlus")
    @Autowired
    private RedisTemplate<K, V> redisTemplate;

    /**
     * 检查给定 key 是否存在。
     */
    public Boolean hasKey(@NonNull K key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除给定的一个key 。
     * 不存在的 key 会被忽略。
     */
    public void del(@NonNull K key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除给定的一个或多个 key 。
     * 不存在的 key 会被忽略。
     */
    public void del(@NonNull K... keys) {
        redisTemplate.delete(Arrays.asList(keys));
    }

    /**
     * 删除给定的一个或多个 key 。
     * 不存在的 key 会被忽略。
     */
    public void del(@NonNull Collection<K> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 为给定 key 设置生存时间(秒)，当 key 过期时(生存时间为 0 )，它会被自动删除。
     * 设置成功返回 true 。
     * 当 key 不存在或者不能为 key 设置生存时间时，返回 false 。
     */
    public Boolean expire(@NonNull K key, @NonNull long timeout, @NonNull TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置key生存时间。key在date这个时间点过期（自动被删除）
     */
    public Boolean expireAt(@NonNull K key, @NonNull Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 清空整个 Redis 服务器的数据(删除所有数据库的所有 key )。
     * 此命令从不失败。
     * 为防止业务乱用，加上 @Deprecated 以警告
     */
    @Deprecated
    public void flushAll() {
        redisTemplate.execute((RedisCallback<Boolean>) conn -> {
            conn.flushAll();
            return true;
        });
    }

    /**
     * 删除在已连接的db的数据
     * 为防止业务乱用，加上 @Deprecated 以警告
     */
    @Deprecated
    public void flushDb() {
        redisTemplate.execute((RedisCallback<Boolean>) conn -> {
            conn.flushDb();
            return true;
        });
    }

    /**
     * Redis的String数据类型
     */
    public class KeyValue {

        /**
         * 获取value
         */
        public V get(@NonNull K key) {
            return redisTemplate.opsForValue().get(key);
        }

        /**
         * 返回所有(一个或多个)给定 key 的值。
         * 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil 。因此，该命令永不失败。
         */
        public java.util.List<V> get(@NonNull Collection<K> keys) {
            return redisTemplate.opsForValue().multiGet(keys);
        }

        /**
         * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
         * 返回给定 key 的旧值。
         * 当 key 没有旧值时，也即是， key 不存在时，返回 null 。
         */
        public V getAndSet(@NonNull K key, @NonNull V value) {
            return redisTemplate.opsForValue().getAndSet(key, value);
        }

        /**
         * 将字符串值 value 关联到 key 。
         * 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。
         * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
         */
        public void set(@NonNull K key, @NonNull V value) {
            redisTemplate.opsForValue().set(key, value);
        }

        /**
         * 将 key 的值设为 value ，当且仅当 key 不存在。
         * 若给定的 key 已经存在，则 SETNX 不做任何动作。
         */
        public Boolean setIfAbsent(@NonNull K key, @NonNull V value) {
            return redisTemplate.opsForValue().setIfAbsent(key, value);
        }

        /**
         * 将字符串值 value 关联到 key 。如果 key 已经持有其他值，就覆写旧值，无视类型。
         * 有效期为 timeout（即 timeout 后被自动删除）
         */
        public void set(@NonNull K key, @NonNull V value, long timeout, @NonNull TimeUnit unit) {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        }

        /**
         * 同时设置一个或多个 key-value 对。
         * 如果某个给定 key 已经存在，那么 MSET 会用新值覆盖原来的旧值.
         * 如果这不是你所希望的效果，请考虑使用 MSETNX 命令：它只会在所有给定 key 都不存在的情况下进行设置操作。
         * MSET 是一个原子性(atomic)操作，所有给定 key 都会在同一时间内被设置，
         * 某些给定 key 被更新而另一些给定 key 没有改变的情况，不可能发生。
         */
        public void set(@NonNull Map<K, V> map) {
            redisTemplate.opsForValue().multiSet(map);
        }

        /**
         * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在。
         * 即使只有一个给定 key 已存在， MSETNX 也会拒绝执行所有给定 key 的设置操作。
         * MSETNX 是原子性的，因此它可以用作设置多个不同 key 表示不同字段(field)的唯一性逻辑对象(unique logic object)，
         * 所有字段要么全被设置，要么全不被设置。
         */
        public void setIfAbsent(@NonNull Map<K, V> map) {
            redisTemplate.opsForValue().multiSetIfAbsent(map);
        }

        /**
         * 返回 key 所储存的字符串值的长度。
         * 当 key 储存的不是字符串值时，返回一个错误。
         */
        public Long size(@NonNull K key) {
            return redisTemplate.opsForValue().size(key);
        }

        /**
         * 删除key
         */
        public void del(@NonNull K key) {
            redisTemplate.delete(key);
        }

        /**
         * value加1.如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 increment 命令。
         */
        public Long increment(@NonNull K key) {
            return redisTemplate.opsForValue().increment(key, 1);
        }

        /**
         * value加 step .如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 increment 命令。
         */
        public Long increment(@NonNull K key, long step) {
            return redisTemplate.opsForValue().increment(key, step);
        }

        /**
         * value加 step .如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 increment 命令。
         */
        public Double increment(@NonNull K key, double step) {
            return redisTemplate.opsForValue().increment(key, step);
        }

        /**
         * 如果 key 已经存在并且是一个字符串， APPEND 命令将 value 追加到 key 原来的值的末尾。
         * 如果 key 不存在， APPEND 就简单地将给定 key 设为 value ，就像执行 SET key value 一样。
         */
        public Integer append(@NonNull K key, @NonNull String value) {
            return redisTemplate.opsForValue().append(key, value);
        }

    }

    /**
     * Redis的Hash数据类型
     */
    public class Hash<HK, HV> {

        public HV get(@NonNull K key, @NonNull String field) {
            return redisTemplate.<HK, HV>boundHashOps(key).get(field);
        }

        public java.util.List<HV> getAll(@NonNull K key) {
            return redisTemplate.<HK, HV>boundHashOps(key).values();
        }

        public Map<HK, HV> getEntries(@NonNull K key) {
            return redisTemplate.<HK, HV>boundHashOps(key).entries();
        }

        public java.util.Set<HK> getFields(@NonNull K key) {
            return redisTemplate.<HK, HV>boundHashOps(key).keys();
        }

        public java.util.List<HV> getValues(@NonNull K key, Collection<HK> fields) {
            BoundHashOperations<K, HK, HV> kObjectObjectBoundHashOperations = redisTemplate.boundHashOps(key);
            return kObjectObjectBoundHashOperations.multiGet(fields);
        }

        public java.util.List<HV> getNotNullValues(@NonNull K key, Collection<HK> fields) {
            java.util.List<HV> hvs = getValues(key, fields);
            if (CollUtil.isEmpty(hvs)) {
                return Collections.emptyList();
            }
            return hvs
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        public java.util.List<HV> getAllValues(@NonNull K key) {
            return redisTemplate.<HK, HV>boundHashOps(key).values();
        }

        /**
         * 返回 map 的 field 个数
         */
        public Long size(@NonNull K key) {
            return redisTemplate.boundHashOps(key).size();
        }

        public void put(@NonNull K key, @NonNull String field, @NonNull V value) {
            redisTemplate.boundHashOps(key).put(field, value);
        }

        /**
         * 当 field 不存在时，添加此值
         */
        public void putIfAbsent(@NonNull K key, @NonNull String field, @NonNull V value) {
            redisTemplate.boundHashOps(key).putIfAbsent(field, value);
        }

        /**
         * 同时将多个 field-value (域-值)对设置到哈希表 key 中。
         * 此命令会覆盖哈希表中已存在的域。
         * 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
         */
        public void putAll(@NonNull K key, @NonNull Map<? extends HK, ? extends HV> values) {
            redisTemplate.boundHashOps(key).putAll(values);
        }

        /**
         * 删除哈希表
         */
        public void del(@NonNull K key) {
            redisTemplate.delete(key);
        }

        /**
         * 删除哈希表
         */
        public void del(@NonNull K... keys) {
            redisTemplate.delete(Arrays.asList(keys));
        }

        /**
         * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略
         */
        public void del(@NonNull K key, @NonNull Object... fields) {
            redisTemplate.boundHashOps(key).delete(fields);
        }

        /**
         * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略
         */
        public void del(@NonNull K key, @NonNull Collection<Object> fields) {
            if (fields.isEmpty()) {
                return;
            }
            redisTemplate.boundHashOps(key).delete(fields.toArray());
        }

        /**
         * 是否存在哈希表
         */
        public Boolean hasKey(@NonNull K key) {
            return redisTemplate.hasKey(key);
        }

        /**
         * 查看哈希表 key 中，给定域 field 是否存在。
         * 如果哈希表含有给定域，返回 true 。
         * 如果哈希表不含有给定域，或 key 不存在，返回 false。
         */
        public Boolean hasField(@NonNull K key, @NonNull Object field) {
            return redisTemplate.boundHashOps(key).hasKey(field);
        }

        /**
         * 为哈希表 key 中的域 field 的值加上增量 1 。
         * 如果 key 不存在，一个新的哈希表被创建并执行 increment 命令。
         * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
         */
        public Long increment(@NonNull K key, @NonNull HK field) {
            return redisTemplate.boundHashOps(key).increment(field, 1);
        }

        /**
         * 为哈希表 key 中的域 field 的值加上增量 step 。
         * 增量也可以为负数，相当于对给定域进行减法操作。
         * 如果 key 不存在，一个新的哈希表被创建并执行 increment 命令。
         * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
         */
        public Long increment(@NonNull K key, @NonNull HK field, long step) {
            return redisTemplate.boundHashOps(key).increment(field, step);
        }

        /**
         * 为哈希表 key 中的域 field 的值加上增量 step 。
         * 增量也可以为负数，相当于对给定域进行减法操作。
         * 如果 key 不存在，一个新的哈希表被创建并执行 increment 命令。
         * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
         */
        public Double increment(@NonNull K key, @NonNull HK field, double step) {
            return redisTemplate.boundHashOps(key).increment(field, step);
        }

    }

    /**
     * Redis的List数据类型
     */
    public class List {

        /**
         * 返回列表 key 中，下标为 index 的元素。
         * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
         * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
         * 如果 key 不是列表类型，返回一个错误。
         */
        public V index(@NonNull K key, long index) {
            return redisTemplate.opsForList().index(key, index);
        }

        /**
         * 移除并返回列表 key 的头元素。
         *
         * @return 列表的头元素。 当 key 不存在时，返回 nil 。
         */
        public V leftPop(@NonNull K key) {
            return redisTemplate.opsForList().leftPop(key);
        }

        /**
         * 将 value 插入到列表 key 的表头
         * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
         * 当 key 存在但不是列表类型时，返回一个错误。
         *
         * @return 执行 LPUSH 命令后，列表的长度。
         */
        public Long leftPush(@NonNull K key, @NonNull V value) {
            return redisTemplate.opsForList().leftPush(key, value);
        }

        /**
         * 将一个或多个值 value 插入到列表 key 的表头
         * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
         * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
         * 当 key 存在但不是列表类型时，返回一个错误。
         *
         * @return 执行 LPUSH 命令后，列表的长度。
         */
        public Long leftPushAll(@NonNull K key, @NonNull V... values) {
            return redisTemplate.opsForList().leftPushAll(key, values);
        }

        /**
         * 将一个或多个值 value 插入到列表 key 的表头
         * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
         * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
         * 当 key 存在但不是列表类型时，返回一个错误。
         *
         * @return 执行 LPUSH 命令后，列表的长度。
         */
        public Long leftPushAll(@NonNull K key, @NonNull Collection<V> values) {
            return redisTemplate.opsForList().leftPushAll(key, values);
        }

        /**
         * 将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表。
         * 和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做。
         */
        public Long leftPushIfPresent(@NonNull K key, @NonNull V value) {
            return redisTemplate.opsForList().leftPushIfPresent(key, value);
        }

        /**
         * 将值 value 插入到列表 key 当中，位于值 pivot 之前或之后。
         * 当 pivot 不存在于列表 key 时，不执行任何操作。
         * 当 key 不存在时， key 被视为空列表，不执行任何操作。
         * 如果 key 不是列表类型，返回一个错误。
         */
        public Long leftPush(@NonNull K key, @NonNull V pivot, @NonNull V value) {
            return redisTemplate.opsForList().leftPush(key, pivot, value);
        }

        /**
         * 返回列表 key 的长度。
         * 如果 key 不存在，则 key 被解释为一个空列表，返回 0 .
         * 如果 key 不是列表类型，返回一个错误。
         */
        public Long size(@NonNull K key) {
            return redisTemplate.opsForList().size(key);
        }

        /**
         * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
         * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
         * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
         */
        public java.util.List<V> range(@NonNull K key, final long start, final long end) {
            return redisTemplate.opsForList().range(key, start, end);
        }

        /**
         * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
         * count 的值可以是以下几种：
         * count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。
         * count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。
         * count = 0 : 移除表中所有与 value 相等的值。
         *
         * @return 被移除元素的数量。因为不存在的 key 被视作空表(empty list)，所以当 key 不存在时， LREM 命令总是返回 0 。
         */
        public Long remove(@NonNull K key, final long count, V value) {
            return redisTemplate.opsForList().remove(key, count, value);
        }

        /**
         * 移除并返回列表 key 的尾元素。
         *
         * @return 列表的尾元素。当 key 不存在时，返回 nil 。
         */
        public V rightPop(@NonNull K key) {
            return redisTemplate.opsForList().rightPop(key);
        }

        /**
         * 将一个值 value 插入到列表 key 的表尾(最右边)
         * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
         * 当 key 存在但不是列表类型时，返回一个错误。
         *
         * @return 执行 RPUSH 操作后，表的长度。
         */
        public Long rightPush(@NonNull K key, V value) {
            return redisTemplate.opsForList().rightPush(key, value);
        }

        /**
         * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
         * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。
         * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
         * 当 key 存在但不是列表类型时，返回一个错误。
         * 在 Redis 2.4 版本以前的 RPUSH 命令，都只接受单个 value 值。
         *
         * @return 执行 RPUSH 操作后，表的长度。
         */
        public Long rightPushAll(@NonNull K key, V... values) {
            return redisTemplate.opsForList().rightPushAll(key, values);
        }

        /**
         * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
         * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。
         * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
         * 当 key 存在但不是列表类型时，返回一个错误。
         * 在 Redis 2.4 版本以前的 RPUSH 命令，都只接受单个 value 值。
         *
         * @return 执行 RPUSH 操作后，表的长度。
         */
        public Long rightPushAll(@NonNull K key, Collection<V> values) {
            return redisTemplate.opsForList().rightPushAll(key, values);
        }

        /**
         * 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表。
         * 和 RPUSH 命令相反，当 key 不存在时， RPUSHX 命令什么也不做。
         *
         * @return RPUSHX 命令执行之后，表的长度。
         */
        public Long rightPushIfPresent(@NonNull K key, V value) {
            return redisTemplate.opsForList().rightPushIfPresent(key, value);
        }

        /**
         * 将值 value 插入到列表 key 当中，位于值 pivot 之前或之后。
         * 当 pivot 不存在于列表 key 时，不执行任何操作。
         * 当 key 不存在时， key 被视为空列表，不执行任何操作。
         * 如果 key 不是列表类型，返回一个错误。
         *
         * @return 如果命令执行成功，返回插入操作完成之后，列表的长度。
         * 如果没有找到 pivot ，返回 -1 。
         * 如果 key 不存在或为空列表，返回 0 。
         */
        public Long rightPush(@NonNull K key, V pivot, V value) {
            return redisTemplate.opsForList().rightPush(key, pivot, value);
        }

        /**
         * 命令 RPOPLPUSH 在一个原子时间内，执行以下两个动作：
         * 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
         * 将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
         * 举个例子，你有两个列表 source 和 destination ， source 列表有元素 a, b, c ， destination 列表有元素 x, y, z ，
         * 执行 RPOPLPUSH source destination 之后， source 列表包含元素 a, b ， destination 列表包含元素 c, x, y, z ，
         * 并且元素 c 会被返回给客户端。
         * 如果 source 不存在，值 nil 被返回，并且不执行其他动作。
         * 如果 source 和 destination 相同，则列表中的表尾元素被移动到表头，并返回该元素，可以把这种特殊情况视作列表的旋转(rotation)操作。
         *
         * @return 被弹出的元素。
         */
        public V rightPopAndLeftPush(K sourceKey, K destinationKey) {
            return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
        }

        /**
         * 将列表 key 下标为 index 的元素的值设置为 value 。
         * 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误。
         * 关于列表下标的更多信息，请参考 LINDEX 命令。
         */
        public void set(@NonNull K key, final long index, V value) {
            redisTemplate.opsForList().set(key, index, value);
        }

        /**
         * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
         * 举个例子，执行命令 LTRIM list 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
         * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
         * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
         * 当 key 不是列表类型时，返回一个错误。
         * LTRIM 命令通常和 LPUSH 命令或 RPUSH 命令配合使用，举个例子：
         * LPUSH log newest_log
         * LTRIM log 0 99
         * 这个例子模拟了一个日志程序，每次将最新日志 newest_log 放到 log 列表中，并且只保留最新的 100 项。
         * 注意当这样使用 LTRIM 命令时，时间复杂度是O(1)，因为平均情况下，每次只有一个元素被移除。
         * 注意LTRIM命令和编程语言区间函数的区别
         * 假如你有一个包含一百个元素的列表 list ，对该列表执行 LTRIM list 0 10 ，结果是一个包含11个元素的列表，
         * 这表明 stop 下标也在 LTRIM 命令的取值范围之内(闭区间)，这和某些语言的区间函数可能不一致，
         * 比如Ruby的 Range.new 、 Array#slice 和Python的 range() 函数。
         * 超出范围的下标
         * 超出范围的下标值不会引起错误。
         * 如果 start 下标比列表的最大下标 end ( LLEN list 减去 1 )还要大，或者 start > stop ，
         * LTRIM 返回一个空列表(因为 LTRIM 已经将整个列表清空)。
         * 如果 stop 下标比 end 下标还要大，Redis将 stop 的值设置为 end 。
         */
        public void trim(@NonNull K key, final long start, final long end) {
            redisTemplate.opsForList().trim(key, start, end);
        }

        /**
         * 删除key
         */
        public void del(K key) {
            redisTemplate.delete(key);
        }

    }

    /**
     * Redis的Set数据类型
     */
    public class Set {

        /**
         * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。
         * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
         * 当 key 不是集合类型时，返回一个错误。
         * 在Redis2.4版本以前， SADD 只接受单个 member 值。
         *
         * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
         */
        public Long add(K key, V... values) {
            return redisTemplate.opsForSet().add(key, values);
        }

        /**
         * 返回一个集合的全部成员，该集合是所有给定集合之间的差集。
         * 不存在的 key 被视为空集。
         *
         * @return 交集成员的列表。
         */
        public java.util.Set<V> difference(K key, K otherKey) {
            return redisTemplate.opsForSet().difference(key, otherKey);
        }

        /**
         * 返回一个集合的全部成员，该集合是所有给定集合之间的差集。
         * 不存在的 key 被视为空集。
         *
         * @return 交集成员的列表。
         */
        public java.util.Set<V> difference(final K key, final Collection<K> otherKeys) {
            return redisTemplate.opsForSet().difference(key, otherKeys);
        }

        /**
         * 这个命令的作用和 SDIFF 类似，但它将结果保存到 destination 集合，而不是简单地返回结果集。
         * 如果 destination 集合已经存在，则将其覆盖。
         * destination 可以是 key 本身。
         *
         * @return 结果集中的元素数量。
         */
        public Long differenceAndStore(K key, K otherKey, K destKey) {
            return redisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
        }

        /**
         * 这个命令的作用和 SDIFF 类似，但它将结果保存到 destination 集合，而不是简单地返回结果集。
         * 如果 destination 集合已经存在，则将其覆盖。
         * destination 可以是 key 本身。
         *
         * @return 结果集中的元素数量。
         */
        public Long differenceAndStore(final K key, final Collection<K> otherKeys, K destKey) {
            return redisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey);
        }

        /**
         * 返回一个集合的全部成员
         * 不存在的 key 被视为空集。
         *
         * @return 交集成员的列表。
         */
        public java.util.Set<V> intersect(K key, K otherKey) {
            return redisTemplate.opsForSet().intersect(key, otherKey);
        }

        /**
         * 返回一个集合的全部成员，该集合是所有给定集合的交集。
         * 不存在的 key 被视为空集。
         * 当给定集合当中有一个空集时，结果也为空集(根据集合运算定律)。
         *
         * @return 交集成员的列表。
         */
        public java.util.Set<V> intersect(K key, Collection<K> otherKeys) {
            return redisTemplate.opsForSet().intersect(key, otherKeys);
        }

        /**
         * 这个命令类似于 SINTER 命令，但它将结果保存到 destination 集合，而不是简单地返回结果集。
         * 如果 destination 集合已经存在，则将其覆盖。
         * destination 可以是 key 本身。
         *
         * @return 结果集中的成员数量。
         */
        public Long intersectAndStore(K key, K otherKey, K destKey) {
            return redisTemplate.opsForSet().intersectAndStore(key, otherKey, destKey);
        }

        /**
         * 这个命令类似于 SINTER 命令，但它将结果保存到 destination 集合，而不是简单地返回结果集。
         * 如果 destination 集合已经存在，则将其覆盖。
         * destination 可以是 key 本身。
         *
         * @return 结果集中的成员数量。
         */
        public Long intersectAndStore(K key, Collection<K> otherKeys, K destKey) {
            return redisTemplate.opsForSet().intersectAndStore(key, otherKeys, destKey);
        }

        /**
         * 判断 member 元素是否集合 key 的成员。
         *
         * @return 如果 member 元素是集合的成员，返回 1 。如果 member 元素不是集合的成员，或 key 不存在，返回 0 。
         */
        public Boolean isMember(K key, Object o) {
            return redisTemplate.opsForSet().isMember(key, o);
        }

        /**
         * 返回集合 key 中的所有成员。
         * 不存在的 key 被视为空集合。
         *
         * @return 集合中的所有成员。
         */
        public java.util.Set<V> members(K key) {
            return redisTemplate.opsForSet().members(key);
        }

        /**
         * 将 member 元素从 source 集合移动到 destination 集合。
         * SMOVE 是原子性操作。
         * 如果 source 集合不存在或不包含指定的 member 元素，则 SMOVE 命令不执行任何操作，仅返回 0 。
         * 否则， member 元素从 source 集合中被移除，并添加到 destination 集合中去。
         * 当 destination 集合已经包含 member 元素时， SMOVE 命令只是简单地将 source 集合中的 member 元素删除。
         * 当 source 或 destination 不是集合类型时，返回一个错误。
         *
         * @return 如果 member 元素被成功移除，返回 1 。
         * 如果 member 元素不是 source 集合的成员，并且没有任何操作对 destination 集合执行，那么返回 0 。
         */
        public Boolean move(K key, V value, K destKey) {
            return redisTemplate.opsForSet().move(key, value, destKey);
        }

        /**
         * 返回集合中的一个随机元素。
         * 从 Redis 2.6 版本开始， SRANDMEMBER 命令接受可选的 count 参数：
         * 该操作和 SPOP 相似，但 SPOP 将随机元素从集合中移除并返回，而 SRANDMEMBER 则仅仅返回随机元素，而不对集合进行任何改动。
         *
         * @return 返回一个元素；如果集合为空，返回 nil 。
         */
        public V randomMember(K key) {
            return redisTemplate.opsForSet().randomMember(key);
        }

        /**
         * 如果命令执行时，只提供了 key 参数，那么返回集合中的一个随机元素。
         * 从 Redis 2.6 版本开始， SRANDMEMBER 命令接受可选的 count 参数：
         * 如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，数组中的元素各不相同。
         * 如果 count 大于等于集合基数，那么返回整个集合。
         * 如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值。
         * 该操作和 SPOP 相似，但 SPOP 将随机元素从集合中移除并返回，而 SRANDMEMBER 则仅仅返回随机元素，而不对集合进行任何改动。
         *
         * @return 只提供 key 参数时，返回一个元素；如果集合为空，返回 nil 。如果提供了 count 参数，那么返回一个数组；如果集合为空，返回空数组。
         */
        public java.util.Set<V> distinctRandomMembers(K key, final long count) {
            return redisTemplate.opsForSet().distinctRandomMembers(key, count);
        }

        /**
         * 如果命令执行时，只提供了 key 参数，那么返回集合中的一个随机元素。
         * 从 Redis 2.6 版本开始， SRANDMEMBER 命令接受可选的 count 参数：
         * 如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，数组中的元素各不相同。如果 count 大于等于集合基数，那么返回整个集合。
         * 如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值。
         * 该操作和 SPOP 相似，但 SPOP 将随机元素从集合中移除并返回，而 SRANDMEMBER 则仅仅返回随机元素，而不对集合进行任何改动。
         *
         * @return 只提供 key 参数时，返回一个元素；如果集合为空，返回 nil 。如果提供了 count 参数，那么返回一个数组；如果集合为空，返回空数组。
         */
        public java.util.List<V> randomMembers(K key, final long count) {
            return redisTemplate.opsForSet().randomMembers(key, count);
        }

        /**
         * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
         * 当 key 不是集合类型，返回一个错误。
         * 在 Redis 2.4 版本以前， SREM 只接受单个 member 值。
         *
         * @return 被成功移除的元素的数量，不包括被忽略的元素。
         */
        public Long remove(K key, V... values) {
            return redisTemplate.opsForSet().remove(key, values);
        }

        /**
         * 移除并返回集合中的一个随机元素。
         * 如果只想获取一个随机元素，但不想该元素从集合中被移除的话，可以使用 SRANDMEMBER 命令。
         *
         * @return 被移除的随机元素。当 key 不存在或 key 是空集时，返回 nil 。
         */
        public Object pop(K key) {
            return redisTemplate.opsForSet().pop(key);
        }

        /**
         * 返回集合 key 的基数(集合中元素的数量)。
         *
         * @return 集合的基数。当 key 不存在时，返回 0 。
         */
        public Long size(K key) {
            return redisTemplate.opsForSet().size(key);
        }

        /**
         * 返回一个集合的全部成员，该集合是所有给定集合的并集。
         * 不存在的 key 被视为空集。
         *
         * @return 并集成员的列表。
         */
        public java.util.Set<V> union(K key, K otherKey) {
            return redisTemplate.opsForSet().union(key, otherKey);
        }

        /**
         * 返回一个集合的全部成员，该集合是所有给定集合的并集。
         * 不存在的 key 被视为空集。
         *
         * @return 并集成员的列表。
         */
        public java.util.Set<V> union(K key, Collection<K> otherKeys) {
            return redisTemplate.opsForSet().union(key, otherKeys);
        }

        /**
         * 这个命令类似于 SUNION 命令，但它将结果保存到 destination 集合，而不是简单地返回结果集。
         * 如果 destination 已经存在，则将其覆盖。
         * destination 可以是 key 本身。
         *
         * @return 结果集中的元素数量。
         */
        public Long unionAndStore(K key, K otherKey, K destKey) {
            return redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
        }

        /**
         * 这个命令类似于 SUNION 命令，但它将结果保存到 destination 集合，而不是简单地返回结果集。
         * 如果 destination 已经存在，则将其覆盖。
         * destination 可以是 key 本身。
         *
         * @return 结果集中的元素数量。
         */
        public Long unionAndStore(K key, Collection<K> otherKeys, K destKey) {
            return redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
        }

        /**
         * 删除key
         */
        public void del(K key) {
            redisTemplate.delete(key);
        }

    }

    /**
     * Redis的SortedSet数据类型
     */
    public class SortedSet {

        /**
         * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
         * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
         * score 值可以是整数值或双精度浮点数。
         * 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
         * 当 key 存在但不是有序集类型时，返回一个错误。
         *
         * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
         */
        public Boolean add(final K key, final V value, final double score) {
            return redisTemplate.opsForZSet().add(key, value, score);
        }

        /**
         * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
         * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
         * score 值可以是整数值或双精度浮点数。
         * 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
         * 当 key 存在但不是有序集类型时，返回一个错误。
         *
         * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
         */
        public Long add(K key, java.util.Set<ZSetOperations.TypedTuple<V>> tuples) {
            return redisTemplate.opsForZSet().add(key, tuples);
        }

        /**
         * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
         * 可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY key -5 member ，就是让 member 的 score 值减去 5 。
         * 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key increment member 等同于 ZADD key increment member 。
         * 当 key 不是有序集类型时，返回一个错误。
         * score 值可以是整数值或双精度浮点数。
         *
         * @return member 成员的新 score 值，以字符串形式表示。
         */
        public Double incrementScore(K key, V value, final double delta) {
            return redisTemplate.opsForZSet().incrementScore(key, value, delta);
        }

        /**
         * 计算给定的一个或多个有序集的交集，其中给定 key 的数量必须以 numkeys 参数指定，并将该交集(结果集)储存到 destination 。
         * 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员 score 值之和.
         * 关于 WEIGHTS 和 AGGREGATE 选项的描述，参见 ZUNIONSTORE 命令。
         *
         * @return 保存到 destination 的结果集的基数。
         */
        public Long intersectAndStore(K key, K otherKey, K destKey) {
            return redisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey);
        }

        /**
         * 计算给定的一个或多个有序集的交集，其中给定 key 的数量必须以 numkeys 参数指定，并将该交集(结果集)储存到 destination 。
         * 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员 score 值之和.
         * 关于 WEIGHTS 和 AGGREGATE 选项的描述，参见 ZUNIONSTORE 命令。
         *
         * @return 保存到 destination 的结果集的基数。
         */
        public Long intersectAndStore(K key, Collection<K> otherKeys, K destKey) {
            return redisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey);
        }

        /**
         * 返回有序集 key 中，指定区间内的成员。
         * 其中成员的位置按 score 值递增(从小到大)来排序。
         * 具有相同 score 值的成员按字典序(lexicographical order )来排列。
         * 如果你需要成员按 score 值递减(从大到小)来排列，请使用 ZREVRANGE 命令。
         * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
         * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
         * 超出范围的下标并不会引起错误。
         * 比如说，当 start 的值比有序集的最大下标还要大，或是 start > stop 时， ZRANGE 命令只是简单地返回一个空列表。
         * 另一方面，假如 stop 参数的值比有序集的最大下标还要大，那么 Redis 将 stop 当作最大下标来处理。
         * 可以通过使用 WITHSCORES 选项，来让成员和它的 score 值一并返回，返回列表以 value1,score1, ..., valueN,scoreN 的格式表示。
         * 客户端库可能会返回一些更复杂的数据类型，比如数组、元组等。
         *
         * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
         */
        public java.util.Set<V> range(K key, final long start, final long end) {
            return redisTemplate.opsForZSet().range(key, start, end);
        }

        /**
         * 返回有序集 key 中，指定区间内的成员。
         * 其中成员的位置按 score 值递减(从大到小)来排列。
         * 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order)排列。
         * 除了成员按 score 值递减的次序排列这一点外， ZREVRANGE 命令的其他方面和 ZRANGE 命令一样。
         *
         * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
         */
        public java.util.Set<V> reverseRange(K key, final long start, final long end) {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        }

        /**
         * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
         * 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
         * 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，
         * 注意当 offset 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
         * 可选的 WITHSCORES 参数决定结果集是单单返回有序集的成员，还是将有序集成员及其 score 值一起返回。
         * 该选项自 Redis 2.0 版本起可用。
         * 区间及无限
         * min 和 max 可以是 -inf 和 +inf ，这样一来，你就可以在不知道有序集的最低和最高 score 值的情况下，使用 ZRANGEBYSCORE 这类命令。
         * 默认情况下，区间的取值使用闭区间 (小于等于或大于等于)，你也可以通过给参数前增加 ( 符号来使用可选的开区间 (小于或大于)。
         * 举个例子：
         * ZRANGEBYSCORE zset (1 5
         * 返回所有符合条件 1 < score <= 5 的成员，而
         * ZRANGEBYSCORE zset (5 (10
         * 则返回所有符合条件 5 < score < 10 的成员。
         *
         * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
         */
        public java.util.Set<V> rangeByScore(K key, final double min, final double max) {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        }

        /**
         * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
         * 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
         * 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，
         * 注意当 offset 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
         * 可选的 WITHSCORES 参数决定结果集是单单返回有序集的成员，还是将有序集成员及其 score 值一起返回。
         * 该选项自 Redis 2.0 版本起可用。
         * 区间及无限
         * min 和 max 可以是 -inf 和 +inf ，这样一来，你就可以在不知道有序集的最低和最高 score 值的情况下，使用 ZRANGEBYSCORE 这类命令。
         * 默认情况下，区间的取值使用闭区间 (小于等于或大于等于)，你也可以通过给参数前增加 ( 符号来使用可选的开区间 (小于或大于)。
         * 举个例子：
         * ZRANGEBYSCORE zset (1 5
         * 返回所有符合条件 1 < score <= 5 的成员，而
         * ZRANGEBYSCORE zset (5 (10
         * 则返回所有符合条件 5 < score < 10 的成员。
         *
         * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
         */
        public java.util.Set<V> rangeByScore(K key,
                                             final double min,
                                             final double max,
                                             final long offset,
                                             final long count) {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
        }

        /**
         * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。
         * 有序集成员按 score 值递减(从大到小)的次序排列。
         * 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
         * 除了成员按 score 值递减的次序排列这一点外， ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE 命令一样。
         *
         * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
         */
        public java.util.Set<V> reverseRangeByScore(K key, final double min, final double max) {
            return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
        }

        /**
         * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。
         * 有序集成员按 score 值递减(从大到小)的次序排列。
         * 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
         * 除了成员按 score 值递减的次序排列这一点外， ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE 命令一样。
         *
         * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
         */
        public java.util.Set<V> reverseRangeByScore(K key,
                                                    final double min,
                                                    final double max,
                                                    final long offset,
                                                    final long count) {
            return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset, count);
        }

        public java.util.Set<ZSetOperations.TypedTuple<V>> rangeByScoreWithScores(K key,
                                                                                  final double min,
                                                                                  final double max) {
            return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
        }

        public java.util.Set<ZSetOperations.TypedTuple<V>> rangeByScoreWithScores(K key,
                                                                                  final double min,
                                                                                  final double max,
                                                                                  final long offset,
                                                                                  final long count) {
            return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, offset, count);
        }

        public java.util.Set<ZSetOperations.TypedTuple<V>> reverseRangeByScoreWithScores(K key,
                                                                                         final double min,
                                                                                         final double max) {
            return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
        }

        public java.util.Set<ZSetOperations.TypedTuple<V>> reverseRangeByScoreWithScores(K key,
                                                                                         final double min,
                                                                                         final double max,
                                                                                         final long offset,
                                                                                         final long count) {
            return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max, offset, count);
        }

        /**
         * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。
         * 排名以 0 为底，也就是说， score 值最小的成员排名为 0 。
         * 使用 ZREVRANK 命令可以获得成员按 score 值递减(从大到小)排列的排名。
         *
         * @return 如果 member 是有序集 key 的成员，返回 member 的排名。如果 member 不是有序集 key 的成员，返回 nil 。
         */
        public Long rank(K key, Object o) {
            return redisTemplate.opsForZSet().rank(key, o);
        }

        /**
         * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。
         * 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。
         * 使用 ZRANK 命令可以获得成员按 score 值递增(从小到大)排列的排名。
         *
         * @return 如果 member 是有序集 key 的成员，返回 member 的排名。如果 member 不是有序集 key 的成员，返回 nil 。
         */
        public Long reverseRank(K key, Object o) {
            return redisTemplate.opsForZSet().reverseRank(key, o);
        }

        /**
         * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。
         * 当 key 存在但不是有序集类型时，返回一个错误。
         * 在 Redis 2.4 版本以前， ZREM 每次只能删除一个元素。
         *
         * @return 被成功移除的成员的数量，不包括被忽略的成员。
         */
        public Long remove(K key, V... values) {
            return redisTemplate.opsForZSet().remove(key, values);
        }

        public Long removeRange(K key, final long start, final long end) {
            return redisTemplate.opsForZSet().removeRange(key, start, end);
        }

        /**
         * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
         * 自版本2.1.6开始， score 值等于 min 或 max 的成员也可以不包括在内，详情请参见 ZRANGEBYSCORE 命令。
         *
         * @return 被移除成员的数量。
         */
        public Long removeRangeByScore(K key, final double min, final double max) {
            return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
        }

        /**
         * 返回有序集 key 中，成员 member 的 score 值。
         * 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 nil 。
         *
         * @return member 成员的 score 值，以字符串形式表示。
         */
        public Double score(K key, Object o) {
            return redisTemplate.opsForZSet().score(key, o);
        }

        /**
         * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
         * 关于参数 min 和 max 的详细使用方法，请参考 ZRANGEBYSCORE 命令。
         *
         * @return score 值在 min 和 max 之间的成员的数量。
         */
        public Long count(K key, final double min, final double max) {
            return redisTemplate.opsForZSet().count(key, min, max);
        }

        /**
         * 返回有序集 key 的基数。
         *
         * @return 当 key 存在且是有序集类型时，返回有序集的基数。当 key 不存在时，返回 0 。
         */
        public Long size(K key) {
            return redisTemplate.opsForZSet().size(key);
        }

        /**
         * 返回有序集 key 的基数。
         *
         * @return 当 key 存在且是有序集类型时，返回有序集的基数。当 key 不存在时，返回 0 。
         */
        public Long zCard(K key) {
            return redisTemplate.opsForZSet().zCard(key);
        }

        /**
         * 计算给定的一个或多个有序集的并集，其中给定 key 的数量必须以 numkeys 参数指定，并将该并集(结果集)储存到 destination 。
         * 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员 score 值之 和 。
         * WEIGHTS
         * 使用 WEIGHTS 选项，你可以为 每个 给定有序集 分别 指定一个乘法因子(multiplication factor)，
         * 每个给定有序集的所有成员的 score 值在传递给聚合函数(aggregation function)之前都要先乘以该有序集的因子。
         * 如果没有指定 WEIGHTS 选项，乘法因子默认设置为 1 。
         * AGGREGATE
         * 使用 AGGREGATE 选项，你可以指定并集的结果集的聚合方式。
         * 默认使用的参数 SUM ，可以将所有集合中某个成员的 score 值之 和 作为结果集中该成员的 score 值；
         * 使用参数 MIN ，可以将所有集合中某个成员的 最小 score 值作为结果集中该成员的 score 值；
         * 而参数 MAX 则是将所有集合中某个成员的 最大 score 值作为结果集中该成员的 score 值。
         *
         * @return 保存到 destination 的结果集的基数。
         */
        public Long unionAndStore(K key, K otherKey, K destKey) {
            return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
        }

        /**
         * 计算给定的一个或多个有序集的并集，其中给定 key 的数量必须以 numkeys 参数指定，并将该并集(结果集)储存到 destination 。
         * 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员 score 值之 和 。
         * WEIGHTS
         * 使用 WEIGHTS 选项，你可以为 每个 给定有序集 分别 指定一个乘法因子(multiplication factor)，
         * 每个给定有序集的所有成员的 score 值在传递给聚合函数(aggregation function)之前都要先乘以该有序集的因子。
         * 如果没有指定 WEIGHTS 选项，乘法因子默认设置为 1 。
         * AGGREGATE
         * 使用 AGGREGATE 选项，你可以指定并集的结果集的聚合方式。
         * 默认使用的参数 SUM ，可以将所有集合中某个成员的 score 值之 和 作为结果集中该成员的 score 值；
         * 使用参数 MIN ，可以将所有集合中某个成员的 最小 score 值作为结果集中该成员的 score 值；
         * 而参数 MAX 则是将所有集合中某个成员的 最大 score 值作为结果集中该成员的 score 值。
         *
         * @return 保存到 destination 的结果集的基数。
         */
        public Long unionAndStore(K key, Collection<K> otherKeys, K destKey) {
            return redisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey);
        }

    }
}
