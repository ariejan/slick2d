package org.newdawn.slick.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;

/**
 * A mouse over area that can be used for menus or buttons
 * 
 * @author kevin
 */
public class MouseOverArea extends AbstractComponent {
	/** The default state */
	private static final int NORMAL = 1;

	/** The mouse down state */
	private static final int MOUSE_DOWN = 2;

	/** The mouse over state */
	private static final int MOUSE_OVER = 3;

	/** The normalImage being displayed in normal state */
	private Image normalImage;

	/** The normalImage being displayed in mouseOver state */
	private Image mouseOverImage;

	/** The normalImage being displayed in mouseDown state */
	private Image mouseDownImage;

	/** The colour used in normal state */
	private Color normalColor = Color.white;

	/** The colour used in mouseOver state */
	private Color mouseOverColor = Color.white;

	/** The colour used in mouseDown state */
	private Color mouseDownColor = Color.white;

	/** The sound for mouse over */
	private Sound mouseOverSound;

	/** The sound for mouse down */
	private Sound mouseDownSound;

	/** The location in the X coordinate */
	protected int x;

	/** The location in the Y coordinate */
	protected int y;
	
	/** The width of the field */
	private int width;

	/** The height of the field */
	private int height;

	/** The current normalImage being displayed */
	private Image currentImage;

	/** The current color being used */
	private Color currentColor;

	/** True if the mouse is over the area */
	private boolean over;

	/** True if the mouse button is pressed */
	private boolean mouseDown;
	
	/** The state of the area */
	private int state = NORMAL;

	/** True if the mouse has been up since last press */
	private boolean mouseUp;

	/**
	 * Create a new mouse over area
	 * 
	 * @param container
	 *            The container displaying the mouse over area
	 * @param image
	 *            The normalImage to display
	 * @param x
	 *            The position of the area
	 * @param y
	 *            the position of the area
	 * @param listener
	 *            A listener to add to the area
	 */
	public MouseOverArea(GUIContext container, Image image, int x, int y, ComponentListener listener) {
		this(container, image, x, y, image.getWidth(), image.getHeight());
		addListener(listener);
	}
	
	/**
	 * Create a new mouse over area
	 * 
	 * @param container
	 *            The container displaying the mouse over area
	 * @param image
	 *            The normalImage to display
	 * @param x
	 *            The position of the area
	 * @param y
	 *            the position of the area
	 */
	public MouseOverArea(GUIContext container, Image image, int x, int y) {
		this(container, image, x, y, image.getWidth(), image.getHeight());
	}

	/**
	 * Create a new mouse over area
	 * 
	 * @param container
	 *            The container displaying the mouse over area
	 * @param image
	 *            The normalImage to display
	 * @param x
	 *            The position of the area
	 * @param y
	 *            the position of the area
	 * @param width
	 *            The width of the area
	 * @param height
	 *            The height of the area
	 * @param listener
	 *            A listener to add to the area
	 */
	public MouseOverArea(GUIContext container, Image image, int x, int y,
			             int width, int height, ComponentListener listener) {
		this(container,image,x,y,width,height);
		addListener(listener);
	}
	
	/**
	 * Create a new mouse over area
	 * 
	 * @param container
	 *            The container displaying the mouse over area
	 * @param image
	 *            The normalImage to display
	 * @param x
	 *            The position of the area
	 * @param y
	 *            the position of the area
	 * @param width
	 *            The width of the area
	 * @param height
	 *            The height of the area
	 */
	public MouseOverArea(GUIContext container, Image image, int x, int y,
			int width, int height) {
		super(container);

		normalImage = image;
		currentImage = image;
		mouseOverImage = image;
		mouseDownImage = image;

		currentColor = normalColor;

		setLocation(x, y);
		this.width = width;
		this.height = height;
		
		state = NORMAL;
		Input input = container.getInput();
		over = Rectangle.contains(input.getMouseX(), input.getMouseY(), x, y, width, height);
		mouseDown = input.isMouseButtonDown(0);
		updateImage();
	}

	/**
	 * Moves the component.
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the position in the X coordinate
	 * 
	 * @return x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the position in the Y coordinate
	 * 
	 * @return y
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Set the normal color used on the image in the default state
	 * 
	 * @param color
	 *            The color to be used
	 */
	public void setNormalColor(Color color) {
		normalColor = color;
		updateImage();
	}

	/**
	 * Set the color to be used when the mouse is over the area
	 * 
	 * @param color
	 *            The color to be used when the mouse is over the area
	 */
	public void setMouseOverColor(Color color) {
		mouseOverColor = color;
		updateImage();
	}

	/**
	 * Set the color to be used when the mouse is down the area
	 * 
	 * @param color
	 *            The color to be used when the mouse is down the area
	 */
	public void setMouseDownColor(Color color) {
		mouseDownColor = color;
		updateImage();
	}

	/**
	 * Set the normal image used on the image in the default state
	 * 
	 * @param image
	 *            The image to be used
	 */
	public void setNormalImage(Image image) {
		normalImage = image;
		updateImage();
	}

	/**
	 * Set the image to be used when the mouse is over the area
	 * 
	 * @param image
	 *            The image to be used when the mouse is over the area
	 */
	public void setMouseOverImage(Image image) {
		mouseOverImage = image;
		updateImage();
	}

	/**
	 * Set the image to be used when the mouse is down the area
	 * 
	 * @param image
	 *            The image to be used when the mouse is down the area
	 */
	public void setMouseDownImage(Image image) {
		mouseDownImage = image;
		updateImage();
	}

	/**
	 * Get the width of the area
	 * 
	 * @return The width of the area
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the area
	 * 
	 * @return The height of the area
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @see org.newdawn.slick.gui.AbstractComponent#render(org.newdawn.slick.gui.GUIContext,
	 *      org.newdawn.slick.Graphics)
	 */
	public void render(GUIContext container, Graphics g) {
		if (currentImage != null) {
			int xp = x + ((width - currentImage.getWidth()) / 2);
			int yp = y + ((height - currentImage.getHeight()) / 2);

			currentImage.draw(xp, yp, currentColor);
		} else {
			g.setColor(currentColor);
			g.fillRect(x, y, width, height);
		}
	}

	/**
	 * Update the current normalImage based on the mouse state
	 */
	private void updateImage() {
		if (!over) {
			currentImage = normalImage;
			currentColor = normalColor;
			state = NORMAL;
			mouseUp = false;
		} else {
			if (mouseDown) {
				if ((state != MOUSE_DOWN) && (mouseUp)) {
					if (mouseDownSound != null) {
						mouseDownSound.play();
					}
					currentImage = mouseDownImage;
					currentColor = mouseDownColor;
					state = MOUSE_DOWN;
					
					notifyListeners();
					mouseUp = false;
				}
			} else {
				mouseUp = true;
				if (state != MOUSE_OVER) {
					if (mouseOverSound != null) {
						mouseOverSound.play();
					}
					currentImage = mouseOverImage;
					currentColor = mouseOverColor;
					state = MOUSE_OVER;
				}
			}
		}

		mouseDown = false;
		state = NORMAL;
	}

	/**
	 * Set the mouse over sound effect
	 * 
	 * @param sound
	 *            The mouse over sound effect
	 */
	public void setMouseOverSound(Sound sound) {
		mouseOverSound = sound;
	}

	/**
	 * Set the mouse down sound effect
	 * 
	 * @param sound
	 *            The mouse down sound effect
	 */
	public void setMouseDownSound(Sound sound) {
		mouseDownSound = sound;
	}

	/**
	 * @see org.newdawn.slick.util.InputAdapter#mouseMoved(int, int, int, int)
	 */
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		over = Rectangle.contains(newx, newy, x, y, width, height);
		updateImage();
	}

	/**
	 * @see org.newdawn.slick.util.InputAdapter#mousePressed(int, int, int)
	 */
	public void mousePressed(int button, int mx, int my) {
		over = Rectangle.contains(mx, my, x, y, width, height);
		if (button == 0) {
			mouseDown = true; 
		}
		updateImage();
	}
	
	/**
	 * @see org.newdawn.slick.util.InputAdapter#mouseReleased(int, int, int)
	 */
	public void mouseReleased(int button, int mx, int my) {
		over = Rectangle.contains(mx, my, x, y, width, height);
		if (button == 0) {
			mouseDown = false; 
		}
		updateImage();
	}
}
