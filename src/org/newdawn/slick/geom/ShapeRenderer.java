package org.newdawn.slick.geom;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GradientFill;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;

/**
 * @author Mark Bernard
 *
 * Use this class to render shpaes directly to OpenGL.  Allows you to bypass the Graphics class.
 */
public final class ShapeRenderer {
    /**
     * Draw the outline of the given shape.  Only the vertices are set.  
     * The colour has to be set independently of this method.
     * 
     * @param shape The shape to draw.
     */
    public static final void draw(Shape shape) {
        Texture t = Texture.getLastBind();
        Texture.bindNone();
        
        float points[] = shape.getPoints();
        
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for(int i=0;i<points.length;i+=2) {
            GL11.glVertex2f(points[i], points[i + 1]);
        }
        GL11.glVertex2f(points[0], points[1]);
        GL11.glEnd();
        
        if (t == null) {
        	Texture.bindNone();
        } else {
        	t.bind();
        }
    }
    
    /**
     * Draw the the given shape filled in.  Only the vertices are set.  
     * The colour has to be set independently of this method.
     * 
     * @param shape The shape to fill.
     */
    public static final void fill(Shape shape) {
        Texture t = Texture.getLastBind();
        Texture.bindNone();
        
        float points[] = shape.getPoints();
        
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        float center[] = shape.getCenter();
        GL11.glVertex2f(center[0], center[1]);

        for(int i=0;i<points.length;i+=2) {
            GL11.glVertex2f(points[i], points[i + 1]);
        }
        GL11.glVertex2f(points[0], points[1]);
        GL11.glEnd();
        
        if (t == null) {
        	Texture.bindNone();
        } else {
        	t.bind();
        }
    }

    /**
     * Draw the the given shape filled in with a texture.  Only the vertices are set.  
     * The colour has to be set independently of this method.
     * 
     * @param shape The shape to texture.
     * @param image The image to tile across the shape
     */
    public static final void texture(Shape shape, Image image) {
    	texture(shape, image, 0.01f, 0.01f);
    }
    
    /**
     * Draw the the given shape filled in with a texture.  Only the vertices are set.  
     * The colour has to be set independently of this method.
     * 
     * @param shape The shape to texture.
     * @param image The image to tile across the shape
     * @param scaleX The scale to apply on the x axis for texturing
     * @param scaleY The scale to apply on the y axis for texturing
     */
    public static final void texture(Shape shape, Image image, float scaleX, float scaleY) {
        float points[] = shape.getPoints();
        
        Texture t = Texture.getLastBind();
        image.getTexture().bind();
        
        float minX = shape.getX();
        float minY = shape.getY();
        float maxX = shape.getMaxX() - minX;
        float maxY = shape.getMaxY() - minY;
        
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        float center[] = shape.getCenter();
        GL11.glTexCoord2f(((center[0] - minX) / maxX) * scaleX, ((center[1] - minY) / maxY) * scaleY);
        GL11.glVertex2f(center[0], center[1]);

        for(int i=0;i<points.length;i+=2) {
            GL11.glTexCoord2f(((points[i] - minX) / maxX) * scaleX, ((points[i + 1] - minY) / maxY) * scaleY);
            GL11.glVertex2f(points[i], points[i + 1]);
        }
        GL11.glTexCoord2f(((points[0] - minX) / maxX) * scaleX, ((points[1] - minY) / maxY) * scaleY);
        GL11.glVertex2f(points[0], points[1]);
        GL11.glEnd();
        
        if (t == null) {
        	Texture.bindNone();
        } else {
        	t.bind();
        }
    }

    /**
     * Draw the outline of the given shape.  Only the vertices are set.  
     * The colour has to be set independently of this method.
     * 
     * @param shape The shape to draw.
     * @param gradient The gradient to apply
     */
    public static final void draw(Shape shape, GradientFill gradient) {
        Texture t = Texture.getLastBind();
        Texture.bindNone();
        
        float points[] = shape.getPoints();

        float center[] = shape.getCenter();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for(int i=0;i<points.length;i+=2) {
            gradient.colorAt(points[i]-center[0], points[i + 1]-center[1]).bind();
            GL11.glVertex2f(points[i], points[i + 1]);
        }
        gradient.colorAt(points[0]-center[0], points[1]-center[1]).bind();
        GL11.glVertex2f(points[0], points[1]);
        GL11.glEnd();
        
        if (t == null) {
        	Texture.bindNone();
        } else {
        	t.bind();
        }
    }

    /**
     * Draw the the given shape filled in.  Only the vertices are set.  
     * The colour has to be set independently of this method.
     * 
     * @param shape The shape to fill.
     * @param gradient The gradient to apply
     */
    public static final void fill(Shape shape, GradientFill gradient) {
        Texture t = Texture.getLastBind();
        Texture.bindNone();
        
        float points[] = shape.getPoints();
        
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        float center[] = shape.getCenter();
        gradient.colorAt(0,0).bind();
        GL11.glVertex2f(center[0], center[1]);

        for(int i=0;i<points.length;i+=2) {
            gradient.colorAt(points[i] - center[0], points[i + 1] - center[1]).bind();
            GL11.glVertex2f(points[i], points[i + 1]);
        }
        gradient.colorAt(points[0] - center[0], points[1] - center[1]).bind();
        GL11.glVertex2f(points[0], points[1]);
        GL11.glEnd();
        
        if (t == null) {
        	Texture.bindNone();
        } else {
        	t.bind();
        }
    }
}
