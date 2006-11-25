package org.newdawn.slick;

import org.newdawn.slick.openal.InternalSound;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.Log;

/**
 * A single sound effect loaded from either OGG or XM/MOD file
 *
 * @author kevin
 */
public class Sound {
	/** The internal sound effect represent this sound */
	private InternalSound sound;
	
	/**
	 * Create a new Sound 
	 * 
	 * @param ref The location of the OGG or MOD/XM to load
	 * @throws SlickException Indicates a failure to load the sound effect
	 */
	public Sound(String ref) throws SlickException {
		SoundStore.get().init();
		
		try {
			if (ref.toLowerCase().endsWith(".ogg")) {
				sound = SoundStore.get().getOgg(ref);
			} else if (ref.toLowerCase().endsWith(".wav")) {
				sound = SoundStore.get().getWAV(ref);
			} else if (ref.toLowerCase().endsWith(".xm") || ref.toLowerCase().endsWith(".mod")) {
				sound = SoundStore.get().getMOD(ref);
			} else {
				throw new SlickException("Only .xm, .mod and .ogg are currently supported.");
			}
		} catch (Exception e) {
			Log.error(e);
			throw new SlickException("Failed to load sound: "+ref);
		}
	}
	
	/**
	 * Play this sound effect at default volume and pitch
	 */
	public void play() {
		play(1.0f,1.0f);
	}
	
	/**
	 * Play this sound effect at a given volume and pitch
	 * 
	 * @param pitch The pitch to play the sound effect at
	 * @param volume The volumen to play the sound effect at
	 */
	public void play(float pitch, float volume) {
		sound.playAsSoundEffect(pitch, volume, false);
	}

	/**
	 * Loop this sound effect at default volume and pitch
	 */
	public void loop() {
		loop(1.0f,1.0f);
	}
	
	/**
	 * Loop this sound effect at a given volume and pitch
	 * 
	 * @param pitch The pitch to play the sound effect at
	 * @param volume The volumen to play the sound effect at
	 */
	public void loop(float pitch, float volume) {
		sound.playAsSoundEffect(pitch, volume, true);
	}
	
	/**
	 * Check if the sound is currently playing
	 * 
	 * @return True if the sound is playing
	 */
	public boolean playing() {
		return sound.isPlaying();
	}
	
	/**
	 * Stop the sound being played
	 */
	public void stop() {
		sound.stop();
	}
}
