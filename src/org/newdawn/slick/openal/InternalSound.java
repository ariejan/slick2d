package org.newdawn.slick.openal;

/**
 * A sound that can be played through OpenAL
 * 
 * @author Kevin Glass
 */
public class InternalSound {
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
	InternalSound(SoundStore store, int buffer) {
		this.store = store;
		this.buffer = buffer;
	}
	
	/**
	 *
	 */
	protected InternalSound() {
		
	}
	
	/**
	 * Stop the sound effect
	 */
	public void stop() {
		if (index != -1) {
			store.stopSource(index);
		}
	}
	
	/**
	 * Check if the sound is playing as sound fx
	 * 
	 * @return True if the sound is playing
	 */
	public boolean isPlaying() {
		if (index != -1) {
			return store.isPlaying(index);
		}
		
		return false;
	}
	
	/**
	 * Play this sound as a sound effect
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @param loop True if we should loop
	 */
	public void playAsSoundEffect(float pitch, float gain, boolean loop) {
		index = store.playAsSound(buffer, pitch, gain, loop);
	}


	/**
	 * Play this sound as a sound effect
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @param loop True if we should loop
	 * @param x The x position of the sound
	 * @param y The y position of the sound
	 * @param z The z position of the sound
	 */
	public void playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
		index = store.playAsSoundAt(buffer, pitch, gain, loop, x, y, z);
	}
	
	/**
	 * Play this sound as music
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @param loop True if we should loop
	 */
	public void playAsMusic(float pitch, float gain, boolean loop) {
		store.playAsMusic(buffer, pitch, gain, loop);
		index = 0;
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
