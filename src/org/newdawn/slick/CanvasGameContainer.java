package org.newdawn.slick;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.AWTInputAdapter;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A game container that displays the game on an AWT Canvas.
 * 
 * @author kevin
 */
public class CanvasGameContainer extends AWTGLCanvas {
	/** The actual container implementation */
	protected Container container;

	/** The game being held in this container */
	protected Game game;

	/** True if a reinit is required */
	protected boolean reinit = false;

	/** True if we've already created the AWTInputAdapter */
	protected boolean createdAdapter = false;

	/**
	 * Create a new panel
	 * 
	 * @param game
	 *            The game being held
	 * @throws LWJGLException
	 */
	public CanvasGameContainer(Game game) throws LWJGLException {
		super();

		this.game = game;
		addComponentListener(new ComponentListener() {

			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
				if (container != null) {
					container.resized();
				}
			}

			public void componentShown(ComponentEvent e) {
			}

		});
	}

	/**
	 * Dispose the container and any resources it holds
	 */
	public void dispose() {
		container.stopRendering();

		Log.info("Clear up");
		AWTInputAdapter.destroy();
		Mouse.destroy();
		Keyboard.destroy();
		AL.destroy();
		createdAdapter = false;
	}

	/**
	 * Get the GameContainer providing this canvas
	 * 
	 * @return The game container providing this canvas
	 */
	public GameContainer getContainer() {
		return container;
	}

	/**
	 * @see org.lwjgl.opengl.AWTGLCanvas#initGL()
	 */
	protected void initGL() {
		container = new Container(game);

		try {
			InternalTextureLoader.get().clear();
			SoundStore.get().clear();

			setVSyncEnabled(true);
			if (!createdAdapter) {
				AWTInputAdapter.create(this);
				createdAdapter = true;
			}
			container.initLocal();
		} catch (Exception e) {
			Log.error(e);
			container.stopRendering();
		}
	}

	/**
	 * @see org.lwjgl.opengl.AWTGLCanvas#paintGL()
	 */
	protected void paintGL() {
		if (reinit) {
			container.initGL();
			container.enterOrtho();
			reinit = false;
		}

		Mouse.poll();
		Keyboard.poll();
		Controllers.poll();

		try {
			container.pollContainer(isVisible());
		} catch (SlickException e) {
			Log.error(e);
			container.stopRendering();
		}

		try {
			swapBuffers();
			if (isVisible()) {
				repaint();
			}
		} catch (Exception e) {/* OK */
			e.printStackTrace();
		}
	}

	/**
	 * A game container to provide the canvas context
	 * 
	 * @author kevin
	 */
	private class Container extends GameContainer {

		/**
		 * Create a new container wrapped round the game
		 * 
		 * @param game
		 *            The game to be held in this container
		 */
		public Container(Game game) {
			super(game);

			width = CanvasGameContainer.this.getWidth();
			height = CanvasGameContainer.this.getHeight();
		}

		/**
		 * Notification that the canvas was resized
		 */
		public void resized() {
			width = CanvasGameContainer.this.getWidth();
			height = CanvasGameContainer.this.getHeight();
			reinit = true;
		}

		/**
		 * Initiliase based on Canvas init
		 * 
		 * @throws SlickException
		 *             Indicates a failure to inialise the basic framework
		 */
		public void initLocal() throws SlickException {
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
		 * Stop the canvas play back
		 */
		public void stopRendering() {
			running = false;
		}

		/**
		 * Poll the canvas update and render loops
		 * 
		 * @param visible
		 *            True if the canvas is currently visible
		 * @throws SlickException
		 *             Indicates a failure the internal game
		 */
		public void pollContainer(boolean visible) throws SlickException {
			if (!running) {
				return;
			}

			int delta = getDelta();

			if (!visible) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
			} else {
				updateAndRender(delta);
			}

			updateFPS();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#getHeight()
		 */
		public int getHeight() {
			return CanvasGameContainer.this.getHeight();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#getWidth()
		 */
		public int getWidth() {
			return CanvasGameContainer.this.getWidth();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#getScreenHeight()
		 */
		public int getScreenHeight() {
			return Toolkit.getDefaultToolkit().getScreenSize().width;
		}

		/**
		 * @see org.newdawn.slick.GameContainer#getScreenWidth()
		 */
		public int getScreenWidth() {
			return Toolkit.getDefaultToolkit().getScreenSize().height;
		}

		/**
		 * @see org.newdawn.slick.GameContainer#hasFocus()
		 */
		public boolean hasFocus() {
			return CanvasGameContainer.this.hasFocus();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setIcon(java.lang.String)
		 */
		public void setIcon(String ref) throws SlickException {
			// unsupported in an canvas
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setMouseGrabbed(boolean)
		 */
		public void setMouseGrabbed(boolean grabbed) {
			// unsupported in an canvas
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setMouseCursor(java.lang.String,
		 *      int, int)
		 */
		public void setMouseCursor(String ref, int hotSpotX, int hotSpotY)
				throws SlickException {
			try {
				BufferedImage image = ImageIO.read(ResourceLoader.getResourceAsStream(ref));
				java.awt.Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(hotSpotX, hotSpotY), ref);
				setCursor(cursor);
			} catch (IOException e) { 
				throw new SlickException("Unable to create cursor for : "+ref, e);
			}
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setIcons(java.lang.String[])
		 */
		public void setIcons(String[] refs) throws SlickException {
			// unsupported in an canvas
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setMouseCursor(org.newdawn.slick.opengl.ImageData,
		 *      int, int)
		 */
		public void setMouseCursor(ImageData data, int hotSpotX, int hotSpotY)
				throws SlickException {
			// unsupported in an canvas
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setMouseCursor(org.lwjgl.input.Cursor,
		 *      int, int)
		 */
		public void setMouseCursor(Cursor cursor, int hotSpotX, int hotSpotY)
				throws SlickException {
			// unsupported in an canvas
		}

		/**
		 * @see org.newdawn.slick.GameContainer#alwaysRender()
		 */
		protected boolean alwaysRender() {
			return true;
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setDefaultMouseCursor()
		 */
		public void setDefaultMouseCursor() {
			// unsupported in an canvas
		}

		/**
		 * @see org.newdawn.slick.GameContainer#setMouseCursor(org.newdawn.slick.Image,
		 *      int, int)
		 */
		public void setMouseCursor(Image image, int hotSpotX, int hotSpotY)
				throws SlickException {

		}
	}
}
