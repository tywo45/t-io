/**
 * 
 */
package org.tio.http.server.mvc;

import java.lang.reflect.Method;

/**
 * @author tanyaowu
 *
 */
public class VariablePathVo {

	/**
	 * 
	 */
	public VariablePathVo() {

	}

	/**
	 * @param path 原path，形如/user/{userid}
	 * @param method 
	 * @param pathUnits 对于/user/{userid}，就是["user", "userid"]
	 */
	public VariablePathVo(String path, Method method, PathUnitVo[] pathUnits) {
		super();
		this.method = method;
		this.pathUnits = pathUnits;
		this.path = path;
	}

	/**
	 * 对于/user/{userid}，就是["user", "userid"]
	 */
	private PathUnitVo[] pathUnits = null;

	/**
	 * 原path，形如/user/{userid}
	 */
	private String path = null;

	private Method method = null;

	/**
	 * 对于/user/{userid}，就是["user", "userid"]
	 */
	public PathUnitVo[] getPathUnits() {
		return pathUnits;
	}

	/**
	 * 对于/user/{userid}，就是["user", "userid"]
	 */
	public void setPathUnits(PathUnitVo[] pathUnits) {
		this.pathUnits = pathUnits;
	}

	/**
	 * 原path，形如/user/{userid}
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 原path，形如/user/{userid}
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

}
