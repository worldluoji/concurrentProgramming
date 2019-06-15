package com.luoji.concurrent.designpatterns;

/**
 * 多线程版本的单例模式
 * 一旦对象创建成功后，就不会执行synchronized(Singletone.class)｛｝相关代码
 * 使用了volatile取消编译器的优化，防止优化带来的小概率空指针异常
 * CPU缓存不存在于内存中的，它是一块比内存更小、读写速度更快的芯片，至于什么时候把数据从缓存写到内存，没有固定的时间，
 * 同样地，对于有volatile语义声明的变量，线程A执行完后会强制将值刷新到内存中，线程B进行相关操作时会强制重新把内存中的内容写入到自己的缓存，这就涉及到了volatile的写入屏障问题，当然也就是所谓happen-before问题。
 * 单例模式，本质上就是Balking模式的一个特例，即状态singletone改变了就执行不同的路径
 * */
class Singletone {
	private static volatile Singletone singletone;
	private Singletone() {}
	public static Singletone getInstance() {
		if (singletone == null) {
			// 如果两个线程都走到了这里判定通过，只有一个线程会先拿到锁
			synchronized(Singletone.class) {
				// 双重检查是第二个线程拿到锁后，可能对象已经不为null了
				if (singletone == null) {
					singletone = new Singletone();
					//第一个线程进入后，编译器由于优化赋值给了singletone地址（指令１），但是还没有初始化对象（指令２）。　然后可能存在这时切换到第二个线程，发现第一个if (singletone == null)不为null就退出了,于是拿到一个空对象。
					//如果对instance进行volatile语义声明，就可以禁止指令重排序，避免该情况发生。
				}
			}
		}
		return singletone;
	}
}

public class SingletoneDemo {

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(()-> {
			Singletone s1 = Singletone.getInstance();
			System.out.println(s1);
		});
		
		Thread t2 = new Thread(()-> {
			Singletone s2 = Singletone.getInstance();
			System.out.println(s2);
		});
		
		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

}
