package org.newdawn.slick;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.FastTrig;

/**
 * A graphics context that can be used to render primatives to the accelerated 
 * canvas provided by LWJGL.
 * 
 * @author kevin
 */
public class Graphics {
	/** The default number of segments that will be used when drawing an oval */
	private static final int DEFAULT_SEGMENTS = 50;
	
	/** The font in use */
	private Font font;
	/** The default font to use */
	private Font defaultFont;
	/** The current color */
	private Color currentColor = Color.white;
	/** The width of the screen */
	private int screenWidth;
	/** The height of the screen */
	private int screenHeight;
	/** True if the matrix has been pushed to the stack */
	private boolean pushed;
	
	/**
	 * Create a new graphics context. Only the container should
	 * be doing this really
	 * 
	 * @param font The default font to use in this context
	 * @param width The width of the screen for this context
	 * @param height The height of the screen for this context
	 */
	Graphics(Font font, int width, int height) {		
		defaultFont = this.font = font;
		screenWidth = width;
		screenHeight = height;
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
		GL11.glClearColor(color.r, color.g, color.b, 1);       
	}
	
	/**
	 * Reset the transformation on this graphics context
	 */
	public void resetTransform() {	
		if (pushed) {
			GL11.glPopMatrix();
			pushed = false;
		}
	}

	/** 
	 * Check if we've pushed the previous matrix, if not then
	 * push it now.
	 */
	private void checkPush() {
		if (!pushed) {
			GL11.glPushMatrix();
			pushed = true;
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
		
		GL11.glScalef(sx,sy,0);
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
		
		translate(rx,ry);
		GL11.glRotatef(ang,0,0,1);
		translate(-rx,-ry);
	}
	
	/**
	 * Apply a translation to everything drawn to the context
	 * 
	 * @param x The amount to translate on the x-axis
	 * @param y The amount of translate on the y-axis
	 */
	public void translate(float x, float y) {
		checkPush();
		
		GL11.glTranslatef(x,y,0);
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
		color.bind();
	}
	
	/**
	 * Draw a line on the canvas in the current colour
	 * 
	 * @param x1 The x coordinate of the start point
	 * @param y1 The y coordinate of the start point
 	 * @param x2 The x coordinate of the end point
	 * @param y2 The y coordinate of the end point
	 */
	public void drawLine(int x1, int y1, int x2, int y2) {
		Texture.bindNone();
		
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2f(x1,y1);
			GL11.glVertex2f(x2,y2);
		GL11.glEnd();
	}
	
	/**
	 * Draw a rectangle to the screen
	 * 
	 * @param rect The rectangle to draw
	 */
	public void draw(Rectangle rect) {
		drawRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
	}
	
	/**
	 * Fill a rectangle on the screen
	 * 
	 * @param rect The rectangle to fill
	 */
	public void fill(Rectangle rect) {
		fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
	}
	
	/**
	 * Draw a circle to the screen
	 * 
	 * @param circle The circle to draw
	 */
	public void draw(Circle circle) {
		drawOval((int) (circle.getX() - circle.getRadius()), (int) (circle.getY() - circle.getRadius()),
				 (int) (circle.getRadius() * 2), (int) (circle.getRadius() * 2));
	}
	
	/**
	 * Fill a circle on the screen
	 * 
	 * @param circle The circle to fill
	 */
	public void fill(Circle circle) {
		fillOval((int) (circle.getX() - circle.getRadius()), (int) (circle.getY() - circle.getRadius()),
				 (int) (circle.getRadius() * 2), (int) (circle.getRadius() * 2));
	}
	
	/**
	 * Draw a rectangle to the canvas in the current colour
	 *  
	 * @param x1 The x coordinate of the top left corner
	 * @param y1 The y coordinate of the top left corner
	 * @param width The width of the rectangle to draw
	 * @param height The height of the rectangle to draw
	 */
	public void drawRect(int x1,int y1,int width,int height) {
		Texture.bindNone();
		
		GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex2f(x1,y1);
			GL11.glVertex2f(x1+width,y1);
			GL11.glVertex2f(x1+width,y1+height);
			GL11.glVertex2f(x1,y1+height);
			GL11.glVertex2f(x1,y1);
		GL11.glEnd();
	}
	
	/**
	 * Clear the clipping being applied. This will allow graphics to
	 * be drawn anywhere on the screen
	 */
	public void clearClip() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	/**
	 * Set the clipping to apply to the drawing
	 * 
	 * @param x The x coordinate of the top left cornder of the allowed area
	 * @param y The y coordinate of the top left cornder of the allowed area
	 * @param width The width of the allowed area
	 * @param height The height of the allowed area
	 */
	public void setClip(int x,int y,int width,int height) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(x,screenHeight-y-height,width,height);
	}
	
	/**
	 * Fill a rectangle on the canvas in the current color
	 * 
	 * @param x1 The x coordinate of the top left corner
	 * @param y1 The y coordinate of the top left corner
	 * @param width The width of the rectangle to fill
	 * @param height The height of the rectangle to fill
	 */
	public void fillRect(int x1,int y1,int width,int height) {
		Texture.bindNone();
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x1,y1);
			GL11.glVertex2f(x1+width,y1);
			GL11.glVertex2f(x1+width,y1+height);
			GL11.glVertex2f(x1,y1+height);
		GL11.glEnd();
	}

	/**
	 * Draw an oval to the canvas
	 * 
	 * @param x1 The x coordinate of the top left corner of a box containing the oval
	 * @param y1 The y coordinate of the top left corner of a box containing the oval
	 * @param width The width of the oval
	 * @param height The height of the oval
	 */
	public void drawOval(int x1, int y1, int width, int height) {
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
	public void drawOval(int x1, int y1, int width, int height,int segments) {
		Texture.bindNone();
		
		float cx = x1 + (width/2.0f);
		float cy = y1 + (height/2.0f);
		
		GL11.glBegin(GL11.GL_LINE_STRIP);
			int step = 360 / segments;
			
			for (int a=0;a<360+step;a+=step) {
				float x = (float) (cx+(FastTrig.cos(Math.toRadians(a))*width/2.0f));
				float y = (float) (cy+(FastTrig.sin(Math.toRadians(a))*height/2.0f));
				
				GL11.glVertex2f(x,y);
			}
		GL11.glEnd();
	}

	/**
	 * Fill an oval to the canvas
	 * 
	 * @param x1 The x coordinate of the top left corner of a box containing the oval
	 * @param y1 The y coordinate of the top left corner of a box containing the oval
	 * @param width The width of the oval
	 * @param height The height of the oval
	 */
	public void fillOval(int x1, int y1, int width, int height) {
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
	public void fillOval(int x1, int y1, int width, int height,int segments) {
		Texture.bindNone();
		
		float cx = x1 + (width/2.0f);
		float cy = y1 + (height/2.0f);
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			int step = 360 / segments;
			
			GL11.glVertex2f(cx,cy);
			
			for (int a=0;a<360+step;a+=step) {
				float x = (float) (cx+(FastTrig.cos(Math.toRadians(a))*width/2.0f));
				float y = (float) (cy+(FastTrig.sin(Math.toRadians(a))*height/2.0f));
				
				GL11.glVertex2f(x,y);
			}
		GL11.glEnd();
	}
	
	/**
	 * Set the with of the line to be used when drawing line based primitives
	 * 
	 * @param width The width of the line to be used when drawing line based primitives
	 */
	public void setLineWidth(float width) {
		GL11.glLineWidth(width);
	}
	
	/**
	 * Reset the line width in use to the default for this graphics context
	 */
	public void resetLineWidth() {
		GL11.glLineWidth(1.0f);
	}
	
	/**
	 * Indicate if we should antialias as we draw primitives
	 * 
	 * @param anti True if we should antialias
	 */
	public void setAntiAlias(boolean anti) {
		if (anti) {
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		} else {
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		}
	}
	
	/**
	 * Draw a string to the screen using the current font
	 * 
	 * @param str The string to draw
	 * @param x The x coordinate to draw the string at
	 * @param y The y coordinate to draw the string at
	 */
	public void drawString(String str,int x,int y) {
		font.drawString(x, y, str, currentColor);
	}

	/**
	 * Draw an image to the screen
	 * 
	 * @param image The image to draw to the screen
	 * @param x The x location at which to draw the image
	 * @param y The y location at which to draw the image
	 * @param col The color to apply to the image as a filter
	 */
	public void drawImage(Image image, int x, int y, Color col) {
		col.bind();
		image.draw(x,y);
	}

	/**
	 * Draw an image to the screen
	 * 
	 * @param image The image to draw to the screen
	 * @param x The x location at which to draw the image
	 * @param y The y location at which to draw the image
	 */
	public void drawImage(Image image, int x, int y) {
		drawImage(image, x, y, Color.white);
		currentColor.bind();
	}
}
