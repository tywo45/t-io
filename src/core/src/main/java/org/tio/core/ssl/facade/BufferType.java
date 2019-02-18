package org.tio.core.ssl.facade;

public enum BufferType {
    /* Defines names for SSL related buffers. */

	/**
	IN_PLAIN is called peerAppData in Java docs and represents the original
	unencrypted data that the peer sent. This buffer is the destination
	buffer for an unwrap operation.
	*/
	IN_PLAIN,

	/**
	IN_CIPHER is called peerNetData in Java docs and represents the
	encrypted data that the peer sent. This buffer is the source buffer for
	an unwrap operation
	*/
	IN_CIPHER,

	/**
	OUT_PLAIN is called myAppData in Java docs and represents the plain
	data that the host app wishes to transmit. This buffer is the source
	buffer for a wrap operation.
	*/
	OUT_PLAIN,

	/**
	 OUT_CIPHER is called myNetData in Java docs and represents the
	 encrypted data that the hosts transmits. This buffer is the destination
	 buffer for a wrap operation.
	 */
	OUT_CIPHER
}
