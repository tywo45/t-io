/*
 * Copyright 2013 Samuel Halliday
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *	notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *	notice, this list of conditions and the following disclaimer in the
 *	documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *	contributors may be used to endorse or promote products derived from
 *	this software without specific prior written permission.
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

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Accesses Javadocs to extract parameter names. Supports:-
 * <ul>
 * <li>Javadoc in zip file</li>
 * <li>Javadoc in directory</li>
 * <li>Javadoc at remote URL</li>
 * </ul>
 *
 * @author Samuel Halliday
 */
public class JavadocParanamer implements Paranamer {

	protected interface JavadocProvider {
		InputStream getRawJavadoc(String canonicalClassName) throws IOException;
	}

	private final JavadocProvider provider;

	/**
	 * @param archiveOrDirectory either a zip archive or base directory of Javadocs.
	 * @throws FileNotFoundException if the parameter or <code>package-list</code> cannot be found.
	 */
	public JavadocParanamer(File archiveOrDirectory) throws IOException {
		if (!archiveOrDirectory.exists())
			throw new FileNotFoundException(archiveOrDirectory.getAbsolutePath());
		if (archiveOrDirectory.isDirectory())
			provider = new DirJavadocProvider(archiveOrDirectory);
		else if (archiveOrDirectory.isFile())
			provider = new ZipJavadocProvider(archiveOrDirectory);
		else
			throw new IllegalArgumentException("neither file nor directory: " + archiveOrDirectory);
	}

	/**
	 * @param url base URL of the JavaDocs
	 * @throws FileNotFoundException if the url does not have a <code>/package-list</code>
	 */
	public JavadocParanamer(URL url) throws IOException {
		this.provider = new UrlJavadocProvider(url);
	}

	public String[] lookupParameterNames(AccessibleObject accessible) {
		return lookupParameterNames(accessible, true);
	}

	public String[] lookupParameterNames(AccessibleObject accessible, boolean throwExceptionIfMissing) {
		if (!(accessible instanceof Member))
			throw new IllegalArgumentException(accessible.getClass().getCanonicalName());
		try {
			String javadocFilename = getJavadocFilename((Member) accessible);
			InputStream stream = provider.getRawJavadoc(javadocFilename);
			String raw = streamToString(stream);

			if (accessible instanceof Method)
				return getMethodParameterNames((Method) accessible, raw);
			else if (accessible instanceof Constructor<?>)
				return getConstructorParameterNames((Constructor<?>) accessible, raw);
			else
				throw new IllegalArgumentException(accessible.getClass().getCanonicalName());
		} catch (IOException e) {
			if (throwExceptionIfMissing)
				throw new ParameterNamesNotFoundException(accessible.toString(), e);
			else
				return Paranamer.EMPTY_NAMES;
		} catch (ParameterNamesNotFoundException e) {
			if (throwExceptionIfMissing)
				throw e;
			else
				return Paranamer.EMPTY_NAMES;
		}
	}

	private String[] getConstructorParameterNames(Constructor<?> cons, String raw) {
		return getParameterNames(cons, cons.getDeclaringClass().getSimpleName(), cons.getParameterTypes(), raw);
	}

	private String[] getMethodParameterNames(Method method, String raw) {
		return getParameterNames(method, method.getName(), method.getParameterTypes(), raw);
	}

	/* Some example patterns
	 *
	 * File#listFiles(FileFilter filter)
	 * File(File parent, String child)
	 * Collections#containsAll(Collection<?> c)
	 * =================================
	 * Java 4, 5 & 6
	 * -------------
	 * <CODE><B><A HREF="../../java/io/File.html#listFiles(java.io.FileFilter)">listFiles</A></B>(<A HREF="../../java/io/FileFilter.html" title="interface in java.io">FileFilter</A>&nbsp;filter)</CODE>
	 * <CODE><B><A HREF="../../java/io/File.html#File(java.io.File, java.lang.String)">File</A></B>(<A HREF="../../java/io/File.html" title="class in java.io">File</A>&nbsp;parent, <A HREF="../../java/lang/String.html" title="class in java.lang">String</A>&nbsp;child)</CODE>
	 *
	 * Java 4
	 * ------
	 * <CODE><B><A HREF="../../java/util/Collection.html#containsAll(java.util.Collection)">containsAll</A></B>(<A HREF="../../java/util/Collection.html" title="interface in java.util">Collection</A>&nbsp;c)</CODE>
	 *
	 * Java 5 & 6
	 * ----------
	 * <CODE><B><A HREF="../../java/util/Collection.html#containsAll(java.util.Collection)">containsAll</A></B>(<A HREF="../../java/util/Collection.html" title="interface in java.util">Collection</A>&lt;?&gt;&nbsp;c)</CODE>
	 *
	 * Java 7
	 * ------
	 * <code><strong><a href="../../java/io/File.html#listFiles(java.io.FileFilter)">listFiles</a></strong>(<a href="../../java/io/FileFilter.html" title="interface in java.io">FileFilter</a>&nbsp;filter)</code>
	 * <code><strong><a href="../../java/io/File.html#File(java.io.File, java.lang.String)">File</a></strong>(<a href="../../java/io/File.html" title="class in java.io">File</a>&nbsp;parent, <a href="../../java/lang/String.html" title="class in java.lang">String</a>&nbsp;child)</code>
	 * <code><strong><a href="../../java/util/Collection.html#containsAll(java.util.Collection)">containsAll</a></strong>(<a href="../../java/util/Collection.html" title="interface in java.util">Collection</a>&lt;?&gt;&nbsp;c)</code>
	 */
	private String[] getParameterNames(AccessibleObject a, String name, Class<?>[] types, String raw) {
		if (types.length == 0)
			return new String[0];

		StringBuilder regex = new StringBuilder();
		regex.append(format(">\\Q%s\\E</A></(?:B|strong)>\\(", name));
		for (Class<?> klass : types) {
			regex.append(format(",?\\s*(?:<A[^>]+>)?[\\w.]*\\Q%s\\E(?:</A>)?(?:&lt;[^&]+&gt;)?&nbsp;([^),\\s]+)", klass.getSimpleName()));
		}
		regex.append(format("\\)</CODE>"));

		Pattern pattern = Pattern.compile(regex.toString(), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(raw);

		if (!matcher.find())
			throw new ParameterNamesNotFoundException(a + ", " + regex);

		String[] names = new String[types.length];
		for (int i = 0; i < names.length; i++)
			names[i] = matcher.group(1 + i).trim();

		return names;
	}

	//////////// CONVENIENCE METHODS ////////////
	// to keep dependencies light, we don't have Guava or http-client

	protected static String getJavadocFilename(Member member) {
		return getCanonicalName(member.getDeclaringClass()).replace('.', '/') + ".html";
	}

	protected static String getCanonicalName(Class<?> klass) {
		// doesn't support names of nested classes
		if (klass.isArray())
			return getCanonicalName(klass.getComponentType()) + "[]";
		return klass.getName();
	}

	protected static String streamToString(InputStream input) throws IOException {
		InputStreamReader reader = new InputStreamReader(input, "UTF-8");
		BufferedReader buffered = new BufferedReader(reader);
		try {
			String line;
			StringBuilder builder = new StringBuilder();
			while ((line = buffered.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}
			return builder.toString();
		} finally {
			buffered.close();
		}
	}

	protected static InputStream urlToStream(URL url) throws IOException {
		URLConnection conn = url.openConnection();
		conn.connect();
		return conn.getInputStream();
	}

	//////////// Provider Implementations ////////////

	protected static class ZipJavadocProvider implements JavadocProvider {
		private final ZipFile zip;

		public ZipJavadocProvider(File file) throws IOException {
			zip = new ZipFile(file);
			find("package-list");
		}

		private ZipEntry find(String postfix) throws FileNotFoundException {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.endsWith(postfix))
					return entry;
			}
			throw new FileNotFoundException(postfix);
		}

		public InputStream getRawJavadoc(String fqn) throws IOException {
			ZipEntry entry = find(fqn);
			return zip.getInputStream(entry);
		}
	}

	protected static class UrlJavadocProvider implements JavadocProvider {
		private final URL base;

		public UrlJavadocProvider(URL base) throws IOException {
			this.base = base;
			streamToString(urlToStream(new URL(base + "/package-list")));
		}

		public InputStream getRawJavadoc(String fqn) throws IOException {
			return urlToStream(new URL(base + "/" + fqn));
		}
	}

	protected static class DirJavadocProvider implements JavadocProvider {
		private final File dir;

		public DirJavadocProvider(File dir) throws IOException {
			this.dir = dir;
			if (!new File(dir, "package-list").exists())
				throw new FileNotFoundException("package-list");
		}

		public InputStream getRawJavadoc(String fqn) throws IOException {
			File file = new File(dir, fqn);
			return new FileInputStream(file);
		}
	}

}
