/**
 * 
 */
package com.jonesgeeks.dacp;

/**
 * @author will
 *
 */
public class Session {
	private final String host;
	private final int port;
	private final int sessionId;
	
	/**
	 * @param host
	 * @param port
	 * @param sessionId
	 */
	public Session(String host, int port, int sessionId) {
		super();
		this.host = host;
		this.port = port;
		this.sessionId = sessionId;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the sessionId
	 */
	public int getSessionId() {
		return sessionId;
	}
}
