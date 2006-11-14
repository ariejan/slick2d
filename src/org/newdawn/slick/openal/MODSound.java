package org.newdawn.slick.openal;

import ibxm.Module;
import ibxm.OpenALMODPlayer;

import java.io.IOException;
import java.io.InputStream;

/**
 * A sound as a MOD file - can only be played as music
 * 
 * @author Kevin Glass
 */
public class MODSound extends InternalSound {
	/** The MOD play back system */
	private static OpenALMODPlayer player = new OpenALMODPlayer();
	
	/** The source to play back on */
	private int source;
	/** The module to play back */
	private Module module;
	/** The sound store this belongs to */
	private SoundStore store;
	
	/**
	 * Create a mod sound to be played back 
	 * 
	 * @param store The store this sound belongs to 
	 * @param source The source to play back on
	 * @param in The input stream to read the data from
	 * @throws IOException Indicates a failure to load a sound
	 */
	public MODSound(SoundStore store, int source, InputStream in) throws IOException {
		this.source = source;
		this.store = store;
		
		module = OpenALMODPlayer.loadModule(in);
	}
	
	/**
	 * @see org.newdawn.slick.openal.InternalSound#playAsMusic(float, float, boolean)
	 */
	public void playAsMusic(float pitch, float gain, boolean loop) {
		player.play(module, source, loop, SoundStore.get().isMusicOn());
		player.setup(pitch, gain);
		
		store.setMOD(this);
	}
	
	/**
	 * Poll the streaming on the MOD
	 */
	public void poll() {
		player.update();
	}
	
	/**
	 * @see org.newdawn.slick.openal.InternalSound#playAsSoundEffect(float, float)
	 */
	public void playAsSoundEffect(float pitch, float gain) {
	}
}
