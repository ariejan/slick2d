package org.newdawn.slick.particles;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class ParticleSystem {
	private static final int DEFAULT_PARTICLES = 100;
	private static final int X = 0;
	private static final int Y = 1;
	private static final int VX = 2;
	private static final int VY = 3;
	private static final int LIFE = 4;
	
	private Image sprite;
	private Particle[] particles;
	private ArrayList emitters = new ArrayList();
	private ArrayList influences = new ArrayList();
	private ArrayList available = new ArrayList();
	private Particle dummy = new Particle(this);
	
	public ParticleSystem(Image sprite) {
		this(sprite, DEFAULT_PARTICLES);
	}
	
	public ParticleSystem(Image image, int maxParticles) {
		this.sprite = sprite;
		particles = new Particle[maxParticles];
	
		for (int i=0;i<particles.length;i++) {
			particles[i] = new Particle(this);
			available.add(particles[i]);
		}
	}
	
	public void render() {
		for (int i=0;i<particles.length;i++) {
			if (particles[i].inUse()) {
				particles[i].render(sprite);
			}
		}
	}
	
	public void update(int delta) {
		for (int i=0;i<emitters.size();i++) {
			ParticleEmitter emitter = (ParticleEmitter) emitters.get(i);
			emitter.update(this, delta);
		}
		
		for (int i=0;i<particles.length;i++) {
			if (particles[i].inUse()) {
				
				for (int j=0;j<influences.size();j++) {
					SystemInfluence influence = (SystemInfluence) influences.get(j);
					influence.update(particles[i], delta);
				}
				particles[i].update(delta);
			}
		}
	}
	
	public Particle getNewParticle() {
		if (available.size() > 0) {
			return (Particle) available.remove(0);
		}
		
		Log.warn("Ran out of particles (increase the limit)!");
		
		return dummy;
	}
	
	public void release(Particle particle) {
		if (particle != dummy) {
			available.add(dummy);
		}
	}
}
