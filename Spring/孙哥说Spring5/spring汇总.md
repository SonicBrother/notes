# Spring整体汇总

## Spring生命周期

### 一。Spring概述

工厂模式：解耦合

~~~java
创建一切想要的对象
public class BeanFactory{
  
    public static Object getBean(String key){
         Object ret = null;
         try {
             Class clazz = Class.forName(env.getProperty(key));
             ret = clazz.newInstance();
         } catch (Exception e) {
            e.printStackTrace();
         }
         return ret;
     }
}
~~~

~~~
1. 定义类型 (类)
2. 通过配置文件的配置告知工厂(applicationContext.properties)
   key = value
3. 通过工厂获得类的对象
   Object ret = BeanFactory.getBean("key")
~~~

### 二。Spring程序

ApplicationContext:

~~~
非web环境 ： ClassPathXmlApplicationContext (main junit)
web环境  ：  XmlWebApplicationContext
~~~

~~~
getBean("person")
getBean("person", Person.class)
ctx.getBean(Person.class)
getBeanDefinitionNames()
ctx.getBeanNamesForType(Person.class)
containsBeanDefinition("a")->name标签定义的别名不能查找
containsBean("person")->name标签定义的别名也可以查找
~~~

### 四。注入：解耦合

注入：通过Spring的配置文件，为成员变量赋值

化简注入方式：

~~~
JDK类型注入 
<property name="name" value="suns"/>
注意：value属性 只能简化 8种基本类型+String 注入标签

用户自定义类型
<property name="userDAO" ref="userDAO"/>
~~~

~~~
JDK类型注入 
<bean id="person" class="xxx.Person" p:name="suns"/>
注意：value属性 只能简化 8种基本类型+String 注入标签

用户自定义类型
<bean id="userService" class="xxx.UserServiceImpl" p:userDAO-ref="userDAO"/>
~~~

### 八。工厂创建复杂对象

不能直接new的对象称为复杂对象

- 实现FactoryBean接口

  ~~~
  getBean("id")获取的为复杂对象，getBean("&id")为id后的Class对象
  ~~~

- 实例工厂：避免代码侵入，整合遗留系统

  ~~~xml
   <bean id="connFactory" class="com.baizhiedu.factorybean.ConnectionFactory"></bean>
  
   <bean id="conn"  factory-bean="connFactory" factory-method="getConnection"/>
  ~~~

  

- 静态工厂：避免代码侵入，整合遗留系统

  ~~~xml
  xxxxxxxxxx <bean id="conn" class="com.baizhiedu.factorybean.StaticConnectionFactory" factory-method="getConnection"/>
  ~~~

### 十。对象的生命周期

#### 生命周期三个阶段：

> 创建对象（new）-》注入属性（di）-》再调用初始化方法

​	创建阶段：单例：随工厂创建（加入lazy-init=true，则也是获取时候创建对象），多例：获取的时候创建

​	初始化阶段：InitializingBean->afterPropertiesSet/init-method=xx。初始化方法由程序员提供，由工厂进行调用。（两个执行顺序：先接口，后配置）

​	销毁阶段：DisposableBean->destory/destory-method。销毁方法只适用于singleton，多例对象由虚拟机负责

### 十一。配置文件参数化

~~~xml
<context:property-placeholder location="xx.properties"/>
~~~

### 十二。类型转换器

#### 自定义类型转换器

~~~xml
1.实现Converter<String, Date>接口
2.在配置文件中注册自定义类型转换器 <bean id="myDateConverter" 
3.注册类型转换器
<!--用于注册类型转换器-->
<bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">
  <property name="converters">
    <set>
      <ref bean="myDateConverter"/>
    </set>
  </property>
</bean>
~~~

~~~
日期格式：2020/05/01 (不支持 ：2020-05-01)
~~~

### 十三。后置处理bean

BeanPostProcesser:AOP



~~~xml
创建对象（new）-> 注入属性（DI）->postProcessBeforeInitialization(BeanPostProcesser)->afterPropertiesSet(InitializingBean) ->init-method(xml)->postProcessAfterInitialization(BeanPostProcesser)->destory(DIsposableBean)->destory-method(xml)
~~~

---

## 代理开发

代理类=原始功能+额外功能+相同接口

### 静态代理

代码冗余，维护困难

### 动态代理

~~~xml
原始对象
<bean
额外功能
<bean
切入点
<aop:config>
	<aop:pointcut id = "pc" expression="execution(* *.*(..))"
</aop:config>
组装
    <aop:advisor advice-ref="before" pointcut-ref="pc"
~~~

动态代理是使用动态字节码技术，在内存中直接生成字节码

MethodIntercepter(方法拦截器)

~~~java
// org.aopalliance.MethodInterceptor 
public class Arround implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
          System.out.println("-----额外功能 log----");
          Object ret = invocation.proceed();
          return ret;
    }
}
~~~

切入点：

~~~
*  *(..)  --> 所有方法

* ---> 修饰符 返回值
* ---> 方法名
()---> 参数表
..---> 对于参数没有要求 (参数有没有，参数有几个都行，参数是什么类型的都行)

execution
within
args
@annotation
~~~

### AOP编程

~~~
1. 原始对象
2. 额外功能 (MethodInterceptor)
3. 切入点
4. 组装切面 (额外功能+切入点)
~~~

JDK的动态代理:Proxy.newProxyInstance

~~~java
public class TestJDKProxy {

    /*
        1. 借用类加载器  TestJDKProxy
                       UserServiceImpl
        2. JDK8.x前 内部类访问外部变量需要使用final修饰

            final UserService userService = new UserServiceImpl();
     */
    public static void main(String[] args) {
        //1 创建原始对象
        UserService userService = new UserServiceImpl();

        //2 JDK创建动态代理
        /*

         */

        InvocationHandler handler = new InvocationHandler(){
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("------proxy  log --------");
                //原始方法运行
                Object ret = method.invoke(userService, args);
                return ret;
            }
        };

        UserService userServiceProxy = (UserService)Proxy.newProxyInstance(UserServiceImpl.class.getClassLoader(),userService.getClass().getInterfaces(),handler);

        userServiceProxy.login("suns", "123456");
        userServiceProxy.register(new User());
    }
}
~~~

CGlib动态代理

~~~java
package com.baizhiedu.cglib;

import com.baizhiedu.proxy.User;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class TestCglib {
    public static void main(String[] args) {
        //1 创建原始对象
        UserService userService = new UserService();

        /*
          2 通过cglib方式创建动态代理对象
            Proxy.newProxyInstance(classloader,interface,invocationhandler)

            Enhancer.setClassLoader()
            Enhancer.setSuperClass()
            Enhancer.setCallback();  ---> MethodInterceptor(cglib)
            Enhancer.create() ---> 代理
         */

        Enhancer enhancer = new Enhancer();

        enhancer.setClassLoader(TestCglib.class.getClassLoader());
        enhancer.setSuperclass(userService.getClass());


        MethodInterceptor interceptor = new MethodInterceptor() {
            //等同于 InvocationHandler --- invoke
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("---cglib log----");
                Object ret = method.invoke(userService, args);

                return ret;
            }
        };

        enhancer.setCallback(interceptor);

        UserService userServiceProxy = (UserService) enhancer.create();

        userServiceProxy.login("suns", "123345");
        userServiceProxy.register(new User());
    }
}

~~~

### 基于注解AOP编程

~~~xml
原始对象
<bean 

额外功能
@Aspect
public class MyAspect {
    @Pointcut("execution(* login(..))")
    public void myPointcut(){}

    @Around(value="myPointcut()")
    public Object arround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("----aspect log ------");
        Object ret = joinPoint.proceed();
        return ret;
    }
}
      
  // 配置文件    
 <bean id="userService" class="com.baizhiedu.aspect.UserServiceImpl"/>

    <!--
       切面
         1. 额外功能
         2. 切入点
         3. 组装切面
    -->
<bean id="arround" class="com.baizhiedu.aspect.MyAspect"/>

<!--告知Spring基于注解进行AOP编程-->
<aop:aspectj-autoproxy />
~~~

## Spring整合MVC框架的核心思路

###### 1. 准备工厂

~~~markdown
1. Web开发过程中如何创建工厂
      ApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
                                   XmlWebApplicationContext()
2. 如何保证工厂唯一同时被共用
   被共用：Web request|session|ServletContext(application)
   工厂存储在ServletContext这个作用域中 ServletContext.setAttribute("xxxx",ctx);
   
   唯一：ServletContext对象 创建的同时 ---》 ApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
       
        ServletContextListener ---> ApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
        ServletContextListener 在ServletContext对象创建的同时，被调用(只会被调用一次) ，把工厂创建的代码，写在ServletContextListener中，也会保证只调用一次，最终工厂就保证了唯一性
 3. 总结
      ServletContextListener(唯一)
             ApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationContext.xml"); // 唯一
             ServletContext.setAttribute("xxx",ctx) //(共用)
             
 4. Spring封装了一个ContextLoaderListener 
     1. 创建工厂
     2. 把工厂存在ServletContext中
~~~

~~~xml
ContextLoaderListener使用方式 

web.xml

<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>

<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext.xml</param-value>
</context-param>

~~~

## 持久层整合

Mybatis开发步骤

~~~
map配置繁琐  代码冗余 

1. 实体
2. 实体别名         配置繁琐 
3. 表
4. 创建DAO接口
5. 实现Mapper文件
6. 注册Mapper文件   配置繁琐 
7. MybatisAPI调用  代码冗余 
~~~

整合思路

~~~xml
<!--连接池-->
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
  <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
  <property name="url" value="jdbc:mysql://localhost:3306/suns?useSSL=false"></property>
  <property name="username" value="root"></property>
  <property name="password" value="123456"></property>
</bean>

<!--创建SqlSessionFactory SqlSessionFactoryBean-->
<bean id="sqlSessionFactoryBean" class="org.mybatis.spring.SqlSessionFactoryBean">
  <property name="dataSource" ref="dataSource"></property>
  <property name="typeAliasesPackage" value="com.baizhiedu.entity"></property>
  <property name="mapperLocations">
    <list>
      <value>classpath:com.baizhiedu.mapper/*Mapper.xml</value>
    </list>
  </property>
</bean>

<!--创建DAO对象 MapperScannerConfigure-->

<bean id="scanner" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
  <property name="sqlSessionFactoryBeanName" value="sqlSessionFactoryBean"></property>
  <property name="basePackage" value="com.baizhiedu.dao"></property>
</bean>
~~~

### Spring控制事务

事务传播属性

| 传播属性的值  | 外部不存在事务 | 外部存在事务               | 用法                                                    | 备注           |
| ------------- | -------------- | -------------------------- | ------------------------------------------------------- | -------------- |
| REQUIRED      | 开启新的事务   | 融合到外部事务中           | @Transactional(propagation = Propagation.REQUIRED)      | 增删改方法     |
| SUPPORTS      | 不开启事务     | 融合到外部事务中           | @Transactional(propagation = Propagation.SUPPORTS)      | 查询方法       |
| REQUIRES_NEW  | 开启新的事务   | 挂起外部事务，创建新的事务 | @Transactional(propagation = Propagation.REQUIRES_NEW)  | 日志记录方法中 |
| NOT_SUPPORTED | 不开启事务     | 挂起外部事务               | @Transactional(propagation = Propagation.NOT_SUPPORTED) | 极其不常用     |
| NEVER         | 不开启事务     | 抛出异常                   | @Transactional(propagation = Propagation.NEVER)         | 极其不常用     |
| MANDATORY     | 抛出异常       | 融合到外部事务中           | @Transactional(propagation = Propagation.MANDATORY)     | 极其不常用     |

~~~
1. 隔离属性   默认值 
2. 传播属性   Required(默认值) 增删改   Supports 查询操作
3. 只读属性   readOnly false  增删改   true 查询操作
4. 超时属性   默认值 -1
5. 异常属性   默认值 

增删改操作   @Transactional
查询操作     @Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
~~~

基于注解/标签的事务配置

~~~xml
基于注解 @Transaction的事务配置回顾
<bean id="userService" class="com.baizhiedu.service.UserServiceImpl">
  <property name="userDAO" ref="userDAO"/>
</bean>

<!--DataSourceTransactionManager-->
<bean id="dataSourceTransactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
  <property name="dataSource" ref="dataSource"/>
</bean>

@Transactional(isolation=,propagation=,...)
public class UserServiceImpl implements UserService {
    private UserDAO userDAO;

<tx:annotation-driven transaction-manager="dataSourceTransactionManager"/>
<!-- ========================================================-->
基于标签的事务配置
<bean id="userService" class="com.baizhiedu.service.UserServiceImpl">
  <property name="userDAO" ref="userDAO"/>
</bean>

<!--DataSourceTransactionManager-->
<bean id="dataSourceTransactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
  <property name="dataSource" ref="dataSource"/>
</bean>

事务属性 
<tx:advice id="txAdvice" transaction-manager="dataSourceTransactionManager">
    <tx:attributes>
          <tx:method name="register" isoloation="",propagation=""></tx:method>
          <tx:method name="login" .....></tx:method>
          <!--等效于 
          @Transactional(isolation=,propagation=,)
          public void register(){}-->
    </tx:attributes>
</tx:advice>

<aop:config>
     <aop:pointcut id="pc" expression="execution(* com.baizhiedu.service.UserServiceImpl.register(..))"></aop:pointcut>
     <aop:advisor advice-ref="txAdvice" pointcut-ref="pc"></aop:advisor>
</aop:config>
~~~

## 注解编程

~~~java
@Scope注解
@Lazy注解
@PostConstruct // 生命周期相关
@PreDestroy // 生命周期相关
~~~

配置文件相关

~~~
<context:property-placeholder location=""/>
@PropertySource
~~~

~~~
<context:component-scan base-package="com.baizhiedu"/> // 注解扫描
~~~

### Spring与yml

~~~
1. 集合处理的问题
   SpringEL表达式解决
   @Value("#{'${list}'.split(',')}")
2. 对象类型的YAML进行配置时 过于繁琐 
   @Value("${account.name}")
   
SpringBoot  @ConfigurationProperties
~~~

