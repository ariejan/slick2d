package org.newdawn.slick.svg;

import org.w3c.dom.Element;

/**
 * Description of a simple XML loader
 *
 * @author kevin
 */
public interface Loader {
	/**
	 * Load the children of a given element
	 * 
	 * @param element The element whose children should be loaded
	 * @throws ParsingException Indicates a failure to read the XML
	 */
	public void loadChildren(Element element) throws ParsingException;
}
