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
public class TextField extends BasicComponent {
	/** The interval between cursor key repeats */
	private static final int REPEAT_TIMER = 50;
	/** The initial pause before key repeat kicks in */
	private static final int INITIAL_PAUSE = 500;

	/** The TextField that currently has focus */
	private static TextField currentFocus = null;
	/** The maximum number of characters allowed to be input */
	private int maxCharacter = 10000;
	/** The value stored in the text field */
	private String value = "";
	/** The area occupied by the text field */
	private Rectangle area;
	/** The font used to render text in the field */
	private Font font;
	/** The border color - null if no border */
	private Color border = Color.white;
	/** The text color */
	private Color text = Color.white;
	/** The background color - null if no background */
	private Color background = new Color(0,0,0,0.5f);
	/** True if this component currently has focus */
	private boolean focus = false;
	/** The current cursor position */
	private int cursorPos;
	/** The timer for repeating left cursor movement */
	private long repeatLeft;
	/** The timer for repeat right cursor movement */
	private long repeatRight;
	/** The time for repeating back space */
	private long repeatBack;
	/** True if the cursor should be visible */
	private boolean visibleCursor = true;
	/** The component listener to be notified on enter */
	private ComponentListener listener;
	
	/**
	 * Create a new text field
	 * 
	 * @param container The container rendering this field
	 * @param font The font to use in the text field
	 * @param x The x coordinate of the top left corner of the text field
	 * @param y The y coordinate of the top left corner of the text field
	 * @param width The width of the text field
	 * @param height The height of the text field
	 * @param listener The listener to be notified when the text is entered into the field
	 */
	public TextField(GUIContext container, Font font, int x, int y, int width, int height, ComponentListener listener) {
		super(container);
		
		this.listener = listener;
		this.font = font;
		area = new Rectangle(x,y,width,height);
	}
	
	/**
	 * Indicate whether this component should be focused or not
	 * 
	 * @param focus True if the component should be focused
	 */
	public void setFocus(boolean focus) {
		if (focus == true) {
			if (currentFocus != null) {
				currentFocus.setFocus(false);
			}
			currentFocus = this;
		} else {
			if (currentFocus == this) {
				currentFocus = null;
			}
		}
		this.focus = focus;
	}
	
	/**
	 * Check if this field currently has text focus
	 * 
	 * @return True if this field currently has text focus
	 */
	public boolean hasFocus() 
	{
		return false;
	}
	
	/**
	 * Set the background color. Set to null to disable the background
	 * 
	 * @param color The color to use for the background
	 */
	public void setBackgroundColor(Color color) {
		background = color;
	}
	
	/**
	 * Set the border color. Set to null to disable the border
	 * 
	 * @param color The color to use for the border
	 */
	public void setBorderColor(Color color) {
		border = color;
	}

	/**
	 * Set the text color. 
	 * 
	 * @param color The color to use for the text
	 */
	public void setTextColor(Color color) {
		text = color;
	}
	
	/**
	 * @see org.newdawn.slick.gui.BasicComponent#renderImpl(org.newdawn.slick.gui.GUIContext, org.newdawn.slick.Graphics)
	 */
	public void renderImpl(GUIContext container, Graphics g) {
		g.setClip((int) area.x, (int) area.y-1, (int) area.width+1, (int) area.height+1);
		if (background != null) {
			g.setColor(background);
			g.fill(area);
		}
		if (border != null) {
			g.setColor(border);
			g.draw(area);
		}
		g.setColor(text);
		Font temp = g.getFont();
		
		int cpos = font.getWidth(value.substring(0, cursorPos));
		int tx = 0;
		if (cpos > area.width) {
			tx = (int) (area.width - cpos) - font.getWidth("_");
		}
		
		g.translate(tx+2,0);
		g.setFont(font);
		g.drawString(value, (int) (area.x+1), (int) (area.y+1));
		
		if ((focus) && (visibleCursor)) {
			g.drawString("_", (int) (area.x+1)+cpos+2, (int) (area.y+1));
		}
		
		g.translate(-tx-2, 0);
		
		g.clearClip();
		

		if (focus) {
			// key repeat handling
			if (input.isKeyDown(Input.KEY_LEFT)) {
				if (container.getTime() - repeatLeft > REPEAT_TIMER) {
					repeatLeft = container.getTime();
					if (cursorPos > 0) {
						cursorPos--;
					}
				}
			}
			if (input.isKeyDown(Input.KEY_RIGHT)) {
				if (container.getTime() - repeatRight > REPEAT_TIMER) {
					repeatRight = container.getTime();
					if (cursorPos < value.length()) {
						cursorPos++;
					}
				}
			}
			if (input.isKeyDown(Input.KEY_BACK)) {
				if (container.getTime() - repeatBack > REPEAT_TIMER) {
					if ((cursorPos > 0) && (value.length() > 0)) {
						if (cursorPos < value.length()) {
							value = value.substring(0, cursorPos-1) + value.substring(cursorPos);
						} else {
							value = value.substring(0, cursorPos-1);
						}
						cursorPos--;
						repeatBack = container.getTime();
					}
				}
			}
		}
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
	 * @param value The value to be displayed in the text field
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
	 * @param pos The new position of the cursor
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
	 * @param visibleCursor True if the mouse cursor should be visible
	 */
	public void setCursorVisible(boolean visibleCursor) {
		this.visibleCursor = visibleCursor;
	}
	
	/**
	 * Set the length of the allowed input
	 * 
	 * @param length The length of the allowed input
	 */
	public void setMaxLength(int length) {
		maxCharacter = length;
		if (value.length() > maxCharacter) {
			value = value.substring(0,maxCharacter);
		}
	}
	
	/**
	 * @see org.newdawn.slick.gui.BasicComponent#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (focus) {
			if ((c < 127) && (c > 31) && (value.length() < maxCharacter)) {
				if (cursorPos < value.length()) {
					value = value.substring(0, cursorPos) + c + value.substring(cursorPos);
				} else {
					value = value.substring(0, cursorPos) + c;
				}
				cursorPos++;
			}
			if (key == Input.KEY_BACK) {
				if ((cursorPos > 0) && (value.length() > 0)) {
					if (cursorPos < value.length()) {
						value = value.substring(0, cursorPos-1) + value.substring(cursorPos);
					} else {
						value = value.substring(0, cursorPos-1);
					}
					cursorPos--;
					repeatBack = container.getTime() + INITIAL_PAUSE;
				}
			}
			if ((key == Input.KEY_LEFT) && (cursorPos > 0)) {
				cursorPos--;
				repeatLeft = container.getTime() + INITIAL_PAUSE;
			}
			if ((key == Input.KEY_RIGHT) && (cursorPos < value.length())) {
				cursorPos++;
				repeatRight = container.getTime() + INITIAL_PAUSE;
			}
			if (key == Input.KEY_RETURN) {
				listener.componentActivated(this);
			}
			consumeEvent();
		}
	}

	/**
	 * @see org.newdawn.slick.gui.BasicComponent#mouseReleased(int, int, int)
	 */
	public void mouseReleased(int button, int x, int y) {
		if (area.contains(x,y)) {
			setFocus(true);
		} else {
			setFocus(false);
		}
	}
}
