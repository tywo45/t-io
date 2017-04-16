package org.tio.examples.im.common.http;

import java.util.Map;

import org.tio.examples.im.common.ImPacket;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpRequestPacket extends ImPacket
{
	private RequestLine requestLine = null;
	private Map<String, String> headers = null;
	private int contentLength;
	private byte[] httpRequestBody;



	public static class RequestLine
	{
		private String method;
		private String requestUrl;
		private String version;

		/**
		 * @return the method
		 */
		public String getMethod()
		{
			return method;
		}

		/**
		 * @param method the method to set
		 */
		public void setMethod(String method)
		{
			this.method = method;
		}

		/**
		 * @return the requestUrl
		 */
		public String getRequestUrl()
		{
			return requestUrl;
		}

		/**
		 * @param requestUrl the requestUrl to set
		 */
		public void setRequestUrl(String requestUrl)
		{
			this.requestUrl = requestUrl;
		}

		/**
		 * @return the version
		 */
		public String getVersion()
		{
			return version;
		}

		/**
		 * @param version the version to set
		 */
		public void setVersion(String version)
		{
			this.version = version;
		}
	}

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:14:40
	 * 
	 */
	public HttpRequestPacket()
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
	 * @return the firstLine
	 */
	public RequestLine getRequestLine()
	{
		return requestLine;
	}

	/**
	 * @param requestLine the requestLine to set
	 */
	public void setRequestLine(RequestLine requestLine)
	{
		this.requestLine = requestLine;
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
	 * @return the httpRequestBody
	 */
	public byte[] getHttpRequestBody()
	{
		return httpRequestBody;
	}

	/**
	 * @param httpRequestBody the httpRequestBody to set
	 */
	public void setHttpRequestBody(byte[] httpRequestBody)
	{
		this.httpRequestBody = httpRequestBody;
	}



}
