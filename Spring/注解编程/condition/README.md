# Spring @Conditional注解 详细讲解及示例

参考地址： [阿云_](https://blog.csdn.net/xcy1193068639)

## 前言：

@Conditional是Spring4新提供的注解，它的作用是按照一定的条件进行判断，满足条件给容器注册bean。

@Conditional的定义：

```java
//此注解可以标注在类和方法上
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME) 
@Documented
public @interface Conditional {
    Class<? extends Condition>[] value();
}
```

从代码中可以看到，需要传入一个Class数组，并且需要继承Condition接口：

```java
public interface Condition {
    boolean matches(ConditionContext var1, AnnotatedTypeMetadata var2);
}
```

Condition是个接口，需要实现matches方法，返回true则注入bean，false则不注入。

## 示例：

首先，创建Person类：

```java
public class Person {
    private String name;
    private Integer age;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
    @Override
    public String toString() {
        return "Person{" + "name='" + name + '\'' + ", age=" + age + '}';
    }
}
```

创建BeanConfig类，用于配置两个Person实例并注入，一个是比尔盖茨，一个是林纳斯。

```java
@Configuration
public class BeanConfig {
    @Bean(name = "bill")
    public Person person1(){
        return new Person("Bill Gates",62);
    }
    @Bean("linus")
    public Person person2(){
        return new Person("Linus",48);
    }
}
```

接着写一个测试类进行验证这两个Bean是否注入成功。

```java
public class ConditionalTest {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfig.class);
    @Test
    public void test1(){
        Map<String, Person> map = applicationContext.getBeansOfType(Person.class);
        System.out.println(map);
    }
}
```

 运行，输出结果是这样的，两个Person实例被注入进容器。

![img](images/20180807235413826)

这是一个简单的例子，现在问题来了，如果我想**根据当前操作系统来注入Person实例**，windows下注入bill，linux下注入linus，怎么实现呢？

这就需要我们用到@Conditional注解了，前言中提到，需要实现Condition接口，并重写方法来自定义match规则。

首先，创建一个WindowsCondition类：

```java
public class WindowsCondition implements Condition {
    /**
     * @param conditionContext:判断条件能使用的上下文环境
     * @param annotatedTypeMetadata:注解所在位置的注释信息
     * */
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        //获取ioc使用的beanFactory
        ConfigurableListableBeanFactory beanFactory = conditionContext.getBeanFactory();
        //获取类加载器
        ClassLoader classLoader = conditionContext.getClassLoader();
        //获取当前环境信息
        Environment environment = conditionContext.getEnvironment();
        //获取bean定义的注册类
        BeanDefinitionRegistry registry = conditionContext.getRegistry();
        //获得当前系统名
        String property = environment.getProperty("os.name");
        //包含Windows则说明是windows系统，返回true
        if (property.contains("Windows")){
            return true;
        }
        return false;
    }
}
```

matches方法的两个参数的意思在注释中讲述了，值得一提的是，conditionContext提供了多种方法，方便获取各种信息，也是SpringBoot中 @ConditonalOnXX注解多样扩展的基础。

接着，创建LinuxCondition类：

```java
public class LinuxCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        String property = environment.getProperty("os.name");
        if (property.contains("Linux")){
            return true;
        }
        return false;
    }
}
```

接着就是使用这两个类了，因为此注解可以标注在方法上和类上，所以分开测试：

### 标注在方法上：

修改BeanConfig：

```java
@Configuration
public class BeanConfig {
    //只有一个类时，大括号可以省略
    //如果WindowsCondition的实现方法返回true，则注入这个bean    
    @Conditional({WindowsCondition.class})
    @Bean(name = "bill")
    public Person person1(){
        return new Person("Bill Gates",62);
    }
    //如果LinuxCondition的实现方法返回true，则注入这个bean
    @Conditional({LinuxCondition.class})
    @Bean("linus")
    public Person person2(){
        return new Person("Linus",48);
    }
}
```

修改测试方法，使其可以打印当前系统名：

```java
    @Test
    public void test1(){
        String osName = applicationContext.getEnvironment().getProperty("os.name");
        System.out.println("当前系统为：" + osName);
        Map<String, Person> map = applicationContext.getBeansOfType(Person.class);
        System.out.println(map);
    }
```

 运行结果如下：

![img](images/20180808002229965)

我是运行在windows上的所以只注入了bill，嗯，没毛病。

接着实验linux下的情况，不能运行在linux下，但可以修改运行时参数：

![img](images/20180808002359363)

![img](images/2018080800243265)

修改后启动测试方法：

![img](images/20180808002507557)

 一个方法只能注入一个bean实例，所以@Conditional标注在方法上只能控制一个bean实例是否注入。

### 标注在类上：

一个类中可以注入很多实例，@Conditional标注在类上就决定了一批bean是否注入。

我们试一下，将BeanConfig改写，这时，如果WindowsCondition返回true，则两个Person实例将被注入(注意：上一个测试将os.name改为linux，这是我将把这个参数去掉)：

```java
@Conditional({WindowsCondition.class})
@Configuration
public class BeanConfig {
    @Bean(name = "bill")
    public Person person1(){
        return new Person("Bill Gates",62);
    }
    @Bean("linus")
    public Person person2(){
        return new Person("Linus",48);
    }
}
```

结果两个实例都被注入： 

![img](images/20180808003233238)

如果将类上的WindowsCondition.class改为LinuxCondition.class，结果应该可以猜到：

![img](images/20180808003530136)

结果就是空的，类中所有bean都没有注入。

## 多个条件类：

前言中说，@Conditional注解传入的是一个Class数组，存在多种条件类的情况。

这种情况貌似判断难度加深了，测试一波，新增新的条件类，实现的matches返回false（这种写死返回false的方法纯属测试用，没有实际意义O(∩_∩)O）

```java
public class ObstinateCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
         return false;
    }
}
```

 BeanConfig修改一下：

```java
@Conditional({WindowsCondition.class,ObstinateCondition.class})
@Configuration
public class BeanConfig {
    @Bean(name = "bill")
    public Person person1(){
        return new Person("Bill Gates",62);
    }
    @Bean("linus")
    public Person person2(){
        return new Person("Linus",48);
    }
}
```

结果：

![img](images/20180808005345487)

现在如果将ObstinateCondition的matches方法返回值改成true，两个bean就被注入进容器：

![img](images/20180808005551555)

结论得：

~~~markdown
第一个条件类实现的方法返回true，第二个返回false，则结果false，不注入进容器。

第一个条件类实现的方法返回true，第二个返回true，则结果true，注入进容器中。
~~~



