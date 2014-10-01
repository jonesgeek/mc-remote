/**
 * 
 */
package com.jonesgeeks.dacp.pairing;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLLog;

/**
 * @author will
 *
 */
@Sharable
public class DACPPairingServerHandler extends SimpleChannelInboundHandler<Object> {
	private static final Logger LOG = FMLLog.getLogger();
	private final static byte[] CHAR_TABLE = new byte[] { (byte) '0',
		(byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
		(byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'A',
		(byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };
	
	private MessageDigest md5;
	private Random random = new Random();
	private String pairingCode = "1234";
	private Collection<PairingListener> listeners = new ArrayList<PairingListener>();

	public DACPPairingServerHandler() {
		super();
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		addListener(new PairingListener() {
			
			@Override
			public void pairMatched(PairingEvent event) {
				LOG.info("Event received for pairing: serviceName: " + event.getServiceName() + ", niceCode: " + event.getCode());
			}
		});
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			if( req.getUri().toLowerCase().startsWith("/pair") ) {
				QueryStringDecoder decoder = new QueryStringDecoder(req.getUri());
				Map<String, List<String>> params = decoder.parameters();
				if( params != null && params.containsKey("pairingcode") && params.containsKey("servicename") ) {
					String pairingCode = params.get("pairingcode").get(0);
					String serviceName = params.get("servicename").get(0);
					String expectedPairingCode = expectedPairingCode(this.pairingCode);
					
					InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
					
					LOG.info("Address: " + address + ", pairingcode: " + pairingCode + ", expectedCode: " + expectedPairingCode + " servicename: " + serviceName);
					
					if (serviceName != null && pairingCode != null && pairingCode.equals(expectedPairingCode)) {
	
						// edit our local PAIRING_RAW to return the correct guid
						byte[] loginGuid = new byte[8];
						random.nextBytes(loginGuid);
						byte[] pairingRaw = getPairingRaw();
						System.arraycopy(loginGuid, 0, pairingRaw, 16, 8);
						final String niceCode = toHex(loginGuid);
						
	
						PairingEvent event = new PairingEvent(serviceName, niceCode, address);
	
						ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
	
						FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(pairingRaw));
						response.headers().set(CONTENT_TYPE, "text/plain");
						response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
	
						ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE).channel().closeFuture().addListener(new CloseChannelListener(event));
	
					} else {
						LOG.info("Wrong pairing code");
						ctx.writeAndFlush(new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND)).addListener(ChannelFutureListener.CLOSE);
					}
				}
			}
		} else {
			ctx.writeAndFlush(new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND)).addListener(ChannelFutureListener.CLOSE);
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
	public void addListener(PairingListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeListener(PairingListener listener) {
		this.listeners.remove(listener);
	}
	
	private String expectedPairingCode(String code) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write("0000000000000001".getBytes("UTF-8"));

			byte passcode[] = code.getBytes("UTF-8");
			for (int c=0; c<passcode.length; c++) {
				os.write(passcode[c]);
				os.write(0);
			}

			return toHex(md5.digest(os.toByteArray()));
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage());
			return "";
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return "";
		}
	}
	
	/**
	 * @param pairingCode the pairingCode to set, must be 4 digits
	 */
	public void setPairingCode(String pairingCode) {
		if(!pairingCode.matches("\\d{4}")) {
			throw new NumberFormatException("The pairing code \"" + pairingCode + "\" must be a 4-digit code.");
		}
		this.pairingCode = pairingCode;
	}
	
	public static String toHex(byte[] code) {
		// somewhat borrowed from rgagnon.com
		byte[] result = new byte[2 * code.length];
		int index = 0;
		for (byte b : code) {
			int v = b & 0xff;
			result[index++] = CHAR_TABLE[v >>> 4];
			result[index++] = CHAR_TABLE[v & 0xf];
		}
		return new String(result);
	}
	
	protected byte[] getPairingRaw() {
		return new byte[] { 0x63, 0x6d, 0x70, 0x61,
				0x00, 0x00, 0x00, 0x3a, 0x63, 0x6d, 0x70, 0x67, 0x00, 0x00, 0x00,
				0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x63, 0x6d,
				0x6e, 0x6d, 0x00, 0x00, 0x00, 0x16, 0x41, 0x64, 0x6d, 0x69, 0x6e,
				0x69, 0x73, 0x74, 0x72, 0x61, 0x74, 0x6f, 0x72, (byte) 0xe2,
				(byte) 0x80, (byte) 0x99, 0x73, 0x20, 0x69, 0x50, 0x6f, 0x64, 0x63,
				0x6d, 0x74, 0x79, 0x00, 0x00, 0x00, 0x04, 0x69, 0x50, 0x6f, 0x64 };
	}
	
	public class CloseChannelListener implements ChannelFutureListener {
		private PairingEvent event;
		
		public CloseChannelListener(PairingEvent event) {
			this.event = event;
		}

		/*
		 * (non-Javadoc)
		 * @see io.netty.util.concurrent.GenericFutureListener#operationComplete(io.netty.util.concurrent.Future)
		 */
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			for(PairingListener listener : listeners) {
				listener.pairMatched(event);
			}
		}
		
	}

}
