/**
 * 
 */
package org.tio.http.server.mvc.intf;

/**
 * @author tanyaowu
 *
 */
public interface ControllerFactory {

	/**
	 * 
	 * @param controllerClazz Controller的class
	 * @return
	 * @throws Exception 
	 */
	Object getInstance(Class<?> controllerClazz) throws Exception;
}
