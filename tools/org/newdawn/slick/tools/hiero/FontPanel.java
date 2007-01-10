package org.newdawn.slick.tools.hiero;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class FontPanel extends JPanel {
    private int width;
    private int height;
    private Font font;
    private Image current;
    private CharSet set;
    private DataSet data;
    private BufferedImage image;
    private BufferedImage overlay;
    private FontTextureGenerator gen;
    
    public FontPanel() {
    	setBackground(Color.black);
    	
    	gen = new FontTextureGenerator();
        setRequiredDimensions(512,512);
        setFont(new Font("Arial", Font.PLAIN, 30));
        setCharSet(Hiero.SET_NEHE);
        generate();
    }
    
    public void paintComponent(Graphics g) { 
        super.paintComponent(g);
        
        g.drawImage(image, 0, 0, null);
        g.drawImage(overlay, 0, 0, null);
    }
    
    public void setCharSet(CharSet set) {
        this.set = set;
        generate();
        repaint(0);
    }
    
    public void setFont(Font font) {
        this.font = font;
        generate();
        repaint(0);
    }
    
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
    	
    	gen.generate(font, width, height, set);
    	image = gen.getImage();
    	overlay = gen.getOverlay();
    	data = gen.getDataSet();
    }
    
    public void save(String ref) throws IOException {
    	ImageIO.write(image, "PNG", new FileOutputStream(ref+".png"));
    	data.toAngelCode(new PrintStream(new FileOutputStream(ref+".fnt")));
    }
    
    public void setRequiredDimensions(int width, int height) {
        setPreferredSize(new Dimension(width,height));
        setSize(width,height);
     
        this.width = width;
        this.height = height;
        
        generate();
        repaint(0);
    }
}
