package com.lock.demo;
import java.util.*;
import java.util.concurrent.locks.*;

/**
 * 用两个条件变量快速实现一个阻塞队列, 一般用一个while死循环做阻塞操作，
 * 利用Contion去wait和signal线程
 * */
class MyBlockedQueue<T> {
	final Lock lock = new ReentrantLock();
	final Condition notFull = lock.newCondition();
	final Condition notEmpty = lock.newCondition();
	List<T> queue;
	int queueMaxSize;
	
	public MyBlockedQueue(int size) {
		queue = new LinkedList<>();
		queueMaxSize = size;
	}
	
	public boolean isFull() {
		return queue.size() == queueMaxSize;
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	/**
	 * 入队列
	 * @throws InterruptedException 
	 * */
	public void enq(T value) throws InterruptedException {
		lock.lock();
		try {
			while(isFull()) {
				notFull.await();
			}
			// 到这里说明队列已经没满，你可入队了
			queue.add(value);
			// 入队后，通知可出队
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 出队列
	 * @throws InterruptedException 
	 * */
	public T deq() throws InterruptedException {
		lock.lock();
		try {
			while(isEmpty()) {
				notEmpty.await();
			}
			// 到这里不为空了
			T value = queue.get(0);
			queue.remove(0);
			//　出队列后，通知可入队
			notFull.signal();
			return value;
		} finally {
			lock.unlock();
		}
	} 
}

public class LockAndConditionDemo {

	public static void main(String[] args) throws InterruptedException {
		MyBlockedQueue<Integer> myBlockedQueue = new MyBlockedQueue<>(3);
		Thread t1 = new Thread(()-> {
			for (int i = 0; i < 5; i++) {
				try {
					myBlockedQueue.enq(i);
					System.out.println("入队列的元素为" + i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		Thread t2 = new Thread(()->{
			for (int i = 0; i < 3; i++) {
				try {
					int value = myBlockedQueue.deq();
					System.out.println("出队列的元素为" + value);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

}
