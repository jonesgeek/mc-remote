/**
 * 
 */
package com.jonesgeeks.mc.remote;

/**
 * @author will
 *
 */
public interface Remote {
	/**
	 * Plays the current song
	 */
	public void play();
	
	/**
	 * Pause the currently playing song
	 */
	public void pause();
	
	/**
	 * Change to the next song
	 */
	public void next();
	
	/**
	 * Change to the previous song
	 */
	public void previous();
	
	/**
	 * 
	 * @return
	 */
	public TrackInfo nowPlaying();
	
	/**
	 * 
	 * @param listener
	 */
	public void addServerStatusChangeListener(ServerStatusListener listener);

}
