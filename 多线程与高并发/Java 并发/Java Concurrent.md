# Java Concurrent

~~~
如果程序采用多线程技术编写的，那么运行在单核单线程的机器上，就会并发执行，运行在多核多线程的机器上，就会并行执行

用来描述线程的对象Thread
~~~



线程状态：新建，运行中，终止

1. 重写Thread的run方法

2. 实现runnable接口，创建Thread时候传进去

3. FutureTask场景 用的不多

   ~~~java
   package com.juc.mulThread;
   
   import java.util.concurrent.Callable;
   import java.util.concurrent.ExecutionException;
   import java.util.concurrent.FutureTask;
   
   public class futureTask {
       public static void main(String[] args) {
           // 3.FutureTask 需要一个Callable接口实现类对象
           // 函数式接口
           Callable<String> callable = ()->{
               System.out.println("子任务");
               return "sub thread";
           };
   
           // 2.FutureTask 间接实现了 Runnable 接口
           FutureTask<String> futureTask = new FutureTask<>(callable);
   
           // 1.创建线程时，传入一个Runnable 对象
           Thread thread = new Thread(futureTask);
           thread.start();
           System.out.println("子线程启动");
   
           try {
               // 调用get()方法后，主线程会不断的询问子线程
               String s = futureTask.get();
               System.out.println("子线程的结果是："+s);
           } catch (InterruptedException e) {
               System.out.println(e.getCause());
           } catch (ExecutionException e) {
               System.out.println(e.getCause());
           }
       }
   }
   
   ~~~

   
