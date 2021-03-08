package com.luoji.concurrent.countdownlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
** 场景：现在有三个任务T1,T2,T3
** T1和T2步调要一致，T1,T2都完成后T3才能执行
**/
public class CountDownLatchDemo {

    // 模拟耗时任务1
    private String doTask1() {
        for (int i = 0; i < 100000; i++) {
            
        }
        return "task1";
    }

     // 模拟耗时任务2
     private String doTask2() {
        for (int i = 0; i < 100000; i++) {
            
        }
        return "task2";
    }

    // 任务1和2完成后执行任务3
    private void doTask3() {
        System.out.println("doing task3");
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatchDemo d = new CountDownLatchDemo();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Executor e = Executors.newFixedThreadPool(2);
        e.execute(() -> {
            System.out.println(d.doTask1());
            // 计数减一
            countDownLatch.countDown();
        });
        e.execute(() -> {
            System.out.println(d.doTask2());
            countDownLatch.countDown();
        });
        // 当计数减为0时，继续往后执行
        countDownLatch.await();
        d.doTask3();
    }
}