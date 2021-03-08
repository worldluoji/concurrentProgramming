package com.completablefurure.demo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
** CompletionService常用来执行异步批量任务
** CompletionService 接口其余的 3 个方法，都是和阻塞队列相关的，take()、poll() 都是从阻塞队列中获取并移除一个元素；
** 它们的区别在于如果阻塞队列是空的，那么调用 take() 方法的线程会被阻塞，而 poll() 方法会返回 null 值。 
** poll(long timeout, TimeUnit unit) 方法支持以超时的方式获取并移除阻塞队列头部的一个元素，
** 如果等待了 timeout unit 时间，阻塞队列还是空的，那么该方法会返回 null 值。
**
** ExecutorCompletionService(Executor executor)；
** ExecutorCompletionService(Executor executor, BlockingQueue<Future<V>> completionQueue)。
** 这两个构造方法都需要传入一个线程池，如果不指定 completionQueue，那么默认会使用无界的 LinkedBlockingQueue。
** 任务执行结果的 Future 对象就是加入到 completionQueue 中。
** 对于简单的并行任务，你可以通过“线程池 + Future”的方案来解决；
** 如果任务之间有聚合关系，无论是 AND 聚合还是 OR 聚合，都可以通过 CompletableFuture 来解决；
** 而批量的并行任务，则可以通过 CompletionService 来解决。
*/
public class CompletableServiceDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newFixedThreadPool(3);
        BlockingQueue<Future<String>> q = new ArrayBlockingQueue<>(100);
        CompletionService<String> cs = new ExecutorCompletionService<String>(es, q);
        cs.submit(() -> {
           return "task1...";
        });
        cs.submit(() -> {
            return "task2..";
        });
        cs.submit(() -> {
            return "task3...";
        });
        for (int i = 0; i < 3; i++) {
            String r = cs.take().get();
            es.execute(() -> {
                System.out.println(r + " finished");
            });
        }
        es.shutdown();
    }
}
