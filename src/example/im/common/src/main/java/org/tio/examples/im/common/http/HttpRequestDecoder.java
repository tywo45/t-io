package org.tio.examples.im.common.http;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.tio.core.exception.AioDecodeException;
import org.tio.examples.im.common.http.HttpRequestPacket.RequestLine;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpRequestDecoder
{

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * 
	 */
	public HttpRequestDecoder()
	{

	}

	public static final int MAX_HEADER_LENGTH = 20480;

	public static HttpRequestPacket decode(ByteBuffer buffer/**, ChannelContext<ImSessionContext, HttpRequestPacket, Object> channelContext*/
	) throws AioDecodeException
	{
		int count = 0;
		Step step = Step.firstline;
		StringBuilder currLine = new StringBuilder();
		Map<String, String> headers = new HashMap<>();
		int contentLength = 0;
		byte[] httpRequestBody = null;
		RequestLine firstLine = null;
		while (buffer.hasRemaining())
		{
			count++;
			if (count > MAX_HEADER_LENGTH)
			{
				throw new AioDecodeException("max http header length " + MAX_HEADER_LENGTH);
			}

			byte b = buffer.get();

			if (b == '\n')
			{
				if (currLine.length() == 0)
				{
					String contentLengthStr = headers.get("Content-Length");
					if (StringUtils.isBlank(contentLengthStr))
					{
						contentLength = 0;
					} else
					{
						contentLength = Integer.parseInt(contentLengthStr);
					}

					int readableLength = buffer.limit() - buffer.position();
					if (readableLength >= contentLength)
					{
						step = Step.body;
						break;
					} else
					{
						return null;
					}
				} else
				{
					if (step == Step.firstline)
					{
						firstLine = parseRequestLine(currLine.toString());
						step = Step.header;
					} else if (step == Step.header)
					{
						KeyValue keyValue = parseHeaderLine(currLine.toString());
						headers.put(keyValue.getKey(), keyValue.getValue());
					}

					currLine.setLength(0);
				}
				continue;
			} else if (b == '\r')
			{
				continue;
			} else
			{
				currLine.append((char) b);
			}
		}

		if (step != Step.body)
		{
			return null;
		}

		if (contentLength > 0)
		{
			httpRequestBody = new byte[contentLength];
			buffer.get(httpRequestBody);
		}

		HttpRequestPacket httpRequestPacket = new HttpRequestPacket();
		httpRequestPacket.setHttpRequestBody(httpRequestBody);
		httpRequestPacket.setRequestLine(firstLine);
		httpRequestPacket.setHeaders(headers);
		httpRequestPacket.setContentLength(contentLength);
		return httpRequestPacket;

	}

	/**
	 * 解析第一行(请求行)
	 * @param line
	 * @return
	 *
	 * @author: tanyaowu
	 * 2017年2月23日 下午1:37:51
	 *
	 */
	public static RequestLine parseRequestLine(String line)
	{
		int index1 = line.indexOf(' ');
		String method = line.substring(0, index1);
		int index2 = line.indexOf(' ', index1 + 1);
		String requestUrl = line.substring(index1 + 1, index2);
		String version = line.substring(index2 + 1);

		RequestLine requestLine = new RequestLine();
		requestLine.setMethod(method);
		requestLine.setRequestUrl(requestUrl);
		requestLine.setVersion(version);
		return requestLine;
	}

	/**
	 * 解析请求头的每一行
	 * @param line
	 * @return
	 *
	 * @author: tanyaowu
	 * 2017年2月23日 下午1:37:58
	 *
	 */
	public static KeyValue parseHeaderLine(String line)
	{
		KeyValue keyValue = new KeyValue();
		int p = line.indexOf(":");
		if (p == -1)
		{
			keyValue.setKey(line);
			return keyValue;
		}

		String name = line.substring(0, p).trim();
		String value = line.substring(p + 1).trim();

		keyValue.setKey(name);
		keyValue.setValue(value);

		return keyValue;
	}

	

	public static enum Step
	{
		firstline, header, body
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * 
	 */
	public static void main(String[] args)
	{

	}

}
