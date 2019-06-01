package com.luoji.concurrent.designpatterns;

/**
 * isTerminated就是两阶段终止模式中的终止标识位
 * 声明为volatile是因为synchronized的start方法中，启动了一个新的线程。synchronized管不到这个线程。
 * 利用volatile的可见性，即后续读一定能看到之前一次的写
 * 这样就保证stop（）中写入isTerminated = true对于start中的while (!isTerminated)可见
 * 而start的读写都在同步方法里，所以不用加volatile
 * */
class CollectData {
	boolean started = false;
	volatile boolean isTerminated = false;
	Thread collectThread;
	synchronized void start() {
		if (started) {
			System.out.println("数据采集正在进行中，请勿重复开启");
			return;
		}
		started = true;
		isTerminated = false;
		collectThread = new Thread(()->{
			while (!isTerminated) {
				System.out.println("正在采集数据.....");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			started = false;
		});
		collectThread.start();
	}
	
	synchronized void stop() {
		isTerminated = true;
		collectThread.interrupt();
		System.out.println("数据采集已停止");
	}
	
}

public class TwoStagesStopPattern {
	public static void main(String[] args) {
		CollectData collector = new CollectData();
		collector.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		collector.stop();
	}
}
