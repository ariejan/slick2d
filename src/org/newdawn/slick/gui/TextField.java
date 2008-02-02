package org.newdawn.slick.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

/**
 * A single text field supporting text entry
 * 
 * @author kevin
 */
public class TextField extends AbstractComponent {
	/** The width of the field */
	private int width;

	/** The height of the field */
	private int height;
	
	/** The location in the X coordinate */
	protected int x;

	/** The location in the Y coordinate */
	protected int y;

	/** The maximum number of characters allowed to be input */
	private int maxCharacter = 10000;

	/** The value stored in the text field */
	private String value = "";

	/** The font used to render text in the field */
	private Font font;

	/** The border color - null if no border */
	private Color border = Color.white;

	/** The text color */
	private Color text = Color.white;

	/** The background color - null if no background */
	private Color background = new Color(0, 0, 0, 0.5f);

	/** The current cursor position */
	private int cursorPos;

	/** True if the cursor should be visible */
	private boolean visibleCursor = true;

	/**
	 * Create a new text field
	 * 
	 * @param container
	 *            The container rendering this field
	 * @param font
	 *            The font to use in the text field
	 * @param x
	 *            The x coordinate of the top left corner of the text field
	 * @param y
	 *            The y coordinate of the top left corner of the text field
	 * @param width
	 *            The width of the text field
	 * @param height
	 *            The height of the text field
	 * @param listener 
	 * 			  The listener to add to the text field
	 */
	public TextField(GUIContext container, Font font, int x, int y, int width,
					 int height, ComponentListener listener) {
		this(container,font,x,y,width,height);
		addListener(listener);
	}
	
	/**
	 * Create a new text field
	 * 
	 * @param container
	 *            The container rendering this field
	 * @param font
	 *            The font to use in the text field
	 * @param x
	 *            The x coordinate of the top left corner of the text field
	 * @param y
	 *            The y coordinate of the top left corner of the text field
	 * @param width
	 *            The width of the text field
	 * @param height
	 *            The height of the text field
	 */
	public TextField(GUIContext container, Font font, int x, int y, int width,
			int height) {
		super(container);

		this.font = font;

		setLocation(x, y);
		this.width = width;
		this.height = height;
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
	 * Get the width of the component
	 * 
	 * @return The width of the component
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the component
	 * 
	 * @return The height of the component
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Set the background color. Set to null to disable the background
	 * 
	 * @param color
	 *            The color to use for the background
	 */
	public void setBackgroundColor(Color color) {
		background = color;
	}

	/**
	 * Set the border color. Set to null to disable the border
	 * 
	 * @param color
	 *            The color to use for the border
	 */
	public void setBorderColor(Color color) {
		border = color;
	}

	/**
	 * Set the text color.
	 * 
	 * @param color
	 *            The color to use for the text
	 */
	public void setTextColor(Color color) {
		text = color;
	}

	/**
	 * @see org.newdawn.slick.gui.AbstractComponent#render(org.newdawn.slick.gui.GUIContext,
	 *      org.newdawn.slick.Graphics)
	 */
	public void render(GUIContext container, Graphics g) {
		Rectangle oldClip = g.getClip();
		g.setClip(x,y,width, height);
		
		// Someone could have set a color for me to blend...
		Color clr = g.getColor();

		if (background != null) {
			g.setColor(background.multiply(clr));
			g.fillRect(x, y, width, height);
		}
		g.setColor(text.multiply(clr));
		Font temp = g.getFont();

		int cpos = font.getWidth(value.substring(0, cursorPos));
		int tx = 0;
		if (cpos > width) {
			tx = width - cpos - font.getWidth("_");
		}

		g.translate(tx + 2, 0);
		g.setFont(font);
		g.drawString(value, x + 1, y + 1);

		if (hasFocus() && visibleCursor) {
			g.drawString("_", x + 1 + cpos + 2, y + 1);
		}

		g.translate(-tx - 2, 0);

		if (border != null) {
			g.setColor(border.multiply(clr));
			g.drawRect(x, y, width, height);
		}
		g.setColor(clr);
		g.setFont(temp);
		g.setClip(oldClip);
	}

	/**
	 * Get the value in the text field
	 * 
	 * @return The value in the text field
	 */
	public String getText() {
		return value;
	}

	/**
	 * Set the value to be displayed in the text field
	 * 
	 * @param value
	 *            The value to be displayed in the text field
	 */
	public void setText(String value) {
		this.value = value;
		if (cursorPos > value.length()) {
			cursorPos = value.length();
		}
	}

	/**
	 * Set the position of the cursor
	 * 
	 * @param pos
	 *            The new position of the cursor
	 */
	public void setCursorPos(int pos) {
		cursorPos = pos;
		if (cursorPos > value.length()) {
			cursorPos = value.length();
		}
	}

	/**
	 * Indicate whether the mouse cursor should be visible or not
	 * 
	 * @param visibleCursor
	 *            True if the mouse cursor should be visible
	 */
	public void setCursorVisible(boolean visibleCursor) {
		this.visibleCursor = visibleCursor;
	}

	/**
	 * Set the length of the allowed input
	 * 
	 * @param length
	 *            The length of the allowed input
	 */
	public void setMaxLength(int length) {
		maxCharacter = length;
		if (value.length() > maxCharacter) {
			value = value.substring(0, maxCharacter);
		}
	}

	/**
	 * @see org.newdawn.slick.gui.AbstractComponent#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (hasFocus()) {
			if (key == Input.KEY_LEFT) {
				if (cursorPos > 0) {
					cursorPos--;
				}
			} else if (key == Input.KEY_RIGHT) {
				if (cursorPos < value.length()) {
					cursorPos++;
				}
			} else if (key == Input.KEY_BACK) {
				if ((cursorPos > 0) && (value.length() > 0)) {
					if (cursorPos < value.length()) {
						value = value.substring(0, cursorPos - 1)
								+ value.substring(cursorPos);
					} else {
						value = value.substring(0, cursorPos - 1);
					}
					cursorPos--;
				}
			} else if ((c < 127) && (c > 31) && (value.length() < maxCharacter)) {
				if (cursorPos < value.length()) {
					value = value.substring(0, cursorPos) + c
							+ value.substring(cursorPos);
				} else {
					value = value.substring(0, cursorPos) + c;
				}
				cursorPos++;
			} else if (key == Input.KEY_RETURN) {
				notifyListeners();
			}

			// Nobody more will be notified
			container.getInput().consumeEvent();
		}
	}
}
