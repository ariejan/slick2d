package org.newdawn.slick.tools.hiero;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.newdawn.slick.tools.hiero.truetype.FontData;

/**
 * A panel displaying the font
 *
 * @author kevin
 */
public class FontPanel extends JPanel {
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
    /** The x padding to apply */
    private int xpadding;
    /** The y padding to apply */
    private int ypadding;
    
    /**
     * Create a new font panel
     */
    public FontPanel() {
    	setBackground(Color.black);
    	
    	gen = new FontTextureGenerator();
        setRequiredDimensions(512,512);
        setFont(new Font("Arial", Font.PLAIN, 30));
        setCharSet(Hiero.SET_NEHE);
        generate();
    }
    
    /**=
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics g) { 
        super.paintComponent(g);
        
        g.drawImage(image, 0, 0, null);
        g.drawImage(overlay, 0, 0, null);
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
    	
    	gen.generate(font, width, height, set, xpadding, ypadding);
    	image = gen.getImage();
    	overlay = gen.getOverlay();
    	data = gen.getDataSet();
    }
    
    /**
     * Save the image and font file out
     * 
     * @param ref The reference to save out to
     * @throws IOException Indicates a failure to write to the file
     */
    public void save(String ref) throws IOException {
    	FileOutputStream fout = new FileOutputStream(ref+".png");
    	ImageIO.write(image, "PNG", fout);
    	fout.close();
    	
    	fout = new FileOutputStream(ref+".fnt");
    	data.toAngelCode(new PrintStream(fout));
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
     * @param padding The padding to apply round characters
     */
    public void setPadding(int padding) {
    	xpadding = padding;
    	ypadding = padding;
    	
    	generate();
    	repaint(0);
    }
}
