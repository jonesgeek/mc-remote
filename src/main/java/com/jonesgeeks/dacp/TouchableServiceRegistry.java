/**
 * 
 */
package com.jonesgeeks.dacp;

import javax.jmdns.JmDNS;

/**
 * @author will
 *
 */
public class TouchableServiceRegistry extends ServiceRegistry {
	public final static String TOUCH_ABLE_SERVER = "_touch-able._tcp.local.";

	/**
	 * @param mdns
	 */
	public TouchableServiceRegistry(JmDNS mdns) {
		super(mdns, TOUCH_ABLE_SERVER);
	}

}
