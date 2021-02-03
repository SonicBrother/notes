## Spring配置文件整合

xml配置文件：

```markdown
1. 参数指定的是Class信息，也就是指向配置类：
    @Import(value = BookConfiguration.class)
2. 引入其他xml配置文件：
    <import resource="beans-another.xml"/>
    @ImportResource("beans-another.xml"/)

```

key-value键值对文件(properties文件)：（待再次确认spring5）

```markdown
1. <context:property-placehoder
2. @PropertySource 
3. <bean id="" class="PropertySourcePlaceholderConfigure"/>
4. @Bean  (class="PropertySourcePlaceholderConfigure"/)  
```

---

### key-value键值对：

##### `application.properties` or `application.yml`

> 在java代码中引入
>
> @Component
> @ConfigurationProperties(prefix = "person")

~~~css
/**
 * 将配置文件中配置的每一个属性的值，映射到这个组件中
 * @ConfigurationProperties：告诉SpringBoot将本类中的所有属性和配置文件中相关的配置进行绑定；
 *      prefix = "person"：配置文件中哪个下面的所有属性进行一一映射
 *
 * 只有这个组件是容器中的组件，才能容器提供的@ConfigurationProperties功能；
 *  @ConfigurationProperties(prefix = "person")默认从全局配置文件中获取值；
 *
 */
@Component
@ConfigurationProperties(prefix = "person")
~~~

##### `application.properties` or `application.yml`

> 在java代码中引入
>
> @Value

```xml
<bean class="Person">
	<property name="lastName" value="字面量/${key}从环境变量、配置文件中获取值/#{SpEL}"></property>
<bean/>
```

@Value获取值和@ConfigurationProperties获取值比较

|                                   | @ConfigurationProperties | @Value     |
| --------------------------------- | ------------------------ | ---------- |
| 功能                              | 批量注入配置文件中的属性 | 一个个指定 |
| 松散绑定（松散语法-自动驼峰命名） | 支持                     | 不支持     |
| SpEL                              | 不支持                   | 支持       |
| JSR303数据校验                    | 支持                     | 不支持     |
| 复杂类型封装                      | 支持                     | 不支持     |

##### `person.properties`

> 在xml文件中引入
>
> ```xml
> <context:property-placeholder location="classpath:person.properties"/>
> ```

> 在java文件中引入
>
> ```java
> @PropertySource(value = {"classpath:person.properties"})
> @Component
> @ConfigurationProperties(prefix = "person")
> ```

> 在java文件中引入
>
> ```java
> @PropertySource(value = {"classpath:person.properties"})
> @Value
> ```

### xml配置文件：

~~~
@ImportResource(locations = {"classpath:beans.xml"})
导入Spring的配置文件让其生效
标注在一个配置类上
~~~

