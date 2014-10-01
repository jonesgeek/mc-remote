/**
 * 
 */
package com.jonesgeeks.mc.remote.itunes.status;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jonesgeeks.daap.Response;
import com.jonesgeeks.mc.remote.ServerStatus;
import com.jonesgeeks.mc.remote.ServerStatusListener;
import com.jonesgeeks.mc.remote.TrackInfo;

/**
 * @author will
 *
 */
@Sharable
public class ServerStatusHandler extends SimpleChannelInboundHandler<Response> {
	private static final Logger LOG = LoggerFactory.getLogger(ServerStatusHandler.class);
	
	private final Collection<ServerStatusListener> listeners;
	
	/**
	 * 
	 * @param listeners
	 */
	public ServerStatusHandler(final Collection<ServerStatusListener> listeners) {
		this.listeners = listeners;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Response msg)
			throws Exception {
		Response cmst = msg.getNested("cmst");
		TrackInfo currentTrack = new TrackInfo(cmst.getString("cann"), cmst.getString("canl"), 
				cmst.getString("cana"), cmst.getString("asai"), cmst.getString("cang"));
		ServerStatus status = new ServerStatus(currentTrack, cmst.getNumberLong("cmsr"));
		
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
