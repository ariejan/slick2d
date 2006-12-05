package org.newdawn.slick.particles;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.util.FastTrig;
import org.newdawn.slick.util.Log;

/**
 * An emitter than can be externally configured. This configuration can also be saved/loaded
 * using the ParticleIO class.
 * 
 * @see ParticleIO
 * 
 * @author kevin
 */
public class ConfigurableEmitter implements ParticleEmitter {
	/** The path from which the images should be loaded */
	private static String relativePath = "";
	
	/**
	 * Set the path from which images should be loaded
	 * 
	 * @param path The path from which images should be loaded
	 */
	public static void setRelativePath(String path) {
		relativePath = path;
	}
	
	/** The spawn interval range property - how often spawn happens */
	public Range spawnInterval = new Range(100, 100);
	/** The spawn count property - how many particles are spawned each time */
	public Range spawnCount = new Range(5, 5);
	/** The initial life of the new pixels */
	public Range initialLife = new Range(1000, 1000);
	/** The initial size of the new pixels */
	public Range initialSize = new Range(10, 10);
	/** The offset from the x position */
	public Range xOffset = new Range(0, 0);
	/** The offset from the y position */
	public Range yOffset = new Range(0, 0);
	/** The spread of the particles */
	public Value spread = new Value(360);
	/** The angular offset */
	public Value angularOffset = new Value(0);
	/** The initial distance of the particles */
	public Range initialDistance = new Range(0, 0);
	/** The speed particles fly out */
	public Range speed = new Range(50, 50);
	/** The growth factor on the particles */
	public Value growthFactor = new Value(0);
	/** The factor of gravity to apply */
	public Value gravityFactor = new Value(0);
	/** The factor of wind to apply */
	public Value windFactor = new Value(0);
	/** The length of the effect */
	public Range length = new Range(1000,1000);
	/** The color range @see ColorRecord */
	public ArrayList colors = new ArrayList();
	/** The starting alpha value */
	public Value startAlpha = new Value(255);
	/** The ending alpha value */
	public Value endAlpha = new Value(0);
	/** The number of particles that will be emitted */
	public Range emitCount = new Range(1000,1000);
	/** The points indicate */
	public int usePoints = Particle.INHERIT_POINTS;
	
	/** The name attribute */
	public String name;
	/** The name of the image in use */
	public String imageName;
	/** The image being used for the particles */
	private Image image;
	/** True if the image needs updating */
	private boolean updateImage;
	
	/** True if the emitter is enabled */
	private boolean enabled = true;
	/** The x coordinate of the position of this emitter */
	private float x;
	/** The y coordinate of the position of this emitter */
	private float y;
	/** The time in milliseconds til the next spawn */
	private int nextSpawn = 0;
	
	/** The timeout counting down to spawn */
	private int timeout;
	/** The number of particles in use by this emitter */
	private int particleCount;
	/** The system this emitter is being updated to */
	private ParticleSystem engine;
	/** The number of particles that are left ot emit */
	private int leftToEmit;
	
	/**
	 * Create a new emitter configurable externally
	 * 
	 * @param name The name of emitter
	 */
	public ConfigurableEmitter(String name) {
		this.name = name;
		leftToEmit = (int) emitCount.random();
		timeout = (int) (length.random());
		
		colors.add(new ColorRecord(0,Color.white));
		colors.add(new ColorRecord(1,Color.red));
	}

	/**
	 * Set the name of the image to use on a per particle basis. The complete
	 * reference to the image is required (based on the relative path)
	 * 
	 * @see #setRelativePath(String)
	 * 
	 * @param imageName The name of the image to use on a per particle reference
	 */
	public void setImageName(String imageName) {
		if (imageName.length() == 0) {
			imageName = null;
		}
		
		this.imageName = imageName;
		if (imageName == null) {
			image = null;
		} else {
			updateImage = true;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[" + name + "]";
	}

	/**
	 * Set the position of this particle source
	 * 
	 * @param x The x coodinate of that this emitter should spawn at
	 * @param y The y coodinate of that this emitter should spawn at
	 */
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the base x coordiante for spawning particles
	 * 
	 * @return The x coordinate for spawning particles
	 */
	public float getX() {
		return x;
	}

	/**
	 * Get the base y coordiante for spawning particles
	 * 
	 * @return The y coordinate for spawning particles
	 */
	public float getY() {
		return y;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#update(org.newdawn.slick.particles.ParticleSystem,
	 *      int)
	 */
	public void update(ParticleSystem system, int delta) {
		this.engine = system;
		
		if (updateImage) {
			updateImage = false;
			try {
				image = new Image(relativePath+"/"+imageName);
			} catch (SlickException e) {
				image = null;
				Log.error(e);
			}
		}

		particleCount = 0;
		
		if (length.isEnabled()) {
			if (timeout < 0) {
				return;
			}
			timeout -= delta;
		}
		if (emitCount.isEnabled()) {
			if (leftToEmit <= 0) {
				return;
			}
		}
		
		nextSpawn -= delta;
		if (nextSpawn < 0) {
			nextSpawn = (int) spawnInterval.random();
			int count = (int) spawnCount.random();

			for (int i = 0; i < count; i++) {
				Particle p = system.getNewParticle(this, initialLife.random());
				p.setSize(initialSize.random());
				p.setPosition(x + xOffset.random(), y + yOffset.random());
				p.setVelocity(0, 0);

				float dist = initialDistance.random();
				float power = speed.random();
				if ((dist != 0) || (power != 0)) {
					float s = spread.random();
					float ang = (s + angularOffset.getValue() - (spread.getValue() / 2)) - 90;
					float xa = (float) FastTrig.cos(Math.toRadians(ang)) * dist;
					float ya = (float) FastTrig.sin(Math.toRadians(ang)) * dist;
					float xv = (float) FastTrig.cos(Math.toRadians(ang))
							* power * 0.001f;
					float yv = (float) FastTrig.sin(Math.toRadians(ang))
							* power * 0.001f;

					p.adjustPosition(xa, ya);
					p.setVelocity(xv, yv);
				}
				
				if (image != null) {
					p.setImage(image);
				}
				
				ColorRecord start = (ColorRecord) colors.get(0);
				p.setColor(start.col.r, start.col.g, start.col.b, startAlpha.getValue() / 255.0f);
				p.setUsePoint(usePoints);
				
				if (emitCount.isEnabled()) {
					leftToEmit--;
					if (leftToEmit <= 0) {
						break;
					}
				}
			}
		}
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#updateParticle(org.newdawn.slick.particles.Particle,
	 *      int)
	 */
	public void updateParticle(Particle particle, int delta) {
		particleCount++;
		
		particle.adjustSize(delta * growthFactor.getValue() * 0.001f);
		particle.adjustVelocity(windFactor.getValue() * 0.0001f, gravityFactor.getValue() * 0.0001f);
		
		float offset = particle.getLife() / particle.getOriginalLife();
		float inv = 1 - offset;
		float colOffset = 0;
		float colInv = 1;
		
		Color startColor = null;
		Color endColor = null;
		for (int i=0;i<colors.size()-1;i++) {
			ColorRecord rec1 = (ColorRecord) colors.get(i);
			ColorRecord rec2 = (ColorRecord) colors.get(i+1);
		
			if ((inv >= rec1.pos) && (inv <= rec2.pos)) {
				startColor = rec1.col;
				endColor = rec2.col;
				
				float step = rec2.pos - rec1.pos;
				colOffset = inv - rec1.pos;
				colOffset /= step;
				colOffset = 1 - colOffset;
				colInv = 1 - colOffset;
			}
		}
		
		if (startColor != null) {	
			float r = (startColor.r * colOffset) + (endColor.r * colInv); 
			float g = (startColor.g * colOffset) + (endColor.g * colInv); 
			float b = (startColor.b * colOffset) + (endColor.b * colInv); 
			float a = ((startAlpha.getValue() / 255.0f) * offset) + 
			          ((endAlpha.getValue() / 255.0f) * inv);
			particle.setColor(r,g,b,a);
		}
		
	}

	/**
	 * Check if this emitter has completed it's cycle
	 * 
	 * @return True if the emitter has completed it's cycle
	 */
	public boolean completed() {
		if (engine == null) {
			return false;
		}
		
		if (length.isEnabled()) {
			if (timeout > 0) {
				return false;
			}
			return (engine.getParticleCount() == 0);
		}
		if (emitCount.isEnabled()) {
			if (leftToEmit > 0) {
				return false;
			}
			return (engine.getParticleCount() == 0);
		}
		
		return false;
	}
	
	/**
	 * Cause the emitter to replay it's circle
	 */
	public void replay() {
		reset();
		nextSpawn = 0;
		leftToEmit = (int) emitCount.random();
		timeout = (int) (length.random());
	}
	
	/**
	 * Release all the particles held by this emitter
	 */
	public void reset() {
		if (engine != null) {
			engine.releaseAll(this);
		}
	}
	
	/**
	 * Check if the replay has died out - used by the editor
	 */
	public void replayCheck() {
		if (completed()) {
			if (engine != null) {
				if (engine.getParticleCount() == 0) {
					replay();
				}
			}
		}
	}
	
	/**
	 * A configurable single value
	 *
	 * @author kevin
	 */
	public class Value {
		/** The value configured */
		private float value;
		/** True if this value should given linear random numbers */
		private boolean linear;
		/** The next value */
		private float next;
		
		/**
		 * Create a new configurable new value
		 * 
		 * @param value The initial value
		 */
		private Value(float value) {
			this.value = value;
		}

		/**
		 * Get the currently configured value
		 * 
		 * @return The currently configured value
		 */
		public float getValue() {
			return value;
		}

		/**
		 * Set the configured value
		 * 
		 * @param value The configured value
		 */
		public void setValue(float value) {
			this.value = value;
		}

		/**
		 * Generate a random number based on the currently configured value
		 * 
		 * @return The random number
		 */
		public float random() {
			if (linear){
				float ret = next;
				next++;
				if (next > value) {
					next = 0;
				}
				
				return ret;
			}
			return (float) (Math.random() * value);
		}
		
		/**
		 * Check if this value should give linear random numbers
		 * 
		 * @return True if this value should give linear random numbers
		 */
		public boolean isLinear() {
			return linear;
		}
		
		/**
		 * Indicate if this value should give linear random numbers
		 * 
		 * @param linear True if this value should give linear random numbers
		 */
		public void setLinear(boolean linear) {
			this.linear = linear;
		}
	}

	/**
	 * A single element in the colour range of this emitter
	 *
	 * @author kevin
	 */
	public class ColorRecord {
		/** The position in the life cycle */
		public float pos;
		/** The color at this position */
		public Color col;
		
		/**
		 * Create a new record
		 * 
		 * @param pos The position in the life cycle (0 = start, 1 = end)
		 * @param col The color applied at this position
		 */
		public ColorRecord(float pos, Color col) {
			this.pos = pos;
			this.col = col;
		}
	}
	
	/**
	 * Add a point in the colour cycle
	 * 
	 * @param pos The position in the life cycle (0 = start, 1 = end)
	 * @param col The color applied at this position
	 */
	public void addColorPoint(float pos, Color col) {
		colors.add(new ColorRecord(pos, col));
	}
	
	/**
	 * A simple bean describing a range of values
	 * 
	 * @author kevin
	 */
	public class Range {
		/** The maximum value in the range */
		private float max;
		/** The minimum value in the range */
		private float min;
		/** True if this range application is enabled */
		private boolean enabled = false;

		/**
		 * Create a new configurable range
		 * 
		 * @param min The minimum value of the range
		 * @param max The maximum value of the range
		 */
		private Range(float min, float max) {
			this.min = min;
			this.max = max;
		}
		
		/**
		 * Generate a random number in the range
		 * 
		 * @return The random number from the range
		 */
		public float random() {
			return (float) (min + (Math.random() * (max - min)));
		}

		/**
		 * Check if this configuration option is enabled
		 * 
		 * @return True if the range is enabled
		 */
		public boolean isEnabled() {
			return enabled;
		}
		
		/**
		 * Indicate if this option should be enabled
		 * 
		 * @param enabled True if this option should be enabled
		 */
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		
		/**
		 * Get the maximum value for this range 
		 * 
		 * @return The maximum value for this range
		 */
		public float getMax() {
			return max;
		}

		/**
		 * Set the maxmium value for this range
		 * 
		 * @param max The maximum value for this range
		 */
		public void setMax(float max) {
			this.max = max;
		}

		/**
		 * Get the minimum value for this range 
		 * 
		 * @return The minimum value for this range
		 */
		public float getMin() {
			return min;
		}

		/**
		 * Set the minimum value for this range
		 * 
		 * @param min The minimum value for this range
		 */
		public void setMin(float min) {
			this.min = min;
		}
	}
}
