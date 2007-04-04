package org.newdawn.slick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A font implementation that will parse the output of the AngelCode font tool available at:
 * 
 * <a href="http://www.angelcode.com/products/bmfont/">http://www.angelcode.com/products/bmfont/</a>
 *
 * This implementation copes with both the font display and kerning information allowing nicer
 * looking paragraphs of text. Note that this utility only supports the text format definition
 * file.
 * 
 * @author kevin
 */
public class AngelCodeFont implements Font {
	/** The image containing the bitmap font */
	private Image font;
	/** The characters building up the font */
	private CharDef[] chars = new CharDef[1000];
	/** The kerning information */
	private int[][] kerning = new int[1000][1000];
	/** The height of a line */
	private int lineHeight;
	
	/**
	 * Create a new font based on a font definition from AngelCode's tool and the font
	 * image generated from the tool.
	 * 
	 * @param fntFile The location of the font defnition file
	 * @param imgFile The location of the font image
	 * @throws SlickException Indicates a failure to load either file
	 */
	public AngelCodeFont(String fntFile, String imgFile) throws SlickException {
		font = new Image(imgFile);
	
		parseFnt(ResourceLoader.getResourceAsStream(fntFile));
	}

	/**
	 * Create a new font based on a font definition from AngelCode's tool and the font
	 * image generated from the tool.
	 * 
	 * @param name The name to assign to the font image in the image store
	 * @param fntFile The stream of the font defnition file
	 * @param imgFile The stream of the font image
	 * @throws SlickException Indicates a failure to load either file
	 */
	public AngelCodeFont(String name, InputStream fntFile, InputStream imgFile) throws SlickException {
		font = new Image(imgFile, name, false);
	
		parseFnt(fntFile);
	}
	
	/**
	 * Parse the font definition file
	 * 
	 * @param fntFile The stream from which the font file can be read
	 * @throws SlickException
	 */
	private void parseFnt(InputStream fntFile) throws SlickException {
		try {
			// now parse the font file
			BufferedReader in = new BufferedReader(new InputStreamReader(fntFile));
			String info = in.readLine();
			String common = in.readLine();
			String page = in.readLine();
			
			boolean done = false;
			while (!done) {
				String line = in.readLine();
				if (line == null) {
					done = true;
				} else {
					if (line.startsWith("chars c")) {
						// ignore
					}
					else if (line.startsWith("char")) {
						CharDef def = parseChar(line);
						chars[def.id] = def;
					}
					if (line.startsWith("kernings c")) {
						// ignore
					}
					else if (line.startsWith("kerning")) {
						StringTokenizer tokens = new StringTokenizer(line," =");
						tokens.nextToken(); // kerning
						tokens.nextToken(); // first
						int first = Integer.parseInt(tokens.nextToken()); // first value
						tokens.nextToken(); // second
						int second = Integer.parseInt(tokens.nextToken()); // second value
						tokens.nextToken(); // offset
						int offset = Integer.parseInt(tokens.nextToken()); // offset value
						
						kerning[first][second] = offset;
					}
				}
			}
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException("Failed to parse font file: "+fntFile);
		}
		
	}
	
	/**
	 * Parse a single character line from the definition
	 * 
	 * @param line The line to be parsed
	 * @return The character definition from the line
	 */
	private CharDef parseChar(String line) {
		CharDef def = new CharDef();
		StringTokenizer tokens = new StringTokenizer(line," =");
		
		tokens.nextToken(); // char
		tokens.nextToken(); // id
		def.id = Integer.parseInt(tokens.nextToken()); // id value
		tokens.nextToken(); // x
		def.x = Integer.parseInt(tokens.nextToken()); // x value
		tokens.nextToken(); // y
		def.y = Integer.parseInt(tokens.nextToken()); // y value
		tokens.nextToken(); // width
		def.width = Integer.parseInt(tokens.nextToken()); // width value
		tokens.nextToken(); // height
		def.height = Integer.parseInt(tokens.nextToken()); // height value
		tokens.nextToken(); // x offset
		def.xoffset = Integer.parseInt(tokens.nextToken()); // xoffset value
		tokens.nextToken(); // y offset
		def.yoffset = Integer.parseInt(tokens.nextToken()); // yoffset value
		tokens.nextToken(); // xadvance
		def.xadvance = Integer.parseInt(tokens.nextToken()); // xadvance
		
		def.init();

		lineHeight = Math.max(def.height+def.yoffset, lineHeight);
		return def;
	}
	
	/**
	 * @see org.newdawn.slick.Font#drawString(float, float, java.lang.String)
	 */
	public void drawString(float x, float y, String text) {
		drawString(x,y,text,Color.white);
	}

	/**
	 * @see org.newdawn.slick.Font#drawString(float, float, java.lang.String, org.newdawn.slick.Color)
	 */
	public void drawString(float x, float y, String text, Color col) {
		col.bind();

		for (int i=0;i<text.length();i++) {
			int id = text.charAt(i);
			if (chars[id] == null) {
				continue;
			}
			chars[id].image.draw(x+chars[id].xoffset, y+chars[id].yoffset, null);
			x += chars[id].xadvance;
			
			if (i < text.length()-1) {
				x += kerning[id][text.charAt(i+1)];
			}
		}
	}

	/**
	 * Get the offset from the draw location the font will place
	 * glyphs
	 * 
	 * @param text The text that is to be tested
	 * @return The yoffset from the y draw location at which text will start
	 */
	public int getYOffset(String text) {
		int minYOffset = 10000;
		for (int i=0;i<text.length();i++) {
			int id = text.charAt(i);
			if (chars[id] == null) {
				continue;
			}
			minYOffset = Math.min(chars[id].yoffset,minYOffset);
		}
		
		return minYOffset;
	}
	
	/**
	 * @see org.newdawn.slick.Font#getHeight(java.lang.String)
	 */
	public int getHeight(String text) {
		int maxHeight = 0;
		
		for (int i=0;i<text.length();i++) {
			int id = text.charAt(i);
			if (chars[id] == null) {
				continue;
			}
			
			maxHeight = Math.max(chars[id].height+chars[id].yoffset, maxHeight);
		}
		
		return maxHeight;
	}
	
	/**
	 * @see org.newdawn.slick.Font#getWidth(java.lang.String)
	 */
	public int getWidth(String text) {
		int width = 0;
		
		for (int i=0;i<text.length();i++) {
			int id = text.charAt(i);
			if (chars[id] == null) {
				continue;
			}
			width += chars[id].xadvance;
			
			if (i < text.length()-1) {
				width += kerning[id][text.charAt(i+1)];
			}
		}
		
		return width;
	}

	/**
	 * The definition of a single character as defined in the AngelCode file format
	 * 
	 * @author kevin
	 */
	private class CharDef {
		/** The id of the character */
		public int id;
		/** The x location on the sprite sheet */
		public int x;
		/** The y location on the sprite sheet */
		public int y;
		/** The width of the character image */
		public int width;
		/** The height of the character image */
		public int height;
		/** The amount the x position should be offset when drawing the image */
		public int xoffset;
		/** The amount the y position should be offset when drawing the image */
		public int yoffset;
		/** The amount to move the current position after drawing the character */
		public int xadvance;
		/** The image containing the character */
		public Image image;
		
		/**
		 * Initialise the image by cutting the right section from the
		 * map produced by the AngelCode tool.
		 */
		public void init() {
			image = font.getSubImage(x, y, width, height);
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "[CharDef id="+id+" x="+x+" y="+y+"]";
		}
	}

	/**
	 * @see org.newdawn.slick.Font#getLineHeight()
	 */
	public int getLineHeight() {
		return lineHeight;
	}
}
