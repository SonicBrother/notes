package com.deadlock;

public class MyDeadLockDemo {
    public static Object lockA = new Object();
    public static Object lockB = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (lockA) {
                System.out.println(Thread.currentThread().getName() + "\t持有" + lockA + ",等待" +lockB);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lockB) {
                    System.out.println("线程A完成");
                }
            }
        }, "A").start();

        new Thread(() -> {
            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + "\t持有" + lockB + ",等待" +lockA);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lockA) {
                    System.out.println("线程B完成");
                }
            }
        }, "B").start();
    }
}

