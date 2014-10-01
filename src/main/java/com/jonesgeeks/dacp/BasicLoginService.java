/**
 * 
 */
package com.jonesgeeks.dacp;

import io.netty.util.concurrent.Future;

/**
 * @author will
 *
 */
public class BasicLoginService implements LoginService {
	private String host;
	private int port;
	private int sessionId;

	/**
	 * 
	 */
	public BasicLoginService() {
		
	}

	/**
	 * @param host
	 * @param port
	 * @param sessionId
	 */
	public BasicLoginService(String host, int port, int sessionId) {
		this.host = host;
		this.port = port;
		this.sessionId = sessionId;
	}

	/* (non-Javadoc)
	 * @see com.jonesgeeks.dacp.LoginService#login()
	 */
	@Override
	public Future<Session> login() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected Session login(String baseUri, int sessionId) {
		
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 * @return this instance
	 */
	public BasicLoginService setHost(String host) {
		this.host = host;
		return this;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 * @return this instance
	 */
	public BasicLoginService setPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * @return the sessionId
	 */
	public int getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 * @return this instance
	 */
	public BasicLoginService setSessionId(int sessionId) {
		this.sessionId = sessionId;
		return this;
	}

}
