/**
 * 
 */
package com.jonesgeeks.mc.remote;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jonesgeeks.daap.Response;
import com.jonesgeeks.dacp.pairing.DACPPairingServer;
import com.jonesgeeks.dacp.pairing.PairingEvent;
import com.jonesgeeks.dacp.pairing.PairingListener;
import com.jonesgeeks.dacp.pairing.ServiceRegistry;
import com.jonesgeeks.dacp.pairing.Session;

/**
 * @author will
 *
 */
public class ITunesRemote {
	private static final Logger LOG = LoggerFactory.getLogger(ITunesRemote.class);
	public final static String TOUCH_ABLE_SERVER = "_touch-able._tcp.local.";
	
	private final JmDNS mdns;
	private final DACPPairingServer server;
	private final ServiceRegistry registry;
	private Session session;
	
	public ITunesRemote(JmDNS mdns) throws IOException {
		this.mdns = mdns;
		server = new DACPPairingServer(this.mdns);
		registry = new ServiceRegistry(this.mdns, TOUCH_ABLE_SERVER);
	}
	
	public void start() throws InterruptedException {
		server.addListener(new PairingListener() {
			
			@Override
			public void pairMatched(PairingEvent event) {
				try {
					ServiceInfo info = registry.get(event.getServiceName());
					if( info != null ) {
						session = new Session(info.getInetAddresses()[0].getHostAddress(), info.getPort(),
							event.getCode());

					} else {
						LOG.error("Remote hasn't shown up yet");
					}
				} catch (Exception e) {
					throw new RuntimeException("AAAAaaaahhhh", e);
				}
			}
		});

		server.start();
	}
	
	public void play() throws Exception {
		if( session != null ) {
			session.play();
		} else {
			throw new Exception("session not available yet.");
		}
	}
	
	public void pause() throws Exception {
		play();
	}
	
	public void stop() throws InterruptedException {
		server.stop();
	}
	
	public void status(ServerStatusListener listener) {
		ServerStatusRequest status = new ServerStatusRequest(session.getHost(), session.getPort(), session.getSessionId());
		status.addListener(listener);
		try {
			status.getStatus();
		} catch (InterruptedException | URISyntaxException e) {
			e.printStackTrace();
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		JmDNS mdns = JmDNS.create();
		final ITunesRemote remote = new ITunesRemote(mdns);
		remote.start();

		LOG.info("Press any key to exit");
		Scanner s = new Scanner(System.in);
		boolean keepReading = true;
		String command;
		while(keepReading) {
			command = s.next();
			try {
				Op op = Op.valueOf(command.toUpperCase());
				
				switch(op) {
				case PLAY :
				case PAUSE :
					remote.play();
					break;
				case STATUS :
					remote.status(new ServerStatusListener() {
						
						@Override
						public void serverSatusReceived(ServerStatus status) {
							try {
								TrackInfo track = status.getCurrentTrack();
								LOG.info("Song: " + track.getName());
								LOG.info("Album: " + track.getAlbum());
								LOG.info("Artist: " + track.getArtist());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					break;
				case EXIT :
				case QUIT :
					keepReading = false;
					break;
				}
			} catch(Throwable t) {
				LOG.warn("Operation not supported: " + command);
			}
		}
		LOG.info("Exit command received, shutting down");
		s.close();
		remote.stop();
		mdns.close();
		System.exit(0);
	}
	
	private enum Op {
		PLAY, PAUSE, STATUS, EXIT, QUIT;
	}

}
