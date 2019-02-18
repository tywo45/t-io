/**
 *
 * Copyright (c) 2013 Stefan Fleiter
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
import java.lang.reflect.Method;

/**
 * Paranamer that works on basis of the parameter position and can be used as
 * last fallback of the <code>AdaptiveParanamer</code>.
 * 
 * @author Stefan Fleiter
 */
public class PositionalParanamer implements Paranamer {

	private final String prefix;

	/**
	 * Default Contstructor with prefix <code>arg</code>.
	 */
	public PositionalParanamer() {
		this("arg");
	}

	/**
	 * Constructor that allows to override the prefix.
	 * 
	 * @param prefix
	 *            string that is prepended before the position of the parameter.
	 */
	public PositionalParanamer(String prefix) {
		super();
		this.prefix = prefix;
	}

	public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
		return lookupParameterNames(methodOrConstructor, true);
	}

	public String[] lookupParameterNames(AccessibleObject methodOrCtor, boolean throwExceptionIfMissing) {
		int count = count(methodOrCtor);
		String[] result = new String[count];
		for (int i = 0; i < result.length; i++) {
			result[i] = prefix + i;
		}
		return result;
	}

	private int count(AccessibleObject methodOrCtor) {
		if (methodOrCtor instanceof Method) {
			Method method = (Method) methodOrCtor;
			return method.getParameterTypes().length;
		}
		Constructor<?> constructor = (Constructor<?>) methodOrCtor;
		return constructor.getParameterTypes().length;
	}

}
