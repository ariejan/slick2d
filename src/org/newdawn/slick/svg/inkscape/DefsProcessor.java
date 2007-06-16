package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.svg.Diagram;
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
	 * @see org.newdawn.slick.svg.inkscape.ElementProcessor#process(org.newdawn.slick.svg.Loader, org.w3c.dom.Element, org.newdawn.slick.svg.Diagram)
	 */
	public void process(Loader loader, Element element, Diagram diagram) throws ParsingException {
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
	}

}
