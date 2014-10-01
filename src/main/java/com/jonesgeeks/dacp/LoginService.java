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
	 * @return a future session
	 */
	public Future<Session> login( );
	
}
