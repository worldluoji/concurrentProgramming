package com.luoji.concurrent.countdownlatch;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
** 交替打印foo bar
**/
public class CyclicBarrierDemo2 {
    public static void main(String[] args) {
        FooBar fb = new FooBar(3);
        ExecutorService es = Executors.newFixedThreadPool(2);
        es.submit(() -> {
            try {
                fb.foo(
                    () -> {
                        System.out.println("foo");
                    }
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        });  

        es.submit(() -> {
            try {
                fb.bar(
                    () -> {
                        System.out.println("bar");
                    }
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
        es.shutdown();
    }
}

class FooBar {
    private int n;

    private final CyclicBarrier cf = new CyclicBarrier(2);
    private final CyclicBarrier cb = new CyclicBarrier(2);

    public FooBar(int n) {
        this.n = n;
    }

    public void foo(Runnable printFoo) throws InterruptedException,BrokenBarrierException {
        
        for (int i = 0; i < n; i++) {
        	printFoo.run();
            cf.await();
            cb.await();
        }
    }

    public void bar(Runnable printBar) throws InterruptedException,BrokenBarrierException {
        
        for (int i = 0; i < n; i++) {            
            cf.await();
        	printBar.run();
            cb.await();
        }
    }
}

/*
cf.await()保证了foo一定在bar之前打印，因为调用两次await时计数才会减到0，这时候才会执行69行bar的打印
cb.await()保证了打印一组foobar后才会进行第二轮
*/
