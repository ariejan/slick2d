package org.newdawn.slick.particles;

/**
 * An emitter is responsible for producing the particles and controlling them during
 * their life. An implementation of this interface can be considered a particle
 * effect.
 *
 * @author kevin
 */
public interface ParticleEmitter {
	/**
	 * Update the emitter, produce any particles required by requesting
	 * them from the particle system provided.
	 * 
	 * @param system The particle system used to create particles
	 * @param delta The amount of time in milliseconds since last emitter update
	 */
	public void update(ParticleSystem system, int delta);

	/**
	 * Check if this emitter has completed it's cycle
	 * 
	 * @return True if the emitter has completed it's cycle
	 */
	public boolean completed();
	
	/**
	 * Update a single particle that this emitter produced
	 * 
	 * @param particle The particle to be updated
	 * @param delta The amount of time in millisecond since last particle update
	 */
	public void updateParticle(Particle particle, int delta);
	
	/**
	 * Check if the emitter is enabled 
	 * 
	 * @return True if the emitter is enabled
	 */
	public boolean isEnabled();
	
	/**
	 * Indicate whether the emitter should be enabled
	 * 
	 * @param enabled True if the emitter should be enabled
	 */
	public void setEnabled(boolean enabled);
}
