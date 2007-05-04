package org.newdawn.slick.openal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Responsible for holding and playing the sounds used in the game.
 * 
 * @author Kevin Glass
 */
public class SoundStore {
	/** The single instance of this class */
	private static SoundStore store = new SoundStore();
	
	/** True if sound effects are turned on */
	private boolean sounds;
	/** True if music is turned on */
	private boolean music;
	/** True if sound initialisation succeeded */
	private boolean soundWorks;
	/** The number of sound sources enabled - default 8 */
	private int sourceCount;
	/** The map of references to IDs of previously loaded sounds */
	private HashMap loaded = new HashMap();
	/** The ID of the buffer containing the music currently being played */
	private int currentMusic = -1;
	/** The OpenGL AL sound sources in use */
	private IntBuffer sources;
	/** The next source to be used for sound effects */
	private int nextSource;
	/** True if the sound system has been initialise */
	private boolean inited = false;
	/** The MODSound to be updated */
	private MODSound mod;
	/** The stream to be updated */
	private OpenALStreamPlayer stream;
	
	/** The global music volume setting */
	private float musicVolume = 1.0f;
	/** The global sound fx volume setting */
	private float soundVolume = 1.0f;
	/** The last "gain" applied to music */
	private float lastMusicGain = 1.0f;

	/** True if the music is paused */
	private boolean paused;
	/** True if we're returning deferred versions of resources */
	private boolean deferred;
	
	/** The buffer used to set the velocity of a source */
    private FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
    /** The buffer used to set the position of a source */
    private FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3);
    
	/**
	 * Create a new sound store
	 */
	private SoundStore() {
	}
	
	/**
	 * Clear out the sound store contents
	 */
	public void clear() {
		store = new SoundStore();
	}

    /**
     * True if we should only record the request to load in the intention
     * of loading the sound later
     * 
     * @param deferred True if the we should load a token
     */
    public void setDeferredLoading(boolean deferred) {
    	this.deferred = deferred;
    }
    
    /**
     * Check if we're using deferred loading
     * 
     * @return True if we're loading deferred sounds
     */
    public boolean isDeferredLoading() {
    	return deferred;
    }
    
	/**
	 * Inidicate whether music should be playing
	 * 
	 * @param music True if music should be played
	 */
	public void setMusicOn(boolean music) {
		if (soundWorks) {
			this.music = music;
			if (music) {
				restartLoop();
			} else {
				pauseLoop();
			}
		}
	}
	
	/**
	 * Check if music should currently be playing
	 * 
	 * @return True if music is currently playing
	 */
	public boolean isMusicOn() {
		return music;
	}

	/**
	 * Set the music volume
	 * 
	 * @param volume The volume for music
	 */
	public void setMusicVolume(float volume) {
		if (volume < 0) {
			volume = 0;
		}
		musicVolume = volume;
		
		if (soundWorks) {
			if (volume == 0) {
				volume = 0.001f;
			}
			AL10.alSourcef(sources.get(0), AL10.AL_GAIN, lastMusicGain * volume); 
		}
	}
	
	/**
	 * Set the sound volume
	 * 
	 * @param volume The volume for sound fx
	 */
	public void setSoundVolume(float volume) {
		if (volume < 0) {
			volume = 0;
		}
		soundVolume = volume;
	}
	
	/**
	 * Check if sound works at all
	 * 
	 * @return True if sound works at all
	 */
	public boolean soundWorks() {
		return soundWorks;
	}
	
	/**
	 * Check if music is currently enabled
	 * 
	 * @return True if music is currently enabled
	 */
	public boolean musicOn() {
		return music;
	}

	/**
	 * Get the volume for sounds
	 * 
	 * @return The volume for sounds
	 */
	public float getSoundVolume() {
		return soundVolume;
	}
	
	/**
	 * Get the volume for music
	 * 
	 * @return The volume for music
	 */
	public float getMusicVolume() {
		return musicVolume;
	}
	
	/**
	 * Get the ID of a given source
	 * 
	 * @param index The ID of a given source
	 * @return The ID of the given source
	 */
	public int getSource(int index) {
		return sources.get(index);
	}
	
	/**
	 * Indicate whether sound effects should be played 
	 * 
	 * @param sounds True if sound effects should be played
	 */
	public void setSoundsOn(boolean sounds) {
		if (soundWorks) {
			this.sounds = sounds;
		}
	}
	
	/**
	 * Check if sound effects are currently enabled
	 * 
	 * @return True if sound effects are currently enabled
	 */
	public boolean soundsOn() {
		return sounds;
	}
	
	/**
	 * Initialise the sound effects stored. This must be called
	 * before anything else will work
	 */
	public void init() {
		if (inited) {
			return;
		}
		Log.info("Initialising sounds..");
		inited = true;
		
		AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
				try {
					AL.create();
					soundWorks = true;
					sounds = true;
					music = true;
					Log.info("- Sound works");
				} catch (Exception e) {
					Log.error("Sound initialisation failure.");
					Log.error(e);
					soundWorks = false;
					sounds = false;
					music = false;
				}
				
				return null;
            }});
		
		if (soundWorks) {
			sourceCount = 8;
			sources = BufferUtils.createIntBuffer(sourceCount);
			AL10.alGenSources(sources);
			
			if (AL10.alGetError() != AL10.AL_NO_ERROR) {
				sounds = false;
				music = false;
				soundWorks = false;
				Log.error("- AL init failed");
			} else {
				FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6).put(
						new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f });
				FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(
						new float[] { 0.0f, 0.0f, 0.0f });
				FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(
						new float[] { 0.0f, 0.0f, 0.0f });
				listenerPos.flip();
				listenerVel.flip();
				listenerOri.flip();
				AL10.alListener(AL10.AL_POSITION, listenerPos);
				AL10.alListener(AL10.AL_VELOCITY, listenerVel);
				AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
   			 
				Log.info("- Sounds source generated");
			}
		}
	}

	/**
	 * Stop a particular sound source
	 * 
	 * @param index The index of the source to stop
	 */
	void stopSource(int index) {
		AL10.alSourceStop(sources.get(index));
	}
	
	/**
	 * Play the specified buffer as a sound effect with the specified
	 * pitch and gain.
	 * 
	 * @param buffer The ID of the buffer to play
	 * @param pitch The pitch to play at
	 * @param gain The gain to play at
	 * @param loop True if the sound should loop
	 * @return source The source that will be used
	 */
	int playAsSound(int buffer,float pitch,float gain,boolean loop) {
		return playAsSoundAt(buffer, pitch, gain, loop, 0, 0, 0);
	}
	
	/**
	 * Play the specified buffer as a sound effect with the specified
	 * pitch and gain.
	 * 
	 * @param buffer The ID of the buffer to play
	 * @param pitch The pitch to play at
	 * @param gain The gain to play at
	 * @param loop True if the sound should loop
	 * @param x The x position to play the sound from
	 * @param y The y position to play the sound from
	 * @param z The z position to play the sound from
	 * @return source The source that will be used
	 */
	int playAsSoundAt(int buffer,float pitch,float gain,boolean loop,float x, float y, float z) {
		gain *= soundVolume;
		if (gain == 0) {
			gain = 0.001f;
		}
		if (soundWorks) {
			if (sounds) {
				int nextSource = findFreeSource();
				if (nextSource == -1) {
					return -1;
				}
				
				AL10.alSourceStop(sources.get(nextSource));
				
				AL10.alSourcei(sources.get(nextSource), AL10.AL_BUFFER, buffer);
				AL10.alSourcef(sources.get(nextSource), AL10.AL_PITCH, pitch);
				AL10.alSourcef(sources.get(nextSource), AL10.AL_GAIN, gain); 
			    AL10.alSourcei(sources.get(nextSource), AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
			    
			    sourcePos.clear();
			    sourceVel.clear();
				sourceVel.put(new float[] { 0, 0, 0 });
				sourcePos.put(new float[] { x, y, z });
			    sourcePos.flip();
			    sourceVel.flip();
			    AL10.alSource(sources.get(nextSource), AL10.AL_POSITION, sourcePos);
    			AL10.alSource(sources.get(nextSource), AL10.AL_VELOCITY, sourceVel);
			    
				AL10.alSourcePlay(sources.get(nextSource)); 
				
				return nextSource;
			}
		}
		
		return -1;
	}
	/**
	 * Check if a particular source is playing
	 * 
	 * @param index The index of the source to check
	 * @return True if the source is playing
	 */
	boolean isPlaying(int index) {
		int state = AL10.alGetSourcei(sources.get(index), AL10.AL_SOURCE_STATE);
		
		return (state == AL10.AL_PLAYING);
	}
	
	/**
	 * Find a free sound source
	 * 
	 * @return The index of the free sound source
	 */
	private int findFreeSource() {
		for (int i=1;i<sourceCount-1;i++) {
			int state = AL10.alGetSourcei(sources.get(i), AL10.AL_SOURCE_STATE);
			
			if (state != AL10.AL_PLAYING) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Play the specified buffer as music (i.e. use the music channel)
	 * 
	 * @param buffer The buffer to be played
	 * @param pitch The pitch to play the music at
	 * @param gain The gaing to play the music at
	 * @param loop True if we should loop the music
	 */
	void playAsMusic(int buffer,float pitch,float gain, boolean loop) {
		lastMusicGain = gain;
		paused = false;
		
		gain *= musicVolume;
		if (gain == 0) {
			gain = 0.001f;
		}
		setMOD(null);
		
		if (soundWorks) {
			if (currentMusic != -1) {
				AL10.alSourceStop(sources.get(0));
			}
			
			getMusicSource();
			
			AL10.alSourcei(sources.get(0), AL10.AL_BUFFER, buffer);
			AL10.alSourcef(sources.get(0), AL10.AL_PITCH, pitch);
			AL10.alSourcef(sources.get(0), AL10.AL_GAIN, gain); 
		    AL10.alSourcei(sources.get(0), AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
			
			currentMusic = sources.get(0);
			
			if (!music) {
				pauseLoop();
			} else {
				AL10.alSourcePlay(sources.get(0)); 
			}
		}
	}
	
	/**
	 * Get the OpenAL source used for music
	 * 
	 * @return The open al source used for music
	 */
	public int getMusicSource() {
		IntBuffer deleteMe = BufferUtils.createIntBuffer(1);
		deleteMe.put(sources.get(0));
		deleteMe.flip();
		AL10.alDeleteSources(deleteMe);
		
		IntBuffer musicChannel = BufferUtils.createIntBuffer(1);
		AL10.alGenSources(musicChannel);
		sources.put(0,musicChannel.get(0));
		
		return sources.get(0);
	}
	
	/**
	 * Set the pitch at which the current music is being played
	 * 
	 * @param pitch The pitch at which the current music is being played
	 */
	public void setMusicPitch(float pitch) {
		if (soundWorks) {
			AL10.alSourcef(sources.get(0), AL10.AL_PITCH, pitch);
		}
	}
	
	/**
	 * Pause the music loop that is currently playing
	 */
	public void pauseLoop() {
		if ((soundWorks) && (currentMusic != -1)){
			paused = true;
			AL10.alSourcePause(currentMusic);
		}
	}

	/**
	 * Restart the music loop that is currently paused
	 */
	public void restartLoop() {
		if ((music) && (soundWorks) && (currentMusic != -1)){
			paused = false;
			AL10.alSourcePlay(currentMusic);
		}
	}
	
	/**
	 * Check if the supplied player is currently being polled by this
	 * sound store.
	 * 
	 * @param player The player to check
	 * @return True if this player is currently in use by this sound store
	 */
	boolean isPlaying(OpenALStreamPlayer player) {
		return stream == player;
	}
	
	/**
	 * Get a MOD sound (mod/xm etc)
	 * 
	 * @param ref The refernece to the mod to load
	 * @return The sound for play back 
	 * @throws IOException Indicates a failure to read the data
	 */
	public InternalSound getMOD(String ref) throws IOException {
		if (!soundWorks) {
			return new InternalSound(this, 0);
		}
		if (!inited) {
			throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
		}
		if (deferred) {
			return new DeferredSound(ref, DeferredSound.MOD);
		}
		Log.info("Loading: "+ref);
		
		return new MODSound(this, ResourceLoader.getResourceAsStream(ref));
	}
	
	/**
	 * Get the Sound based on a specified AIF file
	 * 
	 * @param ref The reference to the AIF file in the classpath
	 * @return The Sound read from the AIF file
	 * @throws IOException Indicates a failure to load the AIF
	 */
	public InternalSound getAIF(String ref) throws IOException {
		if (!soundWorks) {
			return new InternalSound(this, 0);
		}
		if (!inited) {
			throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
		}
		if (deferred) {
			return new DeferredSound(ref, DeferredSound.AIF);
		}
		
		int buffer = -1;
		
		if (loaded.get(ref) != null) {
			buffer = ((Integer) loaded.get(ref)).intValue();
		} else {
			Log.info("Loading: "+ref);
			try {
				IntBuffer buf = BufferUtils.createIntBuffer(1);
				
				InputStream in = ResourceLoader.getResourceAsStream(ref);
				AiffData data = AiffData.create(in);
				AL10.alGenBuffers(buf);
				AL10.alBufferData(buf.get(0), data.format, data.data, data.samplerate);
				
				loaded.put(ref,new Integer(buf.get(0)));
				buffer = buf.get(0);
			} catch (Exception e) {
				Log.error(e);
				IOException x = new IOException("Failed to load: "+ref);
				x.initCause(e);
				
				throw x;
			}
		}
		
		if (buffer == -1) {
			throw new IOException("Unable to load: "+ref);
		}
		
		return new InternalSound(this, buffer);
	}
	
	/**
	 * Get the Sound based on a specified WAV file
	 * 
	 * @param ref The reference to the WAV file in the classpath
	 * @return The Sound read from the WAV file
	 * @throws IOException Indicates a failure to load the WAV
	 */
	public InternalSound getWAV(String ref) throws IOException {
		if (!soundWorks) {
			return new InternalSound(this, 0);
		}
		if (!inited) {
			throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
		}
		if (deferred) {
			return new DeferredSound(ref, DeferredSound.WAV);
		}
		
		int buffer = -1;
		
		if (loaded.get(ref) != null) {
			buffer = ((Integer) loaded.get(ref)).intValue();
		} else {
			Log.info("Loading: "+ref);
			try {
				IntBuffer buf = BufferUtils.createIntBuffer(1);
				
				InputStream in = ResourceLoader.getResourceAsStream(ref);
				WaveData data = WaveData.create(in);
				AL10.alGenBuffers(buf);
				AL10.alBufferData(buf.get(0), data.format, data.data, data.samplerate);
				
				loaded.put(ref,new Integer(buf.get(0)));
				buffer = buf.get(0);
			} catch (Exception e) {
				Log.error(e);
				IOException x = new IOException("Failed to load: "+ref);
				x.initCause(e);
				
				throw x;
			}
		}
		
		if (buffer == -1) {
			throw new IOException("Unable to load: "+ref);
		}
		
		return new InternalSound(this, buffer);
	}

	/**
	 * Get the Sound based on a specified OGG file
	 * 
	 * @param ref The reference to the OGG file in the classpath
	 * @return The Sound read from the OGG file
	 * @throws IOException Indicates a failure to load the OGG
	 */
	public StreamSound getOggStream(String ref) throws IOException {
		setMOD(null);
		setStream(null);
		
		if (soundWorks) {
			if (currentMusic != -1) {
				AL10.alSourceStop(sources.get(0));
			}
			
			getMusicSource();
			currentMusic = sources.get(0);
			
			return new StreamSound(new OpenALStreamPlayer(currentMusic, ref));
		}
		
		return new StreamSound(new OpenALStreamPlayer(0, ref));
	}
	
	/**
	 * Get the Sound based on a specified OGG file
	 * 
	 * @param ref The reference to the OGG file in the classpath
	 * @return The Sound read from the OGG file
	 * @throws IOException Indicates a failure to load the OGG
	 */
	public InternalSound getOgg(String ref) throws IOException {
		if (!soundWorks) {
			return new InternalSound(this, 0);
		}
		if (!inited) {
			throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
		}
		if (deferred) {
			return new DeferredSound(ref, DeferredSound.OGG);
		}
		
		int buffer = -1;
		
		if (loaded.get(ref) != null) {
			buffer = ((Integer) loaded.get(ref)).intValue();
		} else {
			Log.info("Loading: "+ref);
			try {
				IntBuffer buf = BufferUtils.createIntBuffer(1);
				
				InputStream in = ResourceLoader.getResourceAsStream(ref);
				
				OggDecoder decoder = new OggDecoder();
				OggData ogg = decoder.getData(in);
				
				AL10.alGenBuffers(buf);
				AL10.alBufferData(buf.get(0), ogg.channels > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, ogg.data, ogg.rate);
				
				loaded.put(ref,new Integer(buf.get(0)));
				                     
				buffer = buf.get(0);
			} catch (Exception e) {
				Log.error(e);
				Sys.alert("Error","Failed to load: "+ref+" - "+e.getMessage());
				System.exit(0);
			}
		}
		
		if (buffer == -1) {
			throw new IOException("Unable to load: "+ref);
		}
		
		return new InternalSound(this, buffer);
	}
	
	/**
	 * Set the mod thats being streamed if any
	 * 
	 * @param sound The mod being streamed
	 */
	void setMOD(MODSound sound) {
		if (!soundWorks) {
			return;
		}
		
		currentMusic = sources.get(0);
		this.mod = sound;
		if (sound != null) {
			this.stream = null;
		}
		paused = false;
	}

	/**
	 * Set the stream being played
	 * 
	 * @param stream The stream being streamed
	 */
	void setStream(OpenALStreamPlayer stream) {
		if (!soundWorks) {
			return;
		}
		
		currentMusic = sources.get(0);
		this.stream = stream;
		if (stream != null) {
			this.mod = null;
		}
		paused = false;
	}
	
	/**
	 * Poll the streaming system
	 * 
	 * @param delta The amount of time passed since last poll
	 */
	public void poll(int delta) {
		if (!soundWorks) {
			return;
		}
		if (paused) {
			return;
		}

		if (music) {
			if (mod != null) {
				try {
					mod.poll();
				} catch (OpenALException e) {
					Log.error("Error with OpenGL MOD Player on this this platform");
					Log.error(e);
					mod = null;
				}
			}
			if (stream != null) {
				try {
					stream.update();
				} catch (OpenALException e) {
					Log.error("Error with OpenGL MOD Player on this this platform");
					Log.error(e);
					mod = null;
				}
			}
		}
	}
	
	/**
	 * Get the single instance of this class
	 * 
	 * @return The single instnace of this class
	 */
	public static SoundStore get() {
		return store;
	}
}
