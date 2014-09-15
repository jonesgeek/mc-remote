/**
 * 
 */
package com.jonesgeeks.mc.remote;


/**
 * Preliminary model for the track info.
 * 
 * @author will
 *
 */
public class TrackInfo {
	private String name;
	private String album;
	private String artist;
	private String albumId;
	
	/**
	 * @param name
	 * @param album
	 * @param artist
	 * @param albumId
	 */
	public TrackInfo(String name, String album, String artist, String albumId) {
		this.name = name;
		this.album = album;
		this.artist = artist;
		this.albumId = albumId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @return the albumId
	 */
	public String getAlbumId() {
		return albumId;
	}
	
	
}
