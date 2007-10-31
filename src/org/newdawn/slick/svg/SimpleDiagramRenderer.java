package org.newdawn.slick.svg;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.opengl.renderer.Renderer;

/**
 * A very primtive implementation for rendering a diagram. This simply
 * sticks the shapes on the screen in the right fill and stroke colours
 *
 * @author kevin
 */
public class SimpleDiagramRenderer {
	/** The renderer to use for all GL operations */
	protected static SGL GL = Renderer.get();
	
	/** The diagram to be rendered */
	public Diagram diagram;
	/** The display list representing the diagram */
	public int list = -1;
	
	/**
	 * Create a new simple renderer
	 * 
	 * @param diagram The diagram to be rendered
	 */
	public SimpleDiagramRenderer(Diagram diagram) {
		this.diagram = diagram;
	}
	
	/**
	 * Render the diagram to the given graphics context
	 * 
	 * @param g The graphics context to which we should render the diagram
	 */
	public void render(Graphics g) {
		// last list generation
		if (list == -1) {
			list = GL.glGenLists(1);
			GL.glNewList(list, SGL.GL_COMPILE);
				render(g, diagram);
			GL.glEndList();
		}
		
		GL.glCallList(list);
		Texture.bindNone();
	}
	
	/**
	 * Utility method to render a digram in immediate mode
	 * 
	 * @param g The graphics context to render to
	 * @param diagram The diagram to render
	 */
	public static void render(Graphics g, Diagram diagram) {
		for (int i=0;i<diagram.getFigureCount();i++) {
			Figure figure = diagram.getFigure(i);

			if (figure.getData().isColor(NonGeometricData.FILL)) {
				g.setColor(figure.getData().getAsColor(NonGeometricData.FILL));
				g.fill(diagram.getFigure(i).getShape());
				g.setAntiAlias(true);
				g.draw(diagram.getFigure(i).getShape());
				g.setAntiAlias(false);
			}
			if (figure.getData().isColor(NonGeometricData.STROKE)) {
				g.setColor(figure.getData().getAsColor(NonGeometricData.STROKE));
				g.setLineWidth(figure.getData().getAsFloat(NonGeometricData.STROKE_WIDTH));
				g.setAntiAlias(true);
				g.draw(diagram.getFigure(i).getShape());
				g.setAntiAlias(false);
				g.resetLineWidth();
			}
	
			// DEBUG VERSION
//			g.setColor(Color.black);
//			g.draw(diagram.getFigure(i).getShape());
//			g.setColor(Color.red);
//			g.fill(diagram.getFigure(i).getShape());
		}
	}
}
