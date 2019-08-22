package org.tio.utils;

import java.nio.charset.Charset;

/**
 * @author tanyaowu 
 * 2018年7月1日 下午10:51:59
 */
public interface SysConst {
	String	TIO_CORE_VERSION		= "3.5.0.v20190822-RELEASE";
	String	TIO_URL_GITEE			= "https://gitee.com/tywo45/t-io";
	String	TIO_URL_GITHUB			= "https://github.com/tywo45/t-io";
	String	TIO_URL_SITE			= "https://www.t-io.org";
	String	CHECK_LASTVERSION_URL_1	= TIO_URL_SITE + "/mytio/open/lastVersion1.tio_x?v=" + TIO_CORE_VERSION;
	String	CHECK_LASTVERSION_URL_2	= TIO_URL_SITE + "/mytio/open/lastVersion2.tio_x?id=";
	/**
	 * 空串
	 */
	String	BLANK					= "";
	/**
	 * \r
	 */
	byte	CR						= 13;
	/**
	 * \n
	 */
	byte	LF						= 10;
	/**
	 * =
	 */
	byte	EQ						= '=';
	/**
	 * =
	 */
	String	STR_EQ					= "=";
	/**
	 * &
	 */
	byte	AMP						= '&';
	/**
	 * &
	 */
	String	STR_AMP					= "&";
	/**
	 * :
	 */
	byte	COL						= ':';
	/**
	 * :
	 */
	String	STR_COL					= ":";
	/**
	 * ;
	 */
	byte	SEMI_COL				= ';';
	/**
	 * 一个空格
	 */
	byte	SPACE					= ' ';
	/**
	 * 左括号
	 */
	byte	LEFT_BRACKET			= '(';
	/**
	 * 右括号
	 */
	byte	RIGHT_BRACKET			= ')';
	/**
	 * ?
	 */
	byte	ASTERISK				= '?';
	byte[]	CR_LF_CR_LF				= { CR, LF, CR, LF };
	byte[]	CR_LF					= { CR, LF };
	byte[]	LF_LF					= { LF, LF };
	byte[]	SPACE_					= { SPACE };
	byte[]	CR_						= { CR };
	byte[]	LF_						= { LF };
	byte[]	NULL					= { 'n', 'u', 'l', 'l' };
	/**
	 * \r\n
	 */
	String	CRLF					= "\r\n";
	String	DEFAULT_ENCODING		= "utf-8";

	Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_ENCODING);
}
