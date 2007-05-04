package org.newdawn.slick;

import java.io.IOException;
import java.io.InputStream;

import org.lwjgl.opengl.EXTSecondaryColor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.opengl.pbuffer.GraphicsFactory;
import org.newdawn.slick.util.Log;

/**
 * An image loaded from a file and renderable to the canvas
 *
 * @author kevin
 */
public class Image {
	/** The sprite sheet currently in use */
	protected static Image inUse;
	/** Use Linear Filtering */
	public static final int FILTER_LINEAR = 1;
	/** Use Nearest Filtering */
	public static final int FILTER_NEAREST = 2;
	
	/** The OpenGL texture for this image */
	private Texture texture;
	/** The width of the image */
	protected int width;
	/** The height of the image */
	protected int height;
	/** The texture coordinate width to use to find our image */
	private float textureWidth;
	/** The texture coordinate height to use to find our image */
	private float textureHeight;
	/** The x texture offset to use to find our image */
	private float textureOffsetX;
	/** The y texture offset to use to find our image */
	private float textureOffsetY;
	/** The name given for the image */
	private String ref;
	/** True if this image's state has been initialised */
	protected boolean inited = false;
	/** A pixelData holding the pixel data if it's been read for this texture */
	private byte[] pixelData;
	
	/**
	 * Create a texture as a copy of another
	 * 
	 * @param other The other texture to copy
	 */
	protected Image(Image other) {
		this.texture = other.texture;
		this.width = other.width;
		this.height = other.height;
		this.textureWidth = other.textureWidth;
		this.textureHeight = other.textureHeight;
		this.ref = other.ref;
		this.textureOffsetX = other.textureOffsetX;
		this.textureOffsetY = other.textureOffsetY;
	}
	
	/**
	 * Cloning constructor - only used internally.
	 */
	protected Image() {
	}

	/**
	 * Create an image based on a file at the specified location
	 * 
	 * @param ref The location of the image file to load
	 * @throws SlickException Indicates a failure to load the image
	 */
	public Image(String ref) throws SlickException  {
		this(ref, false);
	}

	/**
	 * Create an image based on a file at the specified location
	 * 
	 * @param ref The location of the image file to load
	 * @param trans The color to be treated as transparent
	 * @throws SlickException Indicates a failure to load the image
	 */
	public Image(String ref, Color trans) throws SlickException  {
		this(ref, false, FILTER_LINEAR, trans);
	}
	
	/**
	 * Create an image based on a file at the specified location
	 * 
	 * @param ref The location of the image file to load
	 * @param flipped True if the image should be flipped on the y-axis on load
	 * @throws SlickException Indicates a failure to load the image
	 */
	public Image(String ref, boolean flipped) throws SlickException {
		this(ref, flipped, FILTER_LINEAR);
	}

	/**
	 * Create an image based on a file at the specified location
	 * 
	 * @param ref The location of the image file to load
	 * @param flipped True if the image should be flipped on the y-axis on load
	 * @param filter The filtering method to use when scaling this image
	 * @throws SlickException Indicates a failure to load the image
	 */
	public Image(String ref, boolean flipped, int filter) throws SlickException {
		this(ref, flipped, filter, null);
	}
	
	/**
	 * Create an image based on a file at the specified location
	 * 
	 * @param ref The location of the image file to load
	 * @param flipped True if the image should be flipped on the y-axis on load
	 * @param filter The filtering method to use when scaling this image
	 * @param transparent The color to treat as transparent
	 * @throws SlickException Indicates a failure to load the image
	 */
	public Image(String ref, boolean flipped, int filter, Color transparent) throws SlickException {
		try {
			this.ref = ref;
			int[] trans = null;
			if (transparent != null) {
				trans = new int[3];
				trans[0] = (int) (transparent.r * 255);
				trans[1] = (int) (transparent.g * 255);
				trans[2] = (int) (transparent.b * 255);
			}
			texture = TextureLoader.get().getTexture(ref, flipped, filter == FILTER_LINEAR ? GL11.GL_LINEAR : GL11.GL_NEAREST, trans);
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException("Failed to load image from: "+ref, e);
		}
	}
	
	/**
	 * Create an empty image
	 * 
	 * @param width The width of the image
	 * @param height The height of the image
	 * @throws SlickException Indicates a failure to create the underlying resource
	 */
	public Image(int width, int height) throws SlickException {
		ref = super.toString();
		
		try {
			texture = TextureLoader.get().createTexture(width, height);
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException("Failed to create empty image "+width+"x"+height);
		}
		
		init();
	}
	
	/**
	 * Create an image based on a file at the specified location
	 * 
	 * @param in The input stream to read the image from
	 * @param ref The name that should be assigned to the image
	 * @param flipped True if the image should be flipped on the y-axis  on load
	 * @throws SlickException Indicates a failure to load the image
	 */
	public Image(InputStream in, String ref, boolean flipped) throws SlickException {
		this(in, ref, flipped, FILTER_LINEAR);
	}

	/**
	 * Create an image based on a file at the specified location
	 * 
	 * @param in The input stream to read the image from
	 * @param ref The name that should be assigned to the image
	 * @param flipped True if the image should be flipped on the y-axis on load
	 * @param filter The filter to use when scaling this image
	 * @throws SlickException Indicates a failure to load the image
	 */
	public Image(InputStream in, String ref, boolean flipped,int filter) throws SlickException {
		load(in, ref, flipped, filter, null);
	}
	
	/**
	 * Create an image from a pixelData of pixels
	 * 
	 * @param buffer The pixelData to use to create the image
	 */
	Image(ImageBuffer buffer) {
		this(buffer, FILTER_LINEAR);
	}
	
	/**
	 * Create an image from a pixelData of pixels
	 * 
	 * @param buffer The pixelData to use to create the image
	 * @param filter The filter to use when scaling this image
	 */
	Image(ImageBuffer buffer, int filter) {
		this((ImageData) buffer, filter);
	}

	/**
	 * Create an image from a image data source
	 * 
	 * @param data The pixelData to use to create the image
	 */
	public Image(ImageData data) {
		this(data, FILTER_LINEAR);
	}
	
	/**
	 * Create an image from a image data source. Note that this method uses 
	 * 
	 * @param data The pixelData to use to create the image
	 * @param filter The filter to use when scaling this image
	 */
	public Image(ImageData data, int filter) {
		try {
			texture = TextureLoader.get().getTexture(data, filter == FILTER_LINEAR ? GL11.GL_LINEAR : GL11.GL_NEAREST);
			ref = texture.toString();
		} catch (IOException e) {
			Log.error(e);
		}
	}
	
	/**
	 * Get a graphics context that can be used to draw to this image
	 * 
	 * @return The graphics context used to render to this image
	 * @throws SlickException Indicates a failure to create a graphics context
	 */
	public Graphics getGraphics() throws SlickException {
		return GraphicsFactory.getGraphicsForImage(this);
	}
	
	/**
	 * Load the image
	 * 
	 * @param in The input stream to read the image from
	 * @param ref The name that should be assigned to the image
	 * @param flipped True if the image should be flipped on the y-axis  on load
	 * @param filter The filter to use when scaling this image
	 * @param transparent The color to treat as transparent
	 * @throws SlickException Indicates a failure to load the image
	 */
	private void load(InputStream in, String ref, boolean flipped, int filter, Color transparent) throws SlickException {
		try {
			this.ref = ref;
			int[] trans = null;
			if (transparent != null) {
				trans = new int[3];
				trans[0] = (int) (transparent.r * 255);
				trans[1] = (int) (transparent.g * 255);
				trans[2] = (int) (transparent.b * 255);
			}
			texture = TextureLoader.get().getTexture(in, ref, flipped, filter == FILTER_LINEAR ? GL11.GL_LINEAR : GL11.GL_NEAREST, trans);
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException("Failed to load image from: "+ref, e);
		}
	}

	/**
	 * Bind to the texture of this image
	 */
	public void bind() {
		texture.bind();
	}

	/**
	 * Reinitialise internal data
	 */
	protected void reinit() {
		inited = false;
		init();
	}
	
	/**
	 * Initialise internal data
	 */
	protected final void init() {
		if (inited) {
			return;
		}
		
		inited = true;
		if (texture != null) {
			width = texture.getImageWidth();
			height = texture.getImageHeight();
			textureOffsetX = 0;
			textureOffsetY = 0;
			textureWidth = texture.getWidth();
			textureHeight = texture.getHeight();
		}
		
		initImpl();
	}

	/**
	 * Hook for subclasses to perform initialisation
	 */
	protected void initImpl() {
		
	}
	
	/**
	 * Draw this image at the current location
	 */
	public void draw() {
		draw(0,0);
	}
	
	/**
	 * Draw this image at the specified location
	 * 
	 * @param x The x location to draw the image at
	 * @param y The y location to draw the image at
	 */
	public void draw(float x, float y) {
		init();
		draw(x,y,width,height);
	}
	
	/**
	 * Draw this image at the specified location
	 * 
	 * @param x The x location to draw the image at
	 * @param y The y location to draw the image at
	 * @param filter The color to filter with when drawing
	 */
	public void draw(float x, float y, Color filter) {
		init();
		draw(x,y,width,height, filter);
	}

	/**
	 * Draw this image as part of a collection of images
	 * 
	 * @param x The x location to draw the image at
	 * @param y The y location to draw the image at
	 * @param width The width to render the image at
	 * @param height The height to render the image at
	 */
	public void drawEmbedded(float x,float y,float width,float height) {
		init();
		
	    GL11.glTexCoord2f(textureOffsetX, textureOffsetY);
		GL11.glVertex3f(x, y, 0);
		GL11.glTexCoord2f(textureOffsetX, textureOffsetY + textureHeight);
		GL11.glVertex3f(x, y + height, 0);
		GL11.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY
				+ textureHeight);
		GL11.glVertex3f(x + width, y + height, 0);
		GL11.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY);
		GL11.glVertex3f(x + width, y, 0);
	}

	/**
	 * Draw the image with a given scale
	 * 
	 * @param x The x position to draw the image at
	 * @param y The y position to draw the image at
	 * @param scale The scaling to apply
	 */
	public void draw(float x,float y,float scale) {
		init();
		draw(x,y,width*scale,height*scale,Color.white);
	}
	
	/**
	 * Draw the image with a given scale
	 * 
	 * @param x The x position to draw the image at
	 * @param y The y position to draw the image at
	 * @param scale The scaling to apply
	 * @param filter The colour filter to adapt the image with
	 */
	public void draw(float x,float y,float scale,Color filter) {
		init();
		draw(x,y,width*scale,height*scale,filter);
	}
	
	/**
	 * Draw this image at a specified location and size
	 * 
	 * @param x
	 *            The x location to draw the image at
	 * @param y
	 *            The y location to draw the image at
	 * @param width
	 *            The width to render the image at
	 * @param height
	 *            The height to render the image at
	 */
	public void draw(float x,float y,float width,float height) {
		init();
		draw(x,y,width,height,Color.white);
	}
	
	/**
	 * Draw this image at a specified location and size
	 * 
	 * @param x The x location to draw the image at
	 * @param y The y location to draw the image at
	 * @param width The width to render the image at
	 * @param height The height to render the image at
	 * @param filter The color to filter with while drawing
	 */
	public void draw(float x,float y,float width,float height,Color filter) {
		if (filter != null) {
			filter.bind();
		} 
		
		texture.bind();

		GL11.glBegin(GL11.GL_QUADS);
			drawEmbedded(x,y,width,height);
		GL11.glEnd();
	}

	/**
	 * Draw this image at a specified location and size as a silohette
	 * 
	 * @param x The x location to draw the image at
	 * @param y The y location to draw the image at
	 * @param width The width to render the image at
	 * @param height The height to render the image at
	 */
	public void drawFlash(float x,float y,float width,float height) {
		drawFlash(x,y,width,height,Color.white);
	}
	
	/**
	 * Draw this image at a specified location and size as a silohette
	 * 
	 * @param x The x location to draw the image at
	 * @param y The y location to draw the image at
	 * @param width The width to render the image at
	 * @param height The height to render the image at
	 * @param col The color for the sillohette
	 */
	public void drawFlash(float x,float y,float width,float height, Color col) {
		init();
		
		Color.white.bind();
		texture.bind();

		if (GLContext.getCapabilities().GL_EXT_secondary_color) {
			GL11.glEnable(EXTSecondaryColor.GL_COLOR_SUM_EXT);
			EXTSecondaryColor.glSecondaryColor3ubEXT((byte)(col.r * 255), 
													 (byte)(col.g * 255), 
													 (byte)(col.b * 255));
		}
		
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);

		GL11.glBegin(GL11.GL_QUADS);
			drawEmbedded(x,y,width,height);
		GL11.glEnd();
		if (GLContext.getCapabilities().GL_EXT_secondary_color) {
			GL11.glDisable(EXTSecondaryColor.GL_COLOR_SUM_EXT);
		}
	}

	/**
	 * Draw this image at a specified location and size in a white silohette
	 * 
	 * @param x The x location to draw the image at
	 * @param y The y location to draw the image at
	 */
	public void drawFlash(float x,float y) {
		drawFlash(x,y,getWidth(),getHeight());
	}
	
	/**
	 * Get a sub-part of this image. Note that the create image retains a reference to the
	 * image data so should anything change it will affect sub-images too.
	 * 
	 * @param x The x coordinate of the sub-image
	 * @param y The y coordinate of the sub-image
	 * @param width The width of the sub-image
	 * @param height The height of the sub-image
	 * @return The image represent the sub-part of this image
	 */
	public Image getSubImage(int x,int y,int width,int height) {
		init();
		
		float newTextureOffsetX = ((x / (float) this.width) * textureWidth) + textureOffsetX;
		float newTextureOffsetY = ((y / (float) this.height) * textureHeight) + textureOffsetY;
		float newTextureWidth = ((width / (float) this.width) * textureWidth);
		float newTextureHeight = ((height / (float) this.height) * textureHeight);
		
		Image sub = new Image();
		sub.inited = true;
		sub.texture = this.texture;
		sub.textureOffsetX = newTextureOffsetX;
		sub.textureOffsetY = newTextureOffsetY;
		sub.textureWidth = newTextureWidth;
		sub.textureHeight = newTextureHeight;
		
		sub.width = width;
		sub.height = height;
		sub.ref = ref;
		
		return sub;
	}

	/**
	 * Draw a section of this image at a particular location and scale on the screen
	 * 
	 * @param x The x position to draw the image
	 * @param y The y position to draw the image
	 * @param srcx The x position of the rectangle to draw from this image (i.e. relative to this image)
	 * @param srcy The y position of the rectangle to draw from this image (i.e. relative to this image)
	 * @param srcx2 The x position of the bottom right cornder of rectangle to draw from this image (i.e. relative to this image)
	 * @param srcy2 The t position of the bottom right cornder of rectangle to draw from this image (i.e. relative to this image)
	 */
	public void draw(float x, float y, float srcx, float srcy, float srcx2, float srcy2) {
		draw(x,y,x+width,y+height,srcx,srcy,srcx2,srcy2);
	}
	
	/**
	 * Draw a section of this image at a particular location and scale on the screen
	 * 
	 * @param x The x position to draw the image
	 * @param y The y position to draw the image
	 * @param x2 The x position of the bottom right corner of the drawn image
	 * @param y2 The y position of the bottom right corner of the drawn image
	 * @param srcx The x position of the rectangle to draw from this image (i.e. relative to this image)
	 * @param srcy The y position of the rectangle to draw from this image (i.e. relative to this image)
	 * @param srcx2 The x position of the bottom right cornder of rectangle to draw from this image (i.e. relative to this image)
	 * @param srcy2 The t position of the bottom right cornder of rectangle to draw from this image (i.e. relative to this image)
	 */
	public void draw(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2) {
		draw(x,y,x2,y2,srcx,srcy,srcx2,srcy2,Color.white);
	}
	
	/**
	 * Draw a section of this image at a particular location and scale on the screen
	 * 
	 * @param x The x position to draw the image
	 * @param y The y position to draw the image
	 * @param x2 The x position of the bottom right corner of the drawn image
	 * @param y2 The y position of the bottom right corner of the drawn image
	 * @param srcx The x position of the rectangle to draw from this image (i.e. relative to this image)
	 * @param srcy The y position of the rectangle to draw from this image (i.e. relative to this image)
	 * @param srcx2 The x position of the bottom right cornder of rectangle to draw from this image (i.e. relative to this image)
	 * @param srcy2 The t position of the bottom right cornder of rectangle to draw from this image (i.e. relative to this image)
	 * @param filter The colour filter to apply when drawing
	 */
	public void draw(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2, Color filter) {
		init();

		filter.bind();
		texture.bind();

		float mywidth = x2 - x;
		float myheight = y2 - y;
		float texwidth = srcx2 - srcx;
		float texheight = srcy2 - srcy;

		float newTextureOffsetX = (((srcx) / (width)) * textureWidth)
				+ textureOffsetX;
		float newTextureOffsetY = (((srcy) / (height)) * textureHeight)
				+ textureOffsetY;
		float newTextureWidth = ((texwidth) / (width))
				* textureWidth;
		float newTextureHeight = ((texheight) / (height))
				* textureHeight;

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(newTextureOffsetX, newTextureOffsetY);
		GL11.glVertex3f(x,y, 0.0f);
		GL11.glTexCoord2f(newTextureOffsetX, newTextureOffsetY
				+ newTextureHeight);
		GL11.glVertex3f(x,(y + myheight), 0.0f);
		GL11.glTexCoord2f(newTextureOffsetX + newTextureWidth,
				newTextureOffsetY + newTextureHeight);
		GL11.glVertex3f((x + mywidth),(y + myheight), 0.0f);
		GL11.glTexCoord2f(newTextureOffsetX + newTextureWidth,
				newTextureOffsetY);
		GL11.glVertex3f((x + mywidth),y, 0.0f);
		GL11.glEnd();
	}

	
	/**
	 * Get the width of this image
	 * 
	 * @return The width of this image
	 */
	public int getWidth() {
		init();
		return width;
	}

	/**
	 * Get the height of this image
	 * 
	 * @return The height of this image
	 */
	public int getHeight() {
		init();
		return height;
	}
	
	/**
	 * Get a copy of this image. This is a shallow copy and does not 
	 * duplicate image adata.
	 * 
	 * @return The copy of this image
	 */
	public Image copy() {
		init();
		return getSubImage(0,0,width,height);
	}

	/**
	 * Get a scaled copy of this image with a uniform scale
	 * 
	 * @param scale The scale to apply
	 * @return The new scaled image
	 */
	public Image getScaledCopy(float scale) {
		return getScaledCopy((int) (width*scale),(int) (height*scale));
	}
	
	/**
	 * Get a scaled copy of this image
	 * 
	 * @param width The width of the copy
	 * @param height The height of the copy
	 * @return The new scaled image
	 */
	public Image getScaledCopy(int width, int height) {
		init();
		Image image = copy();
		image.width = width;
		image.height = height;
		
		return image;
	}
	
	/**
	 * Make sure the texture cordinates are inverse on the y axis
	 */
	public void ensureInverted() {
		if (textureHeight > 0) {
			textureOffsetY = textureOffsetY + textureHeight;
			textureHeight = -textureHeight;
		}
	}
	
	/**
	 * Get a copy image flipped on potentially two axis
	 * 
	 * @param flipHorizontal True if we want to flip the image horizontally
	 * @param flipVertical True if we want to flip the image vertically
	 * @return The flipped image instance
	 */
	public Image getFlippedCopy(boolean flipHorizontal, boolean flipVertical) {
		init();
		Image image = copy();
		
		if (flipHorizontal) {
			image.textureOffsetX = textureOffsetX + textureWidth;
			image.textureWidth = -textureWidth;
		}
		if (flipVertical) {
			image.textureOffsetY = textureOffsetY + textureHeight;
			image.textureHeight = -textureHeight;
		}
		
		return image;
	}

	/**
	 * End the use of this sprite sheet and release the lock. 
	 * 
	 * @see #startUse
	 */
	public void endUse() {
		if (inUse != this) {
			throw new RuntimeException("The sprite sheet is not currently in use");
		}
		
		inUse = null;
		GL11.glEnd();
	}
	
	/**
	 * Start using this sheet. This method can be used for optimal rendering of a collection 
	 * of sprites from a single sprite sheet. First, startUse(). Then render each sprite by
	 * calling renderInUse(). Finally, endUse(). Between start and end there can be no rendering
	 * of other sprites since the rendering is locked for this sprite sheet.
	 */
	public void startUse() {
		if (inUse != null) {
			throw new RuntimeException("Attempt to start use of a sprite sheet before ending use with another - see endUse()");
		}
		inUse = this;
		init();
		
		Color.white.bind();
		texture.bind();
		GL11.glBegin(GL11.GL_QUADS);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		init();
		
		return "[Image "+ref+" "+width+"x"+height+"  "+textureOffsetX+","+textureOffsetY+","+textureWidth+","+textureHeight+"]";
	}
	
	/**
	 * Get the OpenGL texture holding this image
	 * 
	 * @return The OpenGL texture holding this image
	 */
	public Texture getTexture() {
		return texture;
	}
	
	/**
	 * Set the texture used by this image
	 * 
	 * @param texture The texture used by this image
	 */
	public void setTexture(Texture texture) {
		this.texture = texture;
		reinit();
	}

	/**
	 * Translate an unsigned int into a signed integer
	 * 
	 * @param b The byte to convert
	 * @return The integer value represented by the byte
	 */
	private int translate(byte b) {
		if (b < 0) {
			return 256 + b;
		}
		
		return b;
	}
	
	/**
	 * Get the colour of a pixel at a specified location in this image
	 * 
	 * @param x The x coordinate of the pixel
	 * @param y The y coordinate of the pixel
	 * @return The Color of the pixel at the specified location
	 */
	public Color getColor(int x, int y) {
		if (pixelData == null) {
			pixelData = texture.getTextureData();
		}
		
		int offset = x + (y * texture.getTextureWidth());
		offset *= texture.hasAlpha() ? 4 : 3;
		
		if (texture.hasAlpha()) {
			return new Color(translate(pixelData[offset]),translate(pixelData[offset+1]),
							 translate(pixelData[offset+2]),translate(pixelData[offset+3]));
		} else {
			return new Color(translate(pixelData[offset]),translate(pixelData[offset+1]),
					 	     translate(pixelData[offset+2]));
		}
	}
}
