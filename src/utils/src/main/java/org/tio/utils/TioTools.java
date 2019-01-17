package org.tio.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.hutool.FileUtil;
import org.tio.utils.hutool.StrUtil;

public class TioTools {
	

	public static void main(String[] args) throws Exception {
//		changeSiteStyle();//切换网站风格
				newProject();  //新工程
//		rename();
//				tio();         //tio升级
//				addBlankFile("F:\\work\\tio-site\\src\\frontend\\web-all\\starter\\src\\main\\resources\\config\\page");
//		deleteFiles(new String[] { "D:\\work\\tio-im-new", "F:\\gitee\\java" }, new String[] { ".externalToolBuilders" });
		
//		replaceFiles(new File("C:\\Users\\tanyw\\Desktop\\nb830.dic"), new String[] { "D:\\work\\tio-im", "F:\\gitee\\java", "D:\\work\\tio-im(改名前)" });
//		replaceFiles(new File("F:\\work\\ip2region\\data\\ip2region.db"), new String[] { "D:\\work\\tio-im", "F:\\gitee\\java" });

		
		//		AtomicInteger count = new AtomicInteger();
		//		findFile("D:\\work", "g-m-db", count);
		//		System.out.println("共找到" + count + "个文件及目录");
		
//		printFiles();
	}
	
	
	private static Logger log = LoggerFactory.getLogger(TioTools.class);
	private static Set<String> acceptedExt = new HashSet<>();
	static {
		acceptedExt.add("js");
		acceptedExt.add("jsp");
		acceptedExt.add("xml");
		acceptedExt.add("bat");
		acceptedExt.add("sh");
		acceptedExt.add("java");
		acceptedExt.add("properties");
		acceptedExt.add("sql");
		acceptedExt.add("txt");
		acceptedExt.add("log");
		acceptedExt.add("css");
		acceptedExt.add("md");
		acceptedExt.add("form");
		acceptedExt.add("dic");
	}

	/**
	 * 
	 * @author: tanyaowu
	 * @创建时间:　2016年6月29日 下午2:47:09
	 */
	public TioTools() {
	}

	//	@SuppressWarnings("rawtypes")
	//	private static SynThreadPoolExecutor threadExecutor = new SynThreadPoolExecutor(40, 120, "quickstart-thread-pool");

	private static int modifiedCount = 0;

	//	private static int renameCount = 0;
	
	public static void printFiles() {
		//F:\work\tio-site\src\frontend\web-all\starter\src\main\resources\config\page\img\avatar
		
		File f = new File("F:\\work\\tio-site\\src\\frontend\\web-all\\starter\\src\\main\\resources\\config\\page\\img\\avatar");
		File[] list = f.listFiles();
		String str = "{";
		for (File file : list) {
			str += "\"/img/avatar/" + file.getName() + "\",";
		}
		str += "}";
		System.out.println(str);
	}

	/**
	 * 
	 * @param rootDirStr
	 * @param old2newFilename 可以为空或null
	 * @throws IOException
	 */
	public static void rename(String rootDirStr, Map<String, String> old2newFilename) throws IOException {
		//		System.out.println("renameCount:" + renameCount++ + ", " + rootDirStr);
		if (old2newFilename == null || old2newFilename.size() == 0) {
			return;
		}

		File rootDir = new File(rootDirStr);
		File[] files = rootDir.listFiles();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			//			String absolutePath = file.getAbsolutePath();
			String filename = file.getName();
			Set<Entry<String, String>> old2newFilenameSet = old2newFilename.entrySet();
			for (Entry<String, String> entry : old2newFilenameSet) {
				String oldfilenamefragment = entry.getKey();
				String newfilenamefragment = entry.getValue();
				if (filename.contains(oldfilenamefragment)) {
					String newfilename = filename.replaceAll(oldfilenamefragment, newfilenamefragment);
					File newFile = new File(file.getParentFile(), newfilename);
					boolean f = file.renameTo(newFile);
					file = newFile;
					if (f) {
						System.out.println("改名成功，原名:" + filename + ", 新名：" + newfilename + ", " + file.getAbsolutePath());
					} else {
						System.out.println("改名失败，原名:" + filename + ", 新名：" + newfilename + ", " + file.getAbsolutePath());
					}
				}
			}
		}

		rootDir = new File(rootDirStr);
		files = rootDir.listFiles(new MyFileFilter());

		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			if (file.isDirectory()) {
				rename(file.getAbsolutePath(), old2newFilename);
			}
		}

	}

	/**
	 * 
	 * @param rootDirStr
	 * @param old2newStr
	 * @throws IOException
	 */
	public static void replaceStr(String rootDirStr, Map<String, String> old2newStr) throws Exception {
		if (old2newStr == null) {
			return;
		}
		
		File rootDir = new File(rootDirStr);
		File[] files = rootDir.listFiles(new MyFileFilter());

		if (files == null) {
			return;
		}

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String absolutePath = file.getAbsolutePath();
			//			String filename = file.getName();
			//			Set<Entry<String, String>> old2newFilenameSet = old2newFilename.entrySet();

			//			String extension = FilenameUtils.getExtension(file.getName());
			//svn-base
			if (file.isFile() && !"TioTools.java".equals(file.getName())) {
				String filecontent = FileUtil.readUTF8String(file);
				boolean needRewrite = false;
				
				Set<Entry<String, String>> old2newStrSet = old2newStr.entrySet();
				for (Entry<String, String> entry1 : old2newStrSet) {
					String oldstrfragment = entry1.getKey();
					String newstrfragment = entry1.getValue();
					if (filecontent.contains(oldstrfragment)) {
						filecontent = filecontent.replaceAll(oldstrfragment, newstrfragment);
						needRewrite = true;
					}
				}
				
				
				if (needRewrite) {
					FileUtil.writeString(filecontent, file.getCanonicalPath(), "utf-8");//.writeStringToFile(file, filecontent, "utf-8");
					System.out.println(++modifiedCount + "、" + file.getAbsolutePath());
				}

			} else if (file.isDirectory()) {
				replaceStr(absolutePath, old2newStr);
			}
		}
	}

	/**
	 * tio版本升级
	 * 
	 * @author tanyaowu
	 */
	public static void tio() {
		String[] rootDirStrs = new String[] { "F:\\work\\tio-site", "F:\\work\\nb", "D:\\work\\tio-im", "D:\\svn_nb\\base", "G:\\work", "D:\\work\\dts", "D:\\work\\tio-webpack", "D:\\svn_nb\\nbyb", "D:\\work\\t-io",
				 "D:\\work\\tio-start", "F:\\gitee", "D:\\work\\tio-im(改名前)", "F:\\work\\nb" };
		
		String newversion = "3.2.6.v20190110-RELEASE";
		
		Map<String, String> old2newStr = new HashMap<>();
//		Map<String, String> old2newFilename = new HashMap<>();

//		old2newStr.put("3.2.2.v20181122-RELEASE", newversion);
//		old2newStr.put("3.2.3.v20181212-RELEASE", newversion);
		old2newStr.put("3.2.5.v20190101-RELEASE", newversion);
		old2newStr.put("3.2.4.v20181218-RELEASE", newversion);
		
		for (String rootDirStr : rootDirStrs) {
//			old2newFilename.putAll(old2newStr);

			try {
				//如果需要修改文件名字，就在这里调一下rename
				//				rename(rootDirStr, old2newFilename);

				//替换文字
				replaceStr(rootDirStr, old2newStr);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public static void newProject() {
		String[] rootDirStrs = new String[] { "G:\\svn_nb\\html\\etf" };

		for (String rootDirStr : rootDirStrs) {
			Map<String, String> old2newStr = new HashMap<>();
			Map<String, String> old2newFilename = new HashMap<>();

			String oldName = "live";
			String newName = "etf";
			
//			String oldName = "im";
//			String newName = "live";
			
			String splitStr = "-";
			//中划线
			splitStr = "-";
			old2newStr.put("tio" + splitStr + oldName, "tio" + splitStr + newName);
			old2newStr.put("tio" + splitStr + StrUtil.upperFirst(oldName), "tio" + splitStr + StrUtil.upperFirst(newName));
			
			old2newStr.put("Tio" + splitStr + oldName, "Tio" + splitStr + newName);
			old2newStr.put("Tio" + splitStr + StrUtil.upperFirst(oldName), "Tio" + splitStr + StrUtil.upperFirst(newName));
			
			old2newStr.put("TIO" + splitStr + oldName.toUpperCase(), "TIO" + splitStr + newName.toUpperCase());
			
			//下划线
			splitStr = "_";
			old2newStr.put("tio" + splitStr + oldName, "tio" + splitStr + newName);
			old2newStr.put("tio" + splitStr + StrUtil.upperFirst(oldName), "tio" + splitStr + StrUtil.upperFirst(newName));
			
			old2newStr.put("Tio" + splitStr + oldName, "Tio" + splitStr + newName);
			old2newStr.put("Tio" + splitStr + StrUtil.upperFirst(oldName), "Tio" + splitStr + StrUtil.upperFirst(newName));
			
			old2newStr.put("TIO" + splitStr + oldName.toUpperCase(), "TIO" + splitStr + newName.toUpperCase());
			
			//一个空格
			splitStr = " ";
			old2newStr.put("tio" + splitStr + oldName, "tio" + splitStr + newName);
			old2newStr.put("tio" + splitStr + StrUtil.upperFirst(oldName), "tio" + splitStr + StrUtil.upperFirst(newName));
			
			old2newStr.put("Tio" + splitStr + oldName, "Tio" + splitStr + newName);
			old2newStr.put("Tio" + splitStr + StrUtil.upperFirst(oldName), "Tio" + splitStr + StrUtil.upperFirst(newName));
			
			old2newStr.put("TIO" + splitStr + oldName.toUpperCase(), "TIO" + splitStr + newName.toUpperCase());
			
			//空串
			splitStr = "";
			old2newStr.put("tio" + splitStr + oldName, "tio" + splitStr + newName);
			old2newStr.put("tio" + splitStr + StrUtil.upperFirst(oldName), "tio" + splitStr + StrUtil.upperFirst(newName));
			
			old2newStr.put("Tio" + splitStr + oldName, "Tio" + splitStr + newName);
			old2newStr.put("Tio" + splitStr + StrUtil.upperFirst(oldName), "Tio" + splitStr + StrUtil.upperFirst(newName));
			
			
			//特殊处理
			old2newStr.put("TIO_LIVEEI", "TIO_IMEI");
			old2newStr.put("tio_liveei", "tio_imei");
			old2newStr.put("TIO-LIVEEI", "TIO-IMEI");
			old2newStr.put("tio-liveei", "tio-imei");
			
			
			old2newFilename.putAll(old2newStr);

			try {
				//如果需要修改文件名字，就在这里调一下rename
				rename(rootDirStr, old2newFilename);

				//替换文字
				replaceStr(rootDirStr, old2newStr);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	
	/**
	 * 切换网站风格
	 * 
	 * @author tanyaowu
	 */
	public static void changeSiteStyle() {
		String[] rootDirStrs = new String[] { "F:\\work\\tio-site\\src\\frontend\\web-all\\starter\\src\\main\\resources\\config\\page" };

		for (String rootDirStr : rootDirStrs) {
			Map<String, String> old2newStr = new HashMap<>();
			Map<String, String> old2newFilename = new HashMap<>();
			old2newStr.put("#00CC00", "#0000CC");
			old2newStr.put("#0376e0", "#03e076");
			
			old2newStr.put("#0099FF", "#00FF99");
			old2newStr.put("#0c7ac9", "#0cc97a");
			old2newStr.put("#00ff80", "#0080ff");
			old2newStr.put("#e7f5ff", "#e7fff5");
			old2newStr.put("#379ef8", "#37f89e");
			old2newStr.put("#9accf9", "#9af9cc");
			old2newStr.put("#6f6", "#66f");
			old2newStr.put("#108ee9", "#10e98e");
			old2newStr.put("#1E9FFF", "#1EFF9F");
			
			
			//
			old2newStr.put("#00FF99", "#03e076");
			
			
			old2newFilename.putAll(old2newStr);

			try {

				//替换文字
				replaceStr(rootDirStr, old2newStr);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	
	
	public static void rename() {
		String[] rootDirStrs = new String[] {"D:\\work\\tio-im", "F:\\gitee\\java", "D:\\work\\tio-im(改名前)" };

		for (String rootDirStr : rootDirStrs) {
			Map<String, String> old2newFilename = new HashMap<>();
			old2newFilename.put("nb830.dic", "nb919.dic");

			try {
				rename(rootDirStr, old2newFilename);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 用newFile替换文件名相同的文件
	 * @param newFile 新的文件
	 * @param rootDirStrs 要搜索的目录
	 * @throws Exception
	 * @author tanyaowu
	 */
	public static void replaceFiles(File newFile, String[] rootDirStrs) throws Exception {
		byte[] newBytes = FileUtil.readBytes(newFile);
		for (String rootDirStr : rootDirStrs) {
			try {
				replaceFile(newFile, newBytes, rootDirStr);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 用newFile替换文件名相同的文件
	 * @param newFile
	 * @param newBytes
	 * @param rootDirStr
	 * @throws Exception
	 * @author tanyaowu
	 */
	private static void replaceFile(File newFile, byte[] newBytes, String rootDirStr) throws Exception {
		File rootDir = new File(rootDirStr);
		File[] files = rootDir.listFiles();

		if (files.length == 0) {

		} else {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				String absolutePath = file.getAbsolutePath();

				if (file.isDirectory()) {
					replaceFile(newFile, newBytes, absolutePath);
				} else {
					if (file.getName().equals(newFile.getName())) {
						FileUtil.writeBytes(newBytes, file);
						System.out.println(file.getCanonicalPath());
					}
				}
			}
		}
	}

	/**
	 * 给空目录添加一个空白文件
	 * @param rootDirStr
	 */
	public static void addBlankFile(String rootDirStr) {
		File rootDir = new File(rootDirStr);
		File[] files = rootDir.listFiles();

		if (files.length == 0) {
			File blankFile = new File(rootDir, "svn.txt");
			log.warn(blankFile.getAbsolutePath());
			try {
				blankFile.createNewFile();
			} catch (IOException e) {
				log.error(e.toString(), e);
			}
		} else {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				String absolutePath = file.getAbsolutePath();

				if (file.isDirectory()) {
					addBlankFile(absolutePath);
				}
			}
		}
	}

	/**
	 * 
	 * @param rootDirStr
	 * @param searchStr
	 * @param count
	 * @author tanyaowu
	 */
	public static void findFile(String rootDirStr, String searchStr, AtomicInteger count) {
		File rootDir = new File(rootDirStr);
		File[] files = rootDir.listFiles();

		if (files.length == 0) {

		} else {
			for (int i = 0; i < files.length; i++) {
				try {
					File file = files[i];
					String absolutePath = file.getAbsolutePath();

					if (StrUtil.containsAny(file.getName(), searchStr)) {
						if (file.isDirectory()) {
							log.warn("\r\n[dir ]" + file.getAbsolutePath());
						} else {
							log.warn("\r\n[file]" + file.getAbsolutePath());
						}
						count.incrementAndGet();
					}
					if (file.isDirectory()) {
						findFile(absolutePath, searchStr, count);
					}
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
			}
		}
	}

	/**
	 * 删除文件名与filenames中相同的
	 * @param rootDirStrs
	 * @param filenames
	 * @author tanyaowu
	 */
	public static void deleteFiles(String[] rootDirStrs, String[] filenames) {
		for (String rootDirStr : rootDirStrs) {
			try {
				deleteFile(rootDirStr, filenames);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
		}
	}

	/**
	 * 删除文件名与filenames中相同的
	 * @param rootDirStr
	 * @author tanyaowu
	 */
	@SuppressWarnings("unused")
	public static void deleteFile(String rootDirStr, String[] filenames) {
		File rootDir = new File(rootDirStr);
		File[] files = rootDir.listFiles();

		if (files.length == 0) {

		} else {
			lab1: for (int i = 0; i < files.length; i++) {
				try {
					File file = files[i];
					String absolutePath = file.getAbsolutePath();

					lab2: for (int j = 0; j < filenames.length; j++) {
						if (file.getName().equals(filenames[j])) {
							FileUtil.del(file);
							log.warn(file.getAbsolutePath());
							continue lab1;
						}
					}

					if (file.isDirectory()) {
						deleteFile(absolutePath, filenames);
					}
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
			}
		}
	}

	static class MyFileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			//			return true;

			String absolutePath = file.getAbsolutePath();
			String filename = file.getName();
			String extension = FileUtil.extName(filename);//.getExtension(filename);
			if (file.isDirectory()) {
				if (absolutePath.contains("\\webapp\\js") || absolutePath.contains("-app\\nginx\\cache") || absolutePath.contains("nginx\\html\\js") || filename.equals("target")) {
					return false;
				}

				if ("svn-base".equalsIgnoreCase(extension)) {
					return false;
				}

				return true;
			}

			String ext = FileUtil.extName(file);
			if (acceptedExt.contains(ext)) {
				return true;
			}

			return false;
		}

	}
}
