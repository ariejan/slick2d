package org.newdawn.slick.particles;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Particle {
	private float x;
	private float y;
	private float vx;
	private float vy;
	private float size = 10;
	private Color color = new Color(1f,1f,1f,1f);
	private float life;
	private ParticleSystem engine;
	
	public Particle(ParticleSystem engine) {
		this.engine = engine;
	}
	
	public boolean inUse() {
		return life >= 0;
	}
	
	public void render(Image sprite) {
		sprite.draw((int) (x-(size/2)),(int) (y-(size/2)),(int) size,(int) size);
	}
	
	public void update(float delta) {
		life -= delta;
		if (life > 0) {
			x += delta * vx;
			y += delta * vy;
		} else {
			engine.release(this);
		}
	}
	
	public void init() {
		x = 0;
		y = 0;
		vx = 0;
		vy = 0;
		
	}
	
	public void setSize(float size) {
		this.size = size;
	}
	
	public void adjustSize(float delta) {
		size += delta;
	}
	
	public void adjustLife(float delta) {
		life += delta;
	}
	
	public void kill() {
		life = 0;
	}
	
	public void setColor(float r, float g, float b, float a) {
		color.r = r;
		color.g = g;
		color.b = b;
		color.a = a;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setVelocity(float x, float y) {
		this.vx = x;
		this.vy = y;
	}
	
	public void adjustPosition(float dx, float dy) {
		x += dx;
		y += dy;
	}
	
	public void adjustColor(float r, float g, float b, float a) {
		color.r += r;
		color.g += g;
		color.b += b;
		color.a += a;
	}
	
	public void adjustColor(int r, int g, int b, int a) {
		color.r += (r / 255.0f);
		color.g += (g / 255.0f);
		color.b += (b / 255.0f);
		color.a += (a / 255.0f);
	}
	
	public void adjustVelocity(float dx, float dy) {
		vx += dx;
		vy += dy;
	}
}
