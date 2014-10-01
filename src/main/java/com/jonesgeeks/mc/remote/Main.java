/**
 * 
 */
package com.jonesgeeks.mc.remote;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jonesgeeks.dacp.LoginService;
import com.jonesgeeks.dacp.ServiceRegistry;
import com.jonesgeeks.dacp.Session;
import com.jonesgeeks.dacp.TouchableServiceRegistry;
import com.jonesgeeks.dacp.pairing.DACPPairingServer;
import com.jonesgeeks.dacp.pairing.PairingEvent;
import com.jonesgeeks.dacp.pairing.PairingListener;
import com.jonesgeeks.dacp.pairing.PairingLoginService;
import com.jonesgeeks.mc.remote.itunes.ItunesRemote;
import com.jonesgeeks.mc.remote.itunes.status.ServerStatusPoller;

/**
 * @author will
 *
 */
public class Main {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	private final JmDNS mdns;
	private final DACPPairingServer server;
	private final ServiceRegistry registry;
	private PairingLoginService session;
	
	public Main(JmDNS mdns) throws IOException {
		this.mdns = mdns;
		server = new DACPPairingServer(this.mdns);
		registry = new TouchableServiceRegistry(this.mdns);
	}
	
	public void start() throws InterruptedException {
		server.addListener(new PairingListener() {
			
			@Override
			public void pairMatched(PairingEvent event) {
				try {
					ServiceInfo info = registry.get(event.getServiceName());
					if( info != null ) {
						session = new PairingLoginService(info.getInetAddresses()[0].getHostAddress(), info.getPort(),
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


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		JmDNS mdns = JmDNS.create();
		
		DACPPairingServer server = new DACPPairingServer(mdns);
		LoginService loginService = new PairingLoginService(host, port, pairingGuid)
		
		final Remote remote = new ItunesRemote(mdns);
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
