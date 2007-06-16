package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Loader;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Element;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class GroupProcessor implements ElementProcessor {

	/**
	 * @see org.newdawn.slick.svg.inkscape.ElementProcessor#handles(org.w3c.dom.Element)
	 */
	public boolean handles(Element element) {
		if (element.getNodeName().equals("g")) {
			return true;
		}
		return false;
	}

	/**
	 * @see org.newdawn.slick.svg.inkscape.ElementProcessor#process(org.newdawn.slick.svg.Loader, org.w3c.dom.Element, org.newdawn.slick.svg.Diagram)
	 */
	public void process(Loader loader, Element element, Diagram diagram) throws ParsingException {
		loader.loadChildren(element);
	}

}
