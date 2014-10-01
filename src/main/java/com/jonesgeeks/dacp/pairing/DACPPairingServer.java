/**
 * 
 */
package com.jonesgeeks.dacp.pairing;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLLog;

/**
 * @author will
 *
 */
public class DACPPairingServer {
	private static final Logger LOG = FMLLog.getLogger();
	public final static String TOUCH_REMOTE_CLIENT = "_touch-remote._tcp.local.";
	
	private int port = 55471;
	private EventLoopGroup workerGroup = new NioEventLoopGroup();
	private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	private Channel channel;
	private ServiceInfo info;
	private JmDNS mdns;
	private String name = "MC Remote";
	private DACPPairingServerHandler pairingHandler;
	
	public DACPPairingServer(JmDNS mdns) {
		this.mdns = mdns;
		Runtime.getRuntime().addShutdownHook(new JmDNSShutDown(mdns));
		pairingHandler = new DACPPairingServerHandler();
	}
	
	/**
	 * 
	 * @throws InterruptedException
	 */
	public void start() throws InterruptedException {
		if( channel == null || !channel.isOpen() ) {

			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new DACPPairingServerInitializer(pairingHandler));
			
			channel = b.bind(port).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					final Map<String, String> values = new HashMap<String, String>();
					values.put("DvNm", name);
					values.put("RemV", "10000");
					values.put("DvTy", "iPod");
					values.put("RemN", "Remote");
					values.put("txtvers", "1");
					values.put("Pair", "0000000000000001");
					
					info = ServiceInfo.create(TOUCH_REMOTE_CLIENT, toHex(name), port, 0, 0, values);
					mdns.registerService(info);
				}
			}).sync().channel();
			
			channel.closeFuture().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {					
					bossGroup.shutdownGracefully();
					workerGroup.shutdownGracefully();
					channel = null;
				}
			});
		}
	}
	
	/**
	 * @throws InterruptedException 
	 * 
	 */
	public void stop() throws InterruptedException {
		if( channel != null ) {
			channel.close();
			channel.closeFuture().sync();
		} if( mdns != null ) {
			mdns.unregisterAllServices();
		}
	}
	

	
	public static String toHex(final String value) {
		try {
			return toHex(value.getBytes("UTF-8"));
		} catch(final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toHex(final byte[] code) {
		final StringBuilder sb = new StringBuilder();
		for(final byte b : code) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * @param pairingCode the pairingCode to set, must be 4 digits
	 */
	public void setPairingCode(String pairingCode) {
		this.pairingHandler.setPairingCode(pairingCode);
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param listener
	 */
	public void addListener(PairingListener listener) {
		pairingHandler.addListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeListener(PairingListener listener) {
		pairingHandler.removeListener(listener);
	}
	
	/**
	 * 
	 * @author will
	 *
	 */
	class JmDNSShutDown extends Thread {
		private JmDNS jmdns;
		/**
		 * 
		 * @param jmdns
		 */
		public JmDNSShutDown(JmDNS jmdns) {
			this.jmdns = jmdns;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			LOG.info("Shutdown received");
			if( jmdns != null ) {
				jmdns.unregisterAllServices();
			}
		}
	}

}
