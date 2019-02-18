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

/**
 * Implementation of Paranamer which chooses between a series of Paranamer instances depending on which can supply data.
 * It prioritizes the paranamer instances according to the order they were passed in. 
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public class AdaptiveParanamer implements Paranamer {

	public static final String __PARANAMER_DATA = "v1.0 \n"
	        + "org.tio.utils.thoughtworksparanamer.AdaptiveParanamer AdaptiveParanamer org.tio.utils.thoughtworksparanamer.Paranamer,org.tio.utils.thoughtworksparanamer.Paranamer delegate,fallback\n"
	        + "org.tio.utils.thoughtworksparanamer.AdaptiveParanamer AdaptiveParanamer org.tio.utils.thoughtworksparanamer.Paranamer,org.tio.utils.thoughtworksparanamer.Paranamer,org.tio.utils.thoughtworksparanamer.Paranamer delegate,fallback,reserve\n"
	        + "org.tio.utils.thoughtworksparanamer.AdaptiveParanamer AdaptiveParanamer org.tio.utils.thoughtworksparanamer.Paranamer[] paranamers\n"
	        + "org.tio.utils.thoughtworksparanamer.AdaptiveParanamer lookupParameterNames java.lang.AccessibleObject methodOrConstructor \n"
	        + "org.tio.utils.thoughtworksparanamer.AdaptiveParanamer lookupParameterNames java.lang.AccessibleObject,boolean methodOrCtor,throwExceptionIfMissing \n";

	private final Paranamer[] paranamers;

	/**
	 * Use DefaultParanamer ahead of BytecodeReadingParanamer
	 */
	public AdaptiveParanamer() {
		this(new DefaultParanamer(), new BytecodeReadingParanamer());
	}

	/**
	 * Prioritize a series of Paranamers
	 * @param paranamers the paranamers in question
	 */
	public AdaptiveParanamer(Paranamer... paranamers) {
		this.paranamers = paranamers;
	}

	public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
		return lookupParameterNames(methodOrConstructor, true);
	}

	public String[] lookupParameterNames(AccessibleObject methodOrCtor, boolean throwExceptionIfMissing) {
		for (int i = 0; i < paranamers.length; i++) {
			Paranamer paranamer = paranamers[i];
			String[] names = paranamer.lookupParameterNames(methodOrCtor, i + 1 < paranamers.length ? false : throwExceptionIfMissing);
			if (names != Paranamer.EMPTY_NAMES) {
				return names;
			}
		}
		return Paranamer.EMPTY_NAMES;
	}

}