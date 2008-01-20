package org.newdawn.slick.openal;

/**
 * A sound that can be played through OpenAL
 * 
 * @author Kevin Glass
 */
public class AudioImpl implements Audio {
	/** The store from which this sound was loaded */
	private SoundStore store;
	/** The buffer containing the sound */
	private int buffer;
	/** The index of the source being used to play this sound */
	private int index = -1;
	
	/**
	 * Create a new sound
	 * 
	 * @param store The sound store from which the sound was created
	 * @param buffer The buffer containing the sound data
	 */
	AudioImpl(SoundStore store, int buffer) {
		this.store = store;
		this.buffer = buffer;
	}
	
	/**
	 * Get the ID of the OpenAL buffer holding this data (if any). This method
	 * is not valid with streaming resources.
	 * 
	 * @return The ID of the OpenAL buffer holding this data 
	 */
	public int getBufferID() {
		return buffer;
	}
	
	/**
	 *
	 */
	protected AudioImpl() {
		
	}
	
	/**
	 * @see org.newdawn.slick.openal.Audio#stop()
	 */
	public void stop() {
		if (index != -1) {
			store.stopSource(index);
		}
	}
	
	/**
	 * @see org.newdawn.slick.openal.Audio#isPlaying()
	 */
	public boolean isPlaying() {
		if (index != -1) {
			return store.isPlaying(index);
		}
		
		return false;
	}
	
	/**
	 * @see org.newdawn.slick.openal.Audio#playAsSoundEffect(float, float, boolean)
	 */
	public int playAsSoundEffect(float pitch, float gain, boolean loop) {
		index = store.playAsSound(buffer, pitch, gain, loop);
		return store.getSource(index);
	}


	/**
	 * @see org.newdawn.slick.openal.Audio#playAsSoundEffect(float, float, boolean, float, float, float)
	 */
	public int playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
		index = store.playAsSoundAt(buffer, pitch, gain, loop, x, y, z);
		return store.getSource(index);
	}
	
	/**
	 * @see org.newdawn.slick.openal.Audio#playAsMusic(float, float, boolean)
	 */
	public int playAsMusic(float pitch, float gain, boolean loop) {
		store.playAsMusic(buffer, pitch, gain, loop);
		index = 0;
		return store.getSource(0);
	}
	
	/**
	 * Pause the music currently being played
	 */
	public static void pauseMusic() {
		SoundStore.get().pauseLoop();
	}

	/**
	 * Restart the music currently being paused
	 */
	public static void restartMusic() {
		SoundStore.get().restartLoop();
	}
}
