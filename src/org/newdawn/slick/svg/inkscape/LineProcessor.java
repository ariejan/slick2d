package org.newdawn.slick.svg.inkscape;

import java.util.StringTokenizer;

import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Figure;
import org.newdawn.slick.svg.NonGeometricData;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A processor for the <line> element
 *
 * @author kevin
 */
public class LineProcessor implements ElementProcessor {

	/**
	 * @see org.newdawn.slick.svg.inkscape.ElementProcessor#process(org.w3c.dom.Document, org.newdawn.slick.svg.Diagram)
	 */
	public void process(Document document, Diagram diagram) throws ParsingException {
		NodeList list = document.getDocumentElement().getElementsByTagName("line");
		for (int i=0;i<list.getLength();i++) {
			processNode((Element) list.item(i), diagram);
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
			if (nextToken.equals("C")) {
				return 0;
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
	 * Process a single line element
	 * 
	 * @param element The element to be processed
	 * @param diagram The diagram to be built
	 * @throws ParsingException Indicates an invalid token in the path
	 */
	private void processNode(Element element, Diagram diagram) throws ParsingException {
		Transform transform = Util.getTransform(element);

		float x1;
		float y1;
		float x2;
		float y2;
		
		if (element.getNodeName().equals("line")) {
			x1 = Float.parseFloat(element.getAttribute("x1"));
			x2 = Float.parseFloat(element.getAttribute("x2"));
			y1 = Float.parseFloat(element.getAttribute("y1"));
			y2 = Float.parseFloat(element.getAttribute("y2"));
		} else {
			String points = element.getAttribute("d");
			StringTokenizer tokens = new StringTokenizer(points, ", ");
			Polygon poly = new Polygon();
			if (processPoly(poly, element, tokens) == 2) {
				x1 = poly.getPoint(0)[0];
				y1 = poly.getPoint(0)[1];
				x2 = poly.getPoint(1)[0];
				y2 = poly.getPoint(1)[1];
			} else {
				return;
			}
		}
		
		float[] in = new float[] {x1,y1,x2,y2};
		float[] out = new float[4];
		
		transform.transform(in,0,out,0,2);
		Line line = new Line(out[0],out[1],out[2],out[3]);
		
		NonGeometricData data = Util.getNonGeometricData(element);
		
		diagram.addFigure(new Figure(line, data));
	}
}
