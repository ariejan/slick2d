/** 
 * Filename: DataSet.java
 * Version: $Version$
 * Copyright(c) Ubiquity Software Corporation
 *         http://www.ubiquitysoftware.com 
 */ 
package org.newdawn.slick.tools.hiero;

import java.io.PrintStream;
import java.util.ArrayList;

public class DataSet
{
    private ArrayList chars = new ArrayList();
    private ArrayList kerning = new ArrayList();
    private String fontName;
    private int height;
    private int width;
    private String setName;
    private String imageName;
    private int size;
    private int lineHeight;
    
    public DataSet(String fontName, int size, int lineHeight, int width, int height, String setName, String imageName) {
        this.fontName = fontName;
        this.height = height;
        this.width = width;
        this.setName = setName;
        this.imageName = imageName;
        this.size = size;
        this.lineHeight = lineHeight;
    }
	
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
	
    public void addCharacter(int code, int xadvance, int x, int y, int width, int height,int yoffset) {
        chars.add(new CharData(code, xadvance, x, y, width, height,size + yoffset));
    }
    
    public void addKerning(int first, int second, int offset) {
        kerning.add(new KerningData(first, second, offset));
    }
    
    public class CharData {
        private int id;
        private int xadvance;
        private int x;
        private int y;
        private int width;
        private int height;
        private int yoffset;
        
        public CharData(int id, int xadvance, int x, int y, int width, int height, int yoffset) {
            this.id = id;
            this.xadvance = xadvance;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.yoffset = yoffset;
        }
        
        public int getID() {
            return id;
        }
        
        public int getXAdvance() {
            return xadvance;
        }
        
        public int getX() {
            return x;          
        }

        public int getY() {
            return y;          
        }
        
        public int getWidth() {
            return width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public int getYOffset() {
        	return yoffset;
        }
    }
    
    public class KerningData {
        private int first;
        private int second;
        private int offset;
        
        public KerningData(int first, int second, int offset) {
            this.first = first;
            this.second = second;
            this.offset = offset;
        }
    }
}
