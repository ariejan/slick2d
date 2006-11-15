package org.newdawn.slick.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class MouseOverArea extends BasicComponent {
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
	
	/** True if the mouse is down */
	private boolean mouseDown;

	/** The rectangle of the are on the screen */
	private Rectangle rect;
	/** The current normalImage being displayed */
	private Image currentImage;
	/** The current color being used */
	private Color currentColor;
	/** The listener to notify of events */
	private ComponentListener listener;
	/** True if the mouse is over the area */
	private boolean over;
	
	/**
	 * Create a new mouse over area
	 * 
	 * @param container The container displaying the mouse over area
	 * @param image The normalImage to display 
	 * @param x The position of the area
	 * @param y the position of the area
	 * @param listener The listener to notify of events
	 */
	public MouseOverArea(GameContainer container, Image image, int x, int y, ComponentListener listener) {
		this(container, image, x, y, image.getWidth(), image.getHeight(), listener);
	}
	
	/**
	 * Create a new mouse over area
	 * 
	 * @param container The container displaying the mouse over area
	 * @param image The normalImage to display 
	 * @param x The position of the area
	 * @param y the position of the area
	 * @param width The width of the area
	 * @param height The height of the area
	 * @param listener The listener to notify of events
	 */
	public MouseOverArea(GameContainer container, Image image, int x, int y, int width, int height, ComponentListener listener) {
		super(container);
		
		normalImage = image;
		currentImage = image;
		mouseOverImage = image;
		mouseDownImage = image;
		
		currentColor = normalColor;
		
		this.listener = listener;
		rect = new Rectangle(x,y,width,height);
	}
	
	/**
	 * Set the normal color used on the image in the default state
	 *  
	 * @param color The color to be used
	 */
	public void setNormalColor(Color color) {
		normalColor = color;
		updateImage(over);
	}
	
	/**
	 * Set the color to be used when the mouse is over the area
	 * 
	 * @param color The color to be used when the mouse is over the area
	 */
	public void setMouseOverColor(Color color) {
		mouseOverColor = color;
		updateImage(over);
	}

	/**
	 * Set the color to be used when the mouse is down the area
	 * 
	 * @param color The color to be used when the mouse is down the area
	 */
	public void setMouseDownColor(Color color) {
		mouseDownColor = color;
		updateImage(over);
	}

	/**
	 * Set the normal image used on the image in the default state
	 *  
	 * @param image The image to be used
	 */
	public void setNormalImage(Image image) {
		normalImage = image;
		updateImage(over);
	}
	
	/**
	 * Set the image to be used when the mouse is over the area
	 * 
	 * @param image The image to be used when the mouse is over the area
	 */
	public void setMouseOverImage(Image image) {
		mouseOverImage = image;
		updateImage(over);
	}

	/**
	 * Set the image to be used when the mouse is down the area
	 * 
	 * @param image The image to be used when the mouse is down the area
	 */
	public void setMouseDownImage(Image image) {
		mouseDownImage = image;
		updateImage(over);
	}
	
	/**
	 * Get the width of the area
	 * 
	 * @return The width of the area
	 */
	public int getWidth() {
		return (int) rect.width;
	}

	/**
	 * Get the height of the area
	 * 
	 * @return The height of the area
	 */
	public int getHeight() {
		return (int) rect.height;
	}
	
	/**
	 * @see org.newdawn.slick.gui.BasicComponent#renderImpl(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void renderImpl(GameContainer container, Graphics g) {
		int xp = (int) (rect.x +((rect.width - currentImage.getWidth()) / 2));
		int yp = (int) (rect.y +((rect.height - currentImage.getHeight()) / 2));
		
		currentImage.draw(xp,yp,currentColor);
	}

	/**
	 * Update the current normalImage based on the mouse state
	 * 
	 * @param over True if the mouse is over the area
	 */
	private void updateImage(boolean over) {
		this.over = over;
		
		if (!over) {
			currentImage = normalImage;
			currentColor = normalColor;
		} else {
			if (mouseDown) {
				currentImage = mouseDownImage;
				currentColor = mouseDownColor;
			} else {
				currentImage = mouseOverImage;
				currentColor = mouseOverColor;
			}
		}
	}
	
	/**
	 * @see org.newdawn.slick.gui.BasicComponent#mouseMoved(int, int, int, int)
	 */
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		updateImage(rect.contains(newx,newy));
	}

	/**
	 * @see org.newdawn.slick.gui.BasicComponent#mousePressed(int, int, int)
	 */
	public void mousePressed(int button, int x, int y) {
		mouseDown = true;
		updateImage(rect.contains(x,y));
		
		if (rect.contains(x,y)) {
			listener.componentActivated(this);
			consumeEvent();
		}
	}

	/**
	 * @see org.newdawn.slick.gui.BasicComponent#mouseReleased(int, int, int)
	 */
	public void mouseReleased(int button, int x, int y) {
		mouseDown = false;
		updateImage(rect.contains(x,y));
	}
}
