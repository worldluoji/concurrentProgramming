package com.luoji.concurrent.semaphore;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/*
init()：设置计数器的初始值。
down()：计数器的值减 1；如果此时计数器的值小于 0，则当前线程将被阻塞，否则当前线程可以继续执行。
up()：计数器的值加 1；如果此时计数器的值小于或者等于 0，则唤醒等待队列中的一个线程，并将其从等待队列中移除。
在 Java SDK 并发包里，down() 和 up() 对应的则是 acquire() 和 release()。
*/
public class SemaphoreDemo {
    
    private static final CountDownLatch c = new CountDownLatch(6);

    static int count;
    //初始化信号量
    static final Semaphore s 
        = new Semaphore(1);
    
    //用信号量保证互斥, 可以达到和synchronized同样的效果
    static void addOne() throws InterruptedException {
        s.acquire();
        try {
            count+=1;
        } finally {
            s.release();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(6);
        for (int k = 0; k < 6; k ++) {
            es.submit(() -> {
                for (int i = 0; i < 10000; i++) {
                    try {
                        addOne();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                c.countDown();
            });
        }
        c.await(5, TimeUnit.SECONDS);
        System.out.println(count);
    }
}

/*
和管程相比，信号量可以实现的独特功能就是同时允许多个线程进入临界区，
但是信号量不能做的就是同时唤醒多个线程去争抢锁，只能唤醒一个阻塞中的线程，
而且信号量模型是没有Condition的概念的，即阻塞线程被醒了直接就运行了而不会去检查此时临界条件是否已经不满足了，
基于此考虑信号量模型才会设计出只能让一个线程被唤醒，否则就会出现因为缺少Condition检查而带来的线程安全问题。
正因为缺失了Condition，所以用信号量来实现阻塞队列就很麻烦，因为要自己实现类似Condition的逻辑。
*/