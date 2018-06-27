package org.tio.http.server.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseStatus;
import org.tio.http.common.MimeType;
import org.tio.http.common.RequestLine;
import org.tio.utils.json.Json;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;

import jodd.io.FileNameUtil;

/**
 * @author tanyaowu
 * 2017年6月29日 下午4:17:24
 */
public class Resps {
	private static Logger log = LoggerFactory.getLogger(Resps.class);

	/**
	 * 构建css响应
	 * Content-Type: text/css;charset=utf-8
	 * @param request
	 * @param bodyString
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse css(HttpRequest request, String bodyString) {
		return css(request, bodyString, request.getHttpConfig().getCharset());
	}

	/**
	 * 构建css响应
	 * Content-Type: text/css;charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse css(HttpRequest request, String bodyString, String charset) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_CSS_CSS.getType() + ";charset=" + charset);
		return ret;
	}

	/**
	 * 根据byte[]创建响应
	 * @param request
	 * @param bodyBytes
	 * @param extension 后缀，可以为空
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse bytes(HttpRequest request, byte[] bodyBytes, String extension) {
		String contentType = null;
		//		String extension = FilenameUtils.getExtension(filename);
		if (StringUtils.isNotBlank(extension)) {
			MimeType mimeType = MimeType.fromExtension(extension);
			if (mimeType != null) {
				contentType = mimeType.getType();
			} else {
				contentType = "application/octet-stream";
			}
		}
		return bytesWithContentType(request, bodyBytes, contentType);
	}

	/**
	 * 根据文件创建响应
	 * @param request
	 * @param fileOnServer
	 * @return
	 * @throws IOException
	 * @author tanyaowu
	 */
	public static HttpResponse file(HttpRequest request, File fileOnServer) throws IOException {
		Date lastModified = FileUtil.lastModifiedTime(fileOnServer);
		HttpResponse ret = try304(request, lastModified.getTime());
		if (ret != null) {
			return ret;
		}

		byte[] bodyBytes = FileUtil.readBytes(fileOnServer);
		String filename = fileOnServer.getName();
		String extension = FileNameUtil.getExtension(filename);
		ret = bytes(request, bodyBytes, extension);
		ret.setLastModified(lastModified.getTime() + "");
		return ret;
	}

	/**
	 * 
	 * @param request
	 * @param path 文件在服务器上的相对pageRoot的路径，形如："/user/index.html"
	 * @param httpConfig
	 * @return
	 * @throws IOException
	 * @author: tanyaowu
	 */
	public static HttpResponse file(HttpRequest request, String path, HttpConfig httpConfig) throws IOException {
		File pageRoot = httpConfig.getPageRoot(request);
		if (pageRoot != null) {
//			String root = FileUtil.getAbsolutePath(pageRoot);
			File file = new File(pageRoot + path);
			if (!file.exists()) {
				return resp404(request, request.getRequestLine(), httpConfig);
			}
			return file(request, file);
		} else {
			return resp404(request, request.getRequestLine(), httpConfig);
		}
	}

	/**
	 * 
	 * @param request
	 * @param requestLine
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse resp404(HttpRequest request, RequestLine requestLine, HttpConfig httpConfig) {
		File pageRoot = httpConfig.getPageRoot(request);
		
		if (pageRoot != null) {
			String file404 = httpConfig.getPage404();
//			String root = FileUtil.getAbsolutePath(pageRoot);
			File file = new File(pageRoot + file404);
			if (file.exists()) {
				HttpResponse ret = Resps.redirect(request, file404 + "?tio_initpath=" + requestLine.getPathAndQuery());
				return ret;
			}
		}
		
		HttpResponse ret = Resps.html(request, "404");
		ret.setStatus(HttpResponseStatus.C404);
		return ret;
	}
	
//	/**
//	 * 
//	 * @param newPath
//	 * @param request
//	 * @return
//	 * @throws Exception
//	 * @author tanyaowu
//	 */
//	public static HttpResponse forward(String newPath, HttpRequest request) throws Exception {
//		RequestLine requestLine  = request.getRequestLine();
//		HttpConfig httpConfig = request.getHttpConfig();
//		
//		httpConfig.getContextPath()
//		
//		if (newPath != null) {
//			requestLine.setPath(newPath);
//		}
//		
//		HttpRequestHandler httpRequestHandler = request.getHttpConfig().getHttpRequestHandler();
//		return httpRequestHandler.handler(request);
//	}

	/**
	 * 
	 * @param request
	 * @param requestLine
	 * @param httpConfig
	 * @param throwable
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse resp500(HttpRequest request, RequestLine requestLine, HttpConfig httpConfig, Throwable throwable) {
		File pageRoot = httpConfig.getPageRoot(request);
		
		if (pageRoot != null) {
			String file500 = httpConfig.getPage500();
//			String root = FileUtil.getAbsolutePath(pageRoot);
			File file = new File(pageRoot + file500);
			if (file.exists()) {
				HttpResponse ret = Resps.redirect(request, file500 + "?tio_initpath=" + requestLine.getPathAndQuery());
				return ret;
			}
		}
		
		HttpResponse ret = Resps.html(request, "500");
		ret.setStatus(HttpResponseStatus.C500);
		return ret;
	}
	
//	/**
//	 * 
//	 * @param request
//	 * @param toPath
//	 * @param throwable
//	 * @return
//	 */
//	public static HttpResponse redirect(HttpRequest request, String toPath, Throwable throwable) {
//		HttpResponse response = new HttpResponse(request);
//		response.setStatus(HttpResponseStatus.C301); 
//	    response.addHeader(HttpConst.ResponseHeaderKey.Location, toPath);
//		return response;
//	}

	/**
	 *
	 * @param request
	 * @param bodyBytes
	 * @param contentType 形如:application/octet-stream等
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse bytesWithContentType(HttpRequest request, byte[] bodyBytes, String contentType) {
		HttpResponse ret = new HttpResponse(request);
		ret.setBody(bodyBytes);
		ret.addHeader(HttpConst.ResponseHeaderKey.Content_Type, contentType);
		return ret;
	}

	/**
	 *
	 * @param request
	 * @param bodyBytes
	 * @param headers
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse bytesWithHeaders(HttpRequest request, byte[] bodyBytes, Map<String, String> headers) {
		HttpResponse ret = new HttpResponse(request);
		ret.setBody(bodyBytes);
		ret.addHeaders(headers);
		return ret;
	}

	/**
	 *
	 * @param request
	 * @param bodyString
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse html(HttpRequest request, String bodyString) {
		HttpConfig httpConfig = request.getHttpConfig();
		return html(request, bodyString, httpConfig.getCharset());
	}

	/**
	 * Content-Type: text/html;charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse html(HttpRequest request, String bodyString, String charset) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_HTML_HTML.getType() + ";charset=" + charset);
		return ret;
	}

	/**
	 * Content-Type: application/javascript;charset=utf-8
	 * @param request
	 * @param bodyString
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse js(HttpRequest request, String bodyString) {
		return js(request, bodyString, request.getHttpConfig().getCharset());
	}

	/**
	 * Content-Type: application/javascript;charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse js(HttpRequest request, String bodyString, String charset) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.APPLICATION_JAVASCRIPT_JS.getType() + ";charset=" + charset);
		return ret;
	}

	/**
	 * Content-Type: application/json;charset=utf-8
	 * @param request
	 * @param body
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse json(HttpRequest request, Object body) {
		return json(request, body, request.getHttpConfig().getCharset());
	}

	/**
	 * Content-Type: application/json;charset=utf-8
	 * @param request
	 * @param body
	 * @param charset
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse json(HttpRequest request, Object body, String charset) {
		HttpResponse ret = null;
		if (body == null) {
			ret = string(request, "", charset, MimeType.TEXT_PLAIN_JSON.getType() + ";charset=" + charset);
		} else {
			if (body.getClass() == String.class || ClassUtil.isBasicType(body.getClass())) {
				ret = string(request, body + "", charset, MimeType.TEXT_PLAIN_JSON.getType() + ";charset=" + charset);
			} else {
				ret = string(request, Json.toJson(body), charset, MimeType.TEXT_PLAIN_JSON.getType() + ";charset=" + charset);
			}
		}
		return ret;
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 * 重定向
	 * @param request
	 * @param path
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse redirect(HttpRequest request, String path) {
		return redirect(request, path, HttpResponseStatus.C302);
	}
	
	/**
	 * 永久重定向
	 * @param request
	 * @param path
	 * @return
	 */
	public static HttpResponse redirectForever(HttpRequest request, String path) {
		return redirect(request, path, HttpResponseStatus.C301);
	}
	
	/**
	 * 
	 * @param request
	 * @param path
	 * @param status
	 * @return
	 */
	public static HttpResponse redirect(HttpRequest request, String path, HttpResponseStatus status) {
		HttpResponse ret = new HttpResponse(request);
		ret.setStatus(status);
		ret.addHeader(HttpConst.ResponseHeaderKey.Location, path);
		return ret;
	}

	/**
	 * 创建字符串输出
	 * @param request
	 * @param bodyString
	 * @param Content_Type
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse string(HttpRequest request, String bodyString, String Content_Type) {
		return string(request, bodyString, request.getHttpConfig().getCharset(), Content_Type);
	}

	/**
	 * 创建字符串输出
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @param Content_Type
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse string(HttpRequest request, String bodyString, String charset, String Content_Type) {
		HttpResponse ret = new HttpResponse(request);
		if (bodyString != null) {
			try {
				ret.setBody(bodyString.getBytes(charset));
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
		}
		ret.addHeader(HttpConst.ResponseHeaderKey.Content_Type, Content_Type);
		return ret;
	}

	/**
	 * 尝试返回304，这个会new一个HttpResponse返回
	 * @param request
	 * @param lastModifiedOnServer 服务器中资源的lastModified
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse try304(HttpRequest request, long lastModifiedOnServer) {
		String If_Modified_Since = request.getHeader(HttpConst.RequestHeaderKey.If_Modified_Since);//If-Modified-Since
		if (StringUtils.isNotBlank(If_Modified_Since)) {
			Long If_Modified_Since_Date = null;
			try {
				If_Modified_Since_Date = Long.parseLong(If_Modified_Since);

				if (lastModifiedOnServer <= If_Modified_Since_Date) {
					HttpResponse ret = new HttpResponse(request);
					ret.setStatus(HttpResponseStatus.C304);
					return ret;
				}
			} catch (NumberFormatException e) {
				log.warn("{}, {}不是整数，浏览器信息:{}", request.getClientIp(), If_Modified_Since, request.getUserAgent());
				return null;
			}
		}

		return null;
	}

	/**
	 * Content-Type: text/plain;charset=utf-8
	 * @param request
	 * @param bodyString
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse txt(HttpRequest request, String bodyString) {
		return txt(request, bodyString, request.getHttpConfig().getCharset());
	}

	/**
	 * Content-Type: text/plain;charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author tanyaowu
	 */
	public static HttpResponse txt(HttpRequest request, String bodyString, String charset) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_PLAIN_TXT.getType() + ";charset=" + charset);
		return ret;
	}

	/**
	 *
	 * @author tanyaowu
	 */
	private Resps() {
	}
}
