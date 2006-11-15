package org.newdawn.slick.particles;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
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
	
	/** The image for the particle */
	private Image sprite;
	/** The particles being rendered and maintained */
	private Particle[] particles;
	/** The list of emittered producing and controlling particles */
	private ArrayList emitters = new ArrayList();
	/** The list of particles left to be used, if this size() == 0 then the particle engine was too small for the effect */
	private ArrayList available = new ArrayList();
	/** The dummy particle to return should no more particles be available */
	private Particle dummy = new Particle(this);
	/** The blending mode */
	private int blendingMode = BLEND_COMBINE;
	
	/**
	 * Create a new particle system
	 * 
	 * @param sprite The sprite to render for each particle
	 */
	public ParticleSystem(Image sprite) {
		this(sprite, DEFAULT_PARTICLES);
	}
	
	/**
	 * Create a new particle system
	 * 
	 * @param image The sprite to render for each particle
	 * @param maxParticles The number of particles available in the system
	 */
	public ParticleSystem(Image image, int maxParticles) {
		this.sprite = image;
		particles = new Particle[maxParticles];
	
		for (int i=0;i<particles.length;i++) {
			particles[i] = new Particle(this);
			available.add(particles[i]);
		}
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
	 * Add a particle emitter to be used on this system
	 * 
	 * @param emitter The emitter to be added
	 */
	public void addEmitter(ParticleEmitter emitter) {
		emitters.add(emitter);
	}
	
	/**
	 * Remove a particle emitter that is currently used in the system
	 * 
	 * @param emitter The emitter to be removed
	 */
	public void removeEmitter(ParticleEmitter emitter) {
		emitters.remove(emitter);
	}
	
	/**
	 * Render the particles in the system
	 */
	public void render() {
		if (blendingMode == BLEND_ADDITIVE) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		}
		
		sprite.startUse();
		for (int i=0;i<particles.length;i++) {
			if (particles[i].inUse()) {
				particles[i].render(sprite);
			}
		}
		sprite.endUse();
		
		if (blendingMode == BLEND_ADDITIVE) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}
	
	/**
	 * Update the system, request the assigned emitters update the particles
	 * 
	 * @param delta The amount of time thats passed since last update in milliseconds
	 */
	public void update(int delta) {
		for (int i=0;i<emitters.size();i++) {
			ParticleEmitter emitter = (ParticleEmitter) emitters.get(i);
			emitter.update(this, delta);
		}
		
		for (int i=0;i<particles.length;i++) {
			if (particles[i].inUse()) {
				particles[i].update(delta);
			}
		}
	}
	
	/**
	 * Get a new particle from the system. This should be used by emitters to 
	 * request particles
	 * 
	 * @param emitter The emitter requesting the particle
	 * @param life The time the new particle should live for
	 * @return A particle from the system
	 */
	public Particle getNewParticle(ParticleEmitter emitter, float life) {
		if (available.size() > 0) {
			Particle p = (Particle) available.remove(0);
			p.init(emitter, life);
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
		if (particle != dummy) {
			available.add(particle);
		}
	}
}
