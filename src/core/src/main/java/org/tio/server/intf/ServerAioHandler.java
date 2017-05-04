package org.tio.server.intf;

import org.tio.core.intf.AioHandler;
import org.tio.core.intf.Packet;

/**
 * 
 * @author tanyaowu 
 *
 */
public interface ServerAioHandler<SessionContext, P extends Packet, R> extends AioHandler<SessionContext, P, R> {

}
