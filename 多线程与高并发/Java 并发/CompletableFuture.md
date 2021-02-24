## CompletableFuture

~~~java
package com.juc.mulThread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletalbeFutureDemo {
    public static void main(String[] args) {
        // 开启异步任务 supplyAsync 供给型函数
//        supplyAsyc();
        // 连接两个任务 thenCompose 功能型函数
//        compose();
        // 同时开启两个任务 thenCombine：异步任务，bifunction
//        combine();
        // 一个任务以后开启另外一个任务：thenApply / thenApplyAsync
//        thenApply();

        // 哪个任务先执行完有结果就使用哪个 applyToEither ：异步任务，function
        // 异常处理 exceptionally：异步任务，function
        // 异常处理 exceptionally可以在最后面，也可以在中间
        applyToEither();
    }

    private static void applyToEither() {
        System.out.println("小白在等公交车");
        CompletableFuture<String> bus = CompletableFuture.supplyAsync(()->{
            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("700路公交车=="+Thread.currentThread().getId());
            if (true) {
                throw new RuntimeException("坏了");
            }
            return  "700路到了。。。";
        }).exceptionally(e->{
            System.out.println("出租车"+e.getMessage());
            return "出租车";
        }).applyToEither(CompletableFuture.supplyAsync(()->{
            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("800路公交车=="+Thread.currentThread().getId());
            return  "800路到了。。。";
        }),first->first);
        System.out.println(bus.join()+"，小白走了");
        /*CompletableFuture<String> bus = CompletableFuture.supplyAsync(()->{
            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("700路公交车=="+Thread.currentThread().getId());
            return  "700路到了。。。";
        }).applyToEither(CompletableFuture.supplyAsync(()->{
                    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                    System.out.println("800路公交车=="+Thread.currentThread().getId());

                    return  "800路到了。。。";
                }),first->{
            if (first.startsWith("700")) {
                throw new RuntimeException("坏了");
            }
                return first;
        }).exceptionally(e->{
            System.out.println("出租车"+e.getMessage());
            return "出租车";
        });
        System.out.println(bus.join()+"，小白走了");*/
    }

    private static void thenApply() {
        System.out.println("小白要求开发票");
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(()->{
            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("服务员收款 500 元=="+Thread.currentThread().getId());

            return  "500";
        }).thenApplyAsync(monney->{
            try{ TimeUnit.SECONDS.sleep(1); }catch (Exception e){ e.printStackTrace(); }
            System.out.println("票务员开了发票=="+monney + Thread.currentThread().getId());
            return monney + "发票";
        });

        System.out.println(completableFuture.join()+"，小白拿到发票");
    }

    private static void combine() {
        System.out.println("小白进入餐厅");
        System.out.println("小白点了 番茄炒蛋 和 米饭");
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(()->{
            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("厨师炒菜");

            return  "番茄炒蛋 ";
        }).thenCombine(CompletableFuture.supplyAsync(()->{
            try{ TimeUnit.SECONDS.sleep(1); }catch (Exception e){ e.printStackTrace(); }
            System.out.println("服务员蒸饭");

            return  "米饭";
        }),(dish,rice)->{
            return dish+rice;
        });
        System.out.println("小白在打王者");
        System.out.println(completableFuture.join()+"，小白开吃");
    }

    private static void compose() {
        System.out.println("小白进入餐厅");
        System.out.println("小白点了 番茄炒蛋 和 米饭");
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(()->{
            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("厨师炒菜");

            return  "番茄炒蛋 ";
        }).thenCompose(dish->CompletableFuture.supplyAsync(()->{
            try{ TimeUnit.SECONDS.sleep(1); }catch (Exception e){ e.printStackTrace(); }
            System.out.println("服务员打饭");

            return dish + "米饭";
        }));
        System.out.println("小白在打王者");
        System.out.println(completableFuture.join()+"，小白开吃");
    }

    private static void supplyAsyc() {
        System.out.println("小白进入餐厅");
        System.out.println("小白点了 番茄炒蛋 和 米饭");

        // supplyAsync 开启异步任务 供给型函数
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(()->{
            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("厨师炒菜");
            try{ TimeUnit.SECONDS.sleep(1); }catch (Exception e){ e.printStackTrace(); }
            System.out.println("厨师打饭");
            return  "番茄炒蛋 + 米饭  ok";
        });
        System.out.println("小白在打王者");
        // join() 相当于FutureTask中的get()方法
        System.out.println(completableFuture.join()+"，小白开吃");
    }
}
~~~

