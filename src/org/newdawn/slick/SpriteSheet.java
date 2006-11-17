package org.newdawn.slick;

import java.io.InputStream;

import org.lwjgl.opengl.GL11;

/**
 * A sheet of sprites that can be drawn individually
 * 
 * @author Kevin Glass
 */
public class SpriteSheet extends Image {
	/** The width of a single element in pixels */
	private int tw;
	/** The height of a single element in pixels  */
	private int th;
	/** Subimages */
	private Image[][] subImages;
	
	/**
	 * Create a new sprite sheet based on a image location
	 * 
	 * @param image The image to based the sheet of
	 * @param tw The width of the tiles on the sheet 
	 * @param th The height of the tiles on the sheet 
	 */
	public SpriteSheet(Image image,int tw,int th) {
		super(image);
		
		this.tw = tw;
		this.th = th;

		subImages = new Image[width/tw][height/th];
		for (int x=0;x<width/tw;x++) {
			for (int y=0;y<height/th;y++) {
				subImages[x][y] = getSprite(x,y);
			}
		}
	}
	
	/**
	 * Create a new sprite sheet based on a image location
	 * 
	 * @param ref The location of the sprite sheet to load
	 * @param tw The width of the tiles on the sheet 
	 * @param th The height of the tiles on the sheet 
	 * @throws SlickException Indicates a failure to load the image
	 */
	public SpriteSheet(String ref,int tw,int th) throws SlickException {
		super(ref);
		
		this.tw = tw;
		this.th = th;

		subImages = new Image[width/tw][height/th];
		for (int x=0;x<width/tw;x++) {
			for (int y=0;y<height/th;y++) {
				subImages[x][y] = getSprite(x,y);
			}
		}
	}
	
	/**
	 * Create a new sprite sheet based on a image location
	 * 
	 * @param name The name to give to the image in the image cache
	 * @param ref The stream from which we can load the image
	 * @param tw The width of the tiles on the sheet 
	 * @param th The height of the tiles on the sheet 
	 * @throws SlickException Indicates a failure to load the image
	 */
	public SpriteSheet(String name, InputStream ref,int tw,int th) throws SlickException {
		super(ref,name,true);
		
		this.tw = tw;
		this.th = th;
		
		subImages = new Image[width/tw][height/th];
		for (int x=0;x<width/tw;x++) {
			for (int y=0;y<height/th;y++) {
				subImages[x][y] = getSprite(x,y);
			}
		}
	}

	/**
	 * Get a sprite at a particular cell on the sprite sheet
	 * 
	 * @param x The x position of the cell on the sprite sheet
	 * @param y The y position of the cell on the sprite sheet
	 * @return The single image from the sprite sheet
	 */
	public Image getSprite(int x, int y) {
		return getSubImage(x*tw,y*th,tw,th);
	}
	
	/**
	 * Get the number of sprites across the sheet
	 * 
	 * @return The number of sprites across the sheet
	 */
	public int getHorizontalCount() {
		return subImages.length;
	}
	
	/**
	 * Get the number of sprites down the sheet
	 * 
	 * @return The number of sprite down the sheet
	 */
	public int getVerticalCount() {
		return subImages[0].length;
	}
	
	/**
	 * Render a sprite when this sprite sheet is in use. 
	 * 
	 * @see #startUse()
	 * @see #endUse()
	 * 
	 * @param x The x position to render the sprite at
	 * @param y The y position to render the sprite at 
	 * @param sx The x location of the cell to render
	 * @param sy The y location of the cell to render
	 */
	public void renderInUse(int x,int y,int sx,int sy) {
		if (inUse != this) {
			throw new RuntimeException("The sprite sheet is not currently in use");
		}
		
		subImages[sx][sy].drawEmbedded(x, y, tw, th);
	}
}
