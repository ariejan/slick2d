package org.newdawn.slick;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;

/**
 * A wrapped for all keyboard, mouse and controller input
 *
 * @author kevin
 */
public class Input {
	/** The controller index to pass to check all controllers */
	public static final int ANY_CONTROLLER = -1;
	
	/** */
	public static final int KEY_ESCAPE          = 0x01;
	/** */
	public static final int KEY_1               = 0x02;
	/** */
	public static final int KEY_2               = 0x03;
	/** */
	public static final int KEY_3               = 0x04;
	/** */
	public static final int KEY_4               = 0x05;
	/** */
	public static final int KEY_5               = 0x06;
	/** */
	public static final int KEY_6               = 0x07;
	/** */
	public static final int KEY_7               = 0x08;
	/** */
	public static final int KEY_8               = 0x09;
	/** */
	public static final int KEY_9               = 0x0A;
	/** */
	public static final int KEY_0               = 0x0B;
	/** */
	public static final int KEY_MINUS           = 0x0C; /* - on main keyboard */
	/** */
	public static final int KEY_EQUALS          = 0x0D;
	/** */
	public static final int KEY_BACK            = 0x0E; /* backspace */
	/** */
	public static final int KEY_TAB             = 0x0F;
	/** */
	public static final int KEY_Q               = 0x10;
	/** */
	public static final int KEY_W               = 0x11;
	/** */
	public static final int KEY_E               = 0x12;
	/** */
	public static final int KEY_R               = 0x13;
	/** */
	public static final int KEY_T               = 0x14;
	/** */
	public static final int KEY_Y               = 0x15;
	/** */
	public static final int KEY_U               = 0x16;
	/** */
	public static final int KEY_I               = 0x17;
	/** */
	public static final int KEY_O               = 0x18;
	/** */
	public static final int KEY_P               = 0x19;
	/** */
	public static final int KEY_LBRACKET        = 0x1A;
	/** */
	public static final int KEY_RBRACKET        = 0x1B;
	/** */
	public static final int KEY_RETURN          = 0x1C; /* Enter on main keyboard */
	/** */
	public static final int KEY_ENTER           = 0x1C; /* Enter on main keyboard */
	/** */
	public static final int KEY_LCONTROL        = 0x1D;
	/** */
	public static final int KEY_A               = 0x1E;
	/** */
	public static final int KEY_S               = 0x1F;
	/** */
	public static final int KEY_D               = 0x20;
	/** */
	public static final int KEY_F               = 0x21;
	/** */
	public static final int KEY_G               = 0x22;
	/** */
	public static final int KEY_H               = 0x23;
	/** */
	public static final int KEY_J               = 0x24;
	/** */
	public static final int KEY_K               = 0x25;
	/** */
	public static final int KEY_L               = 0x26;
	/** */
	public static final int KEY_SEMICOLON       = 0x27;
	/** */
	public static final int KEY_APOSTROPHE      = 0x28;
	/** */
	public static final int KEY_GRAVE           = 0x29; /* accent grave */
	/** */
	public static final int KEY_LSHIFT          = 0x2A;
	/** */
	public static final int KEY_BACKSLASH       = 0x2B;
	/** */
	public static final int KEY_Z               = 0x2C;
	/** */
	public static final int KEY_X               = 0x2D;
	/** */
	public static final int KEY_C               = 0x2E;
	/** */
	public static final int KEY_V               = 0x2F;
	/** */
	public static final int KEY_B               = 0x30;
	/** */
	public static final int KEY_N               = 0x31;
	/** */
	public static final int KEY_M               = 0x32;
	/** */
	public static final int KEY_COMMA           = 0x33;
	/** */
	public static final int KEY_PERIOD          = 0x34; /* . on main keyboard */
	/** */
	public static final int KEY_SLASH           = 0x35; /* / on main keyboard */
	/** */
	public static final int KEY_RSHIFT          = 0x36;
	/** */
	public static final int KEY_MULTIPLY        = 0x37; /* * on numeric keypad */
	/** */
	public static final int KEY_LMENU           = 0x38; /* left Alt */
	/** */
	public static final int KEY_SPACE           = 0x39;
	/** */
	public static final int KEY_CAPITAL         = 0x3A;
	/** */
	public static final int KEY_F1              = 0x3B;
	/** */
	public static final int KEY_F2              = 0x3C;
	/** */
	public static final int KEY_F3              = 0x3D;
	/** */
	public static final int KEY_F4              = 0x3E;
	/** */
	public static final int KEY_F5              = 0x3F;
	/** */
	public static final int KEY_F6              = 0x40;
	/** */
	public static final int KEY_F7              = 0x41;
	/** */
	public static final int KEY_F8              = 0x42;
	/** */
	public static final int KEY_F9              = 0x43;
	/** */
	public static final int KEY_F10             = 0x44;
	/** */
	public static final int KEY_NUMLOCK         = 0x45;
	/** */
	public static final int KEY_SCROLL          = 0x46; /* Scroll Lock */
	/** */
	public static final int KEY_NUMPAD7         = 0x47;
	/** */
	public static final int KEY_NUMPAD8         = 0x48;
	/** */
	public static final int KEY_NUMPAD9         = 0x49;
	/** */
	public static final int KEY_SUBTRACT        = 0x4A; /* - on numeric keypad */
	/** */
	public static final int KEY_NUMPAD4         = 0x4B;
	/** */
	public static final int KEY_NUMPAD5         = 0x4C;
	/** */
	public static final int KEY_NUMPAD6         = 0x4D;
	/** */
	public static final int KEY_ADD             = 0x4E; /* + on numeric keypad */
	/** */
	public static final int KEY_NUMPAD1         = 0x4F;
	/** */
	public static final int KEY_NUMPAD2         = 0x50;
	/** */
	public static final int KEY_NUMPAD3         = 0x51;
	/** */
	public static final int KEY_NUMPAD0         = 0x52;
	/** */
	public static final int KEY_DECIMAL         = 0x53; /* . on numeric keypad */
	/** */
	public static final int KEY_F11             = 0x57;
	/** */
	public static final int KEY_F12             = 0x58;
	/** */
	public static final int KEY_F13             = 0x64; /*                     (NEC PC98) */
	/** */
	public static final int KEY_F14             = 0x65; /*                     (NEC PC98) */
	/** */
	public static final int KEY_F15             = 0x66; /*                     (NEC PC98) */
	/** */
	public static final int KEY_KANA            = 0x70; /* (Japanese keyboard)            */
	/** */
	public static final int KEY_CONVERT         = 0x79; /* (Japanese keyboard)            */
	/** */
	public static final int KEY_NOCONVERT       = 0x7B; /* (Japanese keyboard)            */
	/** */
	public static final int KEY_YEN             = 0x7D; /* (Japanese keyboard)            */
	/** */
	public static final int KEY_NUMPADEQUALS    = 0x8D; /* = on numeric keypad (NEC PC98) */
	/** */
	public static final int KEY_CIRCUMFLEX      = 0x90; /* (Japanese keyboard)            */
	/** */
	public static final int KEY_AT              = 0x91; /*                     (NEC PC98) */
	/** */
	public static final int KEY_COLON           = 0x92; /*                     (NEC PC98) */
	/** */
	public static final int KEY_UNDERLINE       = 0x93; /*                     (NEC PC98) */
	/** */
	public static final int KEY_KANJI           = 0x94; /* (Japanese keyboard)            */
	/** */
	public static final int KEY_STOP            = 0x95; /*                     (NEC PC98) */
	/** */
	public static final int KEY_AX              = 0x96; /*                     (Japan AX) */
	/** */
	public static final int KEY_UNLABELED       = 0x97; /*                        (J3100) */
	/** */
	public static final int KEY_NUMPADENTER     = 0x9C; /* Enter on numeric keypad */
	/** */
	public static final int KEY_RCONTROL        = 0x9D;
	/** */
	public static final int KEY_NUMPADCOMMA     = 0xB3; /* , on numeric keypad (NEC PC98) */
	/** */
	public static final int KEY_DIVIDE          = 0xB5; /* / on numeric keypad */
	/** */
	public static final int KEY_SYSRQ           = 0xB7;
	/** */
	public static final int KEY_RMENU           = 0xB8; /* right Alt */
	/** */
	public static final int KEY_PAUSE           = 0xC5; /* Pause */
	/** */
	public static final int KEY_HOME            = 0xC7; /* Home on arrow keypad */
	/** */
	public static final int KEY_UP              = 0xC8; /* UpArrow on arrow keypad */
	/** */
	public static final int KEY_PRIOR           = 0xC9; /* PgUp on arrow keypad */
	/** */
	public static final int KEY_LEFT            = 0xCB; /* LeftArrow on arrow keypad */
	/** */
	public static final int KEY_RIGHT           = 0xCD; /* RightArrow on arrow keypad */
	/** */
	public static final int KEY_END             = 0xCF; /* End on arrow keypad */
	/** */
	public static final int KEY_DOWN            = 0xD0; /* DownArrow on arrow keypad */
	/** */
	public static final int KEY_NEXT            = 0xD1; /* PgDn on arrow keypad */
	/** */
	public static final int KEY_INSERT          = 0xD2; /* Insert on arrow keypad */
	/** */
	public static final int KEY_DELETE          = 0xD3; /* Delete on arrow keypad */
	/** */
	public static final int KEY_LWIN            = 0xDB; /* Left Windows key */
	/** */
	public static final int KEY_RWIN            = 0xDC; /* Right Windows key */
	/** */
	public static final int KEY_APPS            = 0xDD; /* AppMenu key */
	/** */
	public static final int KEY_POWER           = 0xDE;
	/** */
	public static final int KEY_SLEEP           = 0xDF;
	
	/** Control index */
	private static final int LEFT = 0;
	/** Control index */
	private static final int RIGHT = 1;
	/** Control index */
	private static final int UP = 2;
	/** Control index */
	private static final int DOWN = 3;
	/** Control index */
	private static final int BUTTON1 = 4;
	/** Control index */
	private static final int BUTTON2 = 5;
	/** Control index */
	private static final int BUTTON3 = 6;
	
	/** True if the controllers system has been initialised */
	private static boolean controllersInited = false;
	/** The list of controllers */
	private static ArrayList controllers = new ArrayList();

	/** The last recorded mouse x position */
	private int lastMouseX;
	/** The last recorded mouse y position */
	private int lastMouseY;
	/** The character values representing the pressed keys */
	private char[] keys = new char[1024];
	/** True if the key has been pressed since last queries */
	private boolean[] pressed = new boolean[1024];
	
	/** The control states from the controllers */
	private boolean[][] controls = new boolean[10][8];
	/** True if the event has been consumed */
	private boolean consumed = false;
	/** A list of listeners to be notified of input events */
	private ArrayList listeners = new ArrayList();
	/** The current value of the wheel */
	private int wheel;
	/** The height of the display */
	private int height;
	
	/** True if the display is active */
	private boolean displayActive = true;
	
	/**
	 * Create a new input with the height of the screen
	 * 
	 * @param height The height of the screen
	 */
	public Input(int height) {
		init(height);
	}
	
	/**
	 * Add a listener to be notified of input events
	 * 
	 * @param listener The listener to be notified
	 */
	public void addListener(InputListener listener) {
		if (listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}

	/**
	 * Add a listener to be notified of input events. This listener
	 * will get events before others that are currently registered
	 * 
	 * @param listener The listener to be notified
	 */
	public void addPrimaryListener(InputListener listener) {
		listeners.add(0, listener);
	}
	
	/**
	 * Remove a listener that will no longer be notified
	 * 
	 * @param listener The listen to be removed
	 */
	public void removeListener(InputListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Initialise the input system
	 * 
	 * @param height The height of the window
	 */
	void init(int height) {
		this.height = height;
		lastMouseX = getMouseX();
		lastMouseY = getMouseY();
	}
	
	/**
	 * Get the character representation of the key identified by the specified code
	 * 
	 * @param code The key code of the key to retrieve the name of
	 * @return The name or character representation of the key requested
	 */
	public String getKeyName(int code) {
		return Keyboard.getKeyName(code);
	}
	
	/**
	 * Check if a particular key has been pressed since this method 
	 * was last called for the specified key
	 * 
	 * @param code The key code of the key to check
	 * @return True if the key has been pressed
	 */
	public boolean isKeyPressed(int code) {
		if (pressed[code]) {
			pressed[code] = false;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Clear the state for the <code>isKeyPressed</code> method. This will
	 * resort in all keys returning that they haven't been pressed, until
	 * they are pressed again
	 */
	public void clearKeyPressedRecord() {
		Arrays.fill(pressed, false);
	}
	
	/**
	 * Check if a particular key is down
	 * 
	 * @param code The key code of the key to check
	 * @return True if the key is down
	 */
	public boolean isKeyDown(int code) {
		return Keyboard.isKeyDown(code);
	}
	
	/**
	 * Get the x position of the mouse cursor
	 * 
	 * @return The x position of the mouse cursor
	 */
	public int getMouseX() {
		return Mouse.getX();
	}
	
	/**
	 * Get the y position of the mouse cursor
	 * 
	 * @return The y position of the mouse cursor
	 */
	public int getMouseY() {
		return height-Mouse.getY();
	}
	
	/**
	 * Check if a given mouse button is down
	 * 
	 * @param button The index of the button to check (starting at 0)
	 * @return True if the mouse button is down
	 */
	public boolean isMouseButtonDown(int button) {
		return Mouse.isButtonDown(button);
	}
	
	/**
	 * Get a count of the number of controlles available
	 * 
	 * @return The number of controllers available
	 */
	public int getControllerCount() {
		try {
			initControllers();
		} catch (SlickException e) {
			throw new RuntimeException("Failed to initialise controllers");
		}
		
		return controllers.size();
	}
	
	/**
	 * Check if the controller has the left direction pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the controller is pressed to the left
	 */
	public boolean isControllerLeft(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}
		
		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerLeft(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).getXAxisValue() < -0.5f
				|| ((Controller) controllers.get(controller)).getPovX() < -0.5f;
	}

	/**
	 * Check if the controller has the right direction pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the controller is pressed to the right
	 */
	public boolean isControllerRight(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerRight(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).getXAxisValue() > 0.5f
   				|| ((Controller) controllers.get(controller)).getPovX() > 0.5f;
	}

	/**
	 * Check if the controller has the up direction pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the controller is pressed to the up
	 */
	public boolean isControllerUp(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerUp(i)) {
					return true;
				}
			}
			
			return false;
		}
		return ((Controller) controllers.get(controller)).getYAxisValue() < -0.5f
		   		|| ((Controller) controllers.get(controller)).getPovY() < -0.5f;
	}

	/**
	 * Check if the controller has the down direction pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the controller is pressed to the down
	 */
	public boolean isControllerDown(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerDown(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).getYAxisValue() > 0.5f
			   || ((Controller) controllers.get(controller)).getPovY() > 0.5f;
	       
	}

	/**
	 * Check if button 1 is pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the button is pressed
	 */
	public boolean isButton1Pressed(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isButton1Pressed(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).isButtonPressed(0);
	}

	/**
	 * Check if button 2 is pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the button is pressed
	 */
	public boolean isButton2Pressed(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isButton2Pressed(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).isButtonPressed(1);
	}

	/**
	 * Check if button 3 is pressed
	 * 
	 * @param controller The index of the controller to check
	 * @return True if the button is pressed
	 */
	public boolean isButton3Pressed(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isButton3Pressed(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).isButtonPressed(2);
	}
	
	/**
	 * Initialise the controllers system
	 * 
	 * @throws SlickException Indicates a failure to use the hardware
	 */
	public void initControllers() throws SlickException {
		if (controllersInited) {
			return;
		}
		
		controllersInited = true;
		try {
			Controllers.create();
			int count = Controllers.getControllerCount();
			
			for (int i = 0; i < count; i++) {
				Controller controller = Controllers.getController(i);

				if ((controller.getButtonCount() >= 3) && (controller.getButtonCount() < 20)) {
					controllers.add(controller);
				}
			}
			
			Log.info("Found "+controllers.size()+" controllers");
			for (int i=0;i<controllers.size();i++) {
				Log.info(i+" : "+((Controller) controllers.get(i)).getName());
			}
		} catch (LWJGLException e) {
			if (e.getCause() instanceof ClassNotFoundException) {
				Log.error(e.getCause());
				throw new SlickException("Unable to create controller - no jinput found - add jinput.jar to your classpath");
			}
			Log.error(e);
			throw new SlickException("Unable to create controllers");
		}
	}
	
	/**
	 * Notification from an event handle that an event has been consumed
	 */
	public void consumeEvent() {
		consumed = true;
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
	 * Poll the state of the input
	 * 
	 * @param width The width of the game view
	 * @param height The height of the game view
	 */
	public void poll(int width, int height) {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				keys[Keyboard.getEventKey()] = Keyboard.getEventCharacter();
				pressed[Keyboard.getEventKey()] = true;
				
				consumed = false;
				for (int i=0;i<listeners.size();i++) {
					InputListener listener = (InputListener) listeners.get(i);
					
					if (listener.isAcceptingInput()) {
						listener.keyPressed(Keyboard.getEventKey(), Keyboard.getEventCharacter());
						if (consumed) {
							break;
						}
					}
				}
			} else {
				consumed = false;
				for (int i=0;i<listeners.size();i++) {
					InputListener listener = (InputListener) listeners.get(i);
					if (listener.isAcceptingInput()) {
						listener.keyReleased(Keyboard.getEventKey(), keys[Keyboard.getEventKey()]);
						if (consumed) {
							break;
						}
					}
				}
			}
		}
		
		while (Mouse.next()) {
			if (Mouse.getEventButton() >= 0) {
				if (Mouse.getEventButtonState()) {
					consumed = false;
					for (int i=0;i<listeners.size();i++) {
						InputListener listener = (InputListener) listeners.get(i);
						if (listener.isAcceptingInput()) {
							listener.mousePressed(Mouse.getEventButton(), Mouse.getEventX(), height-Mouse.getEventY());
							if (consumed) {
								break;
							}
						}
					}
				} else {
					consumed = false;
					for (int i=0;i<listeners.size();i++) {
						InputListener listener = (InputListener) listeners.get(i);
						if (listener.isAcceptingInput()) {
							listener.mouseReleased(Mouse.getEventButton(), Mouse.getEventX(), height-Mouse.getEventY());
							if (consumed) {
								break;
							}
						}
					}
				}
			} else {
				if (Mouse.isGrabbed()) {
					if ((Mouse.getEventDX() != 0) || (Mouse.getEventDY() != 0)) {
						consumed = false;
						for (int i=0;i<listeners.size();i++) {
							InputListener listener = (InputListener) listeners.get(i);
							if (listener.isAcceptingInput()) {
								listener.mouseMoved(0, 0, Mouse.getEventDX(), -Mouse.getEventDY());
								if (consumed) {
									break;
								}
							}
						}
					}
				}
				
				int dwheel = Mouse.getEventDWheel();
				wheel += dwheel;
				if (dwheel != 0) {
					consumed = false;
					for (int i=0;i<listeners.size();i++) {
						InputListener listener = (InputListener) listeners.get(i);
						if (listener.isAcceptingInput()) {
							listener.mouseWheelMoved(dwheel);
							if (consumed) {
								break;
							}
						}
					}
				}
			}
		}
		
		if (!displayActive) {
			lastMouseX = getMouseX();
			lastMouseY = getMouseY();
		} else {
			if ((lastMouseX != getMouseX()) || (lastMouseY != getMouseY())) {
				consumed = false;
				for (int i=0;i<listeners.size();i++) {
					InputListener listener = (InputListener) listeners.get(i);
					if (listener.isAcceptingInput()) {
						listener.mouseMoved(lastMouseX,  lastMouseY, getMouseX(), getMouseY());
						if (consumed) {
							break;
						}
					}
				}
				lastMouseX = getMouseX();
				lastMouseY = getMouseY();
			}
		}
		
		if (controllersInited) {
			for (int i=0;i<getControllerCount();i++) {
				for (int c=0;c<=BUTTON3;c++) {
					if (controls[i][c] && !isControlPressed(c, i)) {
						controls[i][c] = false;
						fireControlRelease(c, i);
					} else if (!controls[i][c] && isControlPressed(c, i)) {
						controls[i][c] = true;
						fireControlPress(c, i);
					}
				}
			}
		}
		
		for (int i=0;i<listeners.size();i++) {
			InputListener listener = (InputListener) listeners.get(i);
			listener.inputEnded();
		}
		
		if (Display.isCreated()) {
			displayActive = Display.isActive();
		}
	}
	
	/**
	 * Fire an event indicating that a control has been pressed
	 * 
	 * @param index The index of the control pressed
	 * @param controllerIndex The index of the controller on which the control was pressed
	 */
	private void fireControlPress(int index, int controllerIndex) {
		consumed = false;
		for (int i=0;i<listeners.size();i++) {
			InputListener listener = (InputListener) listeners.get(i);
			if (listener.isAcceptingInput()) {
				switch (index) {
				case LEFT:
					listener.controllerLeftPressed(controllerIndex);
					break;
				case RIGHT:
					listener.controllerRightPressed(controllerIndex);
					break;
				case UP:
					listener.controllerUpPressed(controllerIndex);
					break;
				case DOWN:
					listener.controllerDownPressed(controllerIndex);
					break;
				case BUTTON1:
					listener.controllerButtonPressed(controllerIndex, 1);
					break;
				case BUTTON2:
					listener.controllerButtonPressed(controllerIndex, 2);
					break;
				case BUTTON3:
					listener.controllerButtonPressed(controllerIndex, 3);
					break;
				default:
					throw new RuntimeException("Unknown control index");
				}
				if (consumed) {
					break;
				}
			}
		}
	}

	/**
	 * Fire an event indicating that a control has been released
	 * 
	 * @param index The index of the control released
	 * @param controllerIndex The index of the controller on which the control was released
	 */
	private void fireControlRelease(int index, int controllerIndex) {
		consumed = false;
		for (int i=0;i<listeners.size();i++) {
			InputListener listener = (InputListener) listeners.get(i);
			if (listener.isAcceptingInput()) {
				switch (index) {
				case LEFT:
					listener.controllerLeftReleased(controllerIndex);
					break;
				case RIGHT:
					listener.controllerRightReleased(controllerIndex);
					break;
				case UP:
					listener.controllerUpReleased(controllerIndex);
					break;
				case DOWN:
					listener.controllerDownReleased(controllerIndex);
					break;
				case BUTTON1:
					listener.controllerButtonReleased(controllerIndex, 1);
					break;
				case BUTTON2:
					listener.controllerButtonReleased(controllerIndex, 2);
					break;
				case BUTTON3:
					listener.controllerButtonReleased(controllerIndex, 3);
					break;
				default:
					throw new RuntimeException("Unknown control index");
				}
				if (consumed) {
					break;
				}
			}
		}
	}
	
	/**
	 * Check if a particular control is currently pressed
	 * 
	 * @param index The index of the control
	 * @param controllerIndex The index of the control to which the control belongs
	 * @return True if the control is pressed
	 */
	private boolean isControlPressed(int index, int controllerIndex) {
		switch (index) {
		case LEFT:
			return isControllerLeft(controllerIndex);
		case RIGHT:
			return isControllerRight(controllerIndex);
		case UP:
			return isControllerUp(controllerIndex);
		case DOWN:
			return isControllerDown(controllerIndex);
		case BUTTON1:
			return isButton1Pressed(controllerIndex);
		case BUTTON2:
			return isButton2Pressed(controllerIndex);
		case BUTTON3:
			return isButton3Pressed(controllerIndex);
		}

		throw new RuntimeException("Unknown control index");
	}
}
