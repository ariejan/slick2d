package org.newdawn.slick.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.Log;

/**
 * A particle syste responsible for maintaining a set of data about individual 
 * particles which are created and controlled by assigned emitters. This pseudo 
 * chaotic nature hopes to give more organic looking effects
 *
 * @author kevin
 */
public class ParticleSystem {
	/** The blending mode for the glowy style */
	public static final int BLEND_ADDITIVE = 1;
	/** The blending mode for the normal style */
	public static final int BLEND_COMBINE = 2;
	
	/** The default number of particles in the system */
	private static final int DEFAULT_PARTICLES = 100;
	
	/** The default image for the particles */
	private Image sprite;
	
	/**
	 * A pool of particles being used by a specific emitter
	 * 
	 * @author void
	 */
	private class ParticlePool
	{
		/** The particles being rendered and maintained */
		public Particle[] particles;
		/** The list of particles left to be used, if this size() == 0 then the particle engine was too small for the effect */
		public ArrayList available;

		/**
		 * Create a new particle pool contiaining a set of particles
		 * 
		 * @param system The system that owns the particles over all
		 * @param maxParticles The maximum number of particles in the pool
		 */
		public ParticlePool( ParticleSystem system, int maxParticles )
		{
			particles= new Particle[ maxParticles ];
			available= new ArrayList();
			
			for( int i=0; i<particles.length; i++ )
			{
				particles[i] = createParticle( system );
				available.add(particles[i]);
			}
		}
	}
	
	/**
	 * A map from emitter to a the particle pool holding the particles it uses
	 * void: this is now sorted by emitters to allow emitter specfic state to be set for
	 * each emitter. actually this is used to allow setting an individual blend mode for
	 * each emitter
	 */
	protected HashMap particlesByEmitter = new HashMap();
	/** The maximum number of particles allows per emitter */
	protected int maxParticlesPerEmitter;
	
	/** The list of emittered producing and controlling particles */
	protected ArrayList emitters = new ArrayList();
	
	/** The dummy particle to return should no more particles be available */
	protected Particle dummy;
	/** The blending mode */
	private int blendingMode = BLEND_COMBINE;
	/** The number of particles in use */
	private int pCount;
	/** True if we're going to use points to render the particles */
	private boolean usePoints;
	/** The x coordinate at which this system should be rendered */
	private float x;
	/** The x coordinate at which this system should be rendered */
	private float y;
	/** True if we should remove completed emitters */
	private boolean removeCompletedEmitters = true;
	
	/**
	 * Create a new particle system
	 * 
	 * @param defaultSprite The sprite to render for each particle
	 */
	public ParticleSystem(Image defaultSprite) {
		this(defaultSprite, DEFAULT_PARTICLES);
	}
	
	/**
	 * Indicate if completed emitters should be removed
	 * 
	 * @param remove True if completed emitters should be removed
	 */
	public void setRemoveCompletedEmitters(boolean remove) {
		removeCompletedEmitters = remove;
	}
	
	/**
	 * Indicate if this engine should use points to render the particles
	 * 
	 * @param usePoints True if points should be used to render the particles
	 */
	public void setUsePoints(boolean usePoints) {
		this.usePoints = usePoints;
	}
	
	/**
	 * Check if this engine should use points to render the particles
	 * 
	 * @return True if the engine should use points to render the particles
	 */
	public boolean usePoints() {
		return usePoints;
	}
	
	/**
	 * Create a new particle system
	 * 
	 * @param defaultSprite The sprite to render for each particle
	 * @param maxParticles The number of particles available in the system
	 */
	public ParticleSystem(Image defaultSprite, int maxParticles) {
		this.sprite = defaultSprite;
		this.maxParticlesPerEmitter= maxParticles;
	
		dummy = createParticle(this);
	}
	
	/**
	 * Get the blending mode in use
	 * 
	 * @see #BLEND_COMBINE
	 * @see #BLEND_ADDITIVE
	 * @return The blending mode in use
	 */
	public int getBlendingMode() {
		return blendingMode;
	}
	
	/**
	 * Create a particle specific to this system, override for your own implementations. 
	 * These particles will be cached and reused within this system.
	 * 
	 * @param system The system owning this particle
	 * @return The newly created particle.
	 */
	protected Particle createParticle(ParticleSystem system) {
		return new Particle(system);
	}
	
	/**
	 * Set the blending mode for the particles
	 * 
	 * @param mode The mode for blending particles together
	 */
	public void setBlendingMode(int mode) {
		this.blendingMode = mode;
	}
	
	/**
	 * Get the number of emitters applied to the system
	 * 
	 * @return The number of emitters applied to the system
	 */
	public int getEmitterCount() {
		return emitters.size();
	}
	
	/**
	 * Get an emitter a specified index int he list contained within this system
	 * 
	 * @param index The index of the emitter to retrieve
	 * @return The particle emitter 
	 */
	public ParticleEmitter getEmitter(int index) {
		return (ParticleEmitter) emitters.get(index);
	}
	
	/**
	 * Add a particle emitter to be used on this system
	 * 
	 * @param emitter The emitter to be added
	 */
	public void addEmitter(ParticleEmitter emitter) {
		emitters.add(emitter);
		
		ParticlePool pool= new ParticlePool( this, maxParticlesPerEmitter );
		particlesByEmitter.put( emitter, pool );
	}
	
	/**
	 * Remove a particle emitter that is currently used in the system
	 * 
	 * @param emitter The emitter to be removed
	 */
	public void removeEmitter(ParticleEmitter emitter) {
		emitters.remove(emitter);
		
		particlesByEmitter.remove( emitter );
	}
	
	/**
	 * Set the position at which this system should render relative to the current
	 * graphics context setup
	 * 
	 * @param x The x coordinate at which this system should be centered
 	 * @param y The y coordinate at which this system should be centered
	 */
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Render the particles in the system
	 */
	public void render() {
		GL11.glTranslatef(x,y,0);
		
		if (blendingMode == BLEND_ADDITIVE) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		}
		if (usePoints()) {
			GL11.glEnable( GL11.GL_POINT_SMOOTH ); 
			Texture.bindNone();
		}
		
		// iterate over all emitters
		for( int emitterIdx=0; emitterIdx<emitters.size(); emitterIdx++ )
		{
			// get emitter
			ParticleEmitter emitter= (ParticleEmitter)emitters.get( emitterIdx );
			
			// check for additive override and enable when set
			if( emitter instanceof ConfigurableEmitter )
				if( ((ConfigurableEmitter)emitter).useAdditive )
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			
			// now get the particle pool for this emitter and render all particles that are in use
			ParticlePool pool= (ParticlePool)particlesByEmitter.get( emitter );
			for( int i=0; i<pool.particles.length; i++ )
			{
				if( pool.particles[i].inUse() )
					pool.particles[i].render();
			}

			// reset additive blend mode
			if( emitter instanceof ConfigurableEmitter )
				if( ((ConfigurableEmitter)emitter).useAdditive )
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}

		if (usePoints()) {
			GL11.glDisable( GL11.GL_POINT_SMOOTH ); 
		}
		if (blendingMode == BLEND_ADDITIVE) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		Color.white.bind();
		GL11.glTranslatef(-x,-y,0);
	}
	
	/**
	 * Update the system, request the assigned emitters update the particles
	 * 
	 * @param delta The amount of time thats passed since last update in milliseconds
	 */
	public void update(int delta) {
		ArrayList removeMe = new ArrayList();
		for (int i=0;i<emitters.size();i++) {
			ParticleEmitter emitter = (ParticleEmitter) emitters.get(i);
			if (emitter.isEnabled()) {
				emitter.update(this, delta);
				if (removeCompletedEmitters) {
					if (emitter.completed()) {
						removeMe.add(emitter);
					}
				}
			}
		}
		emitters.removeAll(removeMe);
		
		pCount = 0;
		
		if( !particlesByEmitter.isEmpty() )
		{
			Iterator it= particlesByEmitter.values().iterator();
			while( it.hasNext())
			{
				ParticlePool pool= (ParticlePool)it.next();
				for (int i=0;i<pool.particles.length;i++) {
					if (pool.particles[i].inUse()) {
						pool.particles[i].update(delta);
						pCount++;
					}
				}
			}
		}
	}
	
	/**
	 * Get the number of particles in use in this system
	 * 
	 * @return The number of particles in use in this system
	 */
	public int getParticleCount() {
		return pCount;
	}
	
	/**
	 * Get a new particle from the system. This should be used by emitters to 
	 * request particles
	 * 
	 * @param emitter The emitter requesting the particle
	 * @param life The time the new particle should live for
	 * @return A particle from the system
	 */
	public Particle getNewParticle(ParticleEmitter emitter, float life)
	{
		ParticlePool pool= (ParticlePool)particlesByEmitter.get( emitter );
		if( pool.available.size() > 0 )
		{
			Particle p = (Particle) pool.available.remove( 0 );
			p.init( emitter, life );
			p.setImage( sprite );
			
			return p;
		}
		
		Log.warn("Ran out of particles (increase the limit)!");
		return dummy;
	}
	
	/**
	 * Release a particle back to the system once it has expired
	 * 
	 * @param particle The particle to be released
	 */
	public void release(Particle particle) {
		if (particle != dummy)
		{
			ParticlePool pool= (ParticlePool)particlesByEmitter.get( particle.getEmitter() );
			pool.available.add( particle );
		}
	}
	
	/**
	 * Release all the particles owned by the specified emitter
	 * 
	 * @param emitter The emitter owning the particles that should be released
	 */
	public void releaseAll(ParticleEmitter emitter) {
		if( !particlesByEmitter.isEmpty() )
		{
			Iterator it= particlesByEmitter.values().iterator();
			while( it.hasNext())
			{
				ParticlePool pool= (ParticlePool)it.next();
				for (int i=0;i<pool.particles.length;i++) {
					if (pool.particles[i].inUse()) {
						if (pool.particles[i].getEmitter() == emitter) {
							pool.particles[i].setLife(-1);
							release(pool.particles[i]);
						}
					}
				}
			}
		}
	}
}
