package org.newdawn.slick.svg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.svg.inkscape.ElementProcessor;
import org.newdawn.slick.svg.inkscape.EllipseProcessor;
import org.newdawn.slick.svg.inkscape.LineProcessor;
import org.newdawn.slick.svg.inkscape.PolygonProcessor;
import org.newdawn.slick.svg.inkscape.RectProcessor;
import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A loader specifically for the SVG that is produced from Inkscape
 *
 * @author kevin
 */
public class InkscapeLoader {
	/** The list of XML element processors */
	private static ArrayList processors = new ArrayList();
	
	static {
		processors.add(new RectProcessor());
		processors.add(new EllipseProcessor());
		processors.add(new PolygonProcessor());
		processors.add(new LineProcessor());
	}
	
	/**
	 * Load a SVG document into a diagram
	 * 
	 * @param ref The reference in the classpath to load the diagram from
	 * @return The diagram loaded
	 * @throws SlickException Indicates a failure to process the document
	 */
	public static Diagram load(String ref) throws SlickException {
		return load(ResourceLoader.getResourceAsStream(ref));
	}

	/**
	 * Load a SVG document into a diagram
	 * 
	 * @param in The input stream from which to read the SVG
	 * @return The diagram loaded
	 * @throws SlickException Indicates a failure to process the document
	 */
	public static Diagram load(InputStream in) throws SlickException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(new ByteArrayInputStream(new byte[0]));
				}
				
			});
		
			Document doc = builder.parse(in);
			Diagram diagram = new Diagram();
			
			for (int i=0;i<processors.size();i++) {
				((ElementProcessor) processors.get(i)).process(doc, diagram);
			}
			
			return diagram;
		} catch (Exception e) {
			throw new SlickException("Failed to load inkscape document", e);
		}
	}
}
