package org.tio.core.ssl.facade;

/** By default do nothing on the close event.
 *
 */
public class DefaultOnCloseListener implements ISessionClosedListener {
	@Override
	public void onSessionClosed() {
	}
}
