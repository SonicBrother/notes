IF 常用的判断条件

---

~~~xml
<if test="classify != null and classify != ''">
            
</if>
~~~

~~~xml
<if test="classify.toLowercase() == 'aa' or classify.toLowercase() == 'bb'">

</if>
~~~

```xml
<include refid="tb_user"/>
```

```xml
<if test="userList != null and userList.size() > 0">

</if>
```

```xml
<![CDATA[
and start_data >= concat(#{year},'-01-01') and start_data <= concat(#{year},'-12-31')
]]>
```



~~~xml
<if test="username != null and username.indexOf('ji') == 0"> </if> <!-- 是否以什么开头 -->
<if test="username != null and username.indexOf('ji') >= 0"> </if> <!-- 是否包含某字符 -->
<if test="username != null and username.lastIndexOf('ji') > 0"></if>  <!-- 是否以什么结尾 -->
~~~

~~~
map参数同同理  取值的话 map.key(map中的key名字)即可
~~~

mybatis的if条件判断语句可以直接执行对象的方法

~~~xml
public class DynamicSql1Model {
    public boolean getMySelfMethod(){
        return true;
    }
}

<if test="dynamicSql1Model.getMySelfMethod()">
~~~



---

参考链接

1. https://www.cnblogs.com/sumlen/p/11130554.html