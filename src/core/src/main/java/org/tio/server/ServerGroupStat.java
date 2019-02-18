package org.tio.server;

import java.util.concurrent.atomic.AtomicLong;

import org.tio.core.stat.GroupStat;

/**
 *
 * @author tanyaowu
 *
 */
public class ServerGroupStat extends GroupStat {

	private static final long	serialVersionUID	= -139100692961946342L;
	/**
	 * 接受了多少连接
	 */
	public final AtomicLong		accepted			= new AtomicLong();

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2016年12月3日 下午2:29:28
	 *
	 */
	public ServerGroupStat() {
	}

	/**
	 * @return the accepted
	 */
	public AtomicLong getAccepted() {
		return accepted;
	}
}
