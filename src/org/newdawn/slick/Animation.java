package org.newdawn.slick;

import java.util.ArrayList;

import org.lwjgl.Sys;
import org.newdawn.slick.util.Log;

/**
 * A utility to hold and render animations
 *
 * @author kevin
 * @author DeX (speed updates)
 */
public class Animation {
	/** The list of frames to render in this animation */
	private ArrayList frames = new ArrayList();
	/** The frame currently being displayed */
	private int currentFrame = -1;
	/** The time the next frame change should take place */
	private long nextChange = getTime();
	/** True if the animation is stopped */
	private boolean stopped = false;
	/** The time left til the next frame */
	private long timeLeft;
	/** The current speed of the animation */
	private float speed = 1.0f;
	/** The frame to stop at */
	private int stopAt = -2;
	
	/**
	 * Create an empty animation
	 */
	public Animation() {
		currentFrame = 0;
	}

	/**
	 * Create a new animation from a set of images
	 * 
	 * @param frames The images for the animation frames
	 * @param duration The duration to show each frame
	 */
	public Animation(Image[] frames, int duration) {
		for (int i=0;i<frames.length;i++) {
			addFrame(frames[i], duration);
		}
		currentFrame = 0;
	}
	
	/**
	 * Create a new animation from a set of images
	 * 
	 * @param frames The images for the animation frames
	 * @param durations The duration to show each frame
	 */
	public Animation(Image[] frames, int[] durations) {
		if (frames.length != durations.length) {
			throw new RuntimeException("There must be one duration per frame");
		}
		
		for (int i=0;i<frames.length;i++) {
			addFrame(frames[i], durations[i]);
		}
		currentFrame = 0;
	}
	
	/**
	 * Check if this animation has stopped (either explictly or because it's reached its target frame)
	 * 
	 * @see #stopAt
	 * @return True if the animation has stopped
	 */
	public boolean isStopped() {
		return stopped;
	}

	/**
	  * Adjust the overall speed of the animation.
	  *
	  * @param spd The speed to run the animation. Default: 1.0
	  */
	public void setSpeed(float spd) {
	   if (speed >= 0) speed = spd;
	}

	/**
	 * Returns the current speed of the animation.
	 * 
	 * @return The speed this animation is being played back at
	 */
	public float getSpeed() {
	   return speed;
	}

	
	/**
	 * Stop the animation
	 */
	public void stop() {
		if (frames.size() == 0) {
			return;
		}
		timeLeft = nextChange - getTime();
		stopped = true;
	}

	/**
	 * Start the animation playing again
	 */
	public void start() {
		if (!stopped) {
			return;
		}
		if (frames.size() == 0) {
			return;
		}
		stopped = false;
		nextChange = getTime() + timeLeft;
	}
	
	/**
	 * Restart the animation from the beginning
	 */
	public void restart() {
		if (!stopped) {
			return;
		}
		
		if (frames.size() == 0) {
			return;
		}
		stopped = false;
		currentFrame = 0;
		nextChange = getTime() + (int) (((Frame) frames.get(0)).duration / speed);
		nextFrame();
	}
	
	/**
	 * Add animation frame to the animation
	 * 
	 * @param frame The image to display for the frame
	 * @param duration The duration to display the frame for
	 */
	public void addFrame(Image frame, int duration) {
		if (duration == 0) {
			Log.error("Invalid duration: "+duration);
			throw new RuntimeException("Invalid duration: "+duration);
		}
		
		frames.add(new Frame(frame, duration));
		currentFrame = 0;
	}

	/**
	 * Draw the animation to the screen
	 */
	public void draw() {
		draw(0,0);
	}

	/**
	 * Draw the animation at a specific location
	 * 
	 * @param x The x position to draw the animation at
	 * @param y The y position to draw the animation at
	 */
	public void draw(int x,int y) {
		draw(x,y,((Frame) frames.get(currentFrame)).image.getWidth(),
				 ((Frame) frames.get(currentFrame)).image.getHeight());
	}
	
	/**
	 * Draw the animation
	 * 
	 * @param x The x position to draw the animation at
	 * @param y The y position to draw the animation at
	 * @param width The width to draw the animation at
	 * @param height The height to draw the animation at
	 */
	public void draw(int x,int y,int width,int height) {
		if (frames.size() == 0) {
			return;
		}
		
		nextFrame();
		
		Frame frame = (Frame) frames.get(currentFrame);
		frame.image.draw(x,y,width,height);
	}

	/**
	 * Update the animation cycle without draw the image, useful
	 * for keeping two animations in sync
	 */
	public void updateNoDraw() {
		nextFrame();
	}
	
	/**
	 * Get the index of the current frame
	 * 
	 * @return The index of the current frame
	 */
	public int getFrame() {
		return currentFrame;
	}
	
	/**
	 * Check if we need to move to the next frame
	 */
	private void nextFrame() {
		if (stopped) {
			return;
		}
		if (frames.size() == 0) {
			return;
		}
		
		long now = getTime();
		
		while (((now >= nextChange) || (currentFrame == -1)) && (currentFrame != stopAt)) {
			currentFrame = (currentFrame + 1) % frames.size();
			
			int realDuration = (int) (((Frame) frames.get(currentFrame)).duration / speed);
			nextChange = nextChange + realDuration;

			//System.out.println(((Frame) frames.get(currentFrame)).duration + "," + speed + " = "+realDuration+" currentFrame: "+currentFrame+" now:"+now+" nextChange: "+nextChange);
		}
		
		if (currentFrame == stopAt) {
			stopped = true;
		}
	}
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	private long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/**
	 * Indicate the animation should stop when it reaches the specified
	 * frame index (note, not frame number but index in the animation
	 * 
	 * @param frameIndex The index of the frame to stop at
	 */
	public void stopAt(int frameIndex) {
		stopAt = frameIndex; 
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String res = "[Animation ("+frames.size()+") ";
		for (int i=0;i<frames.size();i++) {
			Frame frame = (Frame) frames.get(i);
			res += frame.duration+",";
		}
		
		res += "]";
		return res;
	}
	
	/**
	 * A single frame within the animation
	 *
	 * @author kevin
	 */
	private class Frame {
		/** The image to display for this frame */
		public Image image;
		/** The duration to display the image fro */
		public int duration;
	
		/**
		 * Create a new animation frame
		 * 
		 * @param image The image to display for the frame
		 * @param duration The duration in millisecond to display the image for
		 */
		public Frame(Image image, int duration) {
			this.image = image;
			this.duration = duration;
		}
	}
}
