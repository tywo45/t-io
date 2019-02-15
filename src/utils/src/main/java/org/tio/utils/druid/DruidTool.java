/**
 * 
 */
package org.tio.utils.druid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.filter.config.ConfigTools;

/**
 * druid数据源工具
 * @author tanyaowu
 *
 */
public class DruidTool {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(DruidTool.class);

	public static String assertTest(String key) {
		return System.getProperty(key);
	}

	/**
	 * 解码
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static void decrypt() throws Exception {

		java.util.Scanner sc = new java.util.Scanner(System.in);
		int i = 1;
		StringBuilder sb = new StringBuilder();
		sb.append("user guide:\r\n");
		sb.append(i++ + "、input 'exit' to exit the program.\r\n");
		sb.append(i++ + "、input string and then press 'Enter' to get the decrypted string.\r\n");
		sb.append("\r\n");
		sb.append("please input string:\r\n");

		System.out.println(sb);

		String s = sc.nextLine(); // 这个就是用户输入的数据
		while (true) {
			String str1 = ConfigTools.decrypt(s);
			System.out.println("the decrypted value for [" + s + "] is: " + str1 + "\r\n");
			//			System.out.println(com.alibaba.druid.filter.config.ConfigTools.decrypt(str1));

			if ("exit".equalsIgnoreCase(s)) {
				System.out.println("Thanks for using! bye bye.");
				break;
			}

			s = sc.nextLine(); // 这个就是用户输入的数据

		}
	}

	/**
	 * 加密
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static void encrypt() throws Exception {

		java.util.Scanner sc = new java.util.Scanner(System.in);
		int i = 1;
		StringBuilder sb = new StringBuilder();
		sb.append("user guide:\r\n");
		sb.append(i++ + "、input 'exit' to exit the program.\r\n");
		sb.append(i++ + "、input string and then press 'Enter' to get the encrypted password.\r\n");
		sb.append("\r\n");
		sb.append("please input string:\r\n");

		System.out.println(sb);

		String s = sc.nextLine(); // 这个就是用户输入的数据
		while (true) {
			String str1 = ConfigTools.encrypt(s);
			System.out.println("the encrypted value for [" + s + "] is: " + str1 + "\r\n");
			//			System.out.println(com.alibaba.druid.filter.config.ConfigTools.decrypt(str1));

			if ("exit".equalsIgnoreCase(s)) {
				System.out.println("Thanks for using! bye bye.");
				break;
			}

			s = sc.nextLine(); // 这个就是用户输入的数据

		}
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if (args != null && args.length > 0) {
			if ("0".equals(args[0])) {
				encrypt();
			} else {
				decrypt();
			}
		} else {
			encrypt();
		}

	}

}
