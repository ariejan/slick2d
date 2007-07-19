package org.newdawn.slick.svg;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.Texture;

/**
 * A very primtive implementation for rendering a diagram. This simply
 * sticks the shapes on the screen in the right fill and stroke colours
 *
 * @author kevin
 */
public class SimpleDiagramRenderer {
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
			list = GL11.glGenLists(1);
			GL11.glNewList(list, GL11.GL_COMPILE);
			
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
					g.setAntiAlias(true);
					g.draw(diagram.getFigure(i).getShape());
					g.setAntiAlias(false);
				}
		
				// DEBUG VERSION
	//			g.setColor(Color.black);
	//			g.draw(diagram.getFigure(i).getShape());
	//			g.setColor(Color.red);
	//			g.fill(diagram.getFigure(i).getShape());
			}
			
			GL11.glEndList();
		}
		
		GL11.glCallList(list);
		Texture.bindNone();
	}
}
