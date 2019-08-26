/**
 * 
 */
package org.tio.http.common;

import java.io.File;
import java.io.InputStream;

/**
 * @author tanyaowu
 *
 */
public class HttpResource {

	private String		path		= null;
	private InputStream	inputStream	= null;
	private File		file		= null;

	public HttpResource(String path, InputStream inputStream, File file) {
		super();
		this.path = path;
		this.inputStream = inputStream;
		this.file = file;
	}

	/**
	 * 
	 */
	public HttpResource() {
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
