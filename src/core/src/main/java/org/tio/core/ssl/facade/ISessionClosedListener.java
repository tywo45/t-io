package org.tio.core.ssl.facade;

/**
 * Monitors end of session notifications
 */
public interface ISessionClosedListener {
	void onSessionClosed();
}
