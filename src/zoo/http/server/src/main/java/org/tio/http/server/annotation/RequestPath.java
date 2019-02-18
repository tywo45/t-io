package org.tio.http.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tanyaowu
 * 2017年6月29日 下午7:52:31
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestPath {

	/**
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * 这个不能用变量，譬如/user/{id}，这样的路径是不允许的
	 * @return
	 */
	String forward() default "";

}
