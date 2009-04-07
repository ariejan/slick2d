package org.newdawn.slick;

import java.awt.Canvas;

import javax.swing.SwingUtilities;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;

/**
 * A game container that displays the game on an AWT Canvas.
 * 
 * @author kevin
 */
public class CanvasGameContainer extends Canvas {
	/** The actual container implementation */
	protected Container container;
	/** The game being held in this container */
	protected Game game;

	/**
	 * Create a new panel
	 * 
	 * @param game The game being held
	 * @throws SlickException Indicates a failure during creation of the container
	 */
	public CanvasGameContainer(Game game) throws SlickException {
		super();

		this.game = game;
		setIgnoreRepaint(true);
		requestFocus();
		setSize(500,500);
		
		container = new Container(game);
		container.setForceExit(false);
	}

	/**
	 * Start the game container rendering
	 * 
	 * @throws SlickException Indicates a failure during game execution
	 */
	public void start() throws SlickException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Input.disableControllers();
					
					try {
						Display.setParent(CanvasGameContainer.this);
					} catch (LWJGLException e) {
						throw new SlickException("Failed to setParent of canvas", e);
					}
					
					container.setup();
					scheduleUpdate();
				} catch (SlickException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		});
	}
	
	/**
	 * Schedule an update on the EDT
	 */
	private void scheduleUpdate() {
		Thread t = new Thread() {
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							container.gameLoop();
						} catch (SlickException e) {
							e.printStackTrace();
						}
						container.checkDimensions();
						scheduleUpdate();
					}
				});
			}
		};
		t.start();
	}
	/**
	 * Dispose the container and any resources it holds
	 */
	public void dispose() {
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
	 * A game container to provide the canvas context
	 * 
	 * @author kevin
	 */
	private class Container extends AppGameContainer {
		/**
		 * Create a new container wrapped round the game
		 * 
		 * @param game
		 *            The game to be held in this container
		 * @throws SlickException Indicates a failure to initialise
		 */
		public Container(Game game) throws SlickException {
			super(game, CanvasGameContainer.this.getWidth(), CanvasGameContainer.this.getHeight(), false);

			width = CanvasGameContainer.this.getWidth();
			height = CanvasGameContainer.this.getHeight();
			
			enableSharedContext();
		}

		/**
		 * Updated the FPS counter
		 */
		protected void updateFPS() {
			super.updateFPS();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#running()
		 */
		protected boolean running() {
			return super.running() && CanvasGameContainer.this.isDisplayable();
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
		 * Check the dimensions of the canvas match the display
		 */
		public void checkDimensions() {
			if ((width != CanvasGameContainer.this.getWidth()) ||
			    (height != CanvasGameContainer.this.getHeight())) {
				
				try {
					setDisplayMode(CanvasGameContainer.this.getWidth(), 
								   CanvasGameContainer.this.getHeight(), false);
				} catch (SlickException e) {
					Log.error(e);
				}
			}
		}
	}
}
