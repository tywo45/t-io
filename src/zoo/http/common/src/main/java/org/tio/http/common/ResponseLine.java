/**
 * 
 */
package org.tio.http.common;

/**
 * @author tanyaowu
 *
 */
public class ResponseLine {
	public String	protocol;
	public String	version;
	public Integer	status;
	public String	desc;
	//	public byte[] bytes;

	//	public static ResponseLine 

	public ResponseLine(String protocol, String version, Integer status, String desc) {
		super();
		this.protocol = protocol;
		this.version = version;
		this.status = status;
		this.desc = desc;

		//		StringBuilder sb = new StringBuilder(32);
		//		sb.append(protocol);
		//		sb.append("/");
		//		sb.append(version);
		//		sb.append(SysConst.SPACE);
		//		sb.append(status);
		//		sb.append(SysConst.SPACE);
		//		sb.append(desc);
		//		this.bytes = sb.toString().getBytes();
	}

}
