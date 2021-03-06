package com.atguigu.cache.config;


import com.atguigu.cache.bean.Department;
import com.atguigu.cache.bean.Employee;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.net.UnknownHostException;
import java.time.Duration;

@Configuration
public class MyRedisConfig {

//    @Bean
    public RedisTemplate<Object, Employee> empRedisTemplate(
            RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<Object, Employee> template = new RedisTemplate<Object, Employee>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<Employee> ser = new Jackson2JsonRedisSerializer<Employee>(Employee.class);
        template.setDefaultSerializer(ser);
        return template;
    }
//    @Bean
    public RedisTemplate<Object, Department> deptRedisTemplate(
            RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<Object, Department> template = new RedisTemplate<Object, Department>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<Department> ser = new Jackson2JsonRedisSerializer<Department>(Department.class);
        template.setDefaultSerializer(ser);
        return template;
    }


    /**
     * ??????SpringBoot2 ??? RedisCacheManager ??????????????????
     * @param redisConnectionFactory
     * @return
     */
    @Primary
    @Bean
    public RedisCacheManager emptCacheManager(RedisConnectionFactory redisConnectionFactory) {
        //???????????????RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //??????CacheManager????????????????????????json?????????
//        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();// ??????object??????
        RedisSerializer<Employee> jsonSerializer = new Jackson2JsonRedisSerializer<Employee>(Employee.class);

        RedisSerializationContext.SerializationPair<Employee> pair = RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);

//        defaultCacheConfig.disableKeyPrefix();// ????????????????????????????????????????????? ?????????????????????????????????
        //???????????????????????????1???
        defaultCacheConfig.entryTtl(Duration.ofHours(1));
        //?????????RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }


    @Bean
    public RedisCacheManager deptCacheManager(RedisConnectionFactory redisConnectionFactory) {
        //???????????????RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //??????CacheManager????????????????????????json?????????
        /**
         * // ??????object?????? ?????????????????????????????????????????? ????????????
         * {
         *   "@class": "com.atguigu.cache.bean.Department",
         *   "id": 1,
         *   "departmentName": "33"
         * }
         */
//        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
        RedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer);

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
        //?????????RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }

}
