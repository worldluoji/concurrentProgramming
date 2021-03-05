package com.luoji.concurrent.stampedlock;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
* ReadWriteLock 支持两种模式：一种是读锁，一种是写锁。
* 而 StampedLock 支持三种模式，分别是：写锁、悲观读锁和乐观读。
* 其中，写锁、悲观读锁的语义和 ReadWriteLock 的写锁、读锁的语义非常类似，
* 允许多个线程同时获取悲观读锁，但是只允许一个线程获取写锁，写锁和悲观读锁是互斥的。
* 不同的是：StampedLock 里的写锁和悲观读锁加锁成功之后，都会返回一个 stamp；
* 然后解锁的时候，需要传入这个 stamp（印记、标识）。
*
* StampedLock 的性能之所以比 ReadWriteLock 还要好，其关键是 StampedLock 支持乐观读的方式。
* ReadWriteLock 支持多个线程同时读，但是当多个线程同时读的时候，所有的写操作会被阻塞；
* 而 StampedLock 提供的乐观读，是允许一个线程获取写锁的，也就是说不是所有的写操作都被阻塞。
*
* 对于读多写少的场景 StampedLock 性能很好，简单的应用场景基本上可以替代 ReadWriteLock，
* 但是 StampedLock 的功能仅仅是 ReadWriteLock 的子集，在使用的时候，还是有几个地方需要注意一下。
* StampedLock 在命名上并没有增加 Reentrant，想必你已经猜测到 StampedLock 应该是不可重入的。
* 事实上，的确是这样的，StampedLock 不支持重入。，StampedLock 的悲观读锁、写锁都不支持条件变量。
* 
* 还有一点需要特别注意，那就是：如果线程阻塞在 StampedLock 的 readLock() 或者 writeLock() 上时，
* 此时调用该阻塞线程的 interrupt() 方法，会导致 CPU 飙升。（内部实现里while循环里面对中断的处理有点问题。）
* 使用 StampedLock 一定不要调用中断操作，如果需要支持中断功能，
* 一定使用可中断的悲观读锁 readLockInterruptibly() 和写锁 writeLockInterruptibly()
*/
public class StampedLockDemo {
    public static void main(String[] args) {
        Point p = new Point(3, 5);
        ExecutorService writePool = Executors.newFixedThreadPool(2);
        ExecutorService calPool = Executors.newFixedThreadPool(2);
		for (int i = 0; i < 10; i++) {
			final int temp = i;
			writePool.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println("write " + Thread.currentThread().getId() + ", i=" + temp);
                    // 写操作
                    p.setX(temp + 3);
				}
			});
		}

        for (int i = 0; i < 10; i++) {
			final int temp = i;
			calPool.execute(new Runnable() {
				@Override
				public void run() {
                    // 由于是乐观读,在读的过程中还是可以写的
					System.out.println("cal " + 
                    Thread.currentThread().getId() + 
                    ", i=" + temp +
                    ", result=" + p.distanceFromOrigin());
				}
			});
		}
        writePool.shutdown();
        calPool.shutdown();
    }
}


class Point {
    private int x, y;
    final StampedLock sl = new StampedLock();
    
    //计算到原点的距离  
    int distanceFromOrigin() {
      // 乐观读
      long stamp = sl.tryOptimisticRead();
      // 读入局部变量，
      // 读的过程数据可能被修改
      int curX = x, curY = y;
      //判断执行读操作期间，
      //是否存在写操作，如果存在，
      //则sl.validate返回false
      if (!sl.validate(stamp)){
        // 升级为悲观读锁
        stamp = sl.readLock();
        try {
          curX = x;
          curY = y;
        } finally {
          //释放悲观读锁
          sl.unlockRead(stamp);
        }
      }
      return (int)Math.sqrt(
        curX * curX + curY * curY);
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
  }