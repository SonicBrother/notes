## Fastjson

~~~java
fastjson
package com.bean;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class FastJsonDemos {

    ArrayList<Student> list = new ArrayList<>();
    Map<String,Student> map = new HashMap<>();

    @Before
    public void test(){
        Student xx = new Student(1, "xx",1,new Date());
        list.add(xx);
        Student cc = new Student(2, "cc",2,new Date());
        list.add(cc);
        map.put("xx",xx);
        map.put("cc",cc);
    }


    @Test
    public void test1(){
        Student xiaowang = new Student(1, "xiaowang",2,new Date());
        String s = JSON.toJSONString(xiaowang);
        System.out.println(s);
        System.out.println(JSON.parseObject(s,Student.class));
    }

    @Test
    public void test2(){
        String x = JSON.toJSONString(list);
        System.out.println(x);
        List<Student> students = JSON.parseArray(x, Student.class);
        System.out.println(students);
    }

    @Test
    public void testMap(){
        String x = JSON.toJSONString(map);
        System.out.println(x);
        Map<String, Student> stringStudentMap = JSON.parseObject(x, new TypeReference<Map<String, Student>>() {
        });
        System.out.println(stringStudentMap);
    }



}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Student {
    @JSONField(name = "xuehao")
    private Integer id;
    private String name;

    @JSONField(name="AGE", serialize=false,ordinal = 3)
    private int age;

    @JSONField(name="DATE OF BIRTH", format="dd日MM月yyyy", ordinal = 3)
    private Date birth;
}

/**
 * public @interface JSONField {
 *     // 配置序列化和反序列化的顺序，1.1.42版本之后才支持
 *     int ordinal() default 0;
 *
 *      // 指定字段的名称
 *     String name() default "";
 *
 *     // 指定字段的格式，对日期格式有用
 *     String format() default "";
 *
 *     // 是否序列化
 *     boolean serialize() default true;
 *
 *     // 是否反序列化
 *     boolean deserialize() default true;
 * }
 */

package com.bean;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class FastJsonDemos {

    ArrayList<Student> list = new ArrayList<>();
    Map<String,Student> map = new HashMap<>();

    @Before
    public void test(){
        Student xx = new Student(1, "xx",1,new Date());
        list.add(xx);
        Student cc = new Student(2, "cc",2,new Date());
        list.add(cc);
        map.put("xx",xx);
        map.put("cc",cc);
    }


    @Test
    public void test1(){
        Student xiaowang = new Student(1, "xiaowang",2,new Date());
        String s = JSON.toJSONString(xiaowang);
        System.out.println(s);
        System.out.println(JSON.parseObject(s,Student.class));
    }

    @Test
    public void test2(){
        String x = JSON.toJSONString(list);
        System.out.println(x);
        List<Student> students = JSON.parseArray(x, Student.class);
        System.out.println(students);
    }

    @Test
    public void testMap(){
        String x = JSON.toJSONString(map);
        System.out.println(x);
        Map<String, Student> stringStudentMap = JSON.parseObject(x, new TypeReference<Map<String, Student>>() {
        });
        System.out.println(stringStudentMap);
    }



}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Student {
    @JSONField(name = "xuehao")
    private Integer id;
    private String name;

    @JSONField(name="AGE", serialize=false,ordinal = 3)
    private int age;

    @JSONField(name="DATE OF BIRTH", format="dd日MM月yyyy", ordinal = 3)
    private Date birth;
}

/**
 * public @interface JSONField {
 *     // 配置序列化和反序列化的顺序，1.1.42版本之后才支持
 *     int ordinal() default 0;
 *
 *      // 指定字段的名称
 *     String name() default "";
 *
 *     // 指定字段的格式，对日期格式有用
 *     String format() default "";
 *
 *     // 是否序列化
 *     boolean serialize() default true;
 *
 *     // 是否反序列化
 *     boolean deserialize() default true;
 * }
 */



~~~

