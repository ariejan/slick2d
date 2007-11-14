package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Gradient;
import org.newdawn.slick.svg.Loader;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A processor for the defs node
 *
 * @author kevin
 */
public class DefsProcessor implements ElementProcessor {

	/**
	 * @see org.newdawn.slick.svg.inkscape.ElementProcessor#handles(org.w3c.dom.Element)
	 */
	public boolean handles(Element element) {
		if (element.getNodeName().equals("defs")) {
			return true;
		}
		
		return false;
	}

	/**
	 * @see org.newdawn.slick.svg.inkscape.ElementProcessor#process(org.newdawn.slick.svg.Loader, org.w3c.dom.Element, org.newdawn.slick.svg.Diagram, org.newdawn.slick.geom.Transform)
	 */
	public void process(Loader loader, Element element, Diagram diagram, Transform transform) throws ParsingException {
		NodeList patterns = element.getElementsByTagName("pattern");
		
		for (int i=0;i<patterns.getLength();i++) {
			Element pattern = (Element) patterns.item(i);
			NodeList list = pattern.getElementsByTagName("image");
			if (list.getLength() == 0) {
				throw new ParsingException(pattern, "No image specified for pattern");
			}
			Element image = (Element) list.item(0);
			
			String patternName = pattern.getAttribute("id");
			String ref = image.getAttributeNS(Util.XLINK, "href");
			diagram.addPatternDef(patternName, ref);
		}
		
		NodeList linear = element.getElementsByTagName("linearGradient");
		for (int i=0;i<linear.getLength();i++) {
			Element lin = (Element) linear.item(i);
			String name = lin.getAttribute("id");
			Gradient gradient = new Gradient(name, false);

			gradient.setTransform(Util.getTransform(lin, "gradientTransform"));
			
			if (lin.getAttribute("x1").length() > 0) {
				gradient.setX1(Float.parseFloat(lin.getAttribute("x1")));
			}
			if (lin.getAttribute("x2").length() > 0) {
				gradient.setX2(Float.parseFloat(lin.getAttribute("x2")));
			}
			if (lin.getAttribute("y1").length() > 0) {
				gradient.setY1(Float.parseFloat(lin.getAttribute("y1")));
			}
			if (lin.getAttribute("y2").length() > 0) {
				gradient.setY2(Float.parseFloat(lin.getAttribute("y2")));
			}
			
			String ref = lin.getAttributeNS("http://www.w3.org/1999/xlink", "href");
			if (ref.length() > 0) {
				Gradient grad = diagram.getGradient(ref.substring(1));
				if (grad == null) {
					throw new ParsingException(lin, "Can't find referenced gradient: "+ref);
				}
				
				gradient.reference(grad);
			} else {
				NodeList steps = lin.getElementsByTagName("stop");
				for (int j=0;j<steps.getLength();j++) {
					Element s = (Element) steps.item(j);
					float offset = Float.parseFloat(s.getAttribute("offset"));
		
					String colInt = Util.extractStyle(s.getAttribute("style"),"stop-color");
					String opaInt = Util.extractStyle(s.getAttribute("style"),"stop-opacity");
					
					int col = Integer.parseInt(colInt.substring(1), 16);
					Color stopColor = new Color(col);
					stopColor.a = Float.parseFloat(opaInt);
					
					gradient.addStep(offset, stopColor);
				}
			}
			
			gradient.getImage();
			diagram.addGradient(name, gradient);
		}
		
		NodeList radial = element.getElementsByTagName("radialGradient");
		for (int i=0;i<radial.getLength();i++) {
			Element rad = (Element) radial.item(i);
			String name = rad.getAttribute("id");
			Gradient gradient = new Gradient(name, true);
			
			gradient.setTransform(Util.getTransform(rad, "gradientTransform"));
			
			if (rad.getAttribute("cx").length() > 0) {
				gradient.setX1(Float.parseFloat(rad.getAttribute("cx")));
			}
			if (rad.getAttribute("cy").length() > 0) {
				gradient.setY1(Float.parseFloat(rad.getAttribute("cy")));
			}
			if (rad.getAttribute("fx").length() > 0) {
				gradient.setX2(Float.parseFloat(rad.getAttribute("fx")));
			}
			if (rad.getAttribute("fy").length() > 0) {
				gradient.setY2(Float.parseFloat(rad.getAttribute("fy")));
			}
			if (rad.getAttribute("r").length() > 0) {
				gradient.setR(Float.parseFloat(rad.getAttribute("r")));
			}
			
			String ref = rad.getAttributeNS("http://www.w3.org/1999/xlink", "href");
			if (ref.length() > 0) {
				Gradient grad = diagram.getGradient(ref.substring(1));
				if (grad == null) {
					throw new ParsingException(rad, "Can't find referenced gradient: "+ref);
				}
				
				gradient.reference(grad);
			} else {
				NodeList steps = rad.getElementsByTagName("stop");
				for (int j=0;j<steps.getLength();j++) {
					Element s = (Element) steps.item(j);
					float offset = Float.parseFloat(s.getAttribute("offset"));
		
					String colInt = Util.extractStyle(s.getAttribute("style"),"stop-color");
					String opaInt = Util.extractStyle(s.getAttribute("style"),"stop-opacity");
					
					int col = Integer.parseInt(colInt.substring(1), 16);
					Color stopColor = new Color(col);
					stopColor.a = Float.parseFloat(opaInt);
					
					gradient.addStep(offset, stopColor);
				}
			}
			
			gradient.getImage();
			diagram.addGradient(name, gradient);
		}
	}

}
