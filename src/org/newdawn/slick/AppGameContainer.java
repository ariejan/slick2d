package org.newdawn.slick;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.TGAImageData;
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
	
	/** True if we're running in fullscreen mode */
	private boolean fullscreen;
	/** The dummy stream used to override output for LWJGL */
	private PrintStream dummyStream;
	
	/**
	 * Create a new container wrapping a game
	 * 
	 * @param game The game to be wrapped
	 * @throws SlickException Indicates a failure to initialise the display
	 */
	public AppGameContainer(Game game) throws SlickException {
		this(game,640,480,false);
		
		dummyStream = new PrintStream(new NullOutputStream());
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
		
		setDisplayMode(width,height,false);
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
		try {
			targetDisplayMode = null;
			if (fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				for (int i=0;i<modes.length;i++) {
					DisplayMode current = modes[i];
					
					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() == originalDisplayMode.getFrequency())) {
							targetDisplayMode = current;
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
			this.fullscreen = fullscreen;

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);
			
			if (Display.isCreated()) {
				initSystem();
				enterOrtho();
			}
		} catch (LWJGLException e) {
			throw new SlickException("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen, e);
		}
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
		try {
			Display.setFullscreen(fullscreen);
		} catch (LWJGLException e) {
			throw new SlickException("Unable to set fullscreen="+fullscreen, e);
		}
	}
	
	/**
	 * Start running the game
	 * 
	 * @throws SlickException Indicates a failure to initialise the system
	 */
	public void start() throws SlickException {
		try {
			getInput().initControllers();
		} catch (SlickException e) {
			Log.info("Controllers not available");
		} catch (Throwable e) {
			Log.info("Controllers not available");
		}
		
		if (targetDisplayMode == null) {
			setDisplayMode(640,480,false);
		}

		Display.setTitle(game.getTitle());

		System.out.println("LWJGL Version: "+Sys.getVersion());
		
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
		
		game.init(this);
		while (running()) {
			int delta = getDelta();
			
			if (!Display.isVisible()) {
				try { Thread.sleep(100); } catch (Exception e) {}
			} else {
				updateAndRender(delta);
			}
			
			updateFPS();

			Display.update();
			
			if (Display.isCloseRequested()) {
				if (game.closeRequested()) {
					running = false;
				}
			}
		}
	
		Display.destroy();
		System.exit(0);
	}

	/**
	 * @see org.newdawn.slick.GameContainer#setIcon(java.lang.String)
	 */
	public void setIcon(String ref) throws SlickException {
		ImageData data;
		boolean flip = false;
		
		if (ref.endsWith(".tga")) {
			data = new TGAImageData();
		} else {
			data = new ImageIOImageData();
			flip = true;
		}
		
		try {
			ByteBuffer buf = data.loadImage(ResourceLoader.getResourceAsStream(ref), flip);
			Display.setIcon(new ByteBuffer[] {buf});
		} catch (Exception e) {
			Log.error(e);
			throw new SlickException("Failed to set the icon");
		}
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
}
