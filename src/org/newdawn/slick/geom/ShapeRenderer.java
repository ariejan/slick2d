package org.newdawn.slick.geom;

import org.lwjgl.opengl.GL11;

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
        float points[] = shape.getPoints();
        
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for(int i=0;i<points.length;i+=2) {
            GL11.glVertex2f(points[i], points[i + 1]);
        }
        GL11.glVertex2f(points[0], points[1]);
        GL11.glEnd();
    }
    /**
     * Draw the the given shape filled in.  Only the vertices are set.  
     * The colour has to be set independently of this method.
     * 
     * @param shape The shape to fill.
     */
    public static final void fill(Shape shape) {
        float points[] = shape.getPoints();
        
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        float center[] = shape.getCenter();
        GL11.glVertex2f(center[0], center[1]);

        for(int i=0;i<points.length;i+=2) {
            GL11.glVertex2f(points[i], points[i + 1]);
        }
        GL11.glVertex2f(points[0], points[1]);
        GL11.glEnd();
    }

}
