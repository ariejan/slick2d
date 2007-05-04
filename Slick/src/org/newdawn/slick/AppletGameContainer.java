package org.newdawn.slick;

import java.applet.Applet;
import java.awt.BorderLayout;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.AWTInputAdapter;
import org.lwjgl.util.applet.LWJGLInstaller;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.Log;

/**
 * A game container that displays the game as an applet. Note however that 
 * the actual game container implementation is an internal class which 
 * can be obtained with the getContainer() method - this is due to the
 * Applet being a class wrap than an interface. 
 *
 * @author kevin
 */
public class AppletGameContainer extends Applet {
	/** The GL Canvas used for this container */
	private ContainerPanel canvas;
	/** The actual container implementation */
	private Container container;
	
	/**
	 * @see java.applet.Applet#destroy()
	 */
	public void destroy() {
		super.destroy();
		container.stopApplet();
		
		Log.info("Clear up");
		AWTInputAdapter.destroy();
		Mouse.destroy();
		Keyboard.destroy();
		AL.destroy();
	}
	
	/**
	 * @see java.applet.Applet#start()
	 */
	public void start() {
	}

	/**
	 * @see java.applet.Applet#stop()
	 */
	public void stop() {
	}
	
	/**
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		try {
			LWJGLInstaller.tempInstall();
		} catch (Exception le) {
			le.printStackTrace();
			return;
		}
		
		setLayout(new BorderLayout());
		
		try {
			Game game = (Game) Class.forName(getParameter("game")).newInstance();

			container = new Container(game);
			canvas = new ContainerPanel(container);
			canvas.setSize(getWidth(), getHeight());
			
			add(canvas);
			canvas.setFocusable(true);
			canvas.requestFocus();
		} catch (Exception e) {
			Log.error(e);
			throw new RuntimeException("Unable to create game container");
		}
	}
	
	/**
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(java.awt.Graphics g) {
		canvas.update(g);
	}
	
	/**
	 * Get the GameContainer providing this applet
	 * 
	 * @return The game container providing this applet
	 */
	public GameContainer getContainer() {
		return container;
	}
	
	/**
	 * Create a new panel to display the GL context
	 *
	 * @author kevin
	 */
	public class ContainerPanel extends AWTGLCanvas {
		/** The container being displayed on this canvas */
		private Container container;
		
		/**
		 * Create a new panel
		 * 
		 * @param container The container proxied by this canvas
		 * @throws LWJGLException
		 */
		public ContainerPanel(Container container) throws LWJGLException {
			super();
			
			this.container = container;
		}
		
		/**
		 * Set the container held by this canvas
		 * 
		 * @param container The container rendered on this canvas
		 */
		public void setContainer(Container container) {
			this.container = container;
		}
		
		/**
		 * @see org.lwjgl.opengl.AWTGLCanvas#initGL()
		 */
		protected void initGL() {
			try {
				TextureLoader.get().clear();
				SoundStore.get().clear();

				setVSyncEnabled(true);
				AWTInputAdapter.create(this);
				container.initApplet();
			} catch (Exception e) {
				Log.error(e);
				container.stopApplet();
			}
		}

		/**
		 * @see org.lwjgl.opengl.AWTGLCanvas#paintGL()
		 */
		protected void paintGL() {
			Mouse.poll();
			Keyboard.poll();
			Controllers.poll();
			
			try {
				container.pollApplet(isVisible());
			} catch (SlickException e) {
				Log.error(e);
				container.stopApplet();
			}
			
			try {
				swapBuffers();
				if (isVisible()) {
					repaint();
				}
			} catch (Exception e) {/*OK*/
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * A game container to provide the applet context
	 *
	 * @author kevin
	 */
	public class Container extends GameContainer {
		/**
		 * Create a new container wrapped round the game
		 * 
		 * @param game The game to be held in this container
		 */
		public Container(Game game) {
			super(game);
			
			width = AppletGameContainer.this.getWidth();
			height = AppletGameContainer.this.getHeight();
		}

		/**
		 * Initiliase based on Applet init
		 * 
		 * @throws SlickException Indicates a failure to inialise the basic framework
		 */
		public void initApplet() throws SlickException {
			initSystem();
			enterOrtho();

			try {
				getInput().initControllers();
			} catch (SlickException e) {
				Log.info("Controllers not available");
			} catch (Throwable e) {
				Log.info("Controllers not available");
			}
			
			game.init(this);
			getDelta();
		}
		
		/**
		 * Stop the applet play back
		 */
		public void stopApplet() {
			running = false;
		}
		
		/**
		 * Poll the applets update and render loops
		 * 
		 * @param visible True if the applet is currently visible
		 * @throws SlickException Indicates a failure the internal game
		 */
		public void pollApplet(boolean visible) throws SlickException {
			if (!running) {
				return;
			}
			
			int delta = getDelta();
			
			if (!visible) {
				try { Thread.sleep(100); } catch (Exception e) {}
			} else {
				updateAndRender(delta);
			}
			
			updateFPS();
		}
		
		/**
		 * @see org.newdawn.slick.GameContainer#getScreenHeight()
		 */
		public int getScreenHeight() {
			return 0;
		}

		/**
		 * @see org.newdawn.slick.GameContainer#getScreenWidth()
		 */
		public int getScreenWidth() {
			return 0;
		}

		/**
		 * @see org.newdawn.slick.GameContainer#hasFocus()
		 */
		public boolean hasFocus() {
			return AppletGameContainer.this.hasFocus();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setIcon(java.lang.String)
		 */
		public void setIcon(String ref) throws SlickException {
			// unsupported in an applet
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setMouseGrabbed(boolean)
		 */
		public void setMouseGrabbed(boolean grabbed) {
			// unsupported in an applet
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setMouseCursor(java.lang.String, int, int)
		 */
		public void setMouseCursor(String ref, int hotSpotX, int hotSpotY) throws SlickException {
			// unsupported in an applet
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setIcons(java.lang.String[])
		 */
		public void setIcons(String[] refs) throws SlickException {
			// unsupported in an applet
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setMouseCursor(org.newdawn.slick.opengl.ImageData, int, int)
		 */
		public void setMouseCursor(ImageData data, int hotSpotX, int hotSpotY) throws SlickException {
			// unsupported in an applet
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setMouseCursor(org.lwjgl.input.Cursor, int, int)
		 */
		public void setMouseCursor(Cursor cursor, int hotSpotX, int hotSpotY) throws SlickException {
			// unsupported in an applet
		}
		
	}
}
