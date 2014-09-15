/**
 * 
 */
package com.jonesgeeks.mc.remote;

/**
 * @author will
 *
 */
public class ServerStatus {
	private TrackInfo currentTrack;

	/**
	 * @return the currentTrack
	 */
	public TrackInfo getCurrentTrack() {
		return currentTrack;
	}

	/**
	 * @param currentTrack
	 */
	public ServerStatus(TrackInfo currentTrack) {
		super();
		this.currentTrack = currentTrack;
	}

}
