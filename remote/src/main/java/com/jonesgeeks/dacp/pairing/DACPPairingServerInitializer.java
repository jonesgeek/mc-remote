package com.jonesgeeks.dacp.pairing;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class DACPPairingServerInitializer extends ChannelInitializer<SocketChannel> {
	private ChannelInboundHandler pairingHandler;

	/**
	 * @param pairingHandler
	 */
	public DACPPairingServerInitializer(ChannelInboundHandler pairingHandler) {
		this.pairingHandler = pairingHandler;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		p.addLast(new HttpServerCodec());
		p.addLast(pairingHandler);
	}

}
