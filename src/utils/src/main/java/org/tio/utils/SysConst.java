package org.tio.utils;

/**
 * @author tanyaowu 
 * 2018年7月1日 下午10:51:59
 */
public interface SysConst {
	String	TIO_URL_GITHUB		= "https://github.com/tywo45/t-io";
	String	TIO_URL_SITE		= "https://www.t-io.org/";
	String	TIO_CORE_VERSION	= "3.2.9.v20190401-RELEASE";
	/**
	 * \r
	 */
	byte	CR					= 13;
	/**
	 * \n
	 */
	byte	LF					= 10;
	/**
	 * =
	 */
	byte	EQ					= '=';
	/**
	 * &
	 */
	byte	AMP					= '&';
	/**
	 * :
	 */
	byte	COL					= ':';
	/**
	 * :
	 */
	String	COL_STR				= ":";
	/**
	 * ;
	 */
	byte	SEMI_COL			= ';';
	/**
	 * 一个空格
	 */
	byte	SPACE				= ' ';
	/**
	 * ?
	 */
	byte	ASTERISK			= '?';
	byte[]	CR_LF_CR_LF			= { CR, LF, CR, LF };
	byte[]	CR_LF				= { CR, LF };
	byte[]	LF_LF				= { LF, LF };
	byte[]	SPACE_				= { SPACE };
	byte[]	CR_					= { CR };
	byte[]	LF_					= { LF };
	/**
	 * \r\n
	 */
	String	CRLF				= "\r\n";
	String	DEFAULT_ENCODING	= "utf-8";
}
