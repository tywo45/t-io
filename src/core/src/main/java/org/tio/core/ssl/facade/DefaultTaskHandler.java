package org.tio.core.ssl.facade;

import javax.net.ssl.SSLException;

public class DefaultTaskHandler implements ITaskHandler {
	@Override
	public void process(ITasks tasks) throws SSLException {
		Runnable task;
		while ((task = tasks.next()) != null) {
			task.run();
		}

		/* Must be called to signal to the SSLFacade that all tasks have
		been completed and that the handshake process should resume
		 */
		tasks.done();
	}
}
