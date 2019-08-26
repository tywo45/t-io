package org.tio.webpack.model;

public class Root {
	private boolean debug;

	private Console console;

	private boolean dev;

	private Compress compress;

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean getDebug() {
		return this.debug;
	}

	public void setConsole(Console console) {
		this.console = console;
	}

	public Console getConsole() {
		return this.console;
	}

	public void setDev(boolean dev) {
		this.dev = dev;
	}

	public boolean getDev() {
		return this.dev;
	}

	public void setCompress(Compress compress) {
		this.compress = compress;
	}

	public Compress getCompress() {
		return this.compress;
	}

}