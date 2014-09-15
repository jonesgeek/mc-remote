package com.jonesgeeks.mc.remote;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;

import com.jonesgeeks.daap.DAAPDecoder;

public class ServerStatusInitializer extends ChannelInitializer<SocketChannel> {
	private ChannelInboundHandler statusHandler;

	/**
	 * @param statusHandler
	 */
	public ServerStatusInitializer(ChannelInboundHandler statusHandler) {
		this.statusHandler = statusHandler;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline()
			.addLast(new HttpClientCodec())
			.addLast(new DAAPDecoder())
			.addLast(statusHandler);
	}

}
