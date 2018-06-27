package org.tio.http.server.watche;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;

/**
 *
 * @author tanyaowu
 * 2017年8月1日 下午3:17:54
 */
public class SimpleWatcher implements Watcher {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		WatchMonitor watchMonitor = WatchMonitor.create(Paths.get("c://"), 1000000, new WatchEvent.Kind() {

			@Override
			public String name() {
				return null;
			}

			@Override
			public Class<?> type() {
				return null;
			}
		});
		SimpleWatcher simpleWatcher = new SimpleWatcher();
		watchMonitor.setWatcher(simpleWatcher);
		watchMonitor.start();
	}

	@Override
	public void onCreate(WatchEvent<?> event, Path currentPath) {
		Object obj = event.context();
		System.out.println("创建：" + obj);

	}

	@Override
	public void onDelete(WatchEvent<?> event, Path currentPath) {
		Object obj = event.context();
		System.out.println("删除：" + obj);
	}

	@Override
	public void onModify(WatchEvent<?> event, Path currentPath) {
		Object obj = event.context();
		System.out.println("修改：" + obj);
	}

	@Override
	public void onOverflow(WatchEvent<?> event, Path currentPath) {
		Object obj = event.context();
		System.out.println("Overflow：" + obj);
	}
}
