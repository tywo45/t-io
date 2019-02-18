/***
 *
 * Copyright (c) 2007 Paul Hammant
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.tio.utils.thoughtworksparanamer;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Default implementation of Paranamer reads from a post-compile added field called '__PARANAMER_DATA'
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Guilherme Silveira
 */
public class DefaultParanamer implements Paranamer {

	private static final String	COMMA	= ",";
	private static final String	SPACE	= " ";

	public static final String __PARANAMER_DATA = "v1.0 \n" + "lookupParameterNames java.lang.AccessibleObject methodOrConstructor \n"
	        + "lookupParameterNames java.lang.AccessibleObject,boolean methodOrCtor,throwExceptionIfMissing \n" + "getParameterTypeName java.lang.Class cls\n";

	public DefaultParanamer() {
	}

	public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
		return lookupParameterNames(methodOrConstructor, true);
	}

	public String[] lookupParameterNames(AccessibleObject methodOrCtor, boolean throwExceptionIfMissing) {
		// Oh for some commonality between Constructor and Method !!
		Class<?>[] types = null;
		Class<?> declaringClass = null;
		String name = null;
		if (methodOrCtor instanceof Method) {
			Method method = (Method) methodOrCtor;
			types = method.getParameterTypes();
			name = method.getName();
			declaringClass = method.getDeclaringClass();
		} else {
			Constructor<?> constructor = (Constructor<?>) methodOrCtor;
			types = constructor.getParameterTypes();
			declaringClass = constructor.getDeclaringClass();
			name = "<init>";
		}

		if (types.length == 0) {
			return EMPTY_NAMES;
		}
		final String parameterTypeNames = getParameterTypeNamesCSV(types);
		final String[] names = getParameterNames(declaringClass, parameterTypeNames, name + SPACE);
		if (names == null) {
			if (throwExceptionIfMissing) {
				throw new ParameterNamesNotFoundException(
				        "No parameter names found for class '" + declaringClass + "', methodOrCtor " + name + " and parameter types " + parameterTypeNames);
			} else {
				return Paranamer.EMPTY_NAMES;
			}
		}
		return names;
	}

	private static String[] getParameterNames(Class<?> declaringClass, String parameterTypes, String prefix) {
		String data = getParameterListResource(declaringClass);
		String line = findFirstMatchingLine(data, prefix + parameterTypes + SPACE);
		String[] parts = line.split(SPACE);
		// assumes line structure: constructorName parameterTypes parameterNames
		if (parts.length == 3 && parts[1].equals(parameterTypes)) {
			String parameterNames = parts[2];
			return parameterNames.split(COMMA);
		}
		return Paranamer.EMPTY_NAMES;
	}

	static String getParameterTypeNamesCSV(Class<?>[] parameterTypes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < parameterTypes.length; i++) {
			sb.append(getParameterTypeName(parameterTypes[i]));
			if (i < parameterTypes.length - 1) {
				sb.append(COMMA);
			}
		}
		return sb.toString();
	}

	private static String getParameterListResource(Class<?> declaringClass) {
		try {
			Field field = declaringClass.getDeclaredField("__PARANAMER_DATA");
			// TODO create acc test which finds field?
			// TODO create acc test which does not find field?
			// TODO create acc test what to do with private? access anyway?
			// TODO create acc test with non static field?
			// TODO create acc test with another type of field?
			if (!Modifier.isStatic(field.getModifiers()) || !field.getType().equals(String.class)) {
				return null;
			}
			return (String) field.get(null);
		} catch (NoSuchFieldException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}

	/**
	 * Filter the mappings and only return lines matching the prefix passed in.
	 * @param data the data encoding the mappings
	 * @param prefix the String prefix
	 * @return A list of lines that match the prefix
	 */
	private static String findFirstMatchingLine(String data, String prefix) {
		if (data == null) {
			return "";
		}
		int ix = data.indexOf(prefix);
		if (ix >= 0) {
			int iy = data.indexOf("\n", ix);
			if (iy > 0) {
				return data.substring(ix, iy);
			}
		}
		return "";
	}

	private static String getParameterTypeName(Class<?> cls) {
		String parameterTypeNameName = cls.getName();
		parameterTypeNameName = parameterTypeNameName.replace("[J", "[Xlong").replace("[I", "[Xint").replace("[Z", "[Xboolean").replace("[S", "[Xshort").replace("[F", "[Xfloat")
		        .replace("[D", "[Xdouble").replace("[B", "[Xbyte").replace("[C", "[Xchar");
		int arrayNestingDepth = 0;
		int ix = parameterTypeNameName.indexOf("[");
		while (ix > -1) {
			arrayNestingDepth++;
			parameterTypeNameName = parameterTypeNameName.replaceFirst("(\\[\\w)|(\\[)", "");
			ix = parameterTypeNameName.indexOf("[");
		}
		parameterTypeNameName = parameterTypeNameName.replaceFirst(";", "");
		for (int k = 0; k < arrayNestingDepth; k++) {
			parameterTypeNameName = parameterTypeNameName + "[]";
		}
		return parameterTypeNameName;

	}
}
