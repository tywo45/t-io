/**
 * 
 */
package org.tio.http.server.stat;

import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;

/**
 * @author tanyaowu
 *
 */
public class DefaultStatPathFilter implements StatPathFilter {

	public static final DefaultStatPathFilter me = new DefaultStatPathFilter();

	/**
	 * 
	 */
	public DefaultStatPathFilter() {
	}

	@Override
	public boolean filter(String path, HttpRequest request, HttpResponse response) {
		return true;
	}

}
