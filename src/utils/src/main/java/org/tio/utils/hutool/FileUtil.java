package org.tio.utils.hutool;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

	/** 类Unix路径分隔符 */
	private static final char UNIX_SEPARATOR = '/';
	/** Windows路径分隔符 */
	private static final char WINDOWS_SEPARATOR = '\\';

	/**
	 * 获取文件扩展名，扩展名不带“.”
	 * 
	 * @param file 文件
	 * @return 扩展名
	 */
	public static String extName(File file) {
		if (null == file) {
			return null;
		}
		if (file.isDirectory()) {
			return null;
		}
		return extName(file.getName());
	}

	/**
	 * 获得文件的扩展名，扩展名不带“.”
	 * 
	 * @param fileName 文件名
	 * @return 扩展名
	 */
	public static String extName(String fileName) {
		if (fileName == null) {
			return null;
		}
		int index = fileName.lastIndexOf(".");
		if (index == -1) {
			return StrUtil.EMPTY;
		} else {
			String ext = fileName.substring(index + 1);
			// 扩展名中不能包含路径相关的符号
			return (ext.contains(String.valueOf(UNIX_SEPARATOR)) || ext.contains(String.valueOf(WINDOWS_SEPARATOR))) ? StrUtil.EMPTY : ext;
		}
	}

	/**
	 * @param data
	 * @param file
	 * @author tanyaowu
	 * @throws IOException 
	 */
	public static void writeBytes(byte[] data, File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}

		//获取全路径
		String canonicalPath = file.getCanonicalPath();
		//通过Files获取文件的输出流
		OutputStream fos = null;
		try {
			fos = Files.newOutputStream(Paths.get(canonicalPath));
			fos.write(data);
			fos.flush();
		} finally {
			if (fos != null) {
				fos.close();
			}
			fos.close();
		}
	}

	/**
	 * 
	 * @param content
	 * @param path
	 * @param charset
	 * @author tanyaowu
	 * @throws IOException 
	 */
	public static void writeString(String content, String path, String charset) throws IOException {
		byte[] data = content.getBytes(charset);
		File file = new File(path);
		writeBytes(data, file);
	}

	/**
	 * 清空文件夹<br>
	 * 注意：清空文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
	 * 某个文件删除失败会终止删除操作
	 * 
	 * @param directory 文件夹
	 * @return 成功与否
	 * @throws IORuntimeException IO异常
	 * @since 3.0.6
	 */
	public static boolean clean(File directory) throws Exception {
		if (directory == null || directory.exists() == false || false == directory.isDirectory()) {
			return true;
		}

		final File[] files = directory.listFiles();
		for (File childFile : files) {
			boolean isOk = del(childFile);
			if (isOk == false) {
				// 删除一个出错则本次删除任务失败
				return false;
			}
		}
		return true;
	}

	/**
	 * 删除文件或者文件夹<br>
	 * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
	 * 某个文件删除失败会终止删除操作
	 * 
	 * @param file 文件对象
	 * @return 成功与否
	 * @throws IORuntimeException IO异常
	 */
	public static boolean del(File file) throws Exception {
		if (file == null || false == file.exists()) {
			return false;
		}

		if (file.isDirectory()) {
			clean(file);
		}
		try {
			Files.delete(file.toPath());
		} catch (IOException e) {
			throw new Exception(e);
		}
		return true;
	}

	public static byte[] readBytes(File file) throws Exception {
		Path fileLocation = file.toPath();//Paths.get(file);
		byte[] data = Files.readAllBytes(fileLocation);
		return data;
	}

	public static String readString(File file) throws Exception {
		byte[] data = readBytes(file);
		return new String(data);
	}
	
	public static String readUTF8String(File file) throws Exception {
		byte[] data = readBytes(file);
		return new String(data, "utf-8");
	}
}
