package org.newdawn.slick;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.ShapeRenderer;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.FastTrig;
import org.newdawn.slick.util.Log;

/**
 * A graphics context that can be used to render primatives to the accelerated 
 * canvas provided by LWJGL.
 * 
 * @author kevin
 */
public class Graphics {
	/** The default number of segments that will be used when drawing an oval */
	private static final int DEFAULT_SEGMENTS = 50;
	/** The last graphics context in use */
	protected static Graphics currentGraphics = null;
	
	/** The font in use */
	private Font font;
	/** The default font to use */
	private Font defaultFont;
	/** The current color */
	private Color currentColor = Color.white;
	/** The width of the screen */
	protected int screenWidth;
	/** The height of the screen */
	protected int screenHeight;
	/** True if the matrix has been pushed to the stack */
	private boolean pushed;
	/** The graphics context clipping */
	private Rectangle clip;
	/** Buffer used for setting the world clip */
	private DoubleBuffer worldClip = BufferUtils.createDoubleBuffer(4);
	/** The buffer used to read a screen pixel */
	private ByteBuffer readBuffer = BufferUtils.createByteBuffer(4);
	/** True if we're antialias */
	private boolean antialias;
	/** The world clip recorded since last set */
	private Rectangle worldClipRecord;
	
	/**
	 * Create a new graphics context. Only the container should
	 * be doing this really
	 * 
	 * @param width The width of the screen for this context
	 * @param height The height of the screen for this context
	 */
	public Graphics(int width, int height) {	
		AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
            	try {
	        		defaultFont = new AngelCodeFont("org/newdawn/slick/data/default.fnt",
		   			"org/newdawn/slick/data/default_00.tga");
            	} catch (SlickException e) {
            		Log.error(e);
            	}
                return null; // nothing to return
            }
        });
			
		this.font = defaultFont;
		screenWidth = width;
		screenHeight = height;
	}
	
	/**
	 * Must be called before all OpenGL operations to maintain
	 * context for dynamic images
	 */
	private void predraw() {
		if (currentGraphics != this) {
			if (currentGraphics != null) {
				currentGraphics.disable();
			}
			currentGraphics = this;
			currentGraphics.enable();
		}
	}

	/**
	 * Must be called after all OpenGL operations to maintain
	 * context for dynamic images
	 */
	private void postdraw() {
	}
	
	/**
	 * Enable rendering to this graphics context
	 */
	protected void enable() {
	}
	
	/**
	 * Flush this graphics context to the underlying rendering context
	 */
	public void flush() {
		if (currentGraphics == this) {
			currentGraphics.disable();
			currentGraphics = null;
		}
	}
	
	/**
	 * Disable rendering to this graphics context
	 */
	protected void disable() {
	}
	
	/**
	 * Get the current font
	 * 
	 * @return The current font
	 */
	public Font getFont() {
		return font;
	}
	
	/**
	 * Set the background colour of the graphics context
	 * 
	 * @param color The background color of the graphics context
 	 */
	public void setBackground(Color color) {
		predraw();
		GL11.glClearColor(color.r, color.g, color.b, color.a);     
		postdraw();  
	}
	
	/**
	 * Get the current graphics context background color 
	 * 
	 * @return The background color of this graphics context
	 */
	public Color getBackground() {
		predraw();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL11.GL_COLOR_CLEAR_VALUE, buffer);
		postdraw();
		
		return new Color(buffer);
	}
	
	/**
	 * Clear the graphics context
	 */
	public void clear() {
		predraw();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		postdraw();  
	}
	
	/**
	 * Reset the transformation on this graphics context
	 */
	public void resetTransform() {	
		if (pushed) {
			predraw();
			GL11.glPopMatrix();
			pushed = false;
			postdraw();  
		}
	}

	/** 
	 * Check if we've pushed the previous matrix, if not then
	 * push it now.
	 */
	private void checkPush() {
		if (!pushed) {
			predraw();
			GL11.glPushMatrix();
			pushed = true;
			postdraw();  
		}
	}
	
	/**
	 * Apply a scaling factor to everything drawn on the graphics context
	 * 
	 * @param sx The scaling factor to apply to the x axis
	 * @param sy The scaling factor to apply to the y axis
	 */
	public void scale(float sx, float sy) {
		checkPush();

		predraw();
		GL11.glScalef(sx,sy,0);
		postdraw();  
	}
	
	/**
	 * Apply a rotation to everything draw on the graphics context
	 * 
	 * @param rx The x coordinate of the center of rotation
	 * @param ry The y coordinate of the center of rotation
	 * @param ang The angle (in degrees) to rotate by
	 */
	public void rotate(float rx, float ry, float ang) {
		checkPush();

		predraw();
		translate(rx,ry);
		GL11.glRotatef(ang,0,0,1);
		translate(-rx,-ry);
		postdraw();  
	}
	
	/**
	 * Apply a translation to everything drawn to the context
	 * 
	 * @param x The amount to translate on the x-axis
	 * @param y The amount of translate on the y-axis
	 */
	public void translate(float x, float y) {
		checkPush();

		predraw();
		GL11.glTranslatef(x,y,0);
		postdraw();  
	}
	
	/**
	 * Set the font to be used when rendering text
	 * 
	 * @param font The font to be used when rendering text
	 */
	public void setFont(Font font) {
		this.font = font;
	}
	
	/**
	 * Reset to using the default font for this context
	 */
	public void resetFont() {
		font = defaultFont;
	}
	
	/**
	 * Set the color to use when rendering to this context
	 * 
	 * @param color The color to use when rendering to this context
	 */
	public void setColor(Color color) {
		currentColor = color;
		predraw();
		color.bind();
		postdraw();  
	}
	
	/**
	 * Get the color in use by this graphics context
	 * 
	 * @return The color in use by this graphics context
	 */
	public Color getColor() {
		return new Color(currentColor);
	}
	
	/**
	 * Draw a line on the canvas in the current colour
	 * 
	 * @param x1 The x coordinate of the start point
	 * @param y1 The y coordinate of the start point
 	 * @param x2 The x coordinate of the end point
	 * @param y2 The y coordinate of the end point
	 */
	public void drawLine(float x1, float y1, float x2, float y2) {
		predraw();
		currentColor.bind();
		Texture.bindNone();

		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2f(x1,y1);
			GL11.glVertex2f(x2,y2);
		GL11.glEnd();
		postdraw();
	}

    /**
     * Draw the outline of the given shape.
     * 
     * @param shape The shape to draw.
     */
    public void draw(Shape shape) {
        predraw();
        Texture.bindNone();
        currentColor.bind();

        ShapeRenderer.draw(shape);

        postdraw();
    }
    /**
     * Draw the the given shape filled in.
     * 
     * @param shape The shape to fill.
     */
    public void fill(Shape shape) {
        predraw();
        Texture.bindNone();
        currentColor.bind();
        
        ShapeRenderer.fill(shape);
        
        postdraw();
    }
	
	/**
	 * Draw a rectangle to the canvas in the current colour
	 *  
	 * @param x1 The x coordinate of the top left corner
	 * @param y1 The y coordinate of the top left corner
	 * @param width The width of the rectangle to draw
	 * @param height The height of the rectangle to draw
	 */
	public void drawRect(float x1,float y1,float width,float height) {
		predraw();
		Texture.bindNone();
		currentColor.bind();
		
		GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex2f(x1,y1);
			GL11.glVertex2f(x1+width+1,y1);
			GL11.glVertex2f(x1+width,y1+height);
			GL11.glVertex2f(x1,y1+height);
			GL11.glVertex2f(x1,y1);
		GL11.glEnd();
		postdraw();
	}
	
	/**
	 * Clear the clipping being applied. This will allow graphics to
	 * be drawn anywhere on the screen
	 */
	public void clearClip() {
		clip = null;
		predraw();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		postdraw();
	}
	
	/**
	 * Set clipping that controls which areas of the world will
	 * be drawn to. Note that world clip is different from standard
	 * screen clip in that it's defined in the space of the current
	 * world coordinate - i.e. it's affected by translate, rotate, scale
	 * etc.
	 * 
	 * @param x The x coordinate of the top left corner of the allowed area
	 * @param y The y coordinate of the top left corner of the allowed area
	 * @param width The width of the allowed area
	 * @param height The height of the allowed area
	 */
	public void setWorldClip(float x,float y,float width,float height) {
		worldClipRecord = new Rectangle(x,y,width,height);
		GL11.glEnable(GL11.GL_CLIP_PLANE0);
		worldClip.put(1).put(0).put(0).put(-x).flip();
		GL11.glClipPlane(GL11.GL_CLIP_PLANE0, worldClip);
		GL11.glEnable(GL11.GL_CLIP_PLANE1);
		worldClip.put(-1).put(0).put(0).put(x+width).flip();
		GL11.glClipPlane(GL11.GL_CLIP_PLANE1, worldClip);
		
		GL11.glEnable(GL11.GL_CLIP_PLANE2);
		worldClip.put(0).put(1).put(0).put(-y).flip();
		GL11.glClipPlane(GL11.GL_CLIP_PLANE2, worldClip);
		GL11.glEnable(GL11.GL_CLIP_PLANE3);
		worldClip.put(0).put(-1).put(0).put(y+height).flip();
		GL11.glClipPlane(GL11.GL_CLIP_PLANE3, worldClip);
	}
	
	/**
	 * Clear world clipping setup. This does not effect screen clipping
	 */
	public void clearWorldClip() {
		worldClipRecord = null;
		GL11.glDisable(GL11.GL_CLIP_PLANE0);
		GL11.glDisable(GL11.GL_CLIP_PLANE1);
		GL11.glDisable(GL11.GL_CLIP_PLANE2);
		GL11.glDisable(GL11.GL_CLIP_PLANE3);
	}
	
	/**
	 * Set the world clip to be applied
	 * 
	 * @see #setWorldClip(float, float, float, float)
	 * @param clip The area still visible
	 */
	public void setWorldClip(Rectangle clip) {
		if (clip == null) {
			clearWorldClip();
		} else {
			setWorldClip(clip.x,clip.y,clip.height,clip.width);
		}
	}
	
	/**
	 * Get the last set world clip or null of the world clip isn't set
	 * 
	 * @return The last set world clip rectangle
	 */
	public Rectangle getWorldClip() {
		return worldClipRecord;
	}
	
	/**
	 * Set the clipping to apply to the drawing. Note that this clipping takes no
	 * note of the transforms that have been applied to the context and is always
	 * in absolute screen space coordinates.
	 * 
	 * @param x The x coordinate of the top left corner of the allowed area
	 * @param y The y coordinate of the top left corner of the allowed area
	 * @param width The width of the allowed area
	 * @param height The height of the allowed area
	 */
	public void setClip(int x,int y,int width,int height) {
		predraw();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		clip = new Rectangle(x,y,width,height);
		GL11.glScissor(x,screenHeight-y-height,width,height);
		postdraw();
	}

	/**
	 * Set the clipping to apply to the drawing. Note that this clipping takes no
	 * note of the transforms that have been applied to the context and is always
	 * in absolute screen space coordinates.
	 * 
	 * @param rect The rectangle describing the clipped area in screen coordinates
	 */
	public void setClip(Rectangle rect) {
		if (rect == null) {
			clearClip();
			return;
		}

		setClip((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height);
	}
	
	/**
	 * Return the currently applied clipping rectangle
	 * 
	 * @return The current applied clipping rectangle or null if no clipping is applied
	 */
	public Rectangle getClip() {
		return clip;
	}
	
	/**
	 * Tile a rectangle with a pattern specifing the offset from the top corner that one tile
	 * should match
	 * 
	 * @param x The x coordinate of the rectangle
	 * @param y The y coordinate of the rectangle
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 * @param pattern The image to tile across the rectangle
	 * @param offX The offset on the x axis from the top left corner
	 * @param offY The offset on the y axis from the top left corner
	 */
	public void fillRect(float x, float y, float width, float height, Image pattern, float offX, float offY) {
		int cols = ((int) Math.ceil(width / pattern.getWidth())) + 2;
		int rows = ((int) Math.ceil(height / pattern.getHeight())) + 2;

		Rectangle preClip = getWorldClip();
		setWorldClip(x,y,width,height);

		predraw();
		// Draw all the quads we need
		for (int c = 0; c < cols; c++) {
			for (int r = 0; r < rows; r++) {
				pattern.draw(c * pattern.getWidth() + x - offX, r
						* pattern.getHeight() + y - offY);
			}
		}
		postdraw();
		
		setWorldClip(preClip);
	}
	
	/**
	 * Fill a rectangle on the canvas in the current color
	 * 
	 * @param x1 The x coordinate of the top left corner
	 * @param y1 The y coordinate of the top left corner
	 * @param width The width of the rectangle to fill
	 * @param height The height of the rectangle to fill
	 */
	public void fillRect(float x1,float y1,float width,float height) {
		predraw();
		Texture.bindNone();
		currentColor.bind();
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x1,y1);
			GL11.glVertex2f(x1+width,y1);
			GL11.glVertex2f(x1+width,y1+height);
			GL11.glVertex2f(x1,y1+height);
		GL11.glEnd();
		postdraw();
	}

	/**
	 * Draw an oval to the canvas
	 * 
	 * @param x1 The x coordinate of the top left corner of a box containing the oval
	 * @param y1 The y coordinate of the top left corner of a box containing the oval
	 * @param width The width of the oval
	 * @param height The height of the oval
	 */
	public void drawOval(float x1, float y1, float width, float height) {
		drawOval(x1,y1,width,height,DEFAULT_SEGMENTS);
	}

	/**
	 * Draw an oval to the canvas
	 * 
	 * @param x1 The x coordinate of the top left corner of a box containing the oval
	 * @param y1 The y coordinate of the top left corner of a box containing the oval
	 * @param width The width of the oval
	 * @param height The height of the oval
	 * @param segments The number of line segments to use when drawing the oval
	 */
	public void drawOval(float x1, float y1, float width, float height,int segments) {
		drawArc(x1,y1,width,height,segments,0,360);
	}

	/**
	 * Draw an oval to the canvas
	 * 
	 * @param x1 The x coordinate of the top left corner of a box containing the arc
	 * @param y1 The y coordinate of the top left corner of a box containing the arc
	 * @param width The width of the arc
	 * @param height The height of the arc
	 * @param start The angle the arc starts at
	 * @param end The angle the arc ends at
	 */
	public void drawArc(float x1, float y1, float width, float height,float start,float end) {
		drawArc(x1,y1,width,height,DEFAULT_SEGMENTS,start,end);
	}
	
	/**
	 * Draw an oval to the canvas
	 * 
	 * @param x1 The x coordinate of the top left corner of a box containing the arc
	 * @param y1 The y coordinate of the top left corner of a box containing the arc
	 * @param width The width of the arc
	 * @param height The height of the arc
	 * @param segments The number of line segments to use when drawing the arc
	 * @param start The angle the arc starts at
	 * @param end The angle the arc ends at
	 */
	public void drawArc(float x1, float y1, float width, float height,int segments,float start,float end) {
		predraw();
		Texture.bindNone();
		currentColor.bind();
		
		while (end < start) {
			end += 360;
		}
		
		float cx = x1 + (width/2.0f);
		float cy = y1 + (height/2.0f);
		
		GL11.glBegin(GL11.GL_LINE_STRIP);
			int step = 360 / segments;
			
			for (int a=(int) start;a<(int) (end+step);a+=step) {
				float ang = a;
				if (ang > end) {
					ang = end;
				}
				float x = (float) (cx+(FastTrig.cos(Math.toRadians(ang))*width/2.0f));
				float y = (float) (cy+(FastTrig.sin(Math.toRadians(ang))*height/2.0f));
				
				GL11.glVertex2f(x,y);
			}
		GL11.glEnd();
		postdraw();
	}

	/**
	 * Fill an oval to the canvas
	 * 
	 * @param x1 The x coordinate of the top left corner of a box containing the oval
	 * @param y1 The y coordinate of the top left corner of a box containing the oval
	 * @param width The width of the oval
	 * @param height The height of the oval
	 */
	public void fillOval(float x1, float y1, float width, float height) {
		fillOval(x1,y1,width,height,DEFAULT_SEGMENTS);
	}

	/**
	 * Fill an oval to the canvas
	 * 
	 * @param x1 The x coordinate of the top left corner of a box containing the oval
	 * @param y1 The y coordinate of the top left corner of a box containing the oval
	 * @param width The width of the oval
	 * @param height The height of the oval
	 * @param segments The number of line segments to use when filling the oval
	 */
	public void fillOval(float x1, float y1, float width, float height,int segments) {
		fillArc(x1,y1,width,height,segments,0,360);
	}

	/**
	 * Fill an arc to the canvas (a wedge)
	 * 
	 * @param x1 The x coordinate of the top left corner of a box containing the arc
	 * @param y1 The y coordinate of the top left corner of a box containing the arc
	 * @param width The width of the arc
	 * @param height The height of the arc
	 * @param start The angle the arc starts at
	 * @param end The angle the arc ends at
	 */
	public void fillArc(float x1, float y1, float width, float height,float start,float end) {
		fillArc(x1,y1,width,height,DEFAULT_SEGMENTS,start,end);
	}
	
	/**
	 * Fill an arc to the canvas (a wedge)
	 * 
	 * @param x1 The x coordinate of the top left corner of a box containing the arc
	 * @param y1 The y coordinate of the top left corner of a box containing the arc
	 * @param width The width of the arc
	 * @param height The height of the arc
	 * @param segments The number of line segments to use when filling the arc
	 * @param start The angle the arc starts at
	 * @param end The angle the arc ends at
	 */
	public void fillArc(float x1, float y1, float width, float height,int segments,float start,float end) {
		predraw();
		Texture.bindNone();
		currentColor.bind();
		
		while (end < start) {
			end += 360;
		}
		
		float cx = x1 + (width/2.0f);
		float cy = y1 + (height/2.0f);
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			int step = 360 / segments;
			
			GL11.glVertex2f(cx,cy);
			
			for (int a=(int) start;a<(int) (end+step);a+=step) {
				float ang = a;
				if (ang > end) {
					ang = end;
				}
				
				float x = (float) (cx+(FastTrig.cos(Math.toRadians(ang))*width/2.0f));
				float y = (float) (cy+(FastTrig.sin(Math.toRadians(ang))*height/2.0f));
				
				GL11.glVertex2f(x,y);
			}
		GL11.glEnd();
		
		if (antialias) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glVertex2f(cx,cy);
				if (end != 360) {
					end -= 10;
				}
				
				for (int a=(int) start;a<(int) (end+step);a+=step) {
					float ang = a;
					if (ang > end) {
						ang = end;
					}
					
					float x = (float) (cx+(FastTrig.cos(Math.toRadians(ang+10))*width/2.0f));
					float y = (float) (cy+(FastTrig.sin(Math.toRadians(ang+10))*height/2.0f));
					
					GL11.glVertex2f(x,y);
				}
			GL11.glEnd();
		}
		
		postdraw();
	}
	
	/**
	 * Set the with of the line to be used when drawing line based primitives
	 * 
	 * @param width The width of the line to be used when drawing line based primitives
	 */
	public void setLineWidth(float width) {
		predraw();
		GL11.glLineWidth(width);
		postdraw();
	}
	
	/**
	 * Reset the line width in use to the default for this graphics context
	 */
	public void resetLineWidth() {
		predraw();
		GL11.glLineWidth(1.0f);
		postdraw();
	}
	
	/**
	 * Indicate if we should antialias as we draw primitives
	 * 
	 * @param anti True if we should antialias
	 */
	public void setAntiAlias(boolean anti) {
		predraw();
		antialias = anti;
		if (anti) {
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		} else {
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		}
		postdraw();
	}

	/**
	 * Draw a string to the screen using the current font
	 * 
	 * @param str The string to draw
	 * @param x The x coordinate to draw the string at
	 * @param y The y coordinate to draw the string at
	 */
	public void drawString(String str,float x,float y) {
		predraw();
		font.drawString(x, y, str, currentColor);
		postdraw();
	}

	/**
	 * Draw an image to the screen
	 * 
	 * @param image The image to draw to the screen
	 * @param x The x location at which to draw the image
	 * @param y The y location at which to draw the image
	 * @param col The color to apply to the image as a filter
	 */
	public void drawImage(Image image, float x, float y, Color col) {
		predraw();
		image.draw(x,y,col);
		currentColor.bind();
		postdraw();
	}
	
	/**
	 * Draw an image to the screen
	 * 
	 * @param image The image to draw to the screen
	 * @param x The x location at which to draw the image
	 * @param y The y location at which to draw the image
	 */
	public void drawImage(Image image, float x, float y) {
		predraw();
		drawImage(image, x, y, Color.white);
		currentColor.bind();
		postdraw();
	}

	/**
	 * Draw a section of an image at a particular location and scale on the screen
	 * 
	 * @param image The image to draw a section of
	 * @param x The x position to draw the image
	 * @param y The y position to draw the image
	 * @param x2 The x position of the bottom right corner of the drawn image
	 * @param y2 The y position of the bottom right corner of the drawn image
	 * @param srcx The x position of the rectangle to draw from this image (i.e. relative to the image)
	 * @param srcy The y position of the rectangle to draw from this image (i.e. relative to the image)
	 * @param srcx2 The x position of the bottom right cornder of rectangle to draw from this image (i.e. relative to the image)
	 * @param srcy2 The t position of the bottom right cornder of rectangle to draw from this image (i.e. relative to the image)
	 */
	public void drawImage(Image image, float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2) {
		predraw();
		image.draw(x,y,x2,y2,srcx,srcy,srcx2,srcy2);
		currentColor.bind();
		postdraw();
	}

	/**
	 * Draw a section of an image at a particular location and scale on the screen
	 * 
	 * @param image The image to draw a section of
	 * @param x The x position to draw the image
	 * @param y The y position to draw the image
	 * @param srcx The x position of the rectangle to draw from this image (i.e. relative to the image)
	 * @param srcy The y position of the rectangle to draw from this image (i.e. relative to the image)
	 * @param srcx2 The x position of the bottom right cornder of rectangle to draw from this image (i.e. relative to the image)
	 * @param srcy2 The t position of the bottom right cornder of rectangle to draw from this image (i.e. relative to the image)
	 */
	public void drawImage(Image image, float x, float y, float srcx, float srcy, float srcx2, float srcy2) {
		drawImage(image, x, y, x+image.getWidth(), y+image.getHeight(), srcx, srcy, srcx2, srcy2);
	}
	
	/**
	 * Copy an area of the rendered screen into an image. The width and height of the area 
	 * are assumed to match that of the image
	 * 
	 * @param target The target image
	 * @param x The x position to copy from
	 * @param y The y position to copy from
	 */
	public void copyArea(Image target, int x, int y) {
		int format = target.getTexture().hasAlpha() ? GL11.GL_RGBA : GL11.GL_RGB;
		target.bind();
		GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, format, x, screenHeight-(y+target.getHeight()), 
							  target.getTexture().getTextureWidth(),
							  target.getTexture().getTextureHeight(), 0);
		target.ensureInverted();
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
	 * Get the colour of a single pixel in this graphics context
	 * 
	 * @param x The x coordinate of the pixel to read
	 * @param y The y coordinate of the pixel to read
	 * @return The colour of the pixel at the specified location
	 */
	public Color getPixel(int x, int y) {
		GL11.glReadPixels(x,screenHeight-y,1,1,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,readBuffer);
		
		return new Color(translate(readBuffer.get(0)),
						 translate(readBuffer.get(1)),
						 translate(readBuffer.get(2)),
						 translate(readBuffer.get(3)));
	}
}
