# 消息队列 /RabbitMq/消息总线

## 1.消息队列

**消息**即是**信息的载体**。为了让消息发送者和消息接收者都能够明白消息所承载的信息（消息发送者需要知道如何构造消息；消息接收者需要知道如何解析消息），它们就需要按照一种**统一的格式**描述消息，这种统一的格式称之为**消息协议**。所以，有效的消息一定具有某一种格式；而没有格式的消息是没有意义的。

而消息从发送者到接收者的方式也有两种。一种我们可以称为**即时消息通讯**，也就是说消息从一端发出后（消息发送者）立即就可以达到另一端（消息接收者），这种方式的具体实现就是我们已经介绍过的**RPC**（当然单纯的http通讯也满足这个定义）；另一种方式称为**延迟消息通讯**，即消息从某一端发出后，首先进入一个容器进行临时存储，当达到某种条件后，再由这个容器发送给另一端。 这个容器的一种具体实现就是**消息队列**。  

## 2.应用场景

`异步处理`，`应用解耦`，`流量削锋`和`日志处理`四个场景。

### 2.1异步处理

场景说明：用户注册后，需要发注册邮件和注册短信。传统的做法有两种1.串行的方式；2.并行方式。

（1）**串行方式**：将注册信息写入数据库成功后，发送注册邮件，再发送注册短信。以上三个任务全部完成后，返回给客户端。

（2）**并行方式**：将注册信息写入数据库成功后，发送注册邮件的同时，发送注册短信。以上三个任务完成后，返回给客户端。与串行的差别是，并行的方式可以提高处理的时间。

假设三个业务节点每个使用50毫秒钟，不考虑网络等其他开销，则串行方式的时间是150毫秒，并行的时间可能是100毫秒。

因为CPU在单位时间内处理的请求数是一定的，假设CPU1秒内吞吐量是100次。则串行方式1秒内CPU可处理的请求量是7次（1000/150）。并行方式处理的请求量是10次（1000/100）。

小结：如以上案例描述，传统的方式系统的性能（并发量，吞吐量，响应时间）会有瓶颈。如何解决这个问题呢？

引入消息队列，将不是必须的业务逻辑，异步处理。改造后的架构如下：

按照以上约定，用户的响应时间相当于是注册信息写入数据库的时间，也就是50毫秒。注册邮件，发送短信写入消息队列后，直接返回，因此写入消息队列的速度很快，基本可以忽略，因此用户的响应时间可能是50毫秒。因此架构改变后，系统的吞吐量提高到每秒20 QPS。比串行提高了3倍，比并行提高了两倍。

### 2.2应用解耦

场景说明：用户下单后，订单系统需要通知库存系统。传统的做法是，订单系统调用库存系统的接口。

传统模式的缺点：

1）  假如库存系统无法访问，则订单减库存将失败，从而导致订单失败；

2）  订单系统与库存系统耦合；

如何解决以上问题呢？引入应用消息队列后的方案

- 订单系统：用户下单后，订单系统完成持久化处理，将消息写入消息队列，返回用户订单下单成功。
- 库存系统：订阅下单的消息，采用拉/推的方式，获取下单信息，库存系统根据下单信息，进行库存操作。
- 假如：在下单时库存系统不能正常使用。也不影响正常下单，因为下单后，订单系统写入消息队列就不再关心其他的后续操作了。实现订单系统与库存系统的应用解耦。

### 2.3流量削锋

流量削锋也是消息队列中的常用场景，一般在秒杀或团抢活动中使用广泛。

应用场景：秒杀活动，一般会因为流量过大，导致流量暴增，应用挂掉。为解决这个问题，一般需要在应用前端加入消息队列。

1. 可以控制活动的人数；
2. 可以缓解短时间内高流量压垮应用；

1. 用户的请求，服务器接收后，首先写入消息队列。假如消息队列长度超过最大数量，则直接抛弃用户请求或跳转到错误页面；
2. 秒杀业务根据消息队列中的请求信息，再做后续处理。

### 2.4日志处理

日志处理是指将消息队列用在日志处理中，比如Kafka的应用，解决大量日志传输的问题。

- 日志采集客户端，负责日志数据采集，定时写受写入Kafka队列；
- Kafka消息队列，负责日志数据的接收，存储和转发；
- 日志处理应用：订阅并消费kafka队列中的日志数据；

(1)**Kafka**：接收用户日志的消息队列。

(2)**Logstash**：做日志解析，统一成JSON输出给Elasticsearch。

(3)**Elasticsearch**：实时日志分析服务的核心技术，一个schemaless，实时的数据存储服务，通过index组织数据，兼具强大的搜索和统计功能。

(4)**Kibana**：基于**Elasticsearch**的数据可视化组件，超强的数据可视化能力是众多公司选择ELK stack的重要原因。

# Rabbit MQ

### 1.安装RabbitMq

RabbitMQ由Erlang语言开发，Erlang语言用于并发及分布式系统的开发，在电信领域应用广泛，OTP（Open 

Telecom Platform）作为Erlang语言的一部分，包含了很多基于Erlang开发的中间件及工具库，安装RabbitMQ需 

要安装Erlang/OTP，并保持版本匹配，如下图： 

RabbitMQ的下载地址：http://www.rabbitmq.com/download.html 

![1](images/1.png)

**使用Erlang/OTP 20.3版本和RabbitMQ3.7.3版本。**

#### 1） 下载erlang

地址如下：

<http://erlang.org/download/otp_win64_20.3.exe>

或去老师提供的软件包中找到 otp_win64_20.3.exe，以管理员方式运行此文件，安装。

1). erlang安装完成需要配置erlang环境变量： ERLANG_HOME=D:\Program Files\erl9.3 在path中添加%ERLANG_HOME%\bin;

#### 2) 安装RabbitMQ

<https://github.com/rabbitmq/rabbitmq-server/releases/tag/v3.7.3>

或去老师提供的软件包中找到 rabbitmq-server-3.7.3.exe，以管理员方式运行此文件，安装。

 **安装成功后会自动创建RabbitMQ服务并且启动。**

 

#### 1） 从开始菜单启动RabbitMQ

完成在开始菜单找到RabbitMQ的菜单

![image-20210307162826940](images/image-20210307162826940.png)

RabbitMQ Service-install :安装服务

RabbitMQ Service-remove 删除服务

RabbitMQ Service-start 启动

RabbitMQ Service-stop 启动

### 2. 如果没有开始菜单则进入安装目录下sbin目录手动启动

![image-20210307162856623](images/image-20210307162856623.png)

##### 1） 安装并运行服务

rabbitmq-service.bat install 安装服务 rabbitmq-service.bat stop 停止服务 rabbitmq-service.bat start 启动服务

##### 2） 安装管理插件

安装rabbitMQ的管理插件，方便在浏览器端管理RabbitMQ

管理员身份运行(shell) rabbitmq-plugins.bat enable rabbitmq_management

### 3、启动成功 登录RabbitMQ

进入浏览器，输入：http://localhost:15672

![image-20210307162909148](images/image-20210307162909148.png)

初始账号和密码：guest/guest

![image-20210307162919415](images/image-20210307162919415.png)

### 4 .**注意事项**

1、安装erlang和rabbitMQ以管理员身份运行。

2、当卸载重新安装时会出现RabbitMQ服务注册失败，此时需要进入注册表清理erlang

搜索RabbitMQ、ErlSrv，将对应的项全部删除。

# 1 RabbitMQ介绍

## 1.1 应用场景

MQ全称为Message Queue，即消息队列， RabbitMQ是由erlang语言开发，基于AMQP（Advanced Message Queue 高级消息队列协议）协议实现的消息队列，它是一种应用程序之间的通信方法，消息队列在分布式系统开发中应用非常广泛。RabbitMQ官方地址：<http://www.rabbitmq.com/>

开发中消息队列通常有如下应用场景：

1、任务异步处理。

将不需要同步处理的并且耗时长的操作由消息队列通知消息接收方进行异步处理。提高了应用程序的响应时间。

2、应用程序解耦合

MQ相当于一个中介，生产方通过MQ与消费方交互，它将应用程序进行解耦合。

 

市场上还有哪些消息队列？

ActiveMQ，RabbitMQ，ZeroMQ，Kafka，MetaMQ，RocketMQ、Redis。

 

为什么使用RabbitMQ呢？

1、使得简单，功能强大。

2、基于AMQP协议。

3、社区活跃，文档完善。

4、高并发性能好，这主要得益于Erlang语言。

5、Spring Boot默认已集成RabbitMQ

## 1.2 其它相关术语

AMQP是什么 ？

![1523259611885](images/1523259611885.png)

总结：AMQP是一套公开的消息队列协议，最早在2003年被提出，它旨在从协议层定义消息通信数据的标准格式，为的就是解决MQ市场上协议不统一的问题。RabbitMQ就是遵循AMQP标准协议开发的MQ服务。

官方：<http://www.amqp.org/>

 

JMS是什么 ？

![1523259867745](images/1523259867745.png)

总结：

JMS是java提供的一套消息服务API标准，其目的是为所有的java应用程序提供统一的消息通信的标准，类似java的jdbc，只要遵循jms标准的应用程序之间都可以进行消息通信。它和AMQP有什么 不同，jms是java语言专属的消息服务标准，它是在api层定义标准，并且只能用于java应用；而AMQP是在协议层定义的标准，是跨语言的 。

# 2 RabbitMQ工作原理

## 2.1 RabbitMQ工作原理

下图是RabbitMQ的基本结构：

![image-20210306121430782](images/image-20210306121430782.png)

 

组成部分说明如下：

- Broker：消息队列服务进程，此进程包括两个部分：Exchange和Queue。
- Exchange：消息队列交换机，按一定的规则将消息路由转发到某个队列，对消息进行过虑。
- Queue：消息队列，存储消息的队列，消息到达队列并转发给指定的消费方。
- Producer：消息生产者，即生产方客户端，生产方客户端将消息发送到MQ。
- Consumer：消息消费者，即消费方客户端，接收MQ转发的消息。

消息发布接收流程：

-----发送消息-----

1、生产者和Broker建立TCP连接。

2、生产者和Broker建立通道。

3、生产者通过通道消息发送给Broker，由Exchange将消息进行转发。

4、Exchange将消息转发到指定的Queue（队列） 

 

----接收消息-----

1、消费者和Broker建立TCP连接

2、消费者和Broker建立通道

3、消费者监听指定的Queue（队列） 

4、当有消息到达Queue时Broker默认将消息推送给消费者。

5、消费者接收到消息。

##  2.2  RabbitMQ下载安装

### 2.2.1 下载安装

​	RabbitMQ由Erlang语言开发，Erlang语言用于并发及分布式系统的开发，在电信领域应用广泛，OTP（Open Telecom Platform）作为Erlang语言的一部分，包含了很多基于Erlang开发的中间件及工具库，安装RabbitMQ需要安装Erlang/OTP，并保持版本匹配，如下图：

RabbitMQ的下载地址：<http://www.rabbitmq.com/download.html>

 

本项目使用Erlang/OTP 20.3版本和RabbitMQ3.7.3版本。

 ![1](images/1.png)

1）下载erlang

地址如下：

<http://erlang.org/download/otp_win64_20.3.exe>

或去老师提供的软件包中找到 otp_win64_20.3.exe，以管理员方式运行此文件，安装。

 

erlang安装完成需要配置erlang环境变量： 	ERLANG_HOME=D:\Program Files\erl9.3 	在path中添加%ERLANG_HOME%\bin;

 

2）安装RabbitMQ

<https://github.com/rabbitmq/rabbitmq-server/releases/tag/v3.7.3>

或去老师提供的软件包中找到 rabbitmq-server-3.7.3.exe，以管理员方式运行此文件，安装。

 

### 2.2.2启动

安装成功后会自动创建RabbitMQ服务并且启动。

 

1）从开始菜单启动RabbitMQ

完成在开始菜单找到RabbitMQ的菜单：

![image-20210306121923244](images/image-20210306121923244.png)

RabbitMQ Service-install :安装服务

RabbitMQ Service-remove 删除服务

RabbitMQ Service-start 启动

RabbitMQ Service-stop 启动

 

2）如果没有开始菜单则进入安装目录下sbin目录手动启动：

![image-20210306121936445](images/image-20210306121936445.png)

 

1）安装并运行服务

   rabbitmq-service.bat install  安装服务    rabbitmq-service.bat stop 停止服务    rabbitmq-service.bat start 启动服务

2）安装管理插件

安装rabbitMQ的管理插件，方便在浏览器端管理RabbitMQ

管理员身份运行 `rabbitmq-plugins.bat enable rabbitmq_management`

 

 

3、启动成功 登录RabbitMQ

进入浏览器，输入：<http://localhost:15672>

![image-20210306122023467](images/image-20210306122023467.png)

初始账号和密码：guest/guest

![image-20210306122040985](images/image-20210306122040985.png)

 

### 2.2.3 注意事项

1、安装erlang和rabbitMQ以管理员身份运行。

2、当卸载重新安装时会出现RabbitMQ服务注册失败，此时需要进入注册表清理erlang

搜索RabbitMQ、ErlSrv，将对应的项全部删除。

# 3 RabbitMQ工作模式

RabbitMQ有以下几种工作模式 ：

1、Work queues

2、Publish/Subscribe

3、Routing

4、Topics

5、Header

6、RPC

## 3.1 Work queues

![image-20210307162955834](images/image-20210307162955834.png)

work queues与入门程序相比，多了一个消费端，两个消费端共同消费同一个队列中的消息。

应用场景：对于 任务过重或任务较多情况使用工作队列可以提高任务处理的速度。

## 3.2 Publish/subscribe

 

### 3.2.1发布订阅模式介绍

![image-20210307163007395](images/image-20210307163007395.png)

发布订阅模式：

1、每个消费者监听自己的队列。

2、生产者将消息发给broker，由交换机将消息转发到绑定此交换机的每个队列，每个绑定交换机的队列都将接收到消息

## 3.3 Routing路由模式

### 3.3.1 路由模式介绍

![image-20210307163021320](images/image-20210307163021320.png)

路由模式：

1、每个消费者监听自己的队列，并且设置routingkey。

2、生产者将消息发给交换机，由交换机根据routingkey来转发消息到指定的队列。

## 3.4 Topics

### 3.4.1统配符模式介绍

![image-20210307163035981](images/image-20210307163035981.png)

路由模式：

1、每个消费者监听自己的队列，并且设置带统配符的routingkey。

2、生产者将消息发给broker，由交换机根据routingkey来转发消息到指定的队列。

 3.5 其它模式

### 3.5.1 Header模式

​	header模式与routing不同的地方在于，header模式取消routingkey，使用header中的 key/value（键值对）匹配队列。

案例：

根据用户的通知设置去通知用户，设置接收Email的用户只接收Email，设置接收sms的用户只接收sms，设置两种通知类型都接收的则两种通知都有效。

### 3.5.2 RPC

![image-20210307163046014](images/image-20210307163046014.png)

 

RPC即客户端远程调用服务端的方法 ，使用MQ可以实现RPC的异步调用，基于Direct交换机实现，流程如下：

1、客户端即是生产者就是消费者，向RPC请求队列发送RPC调用消息，同时监听RPC响应队列。

2、服务端监听RPC请求队列的消息，收到消息后执行服务端的方法，得到方法返回的结果

3、服务端将RPC方法 的结果发送到RPC响应队列

4、客户端（RPC调用方）监听RPC响应队列，接收到RPC调用结果。

# 4 Spring整合RibbitMQ

## 4.1  Direct模式

### 4.1.1 生产者

Direct是RabbitMQ默认的交换机模式,也是最简单的模式.即创建消息队列的时候,指定一个BindingKey.当发送者发送消息的时候,指定对应的Key.当Key和消息队列的BindingKey一致的时候,消息将会被发送到该消息队列中.

#### 4.1.1.1 添加依赖

我们选择基于Spring-Rabbit去操作RabbitMQ

<https://github.com/spring-projects/spring-amqp>

使用spring-boot-starter-amqp会自动添加spring-rabbit依赖，如下：

```
  <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.3.RELEASE</version>
        <relativePath/>
    </parent>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
<!-- rabbitmq 依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </dependency>
    </dependencies>

```



#### 4.1.1.2配置application.yml

配置连接rabbitmq的参数

```
server:
  port: 8010
spring:
  application:
    name: test-rabbitmq-producer
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    passowrd: guest
    virtualHost: /
```

#### 4.1.1.3配置 RabbitMqConfig（生产者）

```
@Configuration
public class RabbitMqConfig {

   @Bean(name="message")
    public Queue queue(){
            return new Queue("direct_queue");
        }
 //定义交换机
    @Bean
    public DirectExchange directExchange(){
    return new DirectExchange(“directExchange”);
    }
//将队列绑定到交换机
	@Bean
	Binding bindingExchangeMessages(@Qualifier("message") Queue queueMessages, DirectExchange exchange) {
	return BindingBuilder.bind(queueMessages).to(exchange).with("direct");
}

}

```

#### 4.1.1.4 使用RarbbitTemplate发送消息

```

@RunWith(SpringRunner.class)
@SpringBootTest
public class SendTest {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void testSend(){
       rabbitTemplate.convertAndSend("directExchange","direct","direct模式"); 
    	}
	}

}

```

### 4.1.2消费者

#### 4.1.2.1 消费者代码

创建消费端工程，添加依赖：

```
  <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.3.RELEASE</version>
        <relativePath/>
    </parent>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </dependency>
    </dependencies>

```

使用@RabbitListener注解监听队列。

```
@Component
public class ReceiveMessage {

    @RabbitListener(queues="queue")    //监听器监听指定的Queue
    public void process(String str) {
        System.out.println("Receive:"+str);
    }
}
```

## 4.2 Topic模式 

###  4.2.1生产者

#### 4.2.1.1 更改RabbitMqConfig

```
@Configuration
public class SenderConf {

        @Bean(name="message")
        public Queue queueMessage() {
            return new Queue("topic.message");
        }

        @Bean(name="messages")
        public Queue queueMessages() {
            return new Queue("topic.messages");
        }

        @Bean
        public TopicExchange exchange() {
            return new TopicExchange("exchange");
        }

        @Bean
        Binding bindingExchangeMessage(@Qualifier("message") Queue queueMessage, TopicExchange exchange) {
            return BindingBuilder.bind(queueMessage).to(exchange).with("topic.message");
        }

        @Bean
        Binding bindingExchangeMessages(@Qualifier("messages") Queue queueMessages, TopicExchange exchange) {
            return BindingBuilder.bind(queueMessages).to(exchange).with("topic.#");//*表示一个词,#表示零个或多个词
        }
}
```

#### 4.2.1.2 发送消息

```
@RunWith(SpringRunner.class)
@SpringBootTest
public class SendTest {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void testSend(){
        rabbitTemplate.convertAndSend("qf-exchange","topic.message","hello,rabbit~~~11");
        rabbitTemplate.convertAndSend("qf-exchange","topic.messages","hello,rabbit~~~22");
    }
}

```



### 4.2.2 Topic模式 -- 消费者

#### 4.2.2.1 消费者监听

```
@RabbitListener(queues="topic.message")    //监听器监听指定的Queue
    public void process1(String str) {    
        System.out.println("message:"+str);
    }
    @RabbitListener(queues="topic.messages")    //监听器监听指定的Queue
    public void process2(String str) {
        System.out.println("messages:"+str);
    }
```



## 4.3 Fanout Exchange模式 

### 4.3.1生产者

#### 4.3.1.1 更改RabbitMqConfig

Fanout Exchange形式又叫广播形式,因此我们发送到路由器的消息会使得绑定到该路由器的每一个Queue接收到消息,这个时候就算指定了Key,或者规则(即上文中convertAndSend方法的参数2),也会被忽略!发送端配置如下:

```
@Configuration
public class SenderConf {

        @Bean(name="Amessage")
        public Queue AMessage() {
            return new Queue("fanout.A");
        }

        @Bean(name="Bmessage")
        public Queue BMessage() {
            return new Queue("fanout.B");
        }

        @Bean(name="Cmessage")
        public Queue CMessage() {
            return new Queue("fanout.C");
        }

        @Bean
        FanoutExchange fanoutExchange() {
            return new FanoutExchange("fanoutExchange");//配置广播路由器
        }

        @Bean
        Binding bindingExchangeA(@Qualifier("Amessage") Queue AMessage,FanoutExchange fanoutExchange) {
            return BindingBuilder.bind(AMessage).to(fanoutExchange);
        }

        @Bean
        Binding bindingExchangeB(@Qualifier("Bmessage") Queue BMessage, FanoutExchange fanoutExchange) {
            return BindingBuilder.bind(BMessage).to(fanoutExchange);
        }

        @Bean
        Binding bindingExchangeC(@Qualifier("Cmessage") Queue CMessage, FanoutExchange fanoutExchange) {
            return BindingBuilder.bind(CMessage).to(fanoutExchange);
        }

}
```

#### 4.3.1.2 发送消息

```
emplate.convertAndSend("fanoutExchange","","xixi,haha");//参数2忽略
```

### 4.3.2  消费者

#### 4.3.2.1 消费者监听

```
@Component
public class HelloReceive {
    @RabbitListener(queues="fanout.A")
    public void processA(String str1) {
        System.out.println("ReceiveA:"+str1);
    }
    @RabbitListener(queues="fanout.B")
    public void processB(String str) {
        System.out.println("ReceiveB:"+str);
    }
    @RabbitListener(queues="fanout.C")
    public void processC(String str) {
        System.out.println("ReceiveC:"+str);
    }

}
```



# 5.消息Ack确认机制

## 5.1确认种类

RabbitMQ的消息确认有两种。

- **消息发送确认**：这种是用来确认生产者将消息发送给交换器，交换器传递给队列的过程中，消息是否成功投递。发送确认分为两步，一是确认是否到达交换器，二是确认是否到达队列。
- **消费接收确认**:这种是确认消费者是否成功消费了队列中的消息。

### 5.1.1 消息发送确认

#### 5.1.1.1**ConfirmCallback**

通过实现ConfirmCallBack接口，消息发送到交换器**Exchange**后触发回调。

ConfirmCallBackHandler.java

```
package com.lay.rabbitmqtwo.config;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @Description:通过实现ConfirmCallBack接口，消息发送到交换器Exchange后触发回调。
 */

public class ConfirmCallBackHandler implements RabbitTemplate.ConfirmCallback {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("消息唯一标识："+correlationData);
        System.out.println("确认结果："+ack);
        System.out.println("失败原因："+cause);
    }
}

```

##### 5.1.1.1.1在RabbitConfig中配置RabbitTempalte

```
 //初始化加载方法，对RabbitTemplate进行配置
    @PostConstruct
    void rabbitTemplate(){
        //消息发送确认，发送到交换器Exchange后触发回调
        rabbitTemplate.setConfirmCallback(new ConfirmCallBackHandler());
    }
```

##### 5.1.1.3 该功能需要开启确认，spring-boot中配置如下：

```properties
#消息发送交换机确认
spring.rabbitmq.publisher-confirms = true
```

#### 5.1.1.2**ReturnCallback**

通过实现ReturnCallback接口，如果消息从交换器发送到对应队列失败时触发（比如根据发送消息时指定的routingKey找不到队列时会触发）

ReturnCallBackHandler.java

```
package com.lay.rabbitmqtwo.config;

/**
 * @Description:通过实现ReturnCallback接口
 * 如果消息从交换器发送到对应队列失败时触发（比如根据发送消息时指定的routingKey找不到队列时会触发）
 */

public class ReturnCallBackHandler implements RabbitTemplate.ReturnCallback {
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("消息主体 message："+message);
        System.out.println("应答码 replyCode: ："+replyCode);
        System.out.println("原因描述 replyText："+replyText);
        System.out.println("交换机 exchange："+exchange);
        System.out.println("消息使用的路由键 routingKey："+routingKey);
    }
}
```

##### 5.1.2.3 在RabbitConfig中配置RabbitTempalte

```
  //初始化加载方法，对RabbitTemplate进行配置
    @PostConstruct
    void rabbitTemplate(){
        //消息发送确认，发送到交换器Exchange后触发回调
        rabbitTemplate.setConfirmCallback(new ConfirmCallBackHandler());
        //消息发送确认，如果消息从交换器发送到对应队列失败时触发（比如根据发送消息时指定的routingKey找不到队列时会触发）
        rabbitTemplate.setReturnCallback(new ReturnCallBackHandler());
     
    }
```

##### 5.1.2.4使用该功能需要开启确认，spring-boot中配置如下：

```properties
#消息发送队列回调
spring.rabbitmq.publisher-returns = true
```

### 5.1.2 消息接收确认

#### （1）确认模式

消息消费者如何通知 Rabbit 消息消费成功？

消息通过 ACK 确认是否被正确接收，每个 Message 都要被确认（acknowledged），可以手动去 ACK 或自动 ACK

1. 自动确认会在消息发送给消费者后立即确认，但存在丢失消息的可能，如果消费端消费逻辑抛出异常，也就是消费端没有处理成功这条消息，那么就相当于丢失了消息
2. 如果消息已经被处理，但后续代码抛出异常，使用 Spring 进行管理的话消费端业务逻辑会进行回滚，这也同样造成了实际意义的消息丢失
3. 如果手动确认则当消费者调用 ack、nack、reject 几种方法进行确认，手动确认可以在业务失败后进行一些操作，如果消息未被 ACK 则会发送到下一个消费者
4. 如果某个服务忘记 ACK 了，则 RabbitMQ 不会再发送数据给它，因为 RabbitMQ 认为该服务的处理能力有限
5. ACK 机制还可以起到限流作用，比如在接收到某条消息时休眠几秒钟



消息确认模式有：
AcknowledgeMode.NONE：自动确认
AcknowledgeMode.AUTO：根据情况确认
AcknowledgeMode.MANUAL：手动确认
确认消息（局部方法处理消息）
默认情况下消息消费者是自动 ack （确认）消息的，如果要手动 ack（确认）则需要修改确认模式为 manual



#### 确认消息

spring-boot中配置方法：

```properties
spring.rabbitmq.listener.simple.acknowledge-mode = manual
```

```
@Component
@RabbitListener(queues = "1905-queue")
public class ReceiveMessage {

    @RabbitHandler
    public void process(String hello, Channel channel, Message message) throws IOException {
        System.out.println("HelloReceiver收到  : " + hello +"收到时间"+new Date());
        try {
            //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            System.out.println("receiver success");
        } catch (IOException e) {
            e.printStackTrace();
            //丢弃这条消息
            //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
            System.out.println("receiver fail");
        }

    }
}

```

需要注意的 basicAck 方法需要传递两个参数 

-  **deliveryTag（唯一标识 ID）**：当一个消费者向 RabbitMQ 注册后，会建立起一个 Channel ，RabbitMQ 会用 basic.deliver 方法向消费者推送消息，这个方法携带了一个 delivery tag， **它代表了 RabbitMQ 向该 Channel 投递的这条消息的唯一标识 ID**，是一个单调递增的正整数，delivery tag 的范围仅限于 Channel
-  **multiple**：为了减少网络流量，手动确认可以被批处理，**当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息**

#### 手动否认、拒绝消息

- 发送一个 header 中包含 error 属性的消息

  ![9434708-0309431dc508a0b2](images/9434708-0309431dc508a0b2.png)

##### 发送者：

```
//在MessagePostProcessor中给message的头做自定义处理（这个功能类似于spring的BeanPostProcessor）对消息的扩展处理
    private final static MessagePostProcessor messagePostProcessor = message -> {
        message.getMessageProperties().setContentType("text/plain");
        message.getMessageProperties().setContentEncoding("UTF-8");
        message.getMessageProperties().setHeader("error","这是一条错误信息");
        return message;
    };

//然后在发送消息给消费者时候带上自己的自定义的消息头： 
@Test
    public void testSend(){

            rabbitTemplate.convertAndSend("1905-topic","niubi","发送给不牛逼的人",messagePostProcessor);

    }
```



##### 消费者：

获取消息时检查到头部包含 error 则 nack 消息

```
@RabbitHandler
public void processMessage2(String message, Channel channel,@Headers Map<String,Object> map) {
    System.out.println(message);
    if (map.get("error")!= null){
        System.out.println("错误的消息");
        try {
            channel.basicNack((Long)map.get(AmqpHeaders.DELIVERY_TAG),false,true);      //否认消息
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    try {
        channel.basicAck((Long)map.get(AmqpHeaders.DELIVERY_TAG),false);            //确认消息
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

此时控制台重复打印，说明该消息被 nack 后一直重新入队列然后一直重新消费

```
hello
错误的消息
hello
错误的消息
hello
错误的消息
hello
错误的消息
```

也可以拒绝该消息，消息会被丢弃，不会重回队列

```
channel.basicReject((Long)map.get(AmqpHeaders.DELIVERY_TAG),false);        //拒绝消息
```

# 6.消息延迟队列

## 6.1什么是延迟队列

延迟队列存储的对象肯定是对应的延时消息，所谓”延时消息”是指当消息被发送以后，并不想让消费者立即拿到消息，而是等待指定时间后，消费者才拿到这个消息进行消费。

> 在很多的业务场景中，延时队列可以实现很多功能，此类业务中，一般上是非实时的，需要延迟处理的，需要进行重试补偿的。

1. 订单超时关闭：在支付场景中，一般上订单在创建后30分钟或1小时内未支付的，会自动取消订单。
2. 短信或者邮件通知：在一些注册或者下单业务时，需要在1分钟或者特定时间后进行短信或者邮件发送相关资料的。本身此类业务于主业务是无关联性的，一般上的做法是进行异步发送。
3. 重试场景：比如消息通知，在第一次通知出现异常时，会在隔几分钟之后进行再次重试发送。

## 6.2 使用要求

RabbitMQ 本身没有直接支持延迟队列的功能，但是可以通过过期时间TTL和死信队列来模拟延迟队列。

### 6.2.1过期时间TTL

`RabbitMQ`中可以对队列和消息分别设置TTL，TTL表明了一条消息可在队列中存活的最大时间。当某条消息被设置了TTL或者当某条消息进入了设置了TTL的队列时，这条消息会在TTL时间后**死亡成为Dead Letter**。如果既配置了消息的TTL，又配置了队列的TTL，那么较小的那个值会被取用。

### 6.2.2 死信队列

上个知识点也提到了，设置了`TTL`的消息或队列最终会成为`Dead Letter`，当消息在一个队列中变成死信之后，它能被重新发送到另一个交换机中，这个交换机就是DLX，绑定此DLX的队列就是死信队列。。



### 6.2.3 具体实现

![image-20210307153903399](images/image-20210307153903399.png)

**延迟任务通过消息的TTL和Dead Letter Exchange来实现。我们需要建立2个队列，一个用于发送消息，一个用于消息过期后的转发目标队列。**

#### 6.2.3.1 rabbitConfig配置文件

```
@Configuration
public class RabbitmqConfig {


    /**
     *
     * 创建实际消费队列绑定的交换机
     */
    @Bean
    public DirectExchange  demoExchange(){


        return new DirectExchange("demoExchange",true,false);
    }

    /**
     *
     * 创建延迟队列（死信队列）绑定的交换机
     */
    @Bean
    public DirectExchange  demoTtlExchange(){

        return new DirectExchange("demoTtlExchange",true,false);
    }


    /**
     *
     * 创建实际消费队列
     */
    @Bean
    public Queue demoQueue(){

        return new Queue("demoQueue",true);
    }


    /**
     *
     * 创建延迟队列（死信队列）
     */
    @Bean
    public Queue demoTtlQueue(){
        Map<String, Object> arguments=new HashMap<>();
        arguments.put("x-dead-letter-exchange", "demoExchange");
        arguments.put("x-dead-letter-routing-key", "demoRoutes");
        return new Queue("demoTtlQueue",true,false, false, arguments);
    }


    /**
     * 绑定实际消费队列到交换机
     */

    @Bean
    public Binding demoBinding(){

        return new Binding("demoQueue", Binding.DestinationType.QUEUE,"demoExchange","demoRoutes",null);
    }

    /**
     * 绑定延迟队列（死信队列）到交换机
     */
    @Bean
    public Binding demoTtlBinding(){


        return new Binding("demoTtlQueue", Binding.DestinationType.QUEUE,"demoTtlExchange","demoTtlRoutes",null);
    }


}
```

#### 6.2.3.2 生产者

```

    public void sendMessage(String message){

        rabbitTemplate.convertAndSend("demoTtlExchange", "demoTtlRoutes", message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
            	//设置60s过期，过期转发到指定路由
                message.getMessageProperties().setExpiration("10000");

                return message;
            }
        });

```

#### 6.2.3.3 消费者

```
@Configuration
@RabbitListener(queues = "demoQueue")
public class ReceiverMessage {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitHandler
    private void  receiverMessage(String message){

        System.out.println("message = "+message);
    }

}
```

#### 6.2.3.4 创建测试类，发送消息

```
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqdemoApplicationTests {

    @Autowired
    private SendMessage sendMessage;

    @Test
    public void contextLoads() {
        for(int i=1;i<=20;i++){

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendMessage.sendMessage("测试消息"+i);

        }


    }

```

#### 6.2.3.6 生产者控制台打印

![producerfirst](images/producerfirst.png)

#### 6.2.3.7 消费者控制台打印

![consumer](images/consumer.png)

### 6.2.4 总结

我们发现，发送的五条消息，都是经过一分钟后，我们的消费者，才会被接收到。说明我们的延迟队列已经生效了，如果下昂要该

# 4. Spring Cloud Bus

Spring cloud bus通过轻量消息代理连接各个分布的节点。这会用在广播状态的变化（例如配置变化）或者其他的消息指令。Spring bus的一个核心思想是通过分布式的启动器对spring boot应用进行扩展，也可以用来建立一个多个应用之间的通信频道。目前唯一实现的方式是用(高级消息队列协议)AMQP消息代理作为通道，同样特性的设置（有些取决于通道的设置）在更多通道的文档中。

Spring cloud bus被国内很多都翻译为消息总线，也挺形象的。大家可以将它理解为管理和传播所有分布式项目中的消息既可，其实本质是利用了MQ的广播机制在分布式的系统中传播消息，目前常用的有Kafka和RabbitMQ。利用bus的机制可以做很多的事情，其中`配置中心客户端刷新`就是典型的应用场景之一，我们用一张图来描述bus在配置中心使用的机制。

![image-20210307162338999](images/image-20210307162338999.png)

根据此图我们可以看出利用Spring Cloud Bus做配置更新的步骤:

- 1、提交代码触发post给客户端A发送/actuator/bus-refresh
- 2、客户端A接收到请求从Server端更新配置并且发送给Spring Cloud Bus
- 3、Spring Cloud bus接到消息并通知给其它客户端
- 4、其它客户端接收到通知，请求Server端获取最新配置
- 5、全部客户端均获取到最新的配置

## 4.1 客户端spring-cloud-config-client改造

### 4.1.1 添加依赖

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

需要多引入`spring-cloud-starter-bus-amqp`包，增加对消息总线的支持

### 4.1.2 配置文件

```
## 开启消息跟踪
management.endpoints.web.exposure.include= bus-refresh
spring.cloud.bus.trace.enabled=true

spring.rabbitmq.host=127.0.0.1
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

配置文件需要增加RebbitMq的相关配置，这样客户端代码就改造完成了。

### 4.1.3 测试

依次启动spring-cloud-eureka、spring-cloud-config-server、spring-cloud-config-client项目，在启动spring-cloud-config-client项目的时候我们会发现启动日志会输出这样的一条记录。

```
2019-07-01 15:56:52.581  INFO 20464 --- [           main] s.b.a.e.w.s.WebMvcEndpointHandlerMapping : Mapped "{[/actuator/refresh],methods=[POST],produces=[application/vnd.spring-boot.actuator.v2+json || application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.web.servlet.AbstractWebMvcEndpointHandlerMapping$OperationHandler.handle(javax.servlet.http.HttpServletRequest,java.util.Map<java.lang.String, java.lang.String>)

```

说明客户端已经具备了消息总线通知的能力了，为了更好的模拟消息总线的效果，我们更改客户端spring-cloud-config-client项目的端口为8011、8012依次启动，这样测试环境就准备好了。启动后eureka后台效果图如下：

![image-20210307162616106](images/image-20210307162616106.png)

我们先分别测试一下服务端和客户端是否正确运行，访问：`http://localhost:8001/neo-config/dev`，返回信息：

```
{"name":"neo-config","profiles":["dev"],"label":null,"version":"6eecd82c8cbbab7d1fc167a5b4e543cdede44cd1","state":null,"propertySources":[{"name":"https://github.com/miaohangbo/config-repo//neo-config-dev.properties","source":{"neo.hello":"hello im dev11123232"}}]}
```

说明server端都正常读取到了配置信息。

依次访问：`http://localhost:8007/hello`、`http://localhost:8011/hello`、`http://localhost:8012/hello`，返回：`hello im dev`。说明客户端都已经读取到了server端的内容。

现在我们更新`neo-config-dev.properties` 中`neo.hello`的值为`hello im dev update`并提交到代码库中，访问：`http://localhost:8002/hello` 依然返回`hello im dev`。我们对端口为8007的客户端发送一个`/actuator/bus-refresh`的post请求。在win下使用下面命令来模拟webhook.

```
curl -X POST http://localhost:8007/actuator/bus-refresh
```

执行完成后，依次访问：`http://localhost:8007/hello`、`http://localhost:8011/hello`、`http://localhost:8012/hello`，返回：`hello im dev update`。说明三个客户端均已经拿到了最新配置文件的信息，这样我们就实现了图一中的示例。

## 4.2 改进版本

在上面的流程中，我们已经到达了利用消息总线触发一个客户端`/actuator/bus/refresh`,而刷新所有客户端的配置的目的。但这种方式并不优雅。原因如下：

- 打破了微服务的职责单一性。微服务本身是业务模块，它本不应该承担配置刷新的职责。
- 破坏了微服务各节点的对等性。
- 有一定的局限性。例如，微服务在迁移时，它的网络地址常常会发生变化，此时如果想要做到自动刷新，那就不得不修改WebHook的配置。

因此我们将上面的架构模式稍微改变一下

![configbus2](images/configbus2.jpg)

这时Spring Cloud Bus做配置更新步骤如下:

- 1、提交代码触发post请求给/actuator/bus-refresh
- 2、server端接收到请求并发送给Spring Cloud Bus
- 3、Spring Cloud bus接到消息并通知给其它客户端
- 4、其它客户端接收到通知，请求Server端获取最新配置
- 5、全部客户端均获取到最新的配置

这样的话我们在server端的代码做一些改动，来支持`/actuator/bus-refresh`

### 4.2.1 添加依赖

```
 <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-bus-amqp</artifactId>
    </dependency>
```

需要多引入`spring-cloud-starter-bus-amqp`包，增加对消息总线的支持

### 4.2.2 修改配置文件

```
server:
  port: 8009
spring:
  application:
    name: spring-cloud-config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/miaohangbo/config-repo/ # 配置git仓库的地址
          #search-paths:  # git仓库地址下的相对地址，可以配置多个，用,分割。
          username: miaohangbo  # git仓库的账号
          password: aini59852369  # git仓库的密码
  rabbitmq:
        host: 127.0.0.1
        port: 5672
        username: guest
        password: guest
eureka:
  client:
    serviceUrl:
      defaultZone: http://admin:123456@localhost:8000/eureka/
management:
  endpoints:
    web:
      exposure:
        include: bus-refresh
```

配置文件增加RebbitMq的相关配置，开启刷新验证。这样server端代码就改造完成了。

### 4.2.3 测试

### 测试

依次启动spring-cloud-eureka、spring-cloud-config-server、spring-cloud-config-client项目，改动spring-cloud-config-client项目端口为8011、8012依次启动。测试环境准备完成。

按照上面的测试方式，访问server端和三个客户端测试均可以正确返回信息。同样修改`neo-config-dev.properties` 中`neo.hello`的值为`hello im dev update`并提交到代码库中。在win下使用下面命令来模拟webhook触发server端`/actuator/bus-refresh`.

执行完成后，依次访问：`http://localhost:8007/hello`、`http://localhost:8011/hello`、`http://localhost:8012/hello`，返回：`hello im dev update`。说明三个客户端均已经拿到了最新配置文件的信息，这样我们就实现了上图中的示例。