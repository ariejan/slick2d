package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Document;

/**
 * The description of a module which processes a single XML element from a SVG (inkscape) 
 * document.
 *
 * @author kevin
 */
public interface ElementProcessor {
	/**
	 * Process a document extracting all the elements that the processor is 
	 * interested in and producing appropriate diagram components for the
	 * element.
	 * 
	 * @param document The document to be processed
	 * @param diagram The diagram to be built
	 * @throws ParsingException Indicates an invalid content to an element
	 */
	public void process(Document document, Diagram diagram) throws ParsingException;
}
