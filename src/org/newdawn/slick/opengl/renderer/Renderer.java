package org.newdawn.slick.opengl.renderer;


/**
 * The static holder for the current GL implementation. Note that this 
 * renderer can only be set before the game has been started.
 * 
 * @author kevin
 */
public class Renderer {		
	/** The indicator for immediate mode renderering (the default) */
	public static final int IMMEDIATE_RENDERER = 1;
	/** The indicator for vertex array based rendering */
	public static final int VERTEX_ARRAY_RENDERER = 2;
	
	/** The renderer in use */
	private static SGL renderer = new ImmediateModeOGLRenderer();

	/** 
	 * Set the renderer to one of the known types
	 * 
	 * @param type The type of renderer to use
	 */
	public static void setRenderer(int type) {
		switch (type) {
		case IMMEDIATE_RENDERER:
			setRenderer(new ImmediateModeOGLRenderer());
			break;
		case VERTEX_ARRAY_RENDERER:
			setRenderer(new VAOGLRenderer());
			break;
		}
	}
	
	/**
	 * Set the renderer to be used
	 * 
	 * @param r The renderer to be used
	 */
	public static void setRenderer(SGL r) {
		renderer = r;
	}
	
	/**
	 * Get the renderer to be used when accessing GL
	 * 
	 * @return The renderer to be used when accessing GL
	 */
	public static SGL get() {
		return renderer;
	}
	
	
}
