package org.newdawn.slick;

import java.util.Properties;

import org.lwjgl.Sys;
import org.lwjgl.input.Cursor;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A generic game container that handles the game loop, fps recording and
 * managing the input system
 *
 * @author kevin
 */
public abstract class GameContainer implements GUIContext {
	/** The time the last frame was rendered */
	private long lastFrame;
	/** The last time the FPS recorded */
	private long lastFPS;
	/** The last recorded FPS */
	private int recordedFPS;
	/** The current count of FPS */
	private int fps;
	/** True if we're currently running the game loop */
	protected boolean running = true;
	
	/** The width of the display */
	protected int width;
	/** The height of the display */
	protected int height;
	/** The game being managed */
	protected Game game;
	
	/** The default font to use in the graphics context */
	private Font defaultFont;
	/** The graphics context to be passed to the game */
	private Graphics graphics;
	
	/** The input system to pass to the game */
	private Input input;
	/** The FPS we want to lock to */
	private int targetFPS = -1;
	/** True if we should show the fps */
	private boolean showFPS = true;
	/** The minimum logic update interval */
	private long minimumLogicInterval = 1;
	/** The stored delta */
	private long storedDelta;
	/** The maximum logic update interval */
	private long maximumLogicInterval = 0;
	/** The last game started */
	private Game lastGame;
	
	/**
	 * Create a new game container wrapping a given game
	 * 
	 * @param game The game to be wrapped
	 */
	protected GameContainer(Game game) {
		this.game = game;
		lastFrame = getTime();
		
		getBuildVersion();
		Log.checkVerboseLogSetting();
	}
	
	/**
	 * Renitialise the game and the context in which it's being rendered
	 * 
	 * @throws SlickException Indicates a failure rerun initialisation routines
	 */
	public void reinit() throws SlickException {
	}
	
	/**
	 * Get the build number of slick 
	 * 
	 * @return The build number of slick
	 */
	public static int getBuildVersion() {
		try {
			Properties props = new Properties();
			props.load(ResourceLoader.getResourceAsStream("version"));
			
			int build = Integer.parseInt(props.getProperty("build"));
			Log.info("Slick Build #"+build);
			
			return build;
		} catch (Exception e) {
			Log.error("Unable to determine Slick build number");
			return -1;
		}
	}
	
	/**
	 * Get the default system font
	 * 
	 * @return The default system font
	 */
	public Font getDefaultFont() {
		return defaultFont;
	}
	
	/**
	 * Check if sound effects are enabled
	 * 
	 * @return True if sound effects are enabled
	 */
	public boolean isSoundOn() {
		return SoundStore.get().soundsOn();
	}

	/**
	 * Check if music is enabled
	 * 
	 * @return True if music is enabled
	 */
	public boolean isMusicOn() {
		return SoundStore.get().musicOn();
	}
	
	/**
	 * Indicate whether music should be enabled
	 * 
	 * @param on True if music should be enabled
	 */ 
	public void setMusicOn(boolean on) {
		SoundStore.get().setMusicOn(on);
	}

	/**
	 * Indicate whether sound effects should be enabled
	 * 
	 * @param on True if sound effects should be enabled
	 */ 
	public void setSoundOn(boolean on) {
		SoundStore.get().setSoundsOn(on);
	}
	
	/**
	 * Get the width of the standard screen resolution
	 * 
	 * @return The screen width
	 */
	public abstract int getScreenWidth();
	
	/**
	 * Get the height of the standard screen resolution
	 * 
	 * @return The screen height
	 */
	public abstract int getScreenHeight();
	
	/**
	 * Get the width of the game canvas
	 * 
	 * @return The width of the game canvas
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Get the height of the game canvas
	 * 
	 * @return The height of the game canvas
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Set the icon to be displayed if possible in this type of
	 * container
	 * 
	 * @param ref The reference to the icon to be displayed
	 * @throws SlickException Indicates a failure to load the icon
	 */
	public abstract void setIcon(String ref) throws SlickException;
	
	/**
	 * Set the icons to be used for this application. Note that the size of the icon
	 * defines how it will be used. Important ones to note
	 * 
	 * Windows window icon must be 16x16
	 * Windows alt-tab icon must be 24x24 or 32x32 depending on Windows version (XP=32)
	 * 
	 * @param refs The reference to the icon to be displayed
	 * @throws SlickException Indicates a failure to load the icon
	 */
	public abstract void setIcons(String[] refs) throws SlickException;
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	/**
	 * Sleep for a given period
	 * 
	 * @param milliseconds The period to sleep for in milliseconds
	 */
	public void sleep(int milliseconds) {
		long target = getTime()+milliseconds;
		while (getTime() < target) {
			try { Thread.sleep(1); } catch (Exception e) {}
		}
	}
	
	/**
	 * Set the mouse cursor to be displayed - this is a hardware cursor and hence
	 * shouldn't have any impact on FPS.
	 * 
	 * @param ref The location of the image to be loaded for the cursor
	 * @param hotSpotX The x coordinate of the hotspot within the cursor image
	 * @param hotSpotY The y coordinate of the hotspot within the cursor image
	 * @throws SlickException Indicates a failure to load the cursor image or create the hardware cursor
	 */
	public abstract void setMouseCursor(String ref, int hotSpotX, int hotSpotY) throws SlickException;

	/**
	 * Set the mouse cursor to be displayed - this is a hardware cursor and hence
	 * shouldn't have any impact on FPS.
	 * 
	 * @param data The image data from which the cursor can be construted
	 * @param hotSpotX The x coordinate of the hotspot within the cursor image
	 * @param hotSpotY The y coordinate of the hotspot within the cursor image
	 * @throws SlickException Indicates a failure to load the cursor image or create the hardware cursor
	 */
	public abstract void setMouseCursor(ImageData data, int hotSpotX, int hotSpotY) throws SlickException;

	/**
	 * Set the mouse cursor to be displayed - this is a hardware cursor and hence
	 * shouldn't have any impact on FPS.
	 * 
	 * @param cursor The cursor to use
	 * @param hotSpotX The x coordinate of the hotspot within the cursor image
	 * @param hotSpotY The y coordinate of the hotspot within the cursor image
	 * @throws SlickException Indicates a failure to load the cursor image or create the hardware cursor
	 */
	public abstract void setMouseCursor(Cursor cursor, int hotSpotX, int hotSpotY) throws SlickException;
	
	/**
	 * Get the input system
	 * 
	 * @return The input system available to this game container
	 */
	public Input getInput() {
		return input;
	}

	/**
	 * Get the current recorded FPS (frames per second)
	 * 
	 * @return The current FPS
	 */
	public int getFPS() {
		return recordedFPS;
	}
	
	/**
	 * Indicate whether mouse cursor should be grabbed or not
	 * 
	 * @param grabbed True if mouse cursor should be grabbed
	 */
	public abstract void setMouseGrabbed(boolean grabbed);
	
	/**
	 * Retrieve the time taken to render the last frame, i.e. the change in time - delta.
	 * 
	 * @return The time taken to render the last frame
	 */
	protected int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;
		
		return delta;
	}
	
	/**
	 * Updated the FPS counter
	 */
	protected void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			lastFPS = getTime();
			recordedFPS = fps;
			fps = 0;
		}
		fps++;
	}
	
	/**
	 * Set the minimum amount of time in milliseonds that has to 
	 * pass before update() is called on the container game. This gives
	 * a way to limit logic updates compared to renders.
	 * 
	 * @param interval The minimum interval between logic updates
	 */
	public void setMinimumLogicUpdateInterval(int interval) {
		minimumLogicInterval = interval;
	}

	/**
	 * Set the maximum amount of time in milliseconds that can passed
	 * into the update method. Useful for collision detection without
	 * sweeping.
	 * 
	 * @param interval The maximum interval between logic updates
	 */
	public void setMaximumLogicUpdateInterval(int interval) {
		maximumLogicInterval = interval;
	}
	
	/**
	 * Update and render the game
	 * 
	 * @param delta The change in time since last update and render
	 * @throws SlickException Indicates an internal fault to the game.
	 */
	protected void updateAndRender(int delta) throws SlickException {
		storedDelta += delta;
		input.poll(width, height);
		
		SoundStore.get().poll(delta);
		if (storedDelta >= minimumLogicInterval) {
			try {
				if (maximumLogicInterval != 0) {
					long cycles = storedDelta / maximumLogicInterval;
					for (int i=0;i<cycles;i++) {
						game.update(this, (int) maximumLogicInterval);
					}
					game.update(this, (int) (delta % maximumLogicInterval));
				} else {
					game.update(this, (int) storedDelta);
				}
				
				storedDelta = 0;
			} catch (Throwable e) {
				Log.error(e);
				throw new SlickException("Game.update() failure - check the game code.");
			}
		}
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
		
		graphics.resetFont();
		graphics.resetLineWidth();
		graphics.setAntiAlias(false);
		try {
			game.render(this, graphics);
		} catch (Throwable e) {
			Log.error(e);
			throw new SlickException("Game.render() failure - check the game code.");
		}
		graphics.resetTransform();
		
		if (showFPS) {
			defaultFont.drawString(10, 10, "FPS: "+recordedFPS);
		}
		
		if (targetFPS != -1) {
			Display.sync(targetFPS);
		}
	}
	
	/**
	 * Initialise the GL context
	 */
	protected void initGL() {
		Log.info("Starting display "+width+"x"+height);
		String extensions = GL11.glGetString(GL11.GL_EXTENSIONS);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);        
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);                    
        
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);                
        GL11.glClearDepth(1);                                       
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glViewport(0,0,width,height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		if (input == null) {
			input = new Input(height);
		}
		input.init(height);
		input.removeListener(lastGame);
		input.addListener(game);
		lastGame = game;
	}
	
	/**
	 * Initialise the system components, OpenGL and OpenAL.
	 * 
	 * @throws SlickException Indicates a failure to create a native handler
	 */
	protected void initSystem() throws SlickException {
		initGL();
		
		graphics = new Graphics(width, height);
		defaultFont = graphics.getFont();
	}
	
	/**
	 * Enter the orthographic mode 
	 */
	protected void enterOrtho() {
		enterOrtho(width, height);
	}
	
	/**
	 * Indicate whether the container should show the FPS
	 * 
	 * @param show True if the container should show the FPS
	 */
	public void setShowFPS(boolean show) {
		showFPS = show;
	}
	
	/**
	 * Check if the FPS is currently showing
	 * 
	 * @return True if the FPS is showing
	 */
	public boolean isShowingFPS() {
		return showFPS;
	}
	
	/**
	 * Set the target fps we're hoping to get
	 * 
	 * @param fps The target fps we're hoping to get
	 */
	public void setTargetFrameRate(int fps) {
		targetFPS = fps;
	}
	
	/**
	 * Indicate whether the display should be synced to the 
	 * vertical refresh (stops tearing)
	 * 
	 * @param vsync True if we want to sync to vertical refresh
	 */
	public void setVSync(boolean vsync) {
		Display.setVSyncEnabled(vsync);
	}
	
	/**
	 * True if the game is running
	 * 
	 * @return True if the game is running
	 */
	protected boolean running() {
		return running;
	}
	
	/**
	 * Inidcate we want verbose logging
	 * 
	 * @param verbose True if we want verbose logging (INFO and DEBUG)
	 */
	public void setVerbose(boolean verbose) {
		Log.setVerbose(verbose);
	}
	
	/**
	 * Cause the game to exit and shutdown cleanly
	 */
	public void exit() {
		running = false;
	}
	
	/**
	 * Check if the game currently has focus
	 * 
	 * @return True if the game currently has focus
	 */
	public abstract boolean hasFocus();
	
	/**
	 * Enter the orthographic mode 
	 * 
	 * @param xsize The size of the panel being used
	 * @param ysize The size of the panel being used
	 */
	protected void enterOrtho(int xsize, int ysize) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glTranslatef((width-xsize)/2,
						  (height-ysize)/2,0);
	}
}
