/** 
 * Filename: DataSet.java
 * Version: $Version$
 * Copyright(c) Ubiquity Software Corporation
 *         http://www.ubiquitysoftware.com 
 */ 
package org.newdawn.slick.tools.hiero;

import java.io.PrintStream;
import java.util.ArrayList;

/**
 * The data set generated from the font rendering
 * 
 * @author kevin
 */
public class DataSet
{
	/** The character information */
    private ArrayList chars = new ArrayList();
    /** The kerning information */
    private ArrayList kerning = new ArrayList();
    /** The name fo the font face */
    private String fontName;
    /** The height of generated texture */
    private int height;
    /** The width of generated texture */
    private int width;
    /** The name of the set */
    private String setName;
    /** The name of the image */
    private String imageName;
    /** The font size */
    private int size;
    /** The global line height */
    private int lineHeight;
    
    /**
     * Create a new data set
     * 
     * @param fontName The name of the font generated
     * @param size The size of the font generated
     * @param lineHeight The height of a line of text
     * @param width The width of the texture generated
     * @param height The height of the texture generated
     * @param setName The name of the set
     * @param imageName The name of the image
     */
    public DataSet(String fontName, int size, int lineHeight, int width, int height, String setName, String imageName) {
        this.fontName = fontName;
        this.height = height;
        this.width = width;
        this.setName = setName;
        this.imageName = imageName;
        this.size = size;
        this.lineHeight = lineHeight;
    }
	
    /**
     * Dump statistics on this set
     */
    public void dumpStats() {
    	System.out.println("Kerning Count: "+kerning.size());
    }
    
    /**
     * Output this data set as an angel code data file
     * 
     * @param out The output stream to write to
     */
	public void toAngelCode(PrintStream out) {
		out.println("info face=\""+fontName+"\" size="+size+" bold=0 italic=0 charset=\""+setName+"\" unicode=0 stretchH=100 smooth=1 aa=1 padding=0,0,0,0 spacing=1,1");
		out.println("common lineHeight="+lineHeight+" base=26 scaleW="+width+" scaleH="+height+" pages=1 packed=0");
		out.println("page id=0 file=\""+imageName+"\"");

		out.println("chars count="+chars.size());
		for (int i=0;i<chars.size();i++) {
			CharData c = (CharData) chars.get(i);
			out.println("char id="+c.getID()+"   x="+c.getX()+"     y="+c.getY()+"     width="+c.getWidth()+"     height="+c.getHeight()+"     xoffset=0     yoffset="+c.getYOffset()+"    xadvance="+c.getXAdvance()+"     page=0  chnl=0 ");
		}
		out.println("kernings count="+kerning.size());
		for (int i=0;i<kerning.size();i++) {
			KerningData k = (KerningData) kerning.get(i);
			out.println("kerning first="+k.first+"  second="+k.second+"  amount="+k.offset);
		}
	}
	
	/**
	 * Add a character to the data set
	 * 
	 * @param code The character code
	 * @param xadvance The advance on the x axis after writing this character
	 * @param x The x position on the sheet of the character
	 * @param y The y position on the sheet of the character
	 * @param width The width of the character on the sheet 
	 * @param height The height of the character on the sheet
	 * @param yoffset The offset on the y axis when drawing the character
	 */
    public void addCharacter(int code, int xadvance, int x, int y, int width, int height,int yoffset) {
        chars.add(new CharData(code, xadvance, x, y, width, height,size + yoffset));
    }
    
    /**
     * Add some kerning data
     * 
     * @param first The first character 
     * @param second The second character
     * @param offset The kerning offset to apply
     */
    public void addKerning(int first, int second, int offset) {
        kerning.add(new KerningData(first, second, offset));
    }
    
    /**
     * Info about a single character
     *
     * @author kevin
     */
    public class CharData {
    	/** The character code */
        private int id;
        /** The advance on the x axis after writing this character */
        private int xadvance;
        /** The x position on the sheet of the character */
        private int x;
        /** The y position on the sheet of the character */
        private int y;
        /** The width of the character on the sheet */
        private int width;
        /** The height of the character on the sheet */
        private int height;
        /** The offset on the y axis when drawing the character */
        private int yoffset;
        
        /**
		 * Create a new set of character data
		 * 
		 * @param id
		 *            The character code
		 * @param xadvance
		 *            The advance on the x axis after writing this character
		 * @param x
		 *            The x position on the sheet of the character
		 * @param y
		 *            The y position on the sheet of the character
		 * @param width
		 *            The width of the character on the sheet
		 * @param height
		 *            The height of the character on the sheet
		 * @param yoffset
		 *            The offset on the y axis when drawing the character
		 */
        public CharData(int id, int xadvance, int x, int y, int width, int height, int yoffset) {
            this.id = id;
            this.xadvance = xadvance;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.yoffset = yoffset;
        }
        
        /**
         * Get the character code
         * 
         * @return The character code
         */
        public int getID() {
            return id;
        }
        
        /**
         * Get the advance on the X axis 
         * 
         * @return The advance on the X axis
         */
        public int getXAdvance() {
            return xadvance;
        }
        
        /**
         * Get the x position of the character on the sheet
         * 
         * @return The x position of the character on the sheet
         */
        public int getX() {
            return x;          
        }

        /**
         * Get the y position of the character on the sheet
         * 
         * @return The y position of the character on the sheet
         */
        public int getY() {
            return y;          
        }

        /**
         * Get the width of the character on the sheet
         * 
         * @return The width of the character on the sheet
         */
        public int getWidth() {
            return width;
        }

        /**
         * Get the height of the character on the sheet
         * 
         * @return The height of the character on the sheet
         */
        public int getHeight() {
            return height;
        }
        
        /**
         * The offset on the y axis when drawing the character
         * 
         * @return The offset on the y axis when drawing the character
         */
        public int getYOffset() {
        	return yoffset;
        }
    }
    
    /**
     * Kerning data for a particular character pair
     *
     * @author kevin
     */
    public class KerningData {
    	/** The first character */
        private int first;
        /** The second character */
        private int second;
        /** The kerning offset */
        private int offset;
        
        /**
         * Create a new kerning pair
         * 
         * @param first The first character 
         * @param second The second character
         * @param offset The kerning offset to apply
         */
        public KerningData(int first, int second, int offset) {
            this.first = first;
            this.second = second;
            this.offset = offset;
        }
    }
}
