package org.newdawn.slick.particles;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;

/**
 * A single particle within a system
 * 
 * @author kevin
 */
public class Particle {
	/** Indicates the particle should inherit it's use of points */
	public static final int INHERIT_POINTS = 1;
	/** Indicates the particle should explicitly use points */
	public static final int USE_POINTS = 2;
	/** Indicates the particle should explicitly not use points */
	public static final int USE_QUADS = 3;

	/** The x coordinate of the particle */
	protected float x;
	/** The y coordinate of the particle */
	protected float y;
	/** The x component of the direction vector of the particle */
	protected float dirx;
	/** The y component of the direction vector of the particle */
	protected float diry;
	/** The speed of the particle heading into the direction vector */
	protected float speed;
	/** The current size in pixels of the particle */
	protected float size = 10;
	/** The colour of the particle */
	protected Color color = new Color(1f, 1f, 1f, 1f);
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
	/** The type identifier of this particle */
	protected int type;
	/** How this particle should be rendered */
	protected int usePoints = INHERIT_POINTS;
	/** True if this particle's quad should be oritented based on it's direction */
	protected boolean oriented = false;
	/** The currently scalar applied on the y axis */
	protected float scaleY = 1.0f;

	/**
	 * Create a new particle belonging to given engine
	 * 
	 * @param engine
	 *            The engine the new particle belongs to
	 */
	public Particle(ParticleSystem engine) {
		this.engine = engine;
	}

	/**
	 * Get the x offset of this particle
	 * 
	 * @return The x offset of this particle
	 */
	public float getX() {
		return x;
	}

	/**
	 * Get the y offset of this particle
	 * 
	 * @return The y offset of this particle
	 */
	public float getY() {
		return y;
	}

	/**
	 * Get the size of this particle
	 * 
	 * @return The size of this particle
	 */
	public float getSize() {
		return size;
	}

	/**
	 * Get the color of this particle
	 * 
	 * @return The color of this particle
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Set the image used to render this particle
	 * 
	 * @param image
	 *            The image used to render this particle
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * Get the original life of this particle
	 * 
	 * @return The original life of this particle
	 */
	public float getOriginalLife() {
		return originalLife;
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
		if ((engine.usePoints() && (usePoints == INHERIT_POINTS))
				|| (usePoints == USE_POINTS)) {
			Texture.bindNone();
			GL11.glEnable(GL11.GL_POINT_SMOOTH);
			GL11.glPointSize(size / 2);
			color.bind();
			GL11.glBegin(GL11.GL_POINTS);
			GL11.glVertex2f(x, y);
			GL11.glEnd();
		} else if (oriented || scaleY != 1.0f) {
			GL11.glPushMatrix();

			GL11.glTranslatef(x, y, 0f);

			if (oriented) {
				float angle = (float) (Math.atan2(y, x) * 180 / Math.PI);
				GL11.glRotatef(angle, 0f, 0f, 1.0f);
			}

			// scale
			GL11.glScalef(1.0f, scaleY, 1.0f);

			image.draw((int) (-(size / 2)), (int) (-(size / 2)), (int) size,
					(int) size, color);
			GL11.glPopMatrix();
		} else {
			image.draw((int) (x - (size / 2)), (int) (y - (size / 2)),
					(int) size, (int) size, color);
		}
	}

	/**
	 * Update the state of this particle
	 * 
	 * @param delta
	 *            The time since the last update
	 */
	public void update(int delta) {
		if (emitter.isEnabled()) {
			emitter.updateParticle(this, delta);
		}

		life -= delta;
		if (life > 0) {
			x += delta * dirx * speed;
			y += delta * diry * speed;
		} else {
			engine.release(this);
		}
	}

	/**
	 * Initialise the state of the particle as it's reused
	 * 
	 * @param emitter
	 *            The emitter controlling this particle
	 * @param life
	 *            The life the particle should have (in milliseconds)
	 */
	public void init(ParticleEmitter emitter, float life) {
		x = 0;
		this.emitter = emitter;
		y = 0;
		dirx = 0;
		diry = 0;
		size = 10;
		type = 0;
		this.originalLife = this.life = life;
		oriented = false;
		scaleY = 1.0f;
	}

	/**
	 * Set the type of this particle
	 * 
	 * @param type
	 *            The type of this particle
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Indicate how this particle should be renered
	 * 
	 * @param usePoints
	 *            The indicator for rendering
	 * @see #USE_POINTS
	 * @see #USE_QUADS
	 * @see #INHERIT_POINTS
	 */
	public void setUsePoint(int usePoints) {
		this.usePoints = usePoints;
	}

	/**
	 * Get the type of this particle
	 * 
	 * @return The type of this particle
	 */
	public int getType() {
		return type;
	}

	/**
	 * Set the size of the particle
	 * 
	 * @param size
	 *            The size of the particle (in pixels)
	 */
	public void setSize(float size) {
		this.size = size;
	}

	/**
	 * Adjust the size of the particle
	 * 
	 * @param delta
	 *            The amount to adjust the size by (in pixels)
	 */
	public void adjustSize(float delta) {
		size += delta;
		size = Math.max(0, size);
	}

	/**
	 * Set the life of the particle
	 * 
	 * @param life
	 *            The life of the particle in milliseconds
	 */
	public void setLife(float life) {
		this.life = life;
	}

	/**
	 * Adjust the life othe particle
	 * 
	 * @param delta
	 *            The amount to adjust the particle by (in milliseconds)
	 */
	public void adjustLife(float delta) {
		life += delta;
	}

	/**
	 * Kill the particle, stop it rendering and send it back to the engine for
	 * use.
	 */
	public void kill() {
		life = 1;
	}

	/**
	 * Set the color of the particle
	 * 
	 * @param r
	 *            The red component of the color
	 * @param g
	 *            The green component of the color
	 * @param b
	 *            The blue component of the color
	 * @param a
	 *            The alpha component of the color
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
	 * @param x
	 *            The new x position of the particle
	 * @param y
	 *            The new y position of the particle
	 */
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Set the velocity of the particle
	 * 
	 * @param dirx
	 *            The x component of the new velocity
	 * @param diry
	 *            The y component of the new velocity
	 * @param speed
	 *            The speed in the given direction
	 */
	public void setVelocity(float dirx, float diry, float speed) {
		this.dirx = dirx;
		this.diry = diry;
		this.speed = speed;
	}

	/**
	 * Set the velocity of the particle
	 * 
	 * @param dirx The x component of the new velocity
	 * @param diry The y component of the new velocity
	 */
	public void setVelocity(float dirx, float diry) {
		setVelocity(dirx,diry,1);
	}
	
	/**
	 * Adjust (add) the position of this particle
	 * 
	 * @param dx
	 *            The amount to adjust the x component by
	 * @param dy
	 *            The amount to adjust the y component by
	 */
	public void adjustPosition(float dx, float dy) {
		x += dx;
		y += dy;
	}

	/**
	 * Adjust (add) the color of the particle
	 * 
	 * @param r
	 *            The amount to adjust the red component by
	 * @param g
	 *            The amount to adjust the green component by
	 * @param b
	 *            The amount to adjust the blue component by
	 * @param a
	 *            The amount to adjust the alpha component by
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
	 * @param r
	 *            The amount to adjust the red component by
	 * @param g
	 *            The amount to adjust the green component by
	 * @param b
	 *            The amount to adjust the blue component by
	 * @param a
	 *            The amount to adjust the alpha component by
	 */
	public void adjustColor(int r, int g, int b, int a) {
		color.r += (r / 255.0f);
		color.g += (g / 255.0f);
		color.b += (b / 255.0f);
		color.a += (a / 255.0f);
	}

	/**
	 * Adjust (add) the direction of this particle
	 * 
	 * @param dx
	 *            The amount to adjust the x component by
	 * @param dy
	 *            The amount to adjust the y component by
	 */
	public void adjustDirection(float dx, float dy) {
		dirx += dx;
		diry += dy;
	}

	/**
	 * Get the emitter that owns this particle
	 * 
	 * @return The emitter that owns this particle
	 */
	public ParticleEmitter getEmitter() {
		return emitter;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() + " : " + life;
	}

	/**
	 * Get the current speed of the particle
	 * 
	 * @return The current speed of the particle
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Set the speed of the particle
	 * 
	 * @param speed The new speed of the particle
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Check if this particle is being oriented based on it's velocity
	 * 
	 * @return True if this particle being oriented based on it's velocity
	 */
	public boolean isOriented() {
		return oriented;
	}

	/**
	 * Indicate if this particle should be oriented based on it's velocity
	 * 
	 * @param oriented True if this particle is being oriented based on it's velocity
	 */
	public void setOriented(boolean oriented) {
		this.oriented = oriented;
	}

	/**
	 * Get the current scalar applied on the y axis
	 * 
	 * @return The scalar applied on the y axis
	 */
	public float getScaleY() {
		return scaleY;
	}

	/**
	 * Set the current scalar applied on the y axis
	 * 
	 * @param scaleY The new scalar to apply on the y axis
	 */
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}
}
