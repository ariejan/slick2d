package org.newdawn.slick;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.TGAImageData;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A game container that will display the game as an stand alone 
 * application.
 *
 * @author kevin
 */
public class AppGameContainer extends GameContainer {
	static {
		AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
        		try {
        			Display.getDisplayMode();
        		} catch (Exception e) {
        			Log.error(e);
        		}
				return null;
            }});
	}
	
	/** The original display mode before we tampered with things */
	private DisplayMode originalDisplayMode;
	/** The display mode we're going to try and use */
	private DisplayMode targetDisplayMode;
	
	/**
	 * Create a new container wrapping a game
	 * 
	 * @param game The game to be wrapped
	 * @throws SlickException Indicates a failure to initialise the display
	 */
	public AppGameContainer(Game game) throws SlickException {
		this(game,640,480,false);
	}

	/**
	 * Create a new container wrapping a game
	 * 
	 * @param game The game to be wrapped
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 * @throws SlickException Indicates a failure to initialise the display
	 */
	public AppGameContainer(Game game,int width,int height,boolean fullscreen) throws SlickException {
		super(game);
		
		originalDisplayMode = Display.getDisplayMode();
		
		setDisplayMode(width,height,fullscreen);
	}
	
	/**
	 * Set the title of the window
	 * 
	 * @param title The title to set on the window
	 */
	public void setTitle(String title) {
		Display.setTitle(title);
	}
	
	/**
	 * Set the display mode to be used 
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 * @throws SlickException Indicates a failure to initialise the display
	 */
	public void setDisplayMode(int width, int height, boolean fullscreen) throws SlickException {
		if ((this.width == width) && (this.height == height) && (isFullscreen() == fullscreen)) {
			return;
		}
		
		try {
			targetDisplayMode = null;
			if (fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;
				
				for (int i=0;i<modes.length;i++) {
					DisplayMode current = modes[i];
					
					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width,height);
			}
			
			if (targetDisplayMode == null) {
				throw new SlickException("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
			}
			
			this.width = width;
			this.height = height;

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);
			
			if (Display.isCreated()) {
				initGL();
				enterOrtho();
			} 
		} catch (LWJGLException e) {
			throw new SlickException("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen, e);
		}
		
		getDelta();
	}
	
	/**
	 * Check if the display is in fullscreen mode
	 * 
	 * @return True if the display is in fullscreen mode
	 */
	public boolean isFullscreen() {
		return Display.isFullscreen();
	}
	
	/**
	 * Indicate whether we want to be in fullscreen mode. Note that the current
	 * display mode must be valid as a fullscreen mode for this to work
	 * 
	 * @param fullscreen True if we want to be in fullscreen mode
	 * @throws SlickException Indicates we failed to change the display mode
	 */
	public void setFullscreen(boolean fullscreen) throws SlickException {
		if (isFullscreen() == fullscreen) {
			return;
		}
		
		if (!fullscreen) {
			try {
				Display.setFullscreen(fullscreen);
			} catch (LWJGLException e) {
				throw new SlickException("Unable to set fullscreen="+fullscreen, e);
			}
		} else {
			setDisplayMode(width, height, fullscreen);
		}
		getDelta();
	}

	/**
	 * @see org.newdawn.slick.GameContainer#setMouseCursor(java.lang.String, int, int)
	 */
	public void setMouseCursor(String ref, int hotSpotX, int hotSpotY) throws SlickException {
		try {
			Cursor cursor = CursorLoader.get().getCursor(ref, hotSpotX, hotSpotY);
			Mouse.setNativeCursor(cursor);
		} catch (Exception e) {
			Log.error("Failed to load and apply cursor.", e);
		}
	}
	
	/**
	 * @see org.newdawn.slick.GameContainer#setMouseCursor(org.newdawn.slick.opengl.ImageData, int, int)
	 */
	public void setMouseCursor(ImageData data, int hotSpotX, int hotSpotY) throws SlickException {
		try {
			Cursor cursor = CursorLoader.get().getCursor(data, hotSpotX, hotSpotY);
			Mouse.setNativeCursor(cursor);
		} catch (Exception e) {
			Log.error("Failed to load and apply cursor.", e);
		}
	}
	
	/**
	 * @see org.newdawn.slick.GameContainer#setMouseCursor(org.lwjgl.input.Cursor, int, int)
	 */
	public void setMouseCursor(Cursor cursor, int hotSpotX, int hotSpotY) throws SlickException {
		try {
			Mouse.setNativeCursor(cursor);
		} catch (Exception e) {
			Log.error("Failed to load and apply cursor.", e);
		}
	}
	
	/**
	 * @see org.newdawn.slick.GameContainer#reinit()
	 */
	public void reinit() throws SlickException {
		TextureLoader.get().clear();
		SoundStore.get().clear();
		initSystem();
		enterOrtho();
		
		try {
			game.init(this);
		} catch (SlickException e) {
			Log.error(e);
			running = false;
		}
	}
	
	/**
	 * Start running the game
	 * 
	 * @throws SlickException Indicates a failure to initialise the system
	 */
	public void start() throws SlickException {
		try {
			if (targetDisplayMode == null) {
				setDisplayMode(640,480,false);
			}
	
			Display.setTitle(game.getTitle());
	
			Log.info("LWJGL Version: "+Sys.getVersion());
			Log.info("OriginalDisplayMode: "+originalDisplayMode);
			Log.info("TargetDisplayMode: "+targetDisplayMode);
			
			AccessController.doPrivileged(new PrivilegedAction() {
	            public Object run() {
	        		try {
	        			Display.create();
	        		} catch (Exception e) {
	        			Log.error(e);
	        		}
					
					return null;
	            }});
			
			if (!Display.isCreated()) {
				throw new SlickException("Failed to initialise the LWJGL display");
			}
			
			initSystem();
			enterOrtho();

			try {
				getInput().initControllers();
			} catch (SlickException e) {
				Log.info("Controllers not available");
			} catch (Throwable e) {
				Log.info("Controllers not available");
			}
			
			try {
				game.init(this);
			} catch (SlickException e) {
				Log.error(e);
				running = false;
			}
			
			getDelta();
			while (running()) {
				int delta = getDelta();
				
				if (!Display.isVisible()) {
					try { Thread.sleep(100); } catch (Exception e) {}
				} else {
					//Thread.yield();
					try {
						updateAndRender(delta);
					} catch (SlickException e) {
						Log.error(e);
						running = false;
						break;
					}
				}
				
				updateFPS();
	
				Display.update();
				
				if (Display.isCloseRequested()) {
					if (game.closeRequested()) {
						running = false;
					}
				}
			}
		} finally {
			AL.destroy();
			Display.destroy();
		}
		
		System.exit(0);
	}

	/**
	 * @see org.newdawn.slick.GameContainer#setIcon(java.lang.String)
	 */
	public void setIcon(String ref) throws SlickException {
		setIcons(new String[] {ref});
	}

	/**
	 * @see org.newdawn.slick.GameContainer#setMouseGrabbed(boolean)
	 */
	public void setMouseGrabbed(boolean grabbed) {
		Mouse.setGrabbed(grabbed);
	}

	/**
	 * @see org.newdawn.slick.GameContainer#hasFocus()
	 */
	public boolean hasFocus() {
		// hmm, not really the right thing, talk to the LWJGL guys
		return Display.isVisible();
	}

	/**
	 * @see org.newdawn.slick.GameContainer#getScreenHeight()
	 */
	public int getScreenHeight() {
		return originalDisplayMode.getHeight();
	}

	/**
	 * @see org.newdawn.slick.GameContainer#getScreenWidth()
	 */
	public int getScreenWidth() {
		return originalDisplayMode.getWidth();
	}
	
	/**
	 * A null stream to clear out those horrid errors
	 *
	 * @author kevin
	 */
	private class NullOutputStream extends OutputStream {
		/**
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
			// null implemetnation
		}
		
	}

	/**
	 * @see org.newdawn.slick.GameContainer#setIcons(java.lang.String[])
	 */
	public void setIcons(String[] refs) throws SlickException {
		ByteBuffer[] bufs = new ByteBuffer[refs.length];
		for (int i=0;i<refs.length;i++) {
			LoadableImageData data;
			boolean flip = true;
			
			if (refs[i].endsWith(".tga")) {
				data = new TGAImageData();
			} else {
				data = new ImageIOImageData();
			}
			
			try {
				bufs[i] = data.loadImage(ResourceLoader.getResourceAsStream(refs[i]), flip, false, null);
			} catch (Exception e) {
				Log.error(e);
				throw new SlickException("Failed to set the icon");
			}
		}
		
		Display.setIcon(bufs);
	}
}
