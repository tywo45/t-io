package org.tio.utils.hutool;

import java.lang.annotation.Annotation;

/**
 * 本对象会帮你找到含有指定Annotation类的class
 * @author tanyaowu
 */
public abstract class ClassScanAnnotationHandler implements ClassScanHandler {

	private Class<? extends Annotation> annotationClass;

	/**
	 * 
	 * @param annotationClass
	 */
	public ClassScanAnnotationHandler(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	@Override
	public void handler(Class<?> clazz) {
		if (!clazz.isAnnotationPresent(annotationClass)) {
			return;
		}
		handlerAnnotation(clazz);
	}

	/**
	 * 
	 * @param clazz 拥有annotationClass注解的class对象
	 * @author tanyaowu
	 */
	public abstract void handlerAnnotation(Class<?> clazz);

}
