package org.newdawn.slick.tools.hiero.effects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * An effect to create a black outline around the glyphs
 *
 * @author kevin
 */
public class OutlineEffect implements Effect {
	/** The line width */
	private float width = 1;
	/** The color of the outline */
	private Color col = new Color(0,0,0);
	
	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#postGlyphRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext, org.newdawn.slick.tools.hiero.effects.Glyph)
	 */
	public void postGlyphRender(Graphics2D g, DrawingContext context, Glyph glyph) {
		g.setStroke(new BasicStroke(width));
		g.setColor(col);
		g.translate(glyph.getX(), glyph.getY());
		g.draw(glyph.getOutlineShape());
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#postPageRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext)
	 */
	public void postPageRender(Graphics2D g, DrawingContext context) {
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#preGlyphRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext, org.newdawn.slick.tools.hiero.effects.Glyph)
	 */
	public void preGlyphRender(Graphics2D g, DrawingContext context, Glyph glyph) {
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#prePageRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext)
	 */
	public void prePageRender(Graphics2D g, DrawingContext context) {
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#setProperty(java.lang.String, java.lang.String)
	 */
	public void setProperty(String key, String value) {
		float v = Float.parseFloat(value);
		
		if (key.equals("width")) {
			width = v;
		}
		if (key.equals("color")) {
			col = new Color(Integer.parseInt(value, 16));
		}
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#getPropertyNames()
	 */
	public String[] getPropertyNames() {
		return new String[] {"width","color"};
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#getProperty(java.lang.String)
	 */
	public String getProperty(String key) {
		if (key.equals("width")) {
			return ""+width;
		}
		if (key.equals("color")) {
			return Integer.toHexString(col.getRGB());
		}
		
		return null;
	}
}
