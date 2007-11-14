package org.newdawn.slick.svg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.inkscape.DefsProcessor;
import org.newdawn.slick.svg.inkscape.ElementProcessor;
import org.newdawn.slick.svg.inkscape.EllipseProcessor;
import org.newdawn.slick.svg.inkscape.GroupProcessor;
import org.newdawn.slick.svg.inkscape.LineProcessor;
import org.newdawn.slick.svg.inkscape.PathProcessor;
import org.newdawn.slick.svg.inkscape.PolygonProcessor;
import org.newdawn.slick.svg.inkscape.RectProcessor;
import org.newdawn.slick.svg.inkscape.UseProcessor;
import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A loader specifically for the SVG that is produced from Inkscape
 *
 * @author kevin
 */
public class InkscapeLoader implements Loader {
	/** The number of times to over trigulate to get enough tesselation for smooth shading */
	public static int RADIAL_TRIANGULATION_LEVEL = 1;
	
	/** The list of XML element processors */
	private static ArrayList processors = new ArrayList();
	/** The diagram loaded */
	private Diagram diagram;
	
	static {
		processors.add(new RectProcessor());
		processors.add(new EllipseProcessor());
		processors.add(new PolygonProcessor());
		processors.add(new PathProcessor());
		processors.add(new LineProcessor());
		processors.add(new GroupProcessor());
		processors.add(new DefsProcessor());
		processors.add(new UseProcessor());
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
		return new InkscapeLoader().loadDiagram(in);
	}

	/**
	 * Private, you should be using the static method
	 */
	private InkscapeLoader() {	
	}

	/**
	 * Load a SVG document into a diagram
	 * 
	 * @param in The input stream from which to read the SVG
	 * @return The diagram loaded
	 * @throws SlickException Indicates a failure to process the document
	 */
	private Diagram loadDiagram(InputStream in) throws SlickException {
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

			diagram = new Diagram();
			Document doc = builder.parse(in);
			Element root = doc.getDocumentElement();
			
			loadChildren(root, new Transform());
			
			return diagram;
		} catch (Exception e) {
			throw new SlickException("Failed to load inkscape document", e);
		}
	}
	
	/**
	 * @see org.newdawn.slick.svg.Loader#loadChildren(org.w3c.dom.Element, org.newdawn.slick.geom.Transform)
	 */
	public void loadChildren(Element element, Transform t) throws ParsingException   {
		NodeList list = element.getChildNodes();
		for (int i=0;i<list.getLength();i++) {
			if (list.item(i) instanceof Element) {
				loadElement((Element) list.item(i), t);
			}
		}
	}
	
	/**
	 * Load a single element into the diagram
	 * 
	 * @param element The element ot be loaded
	 * @param t The transform to apply to the loaded element from the parent
	 * @throws ParsingException Indicates a failure to parse the element
	 */
	private void loadElement(Element element, Transform t) throws ParsingException {
		for (int i=0;i<processors.size();i++) {
			ElementProcessor processor = (ElementProcessor) processors.get(i);
			
			if (processor.handles(element)) {
				processor.process(this, element, diagram, t);
			}
		}
	}
}
