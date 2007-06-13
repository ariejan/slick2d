package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Figure;
import org.newdawn.slick.svg.NonGeometricData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A processor for the <rect> element.
 *
 * @author kevin
 */
public class RectProcessor implements ElementProcessor {

	/**
	 * @see org.newdawn.slick.svg.inkscape.ElementProcessor#process(org.w3c.dom.Document, org.newdawn.slick.svg.Diagram)
	 */
	public void process(Document document, Diagram diagram) {
		NodeList list = document.getDocumentElement().getElementsByTagName("rect");
		for (int i=0;i<list.getLength();i++) {
			processNode((Element) list.item(i), diagram);
		}
	}

	/**
	 * Process a single <rect> element
	 * 
	 * @param element The element to be processed
	 * @param diagram The diagram to be updated
	 */
	private void processNode(Element element, Diagram diagram) {
		Transform transform = Util.getTransform(element);
		
		float width = Float.parseFloat(element.getAttribute("width"));
		float height = Float.parseFloat(element.getAttribute("height"));
		float x = Float.parseFloat(element.getAttribute("x"));
		float y = Float.parseFloat(element.getAttribute("y"));
		
		Rectangle rect = new Rectangle(x,y,width,height);
		Shape shape = rect.transform(transform);
		
		NonGeometricData data = Util.getNonGeometricData(element);
		
		diagram.addFigure(new Figure(shape, data));
	}
}
