## Fastjson

~~~java
package com.bean;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
        String s = JSON.toJSONString(xiaowang, SerializerFeature.PrettyFormat);
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

/**
 *
 QuoteFieldNames,//输出key时是否使用双引号,默认为true

 UseSingleQuotes,//使用单引号而不是双引号,默认为false

 WriteMapNullValue,//是否输出值为null的字段,默认为false

 WriteEnumUsingToString,//Enum输出name()或者original,默认为false

 UseISO8601DateFormat,//Date使用ISO8601格式输出，默认为false

 WriteNullListAsEmpty,//List字段如果为null,输出为[],而非null

 WriteNullStringAsEmpty,//字符类型字段如果为null,输出为"",而非null

 WriteNullNumberAsZero,//数值字段如果为null,输出为0,而非null

 WriteNullBooleanAsFalse,//Boolean字段如果为null,输出为false,而非null

 SkipTransientField,//如果是true，类中的Get方法对应的Field是transient，序列化时将会被忽略。默认为true

 SortField,//按字段名称排序后输出。默认为false

 @Deprecated
 WriteTabAsSpecial,//把\t做转义输出，默认为false

 PrettyFormat,//结果是否格式化,默认为false

 WriteClassName,//序列化时写入类型信息，默认为false。反序列化是需用到

 DisableCircularReferenceDetect,//消除对同一对象循环引用的问题，默认为false

 WriteSlashAsSpecial,//对斜杠'/'进行转义

 BrowserCompatible,//将中文都会序列化为\uXXXX格式，字节数会多一些，但是能兼容IE 6，默认为false

 WriteDateUseDateFormat,//全局修改日期格式,默认为false。JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd";JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);

 NotWriteRootClassName,//暂不知，求告知

 DisableCheckSpecialChar,//一个对象的字符串属性中如果有特殊字符如双引号，将会在转成json时带有反斜杠转移符。如果不需要转义，可以使用这个属性。默认为false
 */

~~~

