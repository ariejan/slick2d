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
public class MODSound extends AudioImpl {
	/** The MOD play back system */
	private static OpenALMODPlayer player = new OpenALMODPlayer();
	
	/** The module to play back */
	private Module module;
	/** The sound store this belongs to */
	private SoundStore store;
	
	/**
	 * Create a mod sound to be played back 
	 * 
	 * @param store The store this sound belongs to 
	 * @param in The input stream to read the data from
	 * @throws IOException Indicates a failure to load a sound
	 */
	public MODSound(SoundStore store, InputStream in) throws IOException {
		this.store = store;
		module = OpenALMODPlayer.loadModule(in);
	}
	
	/**
	 * @see org.newdawn.slick.openal.AudioImpl#playAsMusic(float, float, boolean)
	 */
	public int playAsMusic(float pitch, float gain, boolean loop) {
		player.play(module, store.getSource(0), loop, SoundStore.get().isMusicOn());
		player.setup(pitch, 1.0f);
		store.setMusicVolume(gain);
		
		store.setMOD(this);
		
		return store.getSource(0);
	}
	
	/**
	 * Poll the streaming on the MOD
	 */
	public void poll() {
		player.update();
	}
	
	/**
	 * @see org.newdawn.slick.openal.AudioImpl#playAsSoundEffect(float, float, boolean)
	 */
	public int playAsSoundEffect(float pitch, float gain, boolean loop) {
		return -1;
	}

	public void stop() {
		store.setMOD(null);
	}
}
