package org.newdawn.slick.openal;

import java.io.IOException;

import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.Log;

/**
 * A sound implementation that can load the actual sound file at a later 
 * point.
 *
 * @author kevin
 */
public class DeferredSound extends InternalSound implements DeferredResource {
	/** Indicate a OGG to be loaded */
	public static final int OGG = 1;
	/** Indicate a WAV to be loaded */
	public static final int WAV = 2;
	/** Indicate a MOD/XM to be loaded */
	public static final int MOD = 3;
	/** Indicate a AIF to be loaded */
	public static final int AIF = 4;
	
	/** The type of sound to be loader */
	private int type;
	/** The location of the sound this proxy wraps */
	private String ref;
	/** The loaded sound if it's already been brought up */
	private InternalSound target;
	
	/**
	 * Create a new sound on request to load
	 * 
	 * @param ref The location of the sound to load
	 * @param type The type of sound to load
	 */
	public DeferredSound(String ref, int type) {
		this.ref = ref;
		this.type = type;
		
		LoadingList.get().add(this);
	}

	/**
	 * Check if the target has already been loaded
	 */
	private void checkTarget() {
		if (target == null) {
			throw new RuntimeException("Attempt to use deferred sound before loading");
		}
	}
	
	/**
	 * @see org.newdawn.slick.loading.DeferredResource#load()
	 */
	public void load() throws IOException {
		boolean before = TextureLoader.get().isDeferredLoading();
		SoundStore.get().setDeferredLoading(false);
		switch (type) {
		case OGG:
			target = SoundStore.get().getOgg(ref);
			break;
		case WAV:
			target = SoundStore.get().getWAV(ref);
			break;
		case MOD:
			target = SoundStore.get().getMOD(ref);
			break;
		case AIF:
			target = SoundStore.get().getAIF(ref);
			break;
		default:
			Log.error("Unrecognised sound type: "+type);
			break;
		}
		SoundStore.get().setDeferredLoading(before);
	}

	/**
	 * @see org.newdawn.slick.openal.InternalSound#isPlaying()
	 */
	public boolean isPlaying() {
		checkTarget();
		
		return target.isPlaying();
	}

	/**
	 * @see org.newdawn.slick.openal.InternalSound#playAsMusic(float, float, boolean)
	 */
	public void playAsMusic(float pitch, float gain, boolean loop) {
		checkTarget();
		target.playAsMusic(pitch, gain, loop);
	}

	/**
	 * @see org.newdawn.slick.openal.InternalSound#playAsSoundEffect(float, float, boolean)
	 */
	public void playAsSoundEffect(float pitch, float gain, boolean loop) {
		checkTarget();
		target.playAsSoundEffect(pitch, gain, loop);
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
		checkTarget();
		target.playAsSoundEffect(pitch, gain, loop, x, y, z);
	}
	
	/**
	 * @see org.newdawn.slick.openal.InternalSound#stop()
	 */
	public void stop() {
		checkTarget();
		target.stop();
	}

	/**
	 * @see org.newdawn.slick.loading.DeferredResource#getDescription()
	 */
	public String getDescription() {
		return ref;
	}

}
