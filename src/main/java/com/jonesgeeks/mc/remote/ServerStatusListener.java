/**
 * 
 */
package com.jonesgeeks.mc.remote;

import com.jonesgeeks.daap.Response;

/**
 * @author will
 *
 */
public interface ServerStatusListener {
	
	public void serverSatusReceived(Response response);

}
