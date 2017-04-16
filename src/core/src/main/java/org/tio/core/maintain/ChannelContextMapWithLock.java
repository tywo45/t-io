package org.tio.core.maintain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.tio.core.ObjWithLock;
import org.tio.core.intf.Packet;

/**
 * @author tanyaowu
 */
public class ChannelContextMapWithLock<SessionContext, P extends Packet, R>
{

	/** remoteAndChannelContext key: "ip:port" value: ChannelContext. */
	private ObjWithLock<Map<Integer, P>> map = new ObjWithLock<Map<Integer, P>>(new HashMap<Integer, P>());

	/**
	 * Adds the.
	 *
	 * @param channelContext the channel context
	 */
	public void put(Integer synSeq, P packet)
	{
		Lock lock = map.getLock().writeLock();
		try
		{
			lock.lock();
			Map<Integer, P> m = map.getObj();
			m.put(synSeq, packet);
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}

	public P remove(Integer synSeq)
	{
		Lock lock = map.getLock().writeLock();
		try
		{
			lock.lock();
			Map<Integer, P> m = map.getObj();
			P packet = m.remove(synSeq);
			return packet;
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			lock.unlock();
		}
	}
	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public ObjWithLock<Map<Integer, P>> getMap()
	{
		return map;
	}

}
