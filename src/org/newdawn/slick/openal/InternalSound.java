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
	 * Play this sound as a sound effect
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 */
	public void playAsSoundEffect(float pitch, float gain) {
		store.playAsSound(buffer, pitch, gain);
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
