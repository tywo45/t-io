package org.tio.core.ssl.facade;

import javax.net.ssl.SSLException;

public interface ITaskHandler {
	/*
	In order to continue handshakes after tasks are processed the
	tasks.done() method must be called.
	 */
	public void process(ITasks tasks) throws SSLException;
}
