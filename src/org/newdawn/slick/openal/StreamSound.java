package org.newdawn.slick.openal;

import java.io.IOException;

import org.newdawn.slick.util.Log;

/**
 * A sound implementation wrapped round a player which reads (and potentially) rereads
 * a stream. This supplies streaming audio
 *
 * @author kevin
 */
public class StreamSound extends InternalSound {
	/** The player we're going to ask to stream data */
	private OpenALStreamPlayer player;
	
	/**
	 * Create a new sound wrapped round a stream
	 * 
	 * @param player The stream player we'll use to access the stream
	 */
	public StreamSound(OpenALStreamPlayer player) {
		this.player = player;
	}
	
	/**
	 * @see org.newdawn.slick.openal.InternalSound#isPlaying()
	 */
	public boolean isPlaying() {
		return SoundStore.get().isPlaying(player);
	}

	/**
	 * @see org.newdawn.slick.openal.InternalSound#playAsMusic(float, float, boolean)
	 */
	public void playAsMusic(float pitch, float gain, boolean loop) {
		try {
			player.setup(pitch, gain);
			player.play(loop);
			SoundStore.get().setStream(player);
		} catch (IOException e) {
			Log.error("Failed to read OGG source");
		}
	}

	/**
	 * @see org.newdawn.slick.openal.InternalSound#playAsSoundEffect(float, float, boolean, float, float, float)
	 */
	public void playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
		playAsMusic(pitch, gain, loop);
	}

	/**
	 * @see org.newdawn.slick.openal.InternalSound#playAsSoundEffect(float, float, boolean)
	 */
	public void playAsSoundEffect(float pitch, float gain, boolean loop) {
		playAsMusic(pitch, gain, loop);
	}

	/**
	 * @see org.newdawn.slick.openal.InternalSound#stop()
	 */
	public void stop() {
		SoundStore.get().setStream(null);
	}

}
