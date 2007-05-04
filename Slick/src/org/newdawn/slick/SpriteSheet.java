package org.newdawn.slick;

import java.io.InputStream;

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
	/** The spacing between tiles */
	private int spacing;
	/** The target image for this sheet */
	private Image target;
	
	/**
	 * Create a new sprite sheet based on a image location
	 * 
	 * @param image The image to based the sheet of
	 * @param tw The width of the tiles on the sheet 
	 * @param th The height of the tiles on the sheet 
	 */
	public SpriteSheet(Image image,int tw,int th) {
		super(image);
		
		this.target = image;
		this.tw = tw;
		this.th = th;
	}

	/**
	 * Create a new sprite sheet based on a image location
	 * 
	 * @param image The image to based the sheet of
	 * @param tw The width of the tiles on the sheet 
	 * @param th The height of the tiles on the sheet 
	 * @param spacing The spacing between tiles
	 */
	public SpriteSheet(Image image,int tw,int th,int spacing) {
		super(image);
		
		this.target = image;
		this.tw = tw;
		this.th = th;
		this.spacing = spacing;
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
		this(ref,tw,th,null);
	}
	
	/**
	 * Create a new sprite sheet based on a image location
	 * 
	 * @param ref The location of the sprite sheet to load
	 * @param tw The width of the tiles on the sheet 
	 * @param th The height of the tiles on the sheet 
	 * @param col The colour to treat as transparent
	 * @throws SlickException Indicates a failure to load the image
	 */
	public SpriteSheet(String ref,int tw,int th, Color col) throws SlickException {
		super(ref, false, FILTER_NEAREST, col);

		this.target = this;
		this.tw = tw;
		this.th = th;
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
		super(ref,name,false);

		this.target = this;
		this.tw = tw;
		this.th = th;
	}
	
	/**
	 * @see org.newdawn.slick.Image#initImpl()
	 */
	protected void initImpl() {
		if (subImages != null) {
			return;
		}
		
		subImages = new Image[(getWidth()/(tw+spacing))][(getHeight()/(th+spacing))];
		for (int x=0;x<getWidth()/(tw+spacing);x++) {
			for (int y=0;y<getHeight()/(th+spacing);y++) {
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
		target.init();
		initImpl();
		
		return target.getSubImage(x*(tw+spacing),y*(th+spacing),tw,th);
	}
	
	/**
	 * Get the number of sprites across the sheet
	 * 
	 * @return The number of sprites across the sheet
	 */
	public int getHorizontalCount() {
		target.init();
		initImpl();
		
		return subImages.length;
	}
	
	/**
	 * Get the number of sprites down the sheet
	 * 
	 * @return The number of sprite down the sheet
	 */
	public int getVerticalCount() {
		target.init();
		initImpl();
		
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
