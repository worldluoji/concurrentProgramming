package com.threadpoolexecutor.demo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/*
 * corePoolSize: 线程池持有的最小线程数，有些项目很闲，但也不能把人都拆了，至少留corePoolSize的人留守。
 * maximumPoolSize:线程池最大线程数，表示在线程池中最多能创建多少个线程。如果当线程池中的数量到达这个数字时，新来的任务会抛出异常。
 * keepAliveTime：表示线程没有任务执行时最多能保持多少时间会停止，然后线程池的数目维持在corePoolSize。
 * unit:参数keepAliveTime的时间单位
 * workQueue:一个阻塞队列，用来存储等待执行的任务，如果当前对线程的需求超过了corePoolSize大小，才会放在这里。,一般使用有界队列
 * threadFactory:线程工厂，主要用来创建线程，比如指定线程的名字。
 * handler:如果线程池已满，新的任务处理方式。
 * Java1.6还引入了allowCoreThreadTimeOut(boolean value)方法，让所有线程都支持超时。这意味着如果项目很闲，就会将项目组的成员撤走。
 * */
public class ThreadPoolExecutorDemo2 {

	public static void main(String[] args) {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4));
		for (int i = 1; i <= 6; i++) {
			final int temp = i;
			executor.execute(()-> System.out.println(Thread.currentThread().getId() + ",i：" + temp));
		}
		executor.shutdown();
	}

}
