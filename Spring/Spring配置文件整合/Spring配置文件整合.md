## Spring配置文件整合

xml配置文件：

```markdown
1. 参数指定的是Class信息，也就是指向配置类：
    @Import(value = BookConfiguration.class)
2. 引入其他xml配置文件：
    <import resource="beans-another.xml"/>
    @ImportResource（"beans-another.xml"/）

```

key-value键值对文件：

```markdown
1. <context:property-placehoder
2. @PropertySource 
3. <bean id="" class="PropertySourcePlaceholderConfigure"/>
4. @Bean  (class="PropertySourcePlaceholderConfigure"/)  
```