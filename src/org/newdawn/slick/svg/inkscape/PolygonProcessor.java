package org.newdawn.slick.svg.inkscape;

import java.util.StringTokenizer;

import org.newdawn.slick.geom.Polygon;
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
 * A processor for the <polygon> and <path> elements marked as not an arc.
 *
 * @author kevin
 */
public class PolygonProcessor implements ElementProcessor {

	/**
	 * @see org.newdawn.slick.svg.inkscape.ElementProcessor#process(org.w3c.dom.Document, org.newdawn.slick.svg.Diagram)
	 */
	public void process(Document document, Diagram diagram) throws ParsingException {
		NodeList list = document.getDocumentElement().getElementsByTagName("polygon");
		for (int i=0;i<list.getLength();i++) {
			Element element = (Element) list.item(i);
			processNode(element, diagram);
		}
		
		list = document.getDocumentElement().getElementsByTagName("path");
		for (int i=0;i<list.getLength();i++) {
			Element element = (Element) list.item(i);
			if (!element.getAttributeNS(Util.SODIPODI, "type").equals("arc")) {
				processNode(element, diagram);
			}
		}
	}

	/**
	 * Process the points in a polygon definition
	 * 
	 * @param poly The polygon being built
	 * @param element The XML element being read
	 * @param tokens The tokens representing the path
	 * @return The number of points found
	 * @throws ParsingException Indicates an invalid token in the path
	 */
	private static int processPoly(Polygon poly, Element element, StringTokenizer tokens) throws ParsingException {
		int count = 0;
		
		while (tokens.hasMoreTokens()) {
			String nextToken = tokens.nextToken();
			if (nextToken.equals("L")) {
				continue;
			}
			if (nextToken.equals("z")) {
				break;
			}
			if (nextToken.equals("M")) {
				continue;
			}
			
			String tokenX = nextToken;
			String tokenY = tokens.nextToken();
			
			try {
				float x = Float.parseFloat(tokenX);
				float y = Float.parseFloat(tokenY);
				
				poly.addPoint(x,y);
				count++;
			} catch (NumberFormatException e) {
				throw new ParsingException(element.getAttribute("id"), "Invalid token in points list", e);
			}
		}
		
		return count;
	}
	
	/**
	 * Process a single polygon element
	 * 
	 * @param element The element to be processed
	 * @param diagram The diagram being built
	 * @throws ParsingException Indicates a failure to process the given node as a polygon
	 */
	private void processNode(Element element, Diagram diagram) throws ParsingException {
		Transform transform = Util.getTransform(element);
		String points = element.getAttribute("points");
		if (element.getNodeName().equals("path")) {
			points = element.getAttribute("d");
		}
		
		StringTokenizer tokens = new StringTokenizer(points, ", ");
		Polygon poly = new Polygon();
		if (processPoly(poly, element, tokens) > 2) {
			Shape shape = poly.transform(transform);
	
			NonGeometricData data = Util.getNonGeometricData(element);
			
			diagram.addFigure(new Figure(shape, data));
		}
	}

}
