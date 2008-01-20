package org.newdawn.slick.openal;

import java.io.IOException;

import org.newdawn.slick.util.Log;

/**
 * A sound implementation wrapped round a player which reads (and potentially) rereads
 * a stream. This supplies streaming audio
 *
 * @author kevin
 */
public class StreamSound extends AudioImpl {
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
	 * @see org.newdawn.slick.openal.AudioImpl#isPlaying()
	 */
	public boolean isPlaying() {
		return SoundStore.get().isPlaying(player);
	}

	/**
	 * @see org.newdawn.slick.openal.AudioImpl#playAsMusic(float, float, boolean)
	 */
	public int playAsMusic(float pitch, float gain, boolean loop) {
		try {
			player.setup(pitch, 1.0f);
			SoundStore.get().setMusicVolume(gain);
			player.play(loop);
			SoundStore.get().setStream(player);
		} catch (IOException e) {
			Log.error("Failed to read OGG source: "+player.getSource());
		}
		
		return SoundStore.get().getSource(0);
	}

	/**
	 * @see org.newdawn.slick.openal.AudioImpl#playAsSoundEffect(float, float, boolean, float, float, float)
	 */
	public int playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
		return playAsMusic(pitch, gain, loop);
	}

	/**
	 * @see org.newdawn.slick.openal.AudioImpl#playAsSoundEffect(float, float, boolean)
	 */
	public int playAsSoundEffect(float pitch, float gain, boolean loop) {
		return playAsMusic(pitch, gain, loop);
	}

	/**
	 * @see org.newdawn.slick.openal.AudioImpl#stop()
	 */
	public void stop() {
		SoundStore.get().setStream(null);
	}

}
