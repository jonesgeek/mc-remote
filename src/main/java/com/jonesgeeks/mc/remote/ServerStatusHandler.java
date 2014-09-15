/**
 * 
 */
package com.jonesgeeks.mc.remote;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jonesgeeks.daap.Response;

/**
 * @author will
 *
 */
@Sharable
public class ServerStatusHandler extends SimpleChannelInboundHandler<Response> {
	private static final Logger LOG = LoggerFactory.getLogger(ServerStatusHandler.class);
	
	private Collection<ServerStatusListener> listeners = new ArrayList<>();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Response msg)
			throws Exception {
		Response cmst = msg.getNested("cmst");
		TrackInfo currentTrack = new TrackInfo(cmst.getString("cann"), cmst.getString("canl"), 
				cmst.getString("cana"), cmst.getString("asai"));
		ServerStatus status = new ServerStatus(currentTrack);
		for( ServerStatusListener listener : listeners ) {
			listener.serverSatusReceived(status);
		}
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void addListener(ServerStatusListener listener) {
		this.listeners.add(listener);
	}

}
