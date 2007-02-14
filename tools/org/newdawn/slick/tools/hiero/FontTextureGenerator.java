package org.newdawn.slick.tools.hiero;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.newdawn.slick.tools.hiero.effects.DrawingContext;
import org.newdawn.slick.tools.hiero.effects.Effect;
import org.newdawn.slick.tools.hiero.effects.Glyph;
import org.newdawn.slick.tools.hiero.truetype.FontData;

/**
 * A class to generate a font texture with any given effects
 * 
 * @author kevin
 */
public class FontTextureGenerator implements DrawingContext {
	/** Idenitfier for the top padding */
	public static final int TOP = 1;
	/** Idenitfier for the left padding */
	public static final int LEFT = 2;
	/** Idenitfier for the right padding */
	public static final int RIGHT = 3;
	/** Idenitfier for the bottom padding */
	public static final int BOTTOM = 4;
	/** Idenitfier for the advance padding */
	public static final int ADVANCE = 5;
	
	/** The generated image */
	private BufferedImage image;
	/** The generated overlay */
	private BufferedImage overlay;
	/** The dataset generated */
	private DataSet data;
	/** The font data being rendered */
	private FontData font;
	/** The width of the texture generated */
	private int width;
	/** The height of the texture generated */
	private int height;
	/** The character set being rendered */
	private CharSet set;
	/** The list of glyph bounds */
	private ArrayList rects;
	/** The maximum character height */
	private int maxHeight;
	/** The maximum character decent */
	private int maxDec;
	
	/**
	 * Create a new generator
	 * 
	 */
	public FontTextureGenerator() {
	}

	/**
	 * Get the image generated
	 * 
	 * @return The image generated
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Get the overlay image generated
	 * 
	 * @return The overlay image generated
	 */
	public BufferedImage getOverlay() {
		return overlay;
	}

	/**
	 * Get the data set generated
	 * 
	 * @return The dataset generated
	 */
	public DataSet getDataSet() {
		return data;
	}

	/**
	 * Generate a data set for the rendering
	 */
	public void generateData() {
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

		for (int i = 0; i < rects.size(); i++) {
			GlyphRect rect = (GlyphRect) rects.get(i);
			rect.storeData(data, set);
		}

		data.dumpStats();
	}

	/**
	 * Generate the texture image
	 * 
	 * @param font
	 *            The font to be rendered
	 * @param width
	 *            The width of the texture to be generated
	 * @param height
	 *            The height of the texture to be generated
	 * @param set
	 *            The set to be generated
	 * @param padding 
	 *            The padding information
	 * @param effects The list of effects to apply
	 */
	public void generate(FontData font, int width, int height, CharSet set, int[] padding, ArrayList effects) {
		this.font = font;
        int xp = 0;
        int yp = 0;
        
        this.set = set;
        this.width = width;
        this.height = height;
        
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        overlay = new BufferedImage(width+1, height+1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D og = (Graphics2D) overlay.getGraphics();

        g.setFont(font.getJavaFont());
        
        g.setPaint(new Color(0,0,0,0));
        g.fillRect(0,0,width,height);
        og.setColor(Color.red);
        og.drawRect(0,0,width,height);
        
        int des = g.getFontMetrics().getMaxDescent();
        int lineHeight = des + g.getFontMetrics().getMaxAscent() + padding[BOTTOM] + padding[TOP];
        yp += lineHeight-des;
        maxHeight = g.getFontMetrics().getMaxAscent();
        maxDec = des;
        
        data = new DataSet(font.getName(), (int) font.getSize(), lineHeight, width, height, set.getName(), "font.png");

        rects = new ArrayList();
        
        for (int i=0;i<256;i++) {
        	if (!set.includes((char) i)) {
        		continue;
        	}
        	
            g.setColor(Color.white);       
            char first = (char) i;
            String text = ""+first;
            GlyphVector vector = g.getFont().layoutGlyphVector(g.getFontRenderContext(), text.toCharArray(), 0, text.length(), Font.LAYOUT_LEFT_TO_RIGHT);

            int xoffset = 0;
            int lsb = getGlyphLSB(g, vector);
            int rsb = getGlyphRSB(g, vector);
            int fontWidth = getGlyphAdvanceX(g, vector) + padding[LEFT] + padding[RIGHT] + 1;
            int fontHeight = getGlyphHeight(g, vector)+2 + padding[TOP] + padding[BOTTOM];
            int yoffset = getGlyphYOffset(g, vector)-1;
            int advance = getGlyphAdvanceX(g, vector) + padding[ADVANCE];
            
            if (lsb < 0) {
            	xoffset = -lsb + 1;
            	fontWidth += xoffset;
            }
            if (rsb < 0) {
            	fontWidth -= rsb - 1;
            }
            
            if (xp + fontWidth >= width) {
                xp = 0;
                yp += lineHeight;
            }
            
            GlyphRect rect = new GlyphRect();
            rect.c = first;
            rect.x = xp;
            rect.y = yp+yoffset;
            rect.xDrawOffset = xoffset + padding[LEFT] + 1;
            rect.yDrawOffset = padding[TOP];
            rect.width = fontWidth;
            rect.height = fontHeight + 1;
            rect.yoffset = yoffset;
            rect.advance = advance;
            rect.glyph = vector;
            
            maxHeight = Math.max(fontHeight, maxHeight);
            
            rects.add(rect);
            xp += fontWidth;
        }
        
        xp = 0;
        yp = 0;
        
        Collections.sort(rects, new Comparator() {
			public int compare(Object a, Object b) {
				GlyphRect first = (GlyphRect) a;
				GlyphRect second = (GlyphRect) b;
				
				return second.height - first.height;
			}
        });
        
        int stripHeight = -1;
        int stripY = 0;

        for (int i=0;i<effects.size();i++) {
        	Effect effect = (Effect) effects.get(i);
        	effect.prePageRender(g, this);
        }
        for (int i=0;i<rects.size();i++) {
        	GlyphRect rect = (GlyphRect) rects.get(i);

        	if (xp+rect.width > width) {
        		xp = 0;
        		stripY += stripHeight + 1;
        		stripHeight = -1;
        	}
        	
        	if (stripHeight == -1) {
        		stripHeight = rect.height;
        	}
        	
        	rect.x = xp;
        	rect.y = stripY;
        	
        	g.setColor(Color.white);

            for (int j=0;j<effects.size();j++) {
            	Effect effect = (Effect) effects.get(j);
            	effect.preGlyphRender(g, this, rect);
            }
        	rect.drawGlyph(g);
            for (int j=0;j<effects.size();j++) {
            	Effect effect = (Effect) effects.get(j);
            	effect.postGlyphRender((Graphics2D) g.create(), this, rect);
            }
        	
        	rect.drawOverlay(og);
        
        	xp += rect.width + 1;
        }
        for (int i=0;i<effects.size();i++) {
        	Effect effect = (Effect) effects.get(i);
        	effect.postPageRender((Graphics2D) g.create(), this);
        }


        Collections.sort(rects, new Comparator() {
			public int compare(Object a, Object b) {
				GlyphRect first = (GlyphRect) a;
				GlyphRect second = (GlyphRect) b;
				
				return first.c - second.c;
			}
        });
    }

	/**
	 * Get the kerning value for a pair of characters
	 * 
	 * @param first
	 *            The first character
	 * @param second
	 *            The second character
	 * @return The kerning offset in pixels
	 */
	private int getKerning(char first, char second) {
		return font.getKerning(first, second);
	}

	/**
	 * Get the glyph's offset on y axis for rendering
	 * 
	 * @param g
	 *            The graphics context
	 * @param vector
	 *            The glyph vector to extract the data from
	 * @return The y offset of the glyph
	 */
	private int getGlyphYOffset(Graphics2D g, GlyphVector vector) {
		Rectangle bounds = vector
				.getPixelBounds(g.getFontRenderContext(), 0, 0);

		return (bounds.y);
	}

	/**
	 * Get the glyph's height for rendering
	 * 
	 * @param g
	 *            The graphics context
	 * @param vector
	 *            The glyph vector to extract the data from
	 * @return The height of the glyph
	 */
	private int getGlyphHeight(Graphics2D g, GlyphVector vector) {
		return vector.getGlyphVisualBounds(0).getBounds().height;
	}

	/**
	 * Get the glyph's advance on the x axis for rendering
	 * 
	 * @param g
	 *            The graphics context
	 * @param vector
	 *            The glyph vector to extract the data from
	 * @return The x advance of the glyph
	 */
	private int getGlyphAdvanceX(Graphics2D g, GlyphVector vector) {
		return (int) vector.getGlyphMetrics(0).getAdvanceX();
	}

	/**
	 * Get the glyph's advance on the y axis for rendering
	 * 
	 * @param g
	 *            The graphics context
	 * @param vector
	 *            The glyph vector to extract the data from
	 * @return The y advance of the glyph
	 */
	private int getGlyphAdvanceY(Graphics2D g, GlyphVector vector) {
		return (int) vector.getGlyphMetrics(0).getAdvanceY();
	}

	/**
	 * Get the glyph's LSB for rendering
	 * 
	 * @param g
	 *            The graphics context
	 * @param vector
	 *            The glyph vector to extract the data from
	 * @return The LSB of the glyph
	 */
	private int getGlyphLSB(Graphics2D g, GlyphVector vector) {
		return (int) vector.getGlyphMetrics(0).getLSB();
	}

	/**
	 * Get the glyph's RSB for rendering
	 * 
	 * @param g
	 *            The graphics context
	 * @param vector
	 *            The glyph vector to extract the data from
	 * @return The RSB of the glyph
	 */
	private int getGlyphRSB(Graphics2D g, GlyphVector vector) {
		return (int) vector.getGlyphMetrics(0).getRSB();
	}

	/**
	 * A record of a single rendered character
	 * 
	 * @author kevin
	 */
	private class GlyphRect implements Glyph {
		/** The character rendered */
		public char c;
		/** The x position of the glyph on the sheet */
		public int x;
		/** The y position of the glyph on the sheet */
		public int y;
		/** The width of the glyph on the sheet */
		public int width;
		/** The height of the glyph on the sheet */
		public int height;
		/** The advance on the x axis for the glyph */
		public int advance;
		/** The offset for rendering on the y axis for the glyph */
		public int yoffset;
		/** The x draw offset for rendering purposes */
		public int xDrawOffset;
		/** The y draw offset for rendering purposes */
		public int yDrawOffset;
		/** The glyph */
		public GlyphVector glyph;

		/**
		 * Store the data for the glyph into the data set
		 * 
		 * @param data
		 *            The data set to populate
		 * @param set
		 *            The character set being rendered
		 */
		public void storeData(DataSet data, CharSet set) {
			data.addCharacter(c, advance, x, y, width, height, yoffset);

			int[] kerns = new int[1000];
			int[] ks = new int[100];

			for (int j = 0; j < 256; j++) {
	        	if (!set.includes((char) j)) {
	        		continue;
	        	}
	        	
				char second = (char) j;

				int kerning = getKerning(c, second);
				if (kerning != 0) {
					data.addKerning(c, second, kerning);
				}
			}
		}

		/**
		 * Get the outline shape of the glyph
		 * 
		 * @return The outline shape of the glyph
		 */
		public Shape getGlyphOutline() {
			return glyph.getGlyphOutline(0);
		}

		/**
		 * Draw the glyph
		 * 
		 * @param g
		 *            The graphics context on which to draw
		 */
		public void drawGlyph(Graphics2D g) {
			g.translate(getDrawX(), getDrawY());
			g.fill(getGlyphOutline());
			g.translate(-getDrawX(), -getDrawY());
				
			//g.drawGlyphVector(glyph, getDrawX(), getDrawY());
		}

		/**
		 * Get the x position the glyph is drawn at
		 * 
		 * @return The x position the glyph is drawn at
		 */
		public int getDrawX() {
			return x + xDrawOffset;
		}

		/**
		 * Get the y position the glyph is drawn at
		 * 
		 * @return The y position the glyph is drawn at
		 */
		public int getDrawY() {
			return y - yoffset + yDrawOffset;
		}

		/**
		 * Draw the overlay info to the graphics context
		 * 
		 * @param og
		 *            The overlay graphics to draw onto
		 */
		public void drawOverlay(Graphics2D og) {
			og.setColor(Color.yellow);
			og.drawRect(x, y, width, height);
		}

		/**
		 * @see org.newdawn.slick.tools.hiero.effects.Glyph#getHeight()
		 */
		public int getHeight() {
			return height;
		}

		/**
		 * @see org.newdawn.slick.tools.hiero.effects.Glyph#getOutlineShape()
		 */
		public Shape getOutlineShape() {
			return getGlyphOutline();
		}

		/**
		 * @see org.newdawn.slick.tools.hiero.effects.Glyph#getWidth()
		 */
		public int getWidth() {
			return width;
		}

		/**
		 * @see org.newdawn.slick.tools.hiero.effects.Glyph#getX()
		 */
		public int getX() {
			return x;
		}

		/**
		 * @see org.newdawn.slick.tools.hiero.effects.Glyph#getY()
		 */
		public int getY() {
			return y;
		}

		/**
		 * @see org.newdawn.slick.tools.hiero.effects.Glyph#getYOffset()
		 */
		public int getYOffset() {
			return yoffset;
		}
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.DrawingContext#getTextureHeight()
	 */
	public int getTextureHeight() {
		return image.getHeight();
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.DrawingContext#getTextureWidth()
	 */
	public int getTextureWidth() {
		return image.getWidth();
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.DrawingContext#getMaxGlyphHeight()
	 */
	public int getMaxGlyphHeight() {
		return maxHeight;
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.DrawingContext#getMaxGlyphDecent()
	 */
	public int getMaxGlyphDecent() {
		return maxDec;
	}
}
