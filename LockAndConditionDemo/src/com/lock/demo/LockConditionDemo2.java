package com.lock.demo;

import java.util.concurrent.locks.*;
import java.util.concurrent.*;

/*
* 交替打印010203040506070809...
*/
public class LockConditionDemo2 {
    public static void main(String[] args) {
        ZeroEvenOdd z = new ZeroEvenOdd(10);
        ExecutorService es = Executors.newFixedThreadPool(3);
        es.submit(() -> {
            try {
                for (int i = 0;i < z.n; i++) {
                    z.odd();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        es.submit(() -> {
            try {
                for (int i = 0;i < z.n; i++) {
                    z.zero();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        es.submit(() -> {
            try {
                for (int i = 0;i < z.n; i++) {
                    z.even();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        es.shutdown();
    }
}

class ZeroEvenOdd {
    public int n;
    private int status;
    private int i;

    private final Lock lock = new ReentrantLock();
	private final Condition notZero = lock.newCondition();
	private final Condition notOdd = lock.newCondition();
    private final Condition notEven = lock.newCondition();

    public ZeroEvenOdd(int n) {
        this.n = n;
        this.status = 0;
        this.i = 1;
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero() throws InterruptedException {
        lock.lock();
        try {
            while (this.status != 0) {
                notZero.await();
            }
            System.out.println(0);
            if (this.i % 2 == 1) {
                this.status = 1;
                notOdd.signal();
            } else {
                this.status = 2;
                notEven.signal();
            }
        } finally {
            lock.unlock();
        }
        
    }

    public void even() throws InterruptedException {
        lock.lock();
        try {
            while (this.status != 2) {
                notEven.await();
            }
            System.out.println(this.i++);
            this.status = 0;
            notZero.signal();
        } finally {         
            lock.unlock();
        }
        
    }

    public void odd() throws InterruptedException {
        lock.lock();
        try {
            while (this.status != 1) {
                notOdd.await();
            }
            System.out.println(this.i++);
            this.status = 0;
            notZero.signal();
        } finally {
            lock.unlock();
        }
        
    }

}