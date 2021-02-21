### Mybatis二级缓存实现

#### mybatis的二级缓存是ibatis中的Cache接口实现的。

#### Redis实现二级缓存代码

~~~java
package com.baizhi.cache;

import com.baizhi.util.ApplicationContextUtils;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.DigestUtils;

import java.util.concurrent.TimeUnit;

// 自定义Redis缓存实现
// 由mybatis负责实现类  不是由工厂负责实例化 redisTemplate无法注入 使用工具类获取redisTemplate
public class RedisCache implements Cache {


    //当前放入缓存的mapper的namespace  （对应的接口的全路径）
    private final String id;

    //必须存在构造方法 必须有一个String类型的id的构造方法
    public RedisCache(String id) {
        System.out.println("id:=====================> " + id);
        this.id = id;
    }

    //返回cache唯一标识  name不能为空
    @Override
    public String getId() {
        // id的返回值
        return this.id;
    }


    //缓存放入值  redis RedisTemplate   StringRedisTemplate
    @Override
    public void putObject(Object key, Object value) {
        System.out.println("key:" + key.toString());
        System.out.println("value:" + value);
//        //通过application工具类获取redisTemplate
//        RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        //使用redishash类型作为缓存存储模型  key   hashkey  value
        // 大key是当前dao，小 key 是当前方法
        getRedisTemplate().opsForHash().put(id.toString(),getKeyToMD5(key.toString()),value);



        if(id.equals("com.baizhi.dao.UserDAO")){
            //缓存超时  client  用户   client  员工
            getRedisTemplate().expire(id.toString(),1, TimeUnit.HOURS);
        }


        if(id.equals("com.baizhi.dao.CityDAO")){
            //缓存超时  client  用户   client  员工
            getRedisTemplate().expire(id.toString(),30, TimeUnit.MINUTES);
        }

        //.....指定不同业务模块设置不同缓存超时时间

    }

    //获取中获取数据
    @Override
    public Object getObject(Object key) {
        System.out.println("key:" + key.toString());
//        //通过application工具类获取redisTemplate
//        RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        //根据key 从redis的hash类型中获取数据
        return getRedisTemplate().opsForHash().get(id.toString(), getKeyToMD5(key.toString()));
    }


    //注意:这个方法为mybatis保留方法 默认没有实现 后续版本可能会实现
    @Override
    public Object removeObject(Object key) {
        System.out.println("根据指定key删除缓存");
        return null;
    }

    // 增删改都会清空缓存
    @Override
    public void clear() {
        System.out.println("清空缓存~~~");
        //清空namespace
        getRedisTemplate().delete(id.toString());//清空缓存
    }

    //用来计算缓存数量
    @Override
    public int getSize() {
        //获取hash中key value数量
        // .intValue() 将long转为int
        return getRedisTemplate().opsForHash().size(id.toString()).intValue();
    }


    //封装redisTemplate
    private RedisTemplate getRedisTemplate(){
        //通过application工具类获取redisTemplate
        RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }


    //封装一个对key进行md5处理方法
    private String getKeyToMD5(String key){
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

}

~~~



mapper配置文件设置

~~~xml

    <!--开启mybatis二级缓存  指向自定义Cache-->
    <cache  type="com.baizhi.cache.RedisCache"/>


    <!--关联关系缓存处理-->
    <cache-ref namespace="com.baizhi.dao.UserDAO"/>

~~~



