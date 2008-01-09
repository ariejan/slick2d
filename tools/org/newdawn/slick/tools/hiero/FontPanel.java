package org.newdawn.slick.tools.hiero;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.newdawn.slick.tools.hiero.truetype.FontData;

/**
 * A panel displaying the font
 *
 * @author kevin
 */
public class FontPanel extends JPanel {
	/** Idenitfier for the top padding */
	public static final int TOP = FontTextureGenerator.TOP;
	/** Idenitfier for the left padding */
	public static final int LEFT = FontTextureGenerator.LEFT;
	/** Idenitfier for the right padding */
	public static final int RIGHT = FontTextureGenerator.RIGHT;
	/** Idenitfier for the bottom padding */
	public static final int BOTTOM = FontTextureGenerator.BOTTOM;
	/** Idenitfier for the advance padding */
	public static final int ADVANCE = FontTextureGenerator.ADVANCE;
	
	/** The width of the texture to use */
	private int width;
	/** The height of the texture to use */
    private int height;
    /** The font to render */
    private FontData font;
    /** The character set to render */
    private CharSet set;
    /** The data set generated */
    private DataSet data;
    /** The last image rendered */
    private BufferedImage image;
    /** The last overlay image rendered */
    private BufferedImage overlay;
    /** The genrator used to produce the image */
    private FontTextureGenerator gen;
    /** The padding to apply */
    private int[] padding = new int[6];
	/** The background paint */
	private Paint background;
	/** The list of effects to apply */
	private ArrayList effects = new ArrayList();
	/** True if the background should be checked */
	private boolean transparent = true;
	/** True if we should show the grid */
	private boolean showGrid = true;
	/** The background color */
	private Color backgroundColor = Color.darkGray;
	
    /**
     * Create a new font panel
     */
    public FontPanel() {
		Color base = Color.gray;
		BufferedImage image = new BufferedImage(50, 50,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(base);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setColor(base.darker());
		g.fillRect(image.getWidth() / 2, 0, image.getWidth() / 2, image
				.getHeight() / 2);
		g.fillRect(0, image.getHeight() / 2, image.getWidth() / 2, image
				.getHeight() / 2);

		background = new TexturePaint(image, new Rectangle(0, 0, image
				.getWidth(), image.getHeight()));
		
    	setBackground(Color.black);
    	
    	gen = new FontTextureGenerator();
        setRequiredDimensions(512,512);
        setFont(new Font("Arial", Font.PLAIN, 30));
        setCharSet(Hiero.SET_NEHE);
        generate();
    }
    
    /**
     * Toggle the rendering of the overlay
     */
    public void toggleOverlay() {
    	showGrid = !showGrid;
    	repaint(0);
    }

    /**
     * Toggle the rendering of transparent indicator for the background
     */
    public void toggleTransparent() {
    	transparent = !transparent;
    	repaint(0);
    }
    
    /**
     * Set the background colour
     * 
     * @param bg The background colour
     */
    public void setBackgroundColor(Color bg) {
    	this.backgroundColor = bg;
    	repaint(0);
    }
    
    /**
     * Get the the background color we're previewing over
     * 
     * @return The background color we're previewing over
     */
    public Color getBackgroundColor() {
    	return backgroundColor;
    }
    
    /**
     * Set the list of effects to apply
     * 
     * @param effects The list of effects to apply
     */
    public void setEffects(ArrayList effects) {
    	this.effects = effects;
    	generate();
        repaint(0);
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics g1d) { 
        super.paintComponent(g1d);
        
        Graphics2D g = (Graphics2D) g1d;
        if (transparent) {
        	g.setPaint(background);
        } else {
        	g.setPaint(backgroundColor);
        }
        g.fillRect(0,0,getWidth(), getHeight());
        if (image != null) {
        	g.drawImage(image, 0, 0, null);
        }
        
        if (showGrid) {
        	if (overlay != null) {
        		g.drawImage(overlay, 0, 0, null);
        	}
        }
    }
    
    /**
     * Set the character set to be rendered
     * 
     * @param set The character set to be rendered
     */
    public void setCharSet(CharSet set) {
        this.set = set;
        generate();
        repaint(0);
    }
    
    /**
     * Set the font to be rendered
     * 
     * @param font The font to be rendered
     */
    public void setFont(FontData font) {
        this.font = font;
        generate();
        repaint(0);
    }
    
    /**
     * Generate the data set for the current font
     */
    public void generateData() {
    	gen.generateData();
    }
    
    /**
     * Generate the font image
     */
    public void generate() {
    	if (gen == null) {
    		return;
    	}
    	if (font == null) {
    		return;
    	}
    	if (set == null) {
    		return;
    	}
    	
    	gen.generate(font, width, height, set, padding, effects);
    	image = gen.getImage();
    	overlay = gen.getOverlay();
    	data = gen.getDataSet();
    }
    
    /**
     * Save the image and font file out
     * 
     * @param ref The reference to save out to
     * @param type The type of file to save as (@see {@link Hiero#TEXT} and @see {@link Hiero#XML})
     * @throws IOException Indicates a failure to write to the file
     */
    public void save(String ref, int type) throws IOException {
    	FileOutputStream fout = new FileOutputStream(ref+".png");
    	ImageIO.write(image, "PNG", fout);
    	fout.close();
    	
    	fout = new FileOutputStream(ref+".fnt");
    	if (type == Hiero.XML) {
    		data.toAngelCodeXML(new PrintStream(fout));
    	} else {
    		data.toAngelCodeText(new PrintStream(fout));
    	}
    	
    	fout.close();
    }
    
    /**
     * Set the required dimensions for the texture
     * 
     * @param width The width to generate
     * @param height The height to generate
     */
    public void setRequiredDimensions(int width, int height) {
        setPreferredSize(new Dimension(width,height));
        setSize(width,height);
     
        this.width = width;
        this.height = height;
        
        generate();
        repaint(0);
    }
    
    /**
     * Set the padding to apply round characters
     * 
     * @param type The type of padding to configure
     * @param padding The padding to apply round characters
     */
    public void setPadding(int type, int padding) {
    	this.padding[type] = padding;
    	
    	generate();
    	repaint(0);
    }
}
