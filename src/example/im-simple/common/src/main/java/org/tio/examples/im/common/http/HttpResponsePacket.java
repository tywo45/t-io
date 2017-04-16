package org.tio.examples.im.common.http;

import java.util.Map;

import org.tio.examples.im.common.ImPacket;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpResponsePacket extends ImPacket
{
	private HttpResponseStatus httpResponseStatus = null;
	private Map<String, String> headers = null;
	private int contentLength;
	private byte[] httpResponseBody;

	

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:14:40
	 * 
	 */
	public HttpResponsePacket()
	{
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:14:40
	 * 
	 */
	public static void main(String[] args)
	{
	}


	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders()
	{
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers)
	{
		this.headers = headers;
	}

	/**
	 * @return the bodyLength
	 */
	public int getContentLength()
	{
		return contentLength;
	}

	/**
	 * @param bodyLength the bodyLength to set
	 */
	public void setContentLength(int contentLength)
	{
		this.contentLength = contentLength;
	}

	/**
	 * @return the httpResponseStatus
	 */
	public HttpResponseStatus getHttpResponseStatus()
	{
		return httpResponseStatus;
	}

	/**
	 * @param httpResponseStatus the httpResponseStatus to set
	 */
	public void setHttpResponseStatus(HttpResponseStatus httpResponseStatus)
	{
		this.httpResponseStatus = httpResponseStatus;
	}

	/**
	 * @return the httpResponseBody
	 */
	public byte[] getHttpResponseBody()
	{
		return httpResponseBody;
	}

	/**
	 * @param httpResponseBody the httpResponseBody to set
	 */
	public void setHttpResponseBody(byte[] httpResponseBody)
	{
		this.httpResponseBody = httpResponseBody;
	}



}
