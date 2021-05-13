package com.luoji.concurrent.countdownlatch;

import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 ** 现在有三个任务T1,T2,T3, 都执行N次 T1和T2步调要一致（T1执行x次，T2也执行x次），
 ** 并且能够通知到T3（T1和T2都执行完1次，则通知T3） 但T3当前执行的可以是第1次，但是T1,T2已经开始执行第2次
 ** 相当于T1，T2都执行完成后，扔到一个“消息队列里”，T3自己去取 现成的方案就是使用 CyclicBarrier 创建 CyclicBarrier
 * 的时候，我们还传入了一个回调函数，当计数器减到 0 的时候，会调用这个回调函数。 CyclicBarrier 的计数器有自动重置的功能，当减到 0
 * 的时候，会自动重置你设置的初始值。
 **/
public class CyclicBarrierDemo {
    private Vector<Integer> v1 = new Vector<>();
    private Vector<Integer> v2 = new Vector<>();
    private final CyclicBarrier b = new CyclicBarrier(2, () -> {
        this.check();
    });

    public static void main(String[] args) throws InterruptedException {
        CyclicBarrierDemo c = new CyclicBarrierDemo();
        c.batchProcess();
    }

    private void check() {
        int t1 = v1.remove(0);
        int t2 = v2.remove(0);
        // 执行操作，这里仅模拟打印
        System.out.println(t1 - t2);
    }

    private void batchProcess() {
        Executor e = Executors.newFixedThreadPool(2);
        
        // 使用了CyclicBarrier，实际一个循环里就会进行一次check()操作，而不必等到全部弄完后再check
        e.execute(() -> {
            for (int i = 0; i < 10000; i++) {
                v1.add(10000 - i);
                try {
                    b.await();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (BrokenBarrierException e1) {
                    e1.printStackTrace();
                }
            }
        });

        e.execute(() -> {
            for (int i = 0; i < 10000; i++) {
                v1.add(i);
                try {
                    b.await();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (BrokenBarrierException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
       
}

/**
CountDownLatch 和 CyclicBarrier 是 Java 并发包提供的两个非常易用的线程同步工具类，
这两个工具类用法的区别：CountDownLatch 主要用来解决一个线程等待多个线程的场景，可以类比旅游团团长要等待所有的游客到齐才能去下一个景点；
而 CyclicBarrier 是一组线程之间互相等待，更像是几个驴友之间不离不弃。
除此之外 CountDownLatch 的计数器是不能循环利用的，也就是说一旦计数器减到 0，再有线程调用 await()，该线程会直接通过。
但 CyclicBarrier 的计数器是可以循环利用的，而且具备自动重置的功能，一旦计数器减到 0 会自动重置到你设置的初始值。
除此之外，CyclicBarrier 还可以设置回调函数，可以说是功能丰富。

调用await方法的线程告诉CyclicBarrier自己已经到达同步点，然后当前线程被阻塞。
直到parties个参与线程调用了await方法，CyclicBarrier同样提供带超时时间的await和不带超时时间的await方法
**/