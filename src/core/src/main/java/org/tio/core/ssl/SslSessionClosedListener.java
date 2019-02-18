package org.tio.core.ssl;

import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.ssl.facade.ISessionClosedListener;

public class SslSessionClosedListener implements ISessionClosedListener {
	private ChannelContext channelContext;

	public SslSessionClosedListener(ChannelContext channelContext) {
		this.channelContext = channelContext;
	}

	@Override
	public void onSessionClosed() {
		//		log.info("{} onSessionClosed", channelContext);
		Tio.close(channelContext, "SSL SessionClosed");
	}

}
