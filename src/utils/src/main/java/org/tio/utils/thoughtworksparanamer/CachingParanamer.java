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
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of Paranamer which delegate to another Paranamer implementation,
 * adding caching functionality to speed up usage. It also uses a WeakHashmap as
 * an implementation detail (wrapped in Collections.synchronizedMap(..)), to allow
 * large usages to garbage collect things as big as whole classloaders (after
 * working through all the refs that originated from that classloader). Tomcat and
 * other 'containers' do this during hot application deployment, undeployment and
 * most importantly for Paranamer redeployment.  Basically, this will allow a
 * perm-gen usage keeps growing scenario.
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public class CachingParanamer implements Paranamer {

	public static final String __PARANAMER_DATA = "v1.0 \n"
	        + "org.tio.utils.thoughtworksparanamer.CachingParanamer <init> org.tio.utils.thoughtworksparanamer.Paranamer delegate \n"
	        + "org.tio.utils.thoughtworksparanamer.CachingParanamer lookupParameterNames java.lang.AccessibleObject methodOrConstructor \n"
	        + "org.tio.utils.thoughtworksparanamer.CachingParanamer lookupParameterNames java.lang.AccessibleObject, boolean methodOrCtor,throwExceptionIfMissing \n";

	private final Paranamer delegate;

	private final Map<AccessibleObject, String[]> methodCache = makeMethodCache();

	protected Map<AccessibleObject, String[]> makeMethodCache() {
		return Collections.synchronizedMap(new WeakHashMap<AccessibleObject, String[]>());
	}

	/**
	 * Uses a DefaultParanamer as the implementation it delegates to.
	 */
	public CachingParanamer() {
		this(new DefaultParanamer());
	}

	/**
	 * Specify a Paranamer instance to delegates to.
	 * @param delegate the paranamer instance to use
	 */
	public CachingParanamer(Paranamer delegate) {
		this.delegate = delegate;
	}

	public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
		return lookupParameterNames(methodOrConstructor, true);
	}

	public String[] lookupParameterNames(AccessibleObject methodOrCtor, boolean throwExceptionIfMissing) {
		String[] names = methodCache.get(methodOrCtor);
		// refer PARANAMER-19
		if (names == null) {
			names = delegate.lookupParameterNames(methodOrCtor, throwExceptionIfMissing);
			methodCache.put(methodOrCtor, names);
		}
		return names;
	}

	/**
	 * This implementation has a better concurrent design (ConcurrentHashMap) which
	 * has a better strategy to implement concurrency: segments instead of synchronized.
	 *
	 * It also drops the underlying WeakHashMap implementation as that can't work with
	 * ConcurrentHashMap with some risk of growing permgen for a certain class of usage.
	 *
	 * So instead of wrapping via 'Collections.synchronizedMap(new WeakHashMap())' we now
	 * have 'new ConcurrentHashMap()'
	 *
	 */
	public static class WithoutWeakReferences extends CachingParanamer {

		public WithoutWeakReferences() {
		}

		public WithoutWeakReferences(Paranamer delegate) {
			super(delegate);
		}

		@Override
		protected Map<AccessibleObject, String[]> makeMethodCache() {
			return new ConcurrentHashMap<AccessibleObject, String[]>();
		}
	}

}
