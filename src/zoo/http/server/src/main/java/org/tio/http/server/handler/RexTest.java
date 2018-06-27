/**
 * 
 */
package org.tio.http.server.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tanyaowu
 *
 */
public class RexTest {

	/**
	 * 
	 */
	public RexTest() {
		// TODO Auto-generated constructor stub
	}

	public static String getMatcher(String regex, String source) {
		String result = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			result = matcher.group(1);//只取第一组  
		}
		return result;
	}

	public static void main(String[] arg) {
		//		String splitReqString = "uid=923933544/sid=DFS32DSFS";
		//		String uid = getMatcher("uid=([\\d]+)", splitReqString);
		//		String sid = getMatcher("sid=([0-9a-zA-Z]+)", splitReqString);
		//		
		//		System.out.println(uid);
		//		System.out.println(sid);

		String str = "/user/{1234}";
		String regx = "\\{(.*?)\\}";
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			System.out.println(matcher.group(1));
		}

		
		
		
		String str1 = "/user/{userid}";
		String regx0 = "(\\{[^/]*?\\})";
		String regx1 = "(\\{[^/]*?\\})";
		Pattern pattern1 = Pattern.compile(regx1);
		Matcher matcher1 = pattern1.matcher(str1);
		while (matcher1.find()) {
			System.out.println(matcher1.group(1));
		}

	}

}
