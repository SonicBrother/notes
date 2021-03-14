## mybatis关联查找

### 一对一

```sql
<association property="dep" column="id" select="namespace.id"/>
```

```sql
<association property="dep" javaType="com.baizhi.entity.Emp">
    <result column="id" property="id"/>
</association>
```

### 一对多

```xml
<!--
在关联查询时，如何传入多个参数给关联查询语句
1. 确认被关联语句中的接受参数的属性名
2. 在创建查询的语句中，构建虚拟列作为参数，如以下的：“#{id} as did,#{status} as statu” 这两个属性无需映射对象属性，就是作为参数传给关联对象
3. 使用colume={id=did,status=statu}将对应参数传过去
-->
<select id="getDepVOByidAndType" parameterType="java.util.Map" resultMap="maps">
    select id as id ,depName as depName,#{id} as did,#{status} as statu from dep where id = #{id}
</select>
<resultMap id="maps" type="com.baizhi.entity.Emp">
   <collection property="emps" column="{id=did,status=statu}" select="namespace.id"/>
</resultMap>
```

