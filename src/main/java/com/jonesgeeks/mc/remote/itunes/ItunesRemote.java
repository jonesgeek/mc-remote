/**
 * 
 */
package com.jonesgeeks.mc.remote.itunes;

import java.util.ArrayList;
import java.util.List;

import com.jonesgeeks.dacp.Session;
import com.jonesgeeks.mc.remote.Remote;
import com.jonesgeeks.mc.remote.ServerStatusListener;
import com.jonesgeeks.mc.remote.TrackInfo;
import com.jonesgeeks.mc.remote.itunes.status.ServerStatusPoller;

/**
 * @author will
 *
 */
public class ItunesRemote implements Remote{
	
	private final Session session;
	private List<ServerStatusListener> statusListeners;
	private ServerStatusPoller poller;

	/**
	 * 
	 */
	public ItunesRemote(Session session) {
		this.session = session;
		statusListeners = new ArrayList<>();
		poller = new ServerStatusPoller(session, statusListeners);
	}

	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.mc.remote.Remote#play()
	 */
	@Override
	public void play() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.mc.remote.Remote#pause()
	 */
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.mc.remote.Remote#next()
	 */
	@Override
	public void next() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.mc.remote.Remote#previous()
	 */
	@Override
	public void previous() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.mc.remote.Remote#nowPlaying()
	 */
	@Override
	public TrackInfo nowPlaying() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.mc.remote.Remote#addServerStatusChangeListener(com.jonesgeeks.mc.remote.status.ServerStatusListener)
	 */
	@Override
	public void addServerStatusChangeListener(ServerStatusListener listener) {
		statusListeners.add(listener);
	}

}
