package com.completablefurure.demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureDemo1 {
	public static void sleep(int t, TimeUnit u) {
		try { 
			u.sleep(t); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		CompletableFuture<Void> f1 = CompletableFuture.runAsync(()->{ 
			System.out.println("T1 洗水壶...");
			sleep(1, TimeUnit.SECONDS);
			System.out.println("T1 烧开水...");
			sleep(10, TimeUnit.SECONDS);
	    });
		
		// supplyAsync方法有返回值，runAsync没有返回值
		CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
			System.out.println("T2 洗茶壶...");
			sleep(1, TimeUnit.SECONDS);
			System.out.println("T2 洗茶杯...");
			sleep(2, TimeUnit.SECONDS);
			System.out.println("T2 拿茶叶...");
			sleep(1, TimeUnit.SECONDS);
		    return "绿茶";
		});
		
		// f3要等任务1和任务2结束后再执行，而f1和f2之间时并行的
		CompletableFuture<String> f3 = f1.thenCombine(f2, (__, tf)->{
			System.out.println("T1 拿到茶叶...");
			System.out.println("T1 泡茶...");
			return "上茶" + tf;
		});
		
		// 等待f3的执行结果
		System.out.println(f3.join());
		
		/*
		 * supplyAsync(Supplier<U> supplier,Executor executor)还可以指定线程池，如果不指定，
		 * 使用的就是公共的ForkJoinPool，默认创建的线程数为CPU核数
		 * 建议不同的任务使用不同的线程池，否则某个任务IO操作耗时时，会造成线程饥饿
		 *  applyToEither() 方法来描述一个 OR 汇聚
		 * */
		
	}
}
