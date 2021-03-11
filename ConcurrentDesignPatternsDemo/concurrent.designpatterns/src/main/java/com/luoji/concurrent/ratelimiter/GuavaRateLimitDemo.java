package com.luoji.concurrent.ratelimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.RateLimiter;

/*
* Guava 采用的是令牌桶算法，其核心是要想通过限流器，必须拿到令牌。
* 也就是说，只要我们能够限制发放令牌的速率，那么就能控制流速了:
* 令牌以固定的速率添加到令牌桶中，假设限流的速率是 r/ 秒，则令牌每 1/r 秒会添加一个；
* 假设令牌桶的容量是 b ，如果令牌桶已满，则新的令牌会被丢弃；
* 请求能够通过限流器的前提是令牌桶中有令牌。
*
* Guava限流器其关键是记录并动态计算下一令牌发放的时间。而不是使用定时器。
* 在高并发场景下，当系统压力已经临近极限的时候，定时器的精度误差会非常大，同时定时器本身会创建调度线程，也会对系统的性能产生影响。
*/
public class GuavaRateLimitDemo {
    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(2);
        ExecutorService es = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 20; i++) {
            long pre = System.nanoTime();
            rateLimiter.acquire();
            final int k = i;
            es.execute(() -> {
                long cur = System.nanoTime();
                System.out.println(k + ", " + (cur - pre) / 1000000);
            });
        }
        es.shutdown();
    }    
}

/*
When a lambda expression uses an assigned local variable from its enclosing space there is an important restriction.
A lambda expression may only use local variable whose value doesn’t change. 
That restriction is referred as “variable capture” which is described as; 
lambda expression capture values, not variables. 
The local variables that a lambda expression may use are known as “effectively final“.
An effectively final variable is one whose value does not change after it is first assigned. 
There is no need to explicitly declare such a variable as final, although doing so would not be an error. 
Since there is no need to explicitly declare such a variable as final thus the name effectively final. 
If there is an attempt to change such a variable, anyway compiler will throw an error.
*/