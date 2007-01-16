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
	}
}
