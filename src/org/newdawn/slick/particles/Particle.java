package org.newdawn.slick.particles;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

/**
 * A single particle within a system
 *
 * @author kevin
 */
public class Particle {
	/** The x coordinate of the particle */
	protected float x;
	/** The y coordinate of the particle */
	protected float y;
	/** The x component of the velocity of the particle */
	protected float vx;
	/** The y component of the velocity of the particle */
	protected float vy;
	/** The current size in pixels of the particle */
	protected float size = 10;
	/** The colour of the particle */
	protected Color color = new Color(1f,1f,1f,1f);
	/** The life left in the particle */
	protected float life;
	/** The original life of this particle */
	protected float originalLife;
	/** The engine this particle belongs to */
	private ParticleSystem engine;
	/** The emitter controllng this particle */
	private ParticleEmitter emitter;
	/** The image for this particle */
	protected Image image;
	
	/**
	 * Create a new particle belonging to given engine
	 * 
	 * @param engine The engine the new particle belongs to
	 */
	public Particle(ParticleSystem engine) {
		this.engine = engine;
	}
	
	/**
	 * Set the image used to render this particle
	 * 
	 * @param image The image used to render this particle
	 */
	public void setImage(Image image) {
		this.image = image;
	}
	
	/**
	 * Get the life remaining in the particle in milliseconds
	 * 
	 * @return The life remaining in the particle
	 */
	public float getLife() {
		return life;
	}
	
	/**
	 * Check if this particle is currently in use (i.e. is it rendering?)
	 * 
	 * @return True if the particle is currently in use
	 */
	public boolean inUse() {
		return life > 0;
	}
	
	/**
	 * Render this particle 
	 */
	public void render() {
		image.draw((int) (x-(size/2)),(int) (y-(size/2)),(int) size,(int) size,color);
	}
	
	/**
	 * Update the state of this particle
	 * 
	 * @param delta The time since the last update
	 */
	public void update(int delta) {
		emitter.updateParticle(this, delta);
		
		life -= delta;
		if (life > 0) {
			x += delta * vx;
			y += delta * vy;
		} else {
			engine.release(this);
		}
	}
	
	/**
	 * Initialise the state of the particle as it's reused
	 * 
	 * @param emitter The emitter controlling this particle
	 * @param life The life the particle should have (in milliseconds)
	 */
	public void init(ParticleEmitter emitter, float life) {
		x = 0;
		this.emitter = emitter;
		y = 0;
		vx = 0;
		vy = 0;
		size = 10;
		this.originalLife = this.life = life;
	}
	
	/**
	 * Set the size of the particle
	 * 
	 * @param size The size of the particle (in pixels)
	 */
	public void setSize(float size) {
		this.size = size;
	}
	
	/**
	 * Adjust the size of the particle 
	 * 
	 * @param delta The  amount to adjust the size by (in pixels)
	 */
	public void adjustSize(float delta) {
		size += delta;
	}
	
	/**
	 * Set the life of the particle
	 * 
	 * @param life The life of the particle in milliseconds
	 */
	public void setLife(float life) {
		this.life = life;
	}
	
	/**
	 * Adjust the life othe particle
	 * 
	 * @param delta The amount to adjust the particle by (in milliseconds)
	 */
	public void adjustLife(float delta) {
		life += delta;
	}
	
	/**
	 * Kill the particle, stop it rendering and send it back 
	 * to the engine for use.
	 */
	public void kill() {
		life = 1;
	}
	
	/**
	 * Set the color of the particle
	 * 
	 * @param r The red component of the color
	 * @param g The green component of the color
	 * @param b The blue component of the color
	 * @param a The alpha component of the color
	 */
	public void setColor(float r, float g, float b, float a) {
		color.r = r;
		color.g = g;
		color.b = b;
		color.a = a;
	}
	
	/**
	 * Set the position of this particle
	 * 
	 * @param x The new x position of the particle
	 * @param y The new y position of the particle
	 */
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Set the velocity of the particle
	 * 
	 * @param x The x component of the new velocity
	 * @param y The y component of the new velocity
	 */
	public void setVelocity(float x, float y) {
		this.vx = x;
		this.vy = y;
	}
	
	/**
	 * Adjust (add) the position of this particle
	 * 
	 * @param dx The amount to adjust the x component by
	 * @param dy The amount to adjust the y component by 
	 */
	public void adjustPosition(float dx, float dy) {
		x += dx;
		y += dy;
	}
	
	/**
	 * Adjust (add) the color of the particle
	 * 
	 * @param r The amount to adjust the red component by
	 * @param g The amount to adjust the green component by
	 * @param b The amount to adjust the blue component by
	 * @param a The amount to adjust the alpha component by
	 */
	public void adjustColor(float r, float g, float b, float a) {
		color.r += r;
		color.g += g;
		color.b += b;
		color.a += a;
	}

	/**
	 * Adjust (add) the color of the particle
	 * 
	 * @param r The amount to adjust the red component by
	 * @param g The amount to adjust the green component by
	 * @param b The amount to adjust the blue component by
	 * @param a The amount to adjust the alpha component by
	 */
	public void adjustColor(int r, int g, int b, int a) {
		color.r += (r / 255.0f);
		color.g += (g / 255.0f);
		color.b += (b / 255.0f);
		color.a += (a / 255.0f);
	}

	/**
	 * Adjust (add) the vecloity of this particle
	 * 
	 * @param dx The amount to adjust the x component by
	 * @param dy The amount to adjust the y component by 
	 */
	public void adjustVelocity(float dx, float dy) {
		vx += dx;
		vy += dy;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() + " : "+life;
	}
}
