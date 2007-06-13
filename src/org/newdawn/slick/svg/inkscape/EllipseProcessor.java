package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Figure;
import org.newdawn.slick.svg.NonGeometricData;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Processor for <ellipse> and <path> nodes marked as arcs
 *
 * @author kevin
 */
public class EllipseProcessor implements ElementProcessor {

	/**
	 * @see org.newdawn.slick.svg.inkscape.ElementProcessor#process(org.w3c.dom.Document, org.newdawn.slick.svg.Diagram)
	 */
	public void process(Document document, Diagram diagram) throws ParsingException {
		NodeList list = document.getDocumentElement().getElementsByTagName("ellipse");
		for (int i=0;i<list.getLength();i++) {
			Element element = (Element) list.item(i);
			processNode(element, diagram);
		}
		
		list = document.getDocumentElement().getElementsByTagName("path");
		for (int i=0;i<list.getLength();i++) {
			Element element = (Element) list.item(i);
			if (element.getAttributeNS(Util.SODIPODI, "type").equals("arc")) {
				processNode(element, diagram);
			}
		}
	}
	
	/**
	 * Process a single ellipse element
	 * 
	 * @param element The element to be processed
	 * @param diagram The diagram to be built
	 * @throws ParsingException Indicates an invalid element
	 */
	private void processNode(Element element, Diagram diagram) throws ParsingException {
		Transform transform = Util.getTransform(element);
		float x = Util.getFloatAttribute(element,"cx");
		float y = Util.getFloatAttribute(element,"cy");
		float rx = Util.getFloatAttribute(element,"rx");
		float ry = Util.getFloatAttribute(element,"ry");
		
		Ellipse ellipse = new Ellipse(x,y,rx,ry);
		Shape shape = ellipse.transform(transform);

		NonGeometricData data = Util.getNonGeometricData(element);
		
		diagram.addFigure(new Figure(shape, data));
	}

}
