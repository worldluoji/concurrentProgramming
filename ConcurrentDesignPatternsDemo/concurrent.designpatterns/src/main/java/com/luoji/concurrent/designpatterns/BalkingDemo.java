package com.luoji.concurrent.designpatterns;


/**
 *　Balking模式实际就是一种多线程的if-else模式，一个线程的处理依赖于某个状态，状态会在另一个线程中改变 
 * */

class AutoSaveEditor {
	boolean changed = false;
	public void autoSave() {
		if (!changed) {
			return;
		}
		System.out.println("自动保存成功!!!");
		this.changed = false;
	}
	
	public void edit() throws InterruptedException {
		System.out.println("正在编辑中...");
		change();
		Thread.sleep(3000);
	}
	
	public void change() {
		synchronized(this) {
			this.changed = true;
		}
	}
}


public class BalkingDemo {

	public static void main(String[] args) throws InterruptedException {
		AutoSaveEditor editor = new AutoSaveEditor();
		editor.autoSave();
		editor.edit();
		editor.autoSave();
	}

}
