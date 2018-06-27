/**
 * 
 */
package org.tio.http.common.view;

import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;

/**
 * @author tanyaowu
 *
 */
public interface View {
	/**
	 * 
	 * @param path 请求的路径
	 * @param request
	 * @return
	 */
	public HttpResponse render(String path, HttpRequest request);
}
