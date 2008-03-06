package org.newdawn.slick;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.newdawn.slick.opengl.GLUtils;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.BufferedImageUtil;

/**
 * A TrueType font implementation for Slick
 * 
 * @author James Chambers (Jimmy)
 * @author Jeremy Adams (elias4444)
 * @author Kevin Glass (kevglass)
 */
public class TrueTypeFont implements org.newdawn.slick.Font {
	/** The renderer to use for all GL operations */
	private static final SGL GL = Renderer.get();

	/** Array that holds necessary information about the font characters */
	private IntObject[] charArray = new IntObject[256];

	/** Boolean flag on whether AntiAliasing is enabled or not */
	private boolean antiAlias;

	/** Font's size */
	private int fontSize = 0;

	/** Font's height */
	private int fontHeight = 0;

	/** Texture used to cache the font characters */
	private Texture fontTexture;

	/** Default font texture width */
	private float textureWidth = 512.0f;

	/** Default font texture height */
	private float textureHeight = 512.0f;

	/** A reference to Java's AWT Font that we create our font texture from */
	private java.awt.Font font;

	/** The font metrics for our Java AWT font */
	private FontMetrics fontMetrics;

	/**
	 * This is a special internal class that holds our necessary information for
	 * the font characters. This includes width, height, and where the character
	 * is stored on the font texture.
	 */
	private class IntObject {
		/** Character's width */
		public int width;

		/** Character's height */
		public int height;

		/** Character's stored x position */
		public int storedX;

		/** Character's stored y position */
		public int storedY;
	}

	/**
	 * Constructor for the TrueTypeFont class Pass in the preloaded standard
	 * Java TrueType font, and whether you want it to be cached with
	 * AntiAliasing applied.
	 * 
	 * @param font
	 *            Standard Java AWT font
	 * @param antiAlias
	 *            Whether or not to apply AntiAliasing to the cached font
	 */
	public TrueTypeFont(java.awt.Font font, boolean antiAlias) {
		GLUtils.checkGLContext();
		
		this.font = font;
		this.fontSize = font.getSize();
		this.antiAlias = antiAlias;

		createPlainSet();
	}

	/**
	 * Create a standard Java2D BufferedImage of the given character
	 * 
	 * @param ch
	 *            The character to create a BufferedImage for
	 * 
	 * @return A BufferedImage containing the character
	 */
	private BufferedImage getFontImage(char ch) {
		// Create a temporary image to extract the character's size
		BufferedImage tempfontImage = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
		if (antiAlias == true) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		fontMetrics = g.getFontMetrics();
		int charwidth = fontMetrics.charWidth(ch);

		if (charwidth <= 0) {
			charwidth = 1;
		}
		int charheight = fontMetrics.getHeight();
		if (charheight <= 0) {
			charheight = fontSize;
		}

		// Create another image holding the character we are creating
		BufferedImage fontImage;
		fontImage = new BufferedImage(charwidth, charheight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		if (antiAlias == true) {
			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		gt.setFont(font);

		gt.setColor(Color.WHITE);
		int charx = 0;
		int chary = 0;
		gt.drawString(String.valueOf(ch), (charx), (chary)
				+ fontMetrics.getAscent());

		return fontImage;

	}

	/**
	 * Create and store the font
	 */
	private void createPlainSet() {
		try {
			BufferedImage imgTemp = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) imgTemp.getGraphics();

			int rowHeight = 0;
			int positionX = 0;
			int positionY = 0;

			for (int i = 0; i < 256; i++) {
				char ch = (char) i;
				BufferedImage fontImage = getFontImage(ch);

				IntObject newIntObject = new IntObject();

				newIntObject.width = fontImage.getWidth();
				newIntObject.height = fontImage.getHeight();

				if (positionX + newIntObject.width >= 512) {
					positionX = 0;
					positionY += rowHeight;
					rowHeight = 0;
				}

				newIntObject.storedX = positionX;
				newIntObject.storedY = positionY;

				if (newIntObject.height > fontHeight) {
					fontHeight = newIntObject.height;
				}

				if (newIntObject.height > rowHeight) {
					rowHeight = newIntObject.height;
				}

				// Draw it here
				g.drawImage(fontImage, positionX, positionY, null);

				positionX += newIntObject.width;

				charArray[i] = newIntObject;

				fontImage = null;
			}

			fontTexture = BufferedImageUtil
					.getTexture(font.toString(), imgTemp);

		} catch (IOException e) {
			System.err.println("Failed to create font.");
			e.printStackTrace();
		}

	}

	/**
	 * Draw a textured quad
	 * 
	 * @param drawX
	 *            The left x position to draw to
	 * @param drawY
	 *            The top y position to draw to
	 * @param drawX2
	 *            The right x position to draw to
	 * @param drawY2
	 *            The bottom y position to draw to
	 * @param srcX
	 *            The left source x position to draw from
	 * @param srcY
	 *            The top source y position to draw from
	 * @param srcX2
	 *            The right source x position to draw from
	 * @param srcY2
	 *            The bottom source y position to draw from
	 */
	private void drawQuad(int drawX, int drawY, int drawX2, int drawY2,
			int srcX, int srcY, int srcX2, int srcY2) {
		int DrawWidth = drawX2 - drawX;
		int DrawHeight = drawY2 - drawY;
		float TextureSrcX = srcX / textureWidth;
		float TextureSrcY = srcY / textureHeight;
		float SrcWidth = srcX2 - srcX;
		float SrcHeight = srcY2 - srcY;
		float RenderWidth = (SrcWidth / textureWidth);
		float RenderHeight = (SrcHeight / textureHeight);

		GL.glTexCoord2f(TextureSrcX, TextureSrcY);
		GL.glVertex2f(drawX, drawY);
		GL.glTexCoord2f(TextureSrcX, TextureSrcY + RenderHeight);
		GL.glVertex2f(drawX, drawY + DrawHeight);
		GL.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);
		GL.glVertex2f(drawX + DrawWidth, drawY + DrawHeight);
		GL.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY);
		GL.glVertex2f(drawX + DrawWidth, drawY);
	}

	/**
	 * Get the width of a given String
	 * 
	 * @param whatchars
	 *            The characters to get the width of
	 * 
	 * @return The width of the characters
	 */
	public int getWidth(String whatchars) {
		int totalwidth = 0;
		IntObject intObject = null;
		int currentChar = 0;
		for (int i = 0; i < whatchars.length(); i++) {
			currentChar = whatchars.charAt(i);
			if (currentChar < 256) {
				intObject = charArray[currentChar];
				totalwidth += intObject.width;
			}
		}
		return totalwidth;
	}

	/**
	 * Get the font's height
	 * 
	 * @return The height of the font
	 */
	public int getHeight() {
		return fontHeight;
	}

	/**
	 * Get the height of a String
	 * 
	 * @return The height of a given string
	 */
	public int getHeight(String HeightString) {
		return fontHeight;
	}

	/**
	 * Get the font's line height
	 * 
	 * @return The line height of the font
	 */
	public int getLineHeight() {
		return fontHeight;
	}

	/**
	 * Draw a string
	 * 
	 * @param x
	 *            The x position to draw the string
	 * @param y
	 *            The y position to draw the string
	 * @param whatchars
	 *            The string to draw
	 * @param color
	 *            The color to draw the text
	 */
	public void drawString(float x, float y, String whatchars,
			org.newdawn.slick.Color color) {
		drawString(x,y,whatchars,color,0,whatchars.length()-1);
	}
	
	/**
	 * @see Font#drawString(float, float, String, org.newdawn.slick.Color, int, int)
	 */
	public void drawString(float x, float y, String whatchars,
			org.newdawn.slick.Color color, int startIndex, int endIndex) {
		color.bind();
		fontTexture.bind();

		IntObject intObject = null;
		int charCurrent;

		GL.glBegin(SGL.GL_QUADS);

		int totalwidth = 0;
		for (int i = 0; i < whatchars.length(); i++) {
			charCurrent = whatchars.charAt(i);
			if (charCurrent < 256) {
				intObject = charArray[charCurrent];

				if ((i >= startIndex) || (i <= endIndex)) {
					drawQuad((int) (x + totalwidth), (int) y,
							(int) (x + totalwidth + intObject.width),
							(int) (y + intObject.height), intObject.storedX,
							intObject.storedY, intObject.storedX + intObject.width,
							intObject.storedY + intObject.height);
				}
				totalwidth += intObject.width;
			}
		}

		GL.glEnd();
	}

	/**
	 * Draw a string
	 * 
	 * @param x
	 *            The x position to draw the string
	 * @param y
	 *            The y position to draw the string
	 * @param whatchars
	 *            The string to draw
	 */
	public void drawString(float x, float y, String whatchars) {
		drawString(x, y, whatchars, org.newdawn.slick.Color.white);
	}

}