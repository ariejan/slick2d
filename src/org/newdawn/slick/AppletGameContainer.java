package org.newdawn.slick;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.util.Log;

/**
 * A game container that displays the game as an applet. Note however that the
 * actual game container implementation is an internal class which can be
 * obtained with the getContainer() method - this is due to the Applet being a
 * class wrap than an interface.
 * 
 * @author kevin
 */
public class AppletGameContainer extends Applet {
	/** The GL Canvas used for this container */
	protected ContainerPanel canvas;
	/** The actual container implementation */
	protected Container container;
	/** The parent of the display */
	protected Canvas displayParent;
	/** The thread that is looping for the game */
	protected Thread gameThread;
    /** True if the applet has been started */
	protected boolean started = false;

	/**
	 * @see java.applet.Applet#destroy()
	 */
	public void destroy() {
		remove(displayParent);
		Log.info("Clear up");
	}

	/**
	 * Clean up the LWJGL resources
	 */
	private void destroyLWJGL() {
		container.stopApplet();
		
		try {
			gameThread.join();
		} catch (InterruptedException e) {
			Log.error(e);
		}
	}

	/**
	 * @see java.applet.Applet#start()
	 */
	public void start() {
		if (started) {
			return;
		}
		
		started = true;
		gameThread = new Thread() {
			public void run() {
				canvas.start();
			}
		};

		gameThread.start();
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
		setLayout(new BorderLayout());

		try {
			Game game = (Game) Class.forName(getParameter("game")).newInstance();
			
			container = new Container(game);
			canvas = new ContainerPanel(container);
			displayParent = new Canvas() {
				
				public final void removeNotify() {
					destroyLWJGL();
					super.removeNotify();
				}

			};

			displayParent.setSize(getWidth(), getHeight());
			add(displayParent);
			displayParent.setFocusable(true);
			displayParent.requestFocus();
			displayParent.setIgnoreRepaint(true);
			setVisible(true);
		} catch (Exception e) {
			Log.error(e);
			throw new RuntimeException("Unable to create game container");
		}
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
	public class ContainerPanel {
		/** The container being displayed on this canvas */
		private Container container;

		/**
		 * Create a new panel
		 * 
		 * @param container The container we're running 
		 */
		public ContainerPanel(Container container) {
			this.container = container;
		}

		/**
		 * Start the rendering cycle
		 */
		public void start() {
			try {
				Display.setParent(displayParent);
				Display.setVSyncEnabled(true);

    			PixelFormat format = new PixelFormat(8,8,0);
    			if (container.supportsMultiSample() && (container.getSamples() > 0)) {
    				format = new PixelFormat(8,8,0,2);
    			}
				Display.create(format);
				initGL();
			} catch (LWJGLException e) {
				Log.error(e);
				try {
					Display.create();
					initGL();
				} catch (LWJGLException x) {
					Log.error(x);
				}
			}
			displayParent.requestFocus();
			container.runloop();
		}

		/**
		 * Initialise GL state
		 */
		protected void initGL() {
			try {
				InternalTextureLoader.get().clear();
				SoundStore.get().clear();

				container.initApplet();
			} catch (Exception e) {
				Log.error(e);
				container.stopApplet();
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
		 * Check if the applet is currently running
		 * 
		 * @return True if the applet is running
		 */
		public boolean isRunning() {
			return running;
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
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					// interrupt, ignore
				}
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
			return true;
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

		/**
		 * @see org.newdawn.slick.GameContainer#setDefaultMouseCursor()
		 */
		public void setDefaultMouseCursor() {
		}

		/**
		 * 
		 * @see org.newdawn.slick.GameContainer#setMouseCursor(org.newdawn.slick.Image,
		 *      int, int)
		 * 
		 */

		public void setMouseCursor(Image image, int hotSpotX, int hotSpotY)
				throws SlickException {
		}

		public boolean isFullscreen() {
			return Display.isFullscreen();
		}

		public void setFullscreen(boolean fullscreen) throws SlickException {
			if (fullscreen == isFullscreen()) {
				return;
			}

			try {
				if (fullscreen) {
					// get current screen resolution
					int screenWidth = Display.getDisplayMode().getWidth();
					int screenHeight = Display.getDisplayMode().getHeight();

					// calculate aspect ratio
					float gameAspectRatio = (float) width / height;
					float screenAspectRatio = (float) screenWidth
							/ screenHeight;

					int newWidth;
					int newHeight;

					// get new screen resolution to match aspect ratio

					if (gameAspectRatio >= screenAspectRatio) {
						newWidth = screenWidth;
						newHeight = (int) (height / ((float) width / screenWidth));
					} else {
						newWidth = (int) (width / ((float) height / screenHeight));
						newHeight = screenHeight;
					}

					// center new screen
					int xoffset = (screenWidth - newWidth) / 2;
					int yoffset = (screenHeight - newHeight) / 2;

					// scale game to match new resolution
					GL11.glViewport(xoffset, yoffset, newWidth, newHeight);

					enterOrtho();

					// fix input to match new resolution
					this.getInput().setOffset(
							-xoffset * (float) width / newWidth,
							-yoffset * (float) height / newHeight);

					this.getInput().setScale((float) width / newWidth,
							(float) height / newHeight);

					width = screenWidth;
					height = screenHeight;
					Display.setFullscreen(true);
				} else {
					// restore input
					this.getInput().setOffset(0, 0);
					this.getInput().setScale(1, 1);
					width = AppletGameContainer.this.getWidth();
					height = AppletGameContainer.this.getHeight();
					GL11.glViewport(0, 0, width, height);

					enterOrtho();

					Display.setFullscreen(false);
				}
			} catch (LWJGLException e) {
				Log.error(e);
			}

		}

		/**
		 * The running game loop
		 */
		public void runloop() {
			while (running) {
				int delta = getDelta();

				try {
					updateAndRender(delta);
				} catch (SlickException e) {
					e.printStackTrace();
				}

				updateFPS();
				Display.update();
			}

			Display.destroy();
		}
	}

}