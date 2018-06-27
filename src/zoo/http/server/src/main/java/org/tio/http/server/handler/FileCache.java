package org.tio.http.server.handler;

import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;

/**
 * @author tanyaowu
 * 2017年8月15日 下午5:44:52
 */
public class FileCache implements java.io.Serializable {

	private static final long serialVersionUID = 6517890350387789902L;

//	private static Logger log = LoggerFactory.getLogger(FileCache.class);

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	//this.addHeader(HttpConst.ResponseHeaderKey.Content_Encoding, "gzip");
//	private Map<String, String> headers = null;
	private long lastModified;

//	private byte[] data;
	
	private HttpResponse response;
	
//	/**
//	 * 是否已经被gzip压缩过了，防止重复压缩
//	 */
//	private boolean hasGzipped = false;

	/**
	 *
	 * @author tanyaowu
	 */
	public FileCache() {
	}

	public FileCache(HttpResponse response, long lastModified) {
		super();
		this.response = response;
		//		this.setHeaders(headers);
		this.lastModified = lastModified;
//		this.data = data;
	}

//	public byte[] getData() {
//		return data;
//	}

//	public Map<String, String> getHeaders() {
//		return headers;
//	}

	public long getLastModified() {
		return lastModified;
	}

//	public void setData(byte[] data) {
//		this.data = data;
//	}

//	public void setHeaders(Map<String, String> headers) {
//		this.headers = headers;
//	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}
	
	public HttpResponse cloneResponse(HttpRequest request) {
//		HttpResponse responseInCache = fileCache.getResponse();
		HttpResponse ret = new HttpResponse(request);
		ret.setBody(response.getBody());
		ret.setHasGzipped(response.isHasGzipped());
		ret.setHeaders(response.getHeaders());
		return ret;
	}

//	public boolean isHasGzipped() {
//		return hasGzipped;
//	}
//
//	public void setHasGzipped(boolean hasGzipped) {
//		this.hasGzipped = hasGzipped;
//	}

}
