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
	private long revision;

	/**
	 * @param currentTrack
	 */
	public ServerStatus(TrackInfo currentTrack, long revision) {
		this.currentTrack = currentTrack;
		this.revision = revision;
	}

	/**
	 * @return the currentTrack
	 */
	public TrackInfo getCurrentTrack() {
		return currentTrack;
	}

	/**
	 * @return the revision
	 */
	public long getRevision() {
		return revision;
	}

}
