package com.luoji.concurrent.cache;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Cache<K, V> {
    private final Map<K,V> m = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    
    public void put(K key, V value) {
        readLock.lock();
        try {
            m.put(key, value);
        } finally {
            readLock.unlock();
        }
    }

    public V get(K key) {
        writeLock.lock();
        V v;
        try {
            v = m.get(key);
        } finally {
            writeLock.unlock();
        }
        if (v != null) {
            return v;
        }
        
        // 懒加载，如果数据为空，去查询获取数据
        writeLock.lock();
        try {
            v = m.get(key);
            //其他线程可能已经查询过数据库, 所以再次验证
            if (v == null) {
                // 模拟查询数据库获取数据
                v = (V)"test";
                m.put(key, v);
            }
        } finally {
            writeLock.unlock();
        }
        return v;
    }

}

public class SimpleCache {
   public static void main(String[] args) {
       Cache<String, String> c = new Cache<>();
       c.put("loginUser", "luojiayi");
       c.put("loginTime", LocalDateTime.now().toString());
       System.out.println(c.get("loginUser"));
       System.out.println(c.get("loginTime"));
       System.out.println(c.get("address"));
   }
}

/**
 * 
//读缓存
r.lock();         ①
try {
  v = m.get(key); ②
  if (v == null) {
    w.lock();
    try {
      //再次验证并更新缓存
      //省略详细代码
    } finally{
      w.unlock();
    }
  }
} finally{
  r.unlock();     ③
}
上述代码有什么问题？
先是获取读锁，然后再升级为写锁，对此还有个专业的名字，叫锁的升级。
可惜 ReadWriteLock 并不支持这种升级。在上面的代码示例中，读锁还没有释放，
此时获取写锁，会导致写锁永久等待，最终导致相关线程都被阻塞，永远也没有机会被唤醒。
锁的升级是不允许的，这个你一定要注意。
不过，虽然锁的升级是不允许的，但是锁的降级却是允许的：

class CachedData {
  Object data;
  volatile boolean cacheValid;
  final ReadWriteLock rwl =
    new ReentrantReadWriteLock();
  // 读锁  
  final Lock r = rwl.readLock();
  //写锁
  final Lock w = rwl.writeLock();
  
  void processCachedData() {
    // 获取读锁
    r.lock();
    if (!cacheValid) {
      // 释放读锁，因为不允许读锁的升级
      r.unlock();
      // 获取写锁
      w.lock();
      try {
        // 再次检查状态  
        if (!cacheValid) {
          data = ...
          cacheValid = true;
        }
        // 释放写锁前，降级为读锁
        // 降级是可以的
        r.lock(); ①
      } finally {
        // 释放写锁
        w.unlock(); 
      }
    }
    // 此处仍然持有读锁
    try {use(data);} 
    finally {r.unlock();}
  }
}
 * 
 * 
*/
