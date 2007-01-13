package org.newdawn.slick.tools.hiero;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class FontTextureGenerator {
	private BufferedImage image;
	private BufferedImage overlay;
	private DataSet data;
	private Font font;
	private int width;
	private int height;
	private CharSet set;
	private ArrayList rects;
	
	public BufferedImage getImage() {
		return image;
	}
    
	public BufferedImage getOverlay() {
		return overlay;
	}
	
	public DataSet getDataSet() {
		return data;
	}
	
	public void generateData() {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        
        for (int i=0;i<rects.size();i++) {
        	GlyphRect rect = (GlyphRect) rects.get(i);
        	rect.storeData(g, data, set);
        }
        
        data.dumpStats();
	}

	public void generate(Font font, int width, int height, CharSet set) {
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
        g.setColor(new Color(0f,0f,0f,0f));
        g.fillRect(0,0,width,height);
        overlay = new BufferedImage(width+1, height+1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D og = (Graphics2D) overlay.getGraphics();

        int xpadding = 0;
        int ypadding = 0;
        
        g.setFont(font);
        og.setColor(Color.red);
        og.drawRect(0,0,width,height);
        
        int des = g.getFontMetrics().getMaxDescent();
        int lineHeight = des + g.getFontMetrics().getMaxAscent() + ypadding;
        yp += lineHeight-des;

        data = new DataSet(font.getName(), font.getSize(), lineHeight, width, height, set.getName(), "font.png");

        rects = new ArrayList();
        
        for (int i=set.getStart();i<=set.getEnd();i++) {    
            g.setColor(Color.white);       
            char first = (char) i;
            String text = ""+first;
            GlyphVector vector = g.getFont().layoutGlyphVector(g.getFontRenderContext(), text.toCharArray(), 0, text.length(), Font.LAYOUT_LEFT_TO_RIGHT);

            int xoffset = 0;
            int lsb = getGlyphLSB(g, vector);
            int rsb = getGlyphRSB(g, vector);
            int fontWidth = getGlyphAdvanceX(g, vector) + (xpadding * 2);
            int fontHeight = getGlyphHeight(g, vector)+2;
            int yoffset = getGlyphYOffset(g, vector)-1;
            int advance = getGlyphAdvanceX(g, vector) + (xpadding * 2);
            
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
            rect.xDrawOffset = xoffset + xpadding;
            rect.yDrawOffset = -1 - ypadding;
            rect.width = fontWidth;
            rect.height = fontHeight;
            rect.yoffset = yoffset;
            rect.advance = advance;
            
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
        	
        	rect.drawGlyph(g);
        	rect.drawOverlay(og);
        
        	xp += rect.width + 1;
        }


        Collections.sort(rects, new Comparator() {
			public int compare(Object a, Object b) {
				GlyphRect first = (GlyphRect) a;
				GlyphRect second = (GlyphRect) b;
				
				return first.c - second.c;
			}
        });
    }

    private int getKerning(Graphics2D g, char first, char second) {
    	String text = second+""+first;
        GlyphVector vector = g.getFont().layoutGlyphVector(g.getFontRenderContext(), text.toCharArray(), 0, text.length(), Font.LAYOUT_LEFT_TO_RIGHT);

        Rectangle f = vector.getGlyphPixelBounds(0, g.getFontRenderContext(), 0, 0).getBounds();
        Rectangle s = vector.getGlyphPixelBounds(1, g.getFontRenderContext(), 0, 0).getBounds();
        GlyphMetrics met0 = vector.getGlyphMetrics(0);
        GlyphMetrics met1 = vector.getGlyphMetrics(1);

        float xdif = (float) ((s.getX() - met1.getLSB()) - (f.getX() - met0.getLSB()));
        float advance = met0.getAdvance();
        
        int kern = (int) (xdif - advance);
        return kern;
    }

    private int getGlyphYOffset(Graphics2D g, GlyphVector vector) {
        Rectangle bounds = vector.getPixelBounds(g.getFontRenderContext(), 0,0);
        
        return (int) (bounds.y);
    }
    
    private int getGlyphHeight(Graphics2D g, GlyphVector vector) {
        return (int) vector.getGlyphVisualBounds(0).getBounds().height;
    }
    
    private int getGlyphAdvanceX(Graphics2D g, GlyphVector vector) {
        return (int) vector.getGlyphMetrics(0).getAdvanceX();
    }

    private int getGlyphAdvanceY(Graphics2D g, GlyphVector vector) {
        return (int) vector.getGlyphMetrics(0).getAdvanceY();
    }
    
    private int getGlyphLSB(Graphics2D g, GlyphVector vector) {
        return (int) vector.getGlyphMetrics(0).getLSB();
    }

    private int getGlyphRSB(Graphics2D g, GlyphVector vector) {
        return (int) vector.getGlyphMetrics(0).getRSB();
    }
    
    private class GlyphRect {
    	public char c;
    	public int x;
    	public int y;
    	public int width;
    	public int height;
    	public int advance;
    	public int yoffset;
    	public int xDrawOffset;
    	public int yDrawOffset;
    	
    	public void storeData(Graphics2D g, DataSet data, CharSet set) {
            data.addCharacter(c, advance, x,y, width, height,yoffset);
            
            int[] kerns = new int[1000];
            int[] ks = new int[100];
            
            for (int j=set.getStart();j<=set.getEnd();j++) {    
            	char second = (char) j;
            	
            	int kerning = getKerning(g, c, second);
            	if (kerning != 0) {
            		data.addKerning(c, second, kerning);
            	}
            }
    	}
    	
    	public void drawGlyph(Graphics2D g) {
    		g.setColor(Color.white);
    		g.drawString(""+c, x + xDrawOffset, y - yoffset + yDrawOffset);
    	}
    	
    	public void drawOverlay(Graphics2D og) {
    		og.setColor(Color.yellow);
            og.drawRect(x,y,width,height);
    	}
    }
}
