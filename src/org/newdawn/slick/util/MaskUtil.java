package org.newdawn.slick.util;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

/**
 * A utility to provide full screen masking
 * 
 * @author kevin
 */
public class MaskUtil {
	/** The renderer to use for all GL operations */
	protected static SGL GL = Renderer.get();

	/**
	 * Start defining the screen mask. After calling this use graphics functions to 
	 * mask out the area
	 */
	public static void defineMask() {
		GL.glDepthMask(true);
		GL.glClearDepth(1);
		GL.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL.glDepthFunc(GL11.GL_ALWAYS);
		GL.glEnable(GL11.GL_DEPTH_TEST);
		GL.glDepthMask(true);
		GL.glColorMask(false, false, false, false);
	}
	
	/**
	 * Finish defining the screen mask
	 */
	public static void finishDefineMask() {
		GL.glDepthMask(false);
		GL.glColorMask(true, true, true, true);
	}
	
	/**
	 * Start drawing only on the masked area
	 */
	public static void drawOnMask() {
		GL.glDepthFunc(GL11.GL_EQUAL);
	}

	/**
	 * Start drawing only off the masked area
	 */
	public static void drawOffMask() {
		GL.glDepthFunc(GL11.GL_NOTEQUAL);
	}
	
	/**
	 * Reset the masked area - should be done after you've finished rendering
	 */
	public static void resetMask() {
		GL11.glDepthMask(true);
		GL11.glClearDepth(0);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glDepthMask(false);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
}
