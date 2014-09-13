/**
 * 
 */
package com.jonesgeeks.dacp.pairing;

import org.dyndns.jkiddo.dmcp.chunks.media.PlayingStatus;
import org.dyndns.jkiddo.dmp.chunks.media.LoginResponse;
import org.dyndns.jkiddo.dmp.chunks.media.UpdateResponse;
import org.dyndns.jkiddo.service.daap.client.RequestHelper;

/**
 * @author will
 *
 */
public class Session {

	private final String host;
	private final int port;
	private final int sessionId;

	public Session(final String host, final int port, final String pairingGuid) throws Exception {
		// start a session with the iTunes server
		this.host = host;
		this.port = port;

		final LoginResponse loginResponse = doLogin(pairingGuid);

		sessionId = loginResponse.getSessionId().getValue();
	}

	protected LoginResponse doLogin(final String pairingGuid) throws Exception {
		try {
			return RequestHelper.requestParsed(String.format("%s/login?pairing-guid=0x%s", this.getRequestBase(), pairingGuid));
		} catch( Exception e ) {
			if( e.getMessage().contains("503")) {
				return doLogin(pairingGuid, 0, e);
			}
			throw e;
		}
	}
	
	protected LoginResponse doLogin(final String pairingGuid, int count, final Exception e) throws Exception {
		if( count < 10 ) {
			try {
				Thread.sleep(1000);
				return RequestHelper.requestParsed(String.format("%s/login?pairing-guid=0x%s", this.getRequestBase(), pairingGuid));
			} catch (Exception ex) {
				if(ex.getMessage().contains("503")) {
					doLogin(pairingGuid, ++count, ex);
				} else {
					throw ex;
				}
			}
		}
		throw e;
	}

	protected String getRequestBase() {
		return String.format("http://%s:%d", host, port);
	}

	/**
	 * @return the sessionId
	 */
	public int getSessionId() {
		return sessionId;
	}
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	public void play() throws Exception {
		// http://192.168.254.128:3689/ctrl-int/1/playpause?session-id=130883770
		System.out.println("Playing via: " +String.format("%s/ctrl-int/1/playpause?session-id=%s", getRequestBase(), getSessionId()));
		RequestHelper.dispatch(String.format("%s/ctrl-int/1/playpause?session-id=%s", getRequestBase(), getSessionId()));
	}
	
	public PlayingStatus getNowPlaying() throws Exception {
		// reads the current playing song as a one-item playlist
		// Refactor response into one that looks like a normal items request
		// and trigger listener
		return RequestHelper.requestParsed(String.format("%s/ctrl-int/1/playstatusupdate?revision-number=1&session-id=%s", getRequestBase(), getSessionId(), false));
	}
	
	/**
	 * This call blocks until something happens in iTunes, eg. pushing play.
	 * 
	 * @return
	 * @throws Exception
	 */
	public PlayingStatus getPlayStatusUpdateBlocking() throws Exception
	{
		// try fetching next revision update using socket keepalive
		// approach
		// using the next revision-number will make itunes keepalive
		// until something happens
		// http://192.168.254.128:3689/ctrl-int/1/playstatusupdate?revision-number=1&session-id=1034286700
		return RequestHelper.requestParsed(String.format("%s/ctrl-int/1/playstatusupdate?revision-number=%d&session-id=%s", getRequestBase(), getRevision(), getSessionId()), true);
	}
	
	public long getRevision() throws Exception
	{
		final UpdateResponse state = RequestHelper.requestParsed(String.format("%s/update?session-id=%s&revision-number=%s&delta=0", this.getRequestBase(), sessionId, 1), true);
		long revision = state.getServerRevision().getUnsignedValue();
		return revision;
	}

}
