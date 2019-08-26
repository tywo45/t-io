package org.tio.utils.hutool;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ArrayUtil {

	/** 数组中元素未找到的下标，值为-1 */
	public static final int INDEX_NOT_FOUND = -1;

	/**
	 * 数组中是否包含元素
	 * 
	 * @param <T> 数组元素类型
	 * 
	 * @param array 数组
	 * @param value 被检查的元素
	 * @return 是否包含
	 */
	public static <T> boolean contains(T[] array, T value) {
		return indexOf(array, value) > INDEX_NOT_FOUND;
	}

	/**
	 * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
	 * 
	 * @param <T> 数组类型
	 * @param array 数组
	 * @param value 被检查的元素
	 * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
	 * @since 3.0.7
	 */
	public static <T> int indexOf(T[] array, Object value) {
		if (null != array) {
			for (int i = 0; i < array.length; i++) {
				if (equal(value, array[i])) {
					return i;
				}
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * 比较两个对象是否相等。<br>
	 * 相同的条件有两个，满足其一即可：<br>
	 * <ol>
	 * <li>obj1 == null &amp;&amp; obj2 == null</li>
	 * <li>obj1.equals(obj2)</li>
	 * </ol>
	 * 1. obj1 == null &amp;&amp; obj2 == null 2. obj1.equals(obj2)
	 * 
	 * @param obj1 对象1
	 * @param obj2 对象2
	 * @return 是否相等
	 */
	private static boolean equal(Object obj1, Object obj2) {
		return (obj1 == obj2) || (obj1 != null && obj1.equals(obj2));
	}

	/**
	 * @param array
	 * @param string
	 * @return
	 * @author tanyaowu
	 */
	public static String join(String[] array, String conjunction) {
		if (null == array) {
			return null;
		}

		final StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (String item : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}

	/**
	 * 对象是否为数组对象
	 * 
	 * @param obj 对象
	 * @return 是否为数组对象，如果为{@code null} 返回false
	 */
	public static boolean isArray(Object obj) {
		if (null == obj) {
			// throw new NullPointerException("Object check for isArray is null");
			return false;
		}
		return obj.getClass().isArray();
	}

	/**
	 * @param obj
	 * @return
	 * @author tanyaowu
	 */
	public static String toString(Object obj) {
		if (null == obj) {
			return null;
		}
		if (ArrayUtil.isArray(obj)) {
			try {
				return Arrays.deepToString((Object[]) obj);
			} catch (Exception e) {
				final String className = obj.getClass().getComponentType().getName();
				switch (className) {
				case "long":
					return Arrays.toString((long[]) obj);
				case "int":
					return Arrays.toString((int[]) obj);
				case "short":
					return Arrays.toString((short[]) obj);
				case "char":
					return Arrays.toString((char[]) obj);
				case "byte":
					return Arrays.toString((byte[]) obj);
				case "boolean":
					return Arrays.toString((boolean[]) obj);
				case "float":
					return Arrays.toString((float[]) obj);
				case "double":
					return Arrays.toString((double[]) obj);
				default:
					throw new RuntimeException(e);
				}
			}
		}
		return obj.toString();
	}

	/**
	 * 过滤<br>
	 * 过滤过程通过传入的Editor实现来返回需要的元素内容，这个Editor实现可以实现以下功能：
	 * 
	 * <pre>
	 * 1、过滤出需要的对象，如果返回null表示这个元素对象抛弃
	 * 2、修改元素对象，返回集合中为修改后的对象
	 * </pre>
	 * 
	 * @param <T> 数组元素类型
	 * @param array 数组
	 * @param editor 编辑器接口
	 * @return 过滤后的数组
	 */
	public static <T> T[] filter(T[] array, Editor<T> editor) {
		ArrayList<T> list = new ArrayList<T>(array.length);
		T modified;
		for (T t : array) {
			modified = editor.edit(t);
			if (null != modified) {
				list.add(modified);
			}
		}
		return list.toArray(Arrays.copyOf(array, list.size()));
	}

	/**
	 * 过滤<br>
	 * 过滤过程通过传入的Filter实现来过滤返回需要的元素内容，这个Editor实现可以实现以下功能：
	 * 
	 * <pre>
	 * 1、过滤出需要的对象，{@link Filter#accept(Object)}方法返回true的对象将被加入结果集合中
	 * </pre>
	 * 
	 * @param <T> 数组元素类型
	 * @param array 数组
	 * @param filter 过滤器接口，用于定义过滤规则
	 * @return 过滤后的数组
	 * @since 3.2.1
	 */
	public static <T> T[] filter(T[] array, Filter<T> filter) {
		ArrayList<T> list = new ArrayList<T>(array.length);
		boolean isAccept;
		for (T t : array) {
			isAccept = filter.accept(t);
			if (isAccept) {
				list.add(t);
			}
		}
		return list.toArray(Arrays.copyOf(array, list.size()));
	}

	/**
	 * 新建一个空数组
	 * 
	 * @param <T> 数组元素类型
	 * @param componentType 元素类型
	 * @param newSize 大小
	 * @return 空数组
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(Class<?> componentType, int newSize) {
		return (T[]) Array.newInstance(componentType, newSize);
	}
	
	
	public static byte[] addAll(byte[]... arrays) {
		if (arrays.length == 1) {
			return arrays[0];
		}

		int length = 0;
		for (byte[] array : arrays) {
			if (array == null) {
				continue;
			}
			length += array.length;
		}
		byte[] result = new byte[length];

		length = 0;
		for (byte[] array : arrays) {
			if (array == null) {
				continue;
			}
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}
	

}
