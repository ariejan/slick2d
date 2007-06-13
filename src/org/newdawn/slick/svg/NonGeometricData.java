package org.newdawn.slick.svg;

import java.util.Properties;

import org.newdawn.slick.Color;

/**
 * A set of data about a shape that doesn't fit into it's geometric 
 * configuration.
 *
 * @author kevin
 */
public class NonGeometricData {
	/** The fill type */
	public static final String FILL = "fill";
	/** The stroke type */
	public static final String STROKE = "stroke";
	/** The alpha value for filling */
	public static final String OPACITY = "opacity";
	/** The width of the line to draw */
	public static final String STROKE_WIDTH = "stroke-width";
	/** The mitre of the line to draw */
	public static final String STROKE_MITERLIMIT = "stroke-miterlimit";
	/** The dash definition of the line to draw */
	public static final String STROKE_DASHARRAY = "stroke-dasharray";
	/** The offset into the dash definition of the line to draw */
	public static final String STROKE_DASHOFFSET = "stroke-dashoffset";
	/** The alpha value for drawing */
	public static final String STROKE_OPACITY = "stroke-opacity";
	
	/** Value indicating that no settings has been specified */
	public static final String NONE = "none";
	
	/** The meta data stored for the figure */
	private String metaData = "";
	/** The attributes stored for the figure */
	private Properties props = new Properties();
	
	/**
	 * Create a set of non-geometric data for a figure
	 * 
	 * @param metaData The meta data (either label or id) for the figure
	 */
	public NonGeometricData(String metaData) {
		this.metaData = metaData;
	}
	
	/**
	 * Add a configured style attribute into the data set
	 * 
	 * @param attribute The attribute to add  
	 * @param value The value to assign
	 */
	public void addAttribute(String attribute, String value) {
		props.setProperty(attribute, value);
	}
	
	/**
	 * Check if a given attribute is in colour format
	 * 
	 * @param attribute The attribute to check
	 * @return True if the attirbute value is in colour format
	 */
	public boolean isColor(String attribute) {
		return getAttribute(attribute).startsWith("#");
	}
	
	/**
	 * Get the meta data assigned to the figure. Either the label or
	 * the id value.
	 * 
	 * @return The meta data assigned to the figure
	 */
	public String getMetaData() {
		return metaData;
	}
	
	/**
	 * Get the attribtue value for a given attribute
	 * 
	 * @param attribute The attribute whose value should be obtained
	 * @return The value for the given attribute
	 */
	public String getAttribute(String attribute) {
		return props.getProperty(attribute);
	}
	
	/**
	 * Get an attribute value converted to a color. isColor should first be checked
	 * 
	 * @param attribute The attribute whose value should be interpreted as a color
	 * @return The color based on the attribute
	 */
	public Color getAsColor(String attribute) {
		if (!isColor(attribute)) {
			throw new RuntimeException("Attribute "+attribute+" is not specified as a color");
		}
		
		int col = Integer.parseInt(getAttribute(attribute).substring(1), 16);
		
		return new Color(col);
	}
}
