package org.newdawn.slick.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple wrapper around resource loading should anyone decide to change
 * their minds how this is meant to work in the future.
 * 
 * @author Kevin Glass
 */
public class ResourceLoader {

	/**
	 * Get a resource
	 * 
	 * @param ref The reference to the resource to retrieve
	 * @return A stream from which the resource can be read
	 */
	public static InputStream getResourceAsStream(String ref) {
		InputStream in = ResourceLoader.class.getClassLoader().getResourceAsStream(ref);
		
		if (in == null) {
			File file = new File(ref);
			try {
				if (System.getProperty("jnlp.slick.webstart", "false").equals("false")) {
					in = new FileInputStream(file);
					return new BufferedInputStream(in);
				} else {
					Log.error("Resource not found: "+ref);
					throw new RuntimeException("Resource not found: "+ref);
				}
			} catch (IOException e) {
				Log.error("Resource not found: "+ref);
				throw new RuntimeException("Resource not found: "+ref);
			} 
		}
		
		return new BufferedInputStream(in);
	}
}
