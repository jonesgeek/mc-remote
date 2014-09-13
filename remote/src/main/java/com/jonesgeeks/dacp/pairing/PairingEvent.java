/**
 * 
 */
package com.jonesgeeks.dacp.pairing;

import java.net.InetSocketAddress;

/**
 * @author will
 *
 */
public class PairingEvent {
	private String serviceName;
	private String code;
	private InetSocketAddress address;

	/**
	 * @param serviceName
	 * @param code
	 * @param address
	 */
	public PairingEvent(String serviceName, String code,
			InetSocketAddress address) {
		this.serviceName = serviceName;
		this.code = code;
		this.address = address;
	}
	
	/**
	 * @return the address
	 */
	public InetSocketAddress getAddress() {
		return address;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

}
