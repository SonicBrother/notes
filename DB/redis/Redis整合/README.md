# 1. 为什么要自定义RedisTemplate

我们在代码中，可以完成RedisTemplate的注入，而实际上，我们只是单纯的配置了yml文件，在哪里创建了redisTemplate这个bean对象呢？

**redisTemplate自动装配的源码：**



```java
@Configuration
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@Import({ LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class })
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
```

通过源码可以看出，SpringBoot自动帮我们在容器中生成了一个**RedisTemplate**和一个**StringRedisTemplate**。

1. RedisTemplate的泛型是<Object,Object>，写代码不是很方便，一般我们的key是String类型，我们需要一个<String,Object>的泛型。
2. RedisTemplate没有设置数据存储在Redis时，Key和Value的序列化方式。（采用默认的JDK序列化方式）

**如何优雅的解决上述两个问题呢？**

> **`@ConditionalOnMissing注解`**：如果Spring容器中已经定义了id为redisTemplate的Bean，那么自动装配的RedisTemplate不会实例化。因此我们可以写一个配置类，配置Redisemplate。

若未自定义RedisTemplate，默认会对key进行jdk序列化。

# 2. 如何自定义RedisTemplate

## 2.1 Redis数据的序列化问题

针对StringRedisSerializer，Jackson2JsonRedisSerializer和JdkSerializationRedisSerializer进行测试

| 数据结构  | 序列化类                        | 序列化前   | 序列化后          |
| :-------- | :------------------------------ | :--------- | :---------------- |
| Key/Value | StringRedisSerializer           | test_value | test_value        |
| Key/Value | Jackson2JsonRedisSerializer     | test_value | "test_value"      |
| Key/Value | JdkSerializationRedisSerializer | test_value | 乱码              |
| Hash      | StringRedisSerializer           | 2016-08-18 | 2016-08-18        |
| Hash      | Jackson2JsonRedisSerializer     | 2016-08-18 | "2016-08-18"      |
| Hash      | JdkSerializationRedisSerializer | 2016-08-18 | \xAC\xED\x00\x05t |

**由此可以得到结论：**

1. StringRedisSerializer进行序列化后的值，在Java和Redis中保存的内容时一致的。
2. 用Jackson2JsonRedisSerializer序列化后，在Redis中保存的内容，比Java中多一对逗号。
3. 用JdkSerializationRedisSerializer序列化后，对于Key-Value结构来说，在Redis中不可读；对于Hash的Value来说，比Java的内容多了一些字符。

**自定义的redisTemplate实例：**



```cpp
@Configuration
public class RedisConfig {
    /**
     * 由于原生的redis自动装配，在存储key和value时，没有设置序列化方式，故自己创建redisTemplate实例
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
```

**对于Key来说，我们采用stringRedisSerializer。而对于Value来我们采用jackson2JsonRedisSerializer的序列化方式。**



作者：小胖学编程
链接：https://www.jianshu.com/p/0d4aea41a70c
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。