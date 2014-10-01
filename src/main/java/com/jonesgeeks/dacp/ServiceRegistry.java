/**
 * 
 */
package com.jonesgeeks.dacp;

import java.util.concurrent.ConcurrentHashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLLog;

/**
 * @author will
 *
 */
@SuppressWarnings("serial")
public class ServiceRegistry extends ConcurrentHashMap<String, ServiceInfo>
		implements ServiceListener {
	private static final Logger LOG = FMLLog.getLogger();
	
	public ServiceRegistry(JmDNS mdns, String serviceType) {
		mdns.addServiceListener(serviceType, this);
	}

	/* (non-Javadoc)
	 * @see javax.jmdns.ServiceListener#serviceAdded(javax.jmdns.ServiceEvent)
	 */
	@Override
	public void serviceAdded(ServiceEvent event) {
		ServiceInfo info = event.getDNS().getServiceInfo(event.getType(), event.getName());
		LOG.info("Service Added: " + info);
		this.put(info.getName(), info);
	}

	/* (non-Javadoc)
	 * @see javax.jmdns.ServiceListener#serviceRemoved(javax.jmdns.ServiceEvent)
	 */
	@Override
	public void serviceRemoved(ServiceEvent event) {
		LOG.info("Service removed: " + event);
		this.remove(event.getInfo().getName());
	}

	/* (non-Javadoc)
	 * @see javax.jmdns.ServiceListener#serviceResolved(javax.jmdns.ServiceEvent)
	 */
	@Override
	public void serviceResolved(ServiceEvent event) {
		
	}

}
