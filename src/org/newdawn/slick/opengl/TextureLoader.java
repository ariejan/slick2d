package org.newdawn.slick.opengl;

import java.io.IOException;
import java.io.InputStream;

import org.lwjgl.opengl.GL11;

/**
 * A utility class to wrap the Slick internal texture loader and present a
 * rational interface.
 * 
 * @author kevin
 */
public class TextureLoader {
	/** An indicator for the TGA format - only a non-compressed 24/32 bit supported */
	public static final String TGA = "TGA";
	/** An indicator for the JPG format */
	public static final String JPG = "JPG";
	/** An indicator for the GIF format */
	public static final String GIF = "GIF";
	/** An indicator for the PNG format */
	public static final String PNG = "PNG";
	
	/**
	 * Load a texture with a given format from the supplied input stream
	 * 
	 * @param format The format of the texture to be loaded
	 * @param in The input stream from which the image data will be read
	 * @return The newly created texture
	 * @throws IOException Indicates a failure to read the image data
	 */
	public static Texture getTexture(String format, InputStream in) throws IOException {
		return getTexture(format, in, false, GL11.GL_LINEAR);
	}

	/**
	 * Load a texture with a given format from the supplied input stream
	 * 
	 * @param format The format of the texture to be loaded
	 * @param in The input stream from which the image data will be read
	 * @param flipped True if the image should be flipped vertically on loading
	 * @return The newly created texture
	 * @throws IOException Indicates a failure to read the image data
	 */
	public static Texture getTexture(String format, InputStream in, boolean flipped)  throws IOException {
		return getTexture(format, in, flipped, GL11.GL_LINEAR);
	}

	/**
	 * Load a texture with a given format from the supplied input stream
	 * 
	 * @param format The format of the texture to be loaded
	 * @param in The input stream from which the image data will be read
	 * @param filter The GL texture filter to use for scaling up and down
	 * @return The newly created texture
	 * @throws IOException Indicates a failure to read the image data
	 */
	public static Texture getTexture(String format, InputStream in, int filter) throws IOException {
		return getTexture(format, in, false, filter);
	}

	/**
	 * Load a texture with a given format from the supplied input stream
	 * 
	 * @param format The format of the texture to be loaded
	 * @param in The input stream from which the image data will be read
	 * @param flipped True if the image should be flipped vertically on loading
	 * @param filter The GL texture filter to use for scaling up and down
	 * @return The newly created texture
	 * @throws IOException Indicates a failure to read the image data
	 */
	public static Texture getTexture(String format, InputStream in, boolean flipped, int filter) throws IOException {
		return InternalTextureLoader.get().getTexture(in, in.toString()+"."+format, flipped, filter);
	}
}
