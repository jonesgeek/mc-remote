/**
 * 
 */
package com.jonesgeeks.dacp;

import com.jonesgeeks.dacp.Session;

import io.netty.util.concurrent.Future;

/**
 * @author will
 *
 */
public interface LoginService {
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param pairingGuid
	 * @return
	 */
	public Future<Session> login(final String host, final int port, final String pairingGuid);
	
}
