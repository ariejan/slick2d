package org.newdawn.slick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
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
	/** The renderer to use for all GL operations */
	private static SGL GL = Renderer.get();
	
	/** The line cache size, this is how many lines we can render before starting to regenerate lists */
	private static final int LINE_CACHE_SIZE = 200;
	
	/** True if this font should use display list caching */
	private boolean displayListCaching = true;
	
	/** The image containing the bitmap font */
	private Image font;
	/** The characters building up the font */
	private CharDef[] chars = new CharDef[1000];
	/** The kerning information */
	private HashMap kerning = new HashMap();
	/** The height of a line */
	private int lineHeight;
	/** The caches for rendered lines */
	private ArrayList cache = new ArrayList();
	
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
	 * @param fntFile The location of the font defnition file
	 * @param imgFile The location of the font image
	 * @param caching True if this font should use display list caching
	 * @throws SlickException Indicates a failure to load either file
	 */
	public AngelCodeFont(String fntFile, String imgFile, boolean caching) throws SlickException {
		font = new Image(imgFile);
		displayListCaching = caching;
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
	 * Create a new font based on a font definition from AngelCode's tool and the font
	 * image generated from the tool.
	 * 
	 * @param name The name to assign to the font image in the image store
	 * @param fntFile The stream of the font defnition file
	 * @param imgFile The stream of the font image
	 * @param caching True if this font should use display list caching
	 * @throws SlickException Indicates a failure to load either file
	 */
	public AngelCodeFont(String name, InputStream fntFile, InputStream imgFile, boolean caching) throws SlickException {
		font = new Image(imgFile, name, false);

		displayListCaching = caching;
		parseFnt(fntFile);
	}

	/**
	 * Get kerning information for a particular character pair
	 * 
	 * @param first The first character being drawn
	 * @param second The second character being drawn
	 * @param value The kerning distance between the two characters
	 */
	private void setKerning(int first, int second, int value) {
		HashMap secondMap = (HashMap) kerning.get(new Integer(first));
		if (secondMap == null) {
			secondMap = new HashMap();
			kerning.put(new Integer(first), secondMap);
		}
		
		secondMap.put(new Integer(second), new Integer(value));
	}
	
	/**
	 * Get kerning information for a particular character pair
	 * 
	 * @param first The first character being drawn
	 * @param second The second character being drawn
	 * @return The kerning distance between the two characters
	 */
	public int getKerning(int first, int second) {
		HashMap secondMap = (HashMap) kerning.get(new Integer(first));
		if (secondMap == null) {
			return 0;
		}
		
		Integer value = (Integer) secondMap.get(new Integer(second));
		if (value == null) {
			return 0;
		}
		
		return value.intValue();
	}
	
	/**
	 * Parse the font definition file
	 * 
	 * @param fntFile The stream from which the font file can be read
	 * @throws SlickException
	 */
	private void parseFnt(InputStream fntFile) throws SlickException {
		if (displayListCaching) {
			int dlBase = GL.glGenLists(LINE_CACHE_SIZE);
			for (int i=0;i<LINE_CACHE_SIZE;i++) {
				cache.add(new CachedString(dlBase+i));
			}
		}
		
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
						
						setKerning(first, second, offset);
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

		if (def.id != ' ') {
			lineHeight = Math.max(def.height+def.yoffset, lineHeight);
		}
		
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
		drawString(x,y,text,col,0,text.length()-1);
	}
	
	/**
	 * @see Font#drawString(float, float, String, Color, int, int)
	 */
	public void drawString(float x, float y, String text, Color col, int startIndex, int endIndex) {
		col.bind();
		font.bind();

		CachedString key = new CachedString(text);
		if ((displayListCaching) && (startIndex == 0) && (endIndex == text.length()-1)) {
			if (cache.contains(key)) {
				int index = cache.indexOf(key);
				CachedString cached = (CachedString) cache.get(index);
				cache.remove(index);
				cache.add(cached);
				
				GL.glTranslatef(x,y,0);
				cached.render();
				GL.glTranslatef(-x,-y,0);
			} else {
				CachedString cached = (CachedString) cache.get(0);
				cached.setString(text);
				cache.remove(0);
				cache.add(cached);
				
				GL.glTranslatef(x,y,0);
				cached.render();
				GL.glTranslatef(-x,-y,0);
			}
		} else {
			key.render(x,y,startIndex,endIndex);
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
			// ignore space, it doesn't contribute to height
			if (id == ' ') {
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
		
		char[] data = text.toCharArray();
		
		for (int i=0;i<data.length;i++) {
			int id = data[i];
			if (chars[id] == null) {
				continue;
			}
			width += chars[id].xadvance;
			
			if (i < data.length-1) {
				width += getKerning(id, data[i+1]);
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
		/** The display list index for this character */
		public int dlIndex;
		
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
		
		/** 
		 * Draw this character embedded in a image draw
		 *
		 * @param x The x position at which to draw the text
		 * @param y The y position at which to draw the text
		 */
		public void draw(float x, float y) {
			image.drawEmbedded(x+xoffset,y+yoffset,width,height);
		}
	}

	/**
	 * @see org.newdawn.slick.Font#getLineHeight()
	 */
	public int getLineHeight() {
		return lineHeight;
	}
	
	/**
	 * Cached strings to speed up rendering if we're consistantly rendering the same
	 * string over and over
	 */
	private class CachedString {
		/** The string that's cached in this list */
		private String text = "";
		/** The list that is held for this cached string */
		private int list;
		
		/** 
		 * Create a new cached string simply as a key to search for others
		 * 
		 * @param text The text for the cache
		 */
		public CachedString(String text) {
			this.text = text;
		}
		
		/**
		 * Create a new cached string to hold a rendered line in a display list
		 * 
		 * @param list The list holding the string
		 */ 
		public CachedString(int list) {
			this.list = list;
		}

		/**
		 * Set the string to be rendered
		 * 
		 * @param text The string to be rendered
		 */
		public void setString(String text) {
			this.text = text;
			GL.glNewList(list, SGL.GL_COMPILE);
			render(0, 0, 0, text.length()-1);
			GL.glEndList();
		}
		
		/**
		 * Render based on the display list
		 */
		public void render() {
			GL.glCallList(list);
		}
		
		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return text.hashCode();
		}
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object other) {
			return ((CachedString) other).text.equals(text);
		}
		
		/**
		 * Render based on immediate rendering
		 * 
		 * @param x The x coordinate to render the string at
		 * @param y The y coordinate to render the string at
		 * @param start The index of the first character in the string to render
		 * @param end The index of the last character in the string to render
		 */
		private void render(float x, float y, int start, int end) {
			font.init();
			GL.glBegin(SGL.GL_QUADS);
			
			char[] data = text.toCharArray();
			
			for (int i=0;i<data.length;i++) {
				int id = data[i];
				if (id >= chars.length) {
					continue;
				}
				if (chars[id] == null) {
					continue;
				}

				if ((i >= start) || (i <= end)) {
					chars[id].draw(x,y);
				}
				x += chars[id].xadvance;
				
				if (i < data.length-1) {
					x += getKerning(id, data[i+1]);
				}
			}
			GL.glEnd();
		}
	}
}
