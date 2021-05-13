package com.luoji.concurrent.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/** 
** leetcode 1279
**
首先过滤一下题目中的废话，提取有用信息如下：
有两条路，Road1和Road2，组成十字路口；
车与车的唯一有用的区别就是它在只有Road1可以走或者只有Road2可以走。
每辆车代表一个线程，
TrafficLight这个类，就是红绿灯遥控器，很明显每个车都要抢这个遥控器。
思路： 依次获取两把锁（对应Road1和Road2），只有拿到锁后，对应车才可以跑。
这里要注意的是，可能车1和车2都在Road1上跑，不能重复亮绿灯。
**/
class TrafficLight {

    private static final Semaphore sma = new Semaphore(1);
    private static final Semaphore smb = new Semaphore(1);
    private volatile static boolean passA;
    private volatile static boolean passB;

    public TrafficLight() {
        passA = true;
        passB = false;
    }
    
    public void carArrived (
        int carId,           // ID of the car
        int roadId,          // ID of the road the car travels on. Can be 1 (road A) or 2 (road B)
        int direction,       // Direction of the car
        Runnable turnGreen,  // Use turnGreen.run() to turn light to green on current road
        Runnable crossCar    // Use crossCar.run() to make car cross the intersection 
    ) throws InterruptedException {
        // 先抢第一把锁
        sma.acquire();
        try {
           // 再抢第二把锁
           smb.acquire();
           try {
               if ((direction < 3)) {
                   if (passA && !passB) {
                       crossCar.run();
                   } else {
                       turnGreen.run();
                       crossCar.run();
                   }
                   passA = true;
                   passB = false;
               } else {
                   if (passB && !passA) {
                       crossCar.run();
                   } else {
                       turnGreen.run();
                       crossCar.run();
                   }
                   passA = false;
                   passB = true;
               }
              
           } finally {
               smb.release();
           }
        } finally {
            sma.release();
        }
       
    }

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(6);
        TrafficLight light = new TrafficLight();
        for (int i = 1; i < 10; i++) {
            final int dir = i % 4 + 1;
            final int index = i;
            es.submit(() -> {
                try {
                    light.carArrived(index,index, dir, 
                        () -> {System.out.println("turn on " + dir);}, 
                        () -> {System.out.println("cross car " + index + ", direction:"+ dir);});
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        
    }
}