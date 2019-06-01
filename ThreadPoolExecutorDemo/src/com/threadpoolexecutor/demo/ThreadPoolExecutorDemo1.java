package com.threadpoolexecutor.demo;

import java.util.concurrent.*;

/**
 * 创建线程是一个重量级操作，需要调用系统的重量级API,然后操作系统为线程分配一系列的资源
 * 所以应该避免频繁的创建和销毁线程
 * 目前不再建议使用Executors,　原因是Executors提供的很多方法默认都使用的是无界的LinkedBlockingQueue,高负载
 * 时会导致OOM, OOM会导致所有请求都无法处理，是致命问题
 * */
public class ThreadPoolExecutorDemo1 {
	public static void main(String[] args) {
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(3);
		for (int i = 0;i < 10;i++) {
			final int temp = i;
			newFixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println(Thread.currentThread().getId() + ",i：" + temp);
				}
			});
		}
	}
}
