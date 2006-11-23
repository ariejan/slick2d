package org.newdawn.slick.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * A description of any class providing ImageData in a form suitable for OpenGL texture
 * creation.
 * 
 * @author kevin
 */
public interface ImageData {

	/**
	 * Get the last bit depth read from a TGA
	 * 
	 * @return The last bit depth read
	 */
	public int getDepth();

	/**
	 * Get the last width read from a TGA
	 * 
	 * @return Get the last width in pixels fread from a TGA
	 */
	public int getWidth();

	/**
	 * Get the last height read from a TGA
	 * 
	 * @return Get the last height in pixels fread from a TGA
	 */
	public int getHeight();

	/**
	 * Get the last required texture width for a loaded image
	 * 
	 * @return Get the ast required texture width for a loaded image
	 */
	public int getTexWidth();

	/**
	 * Get the ast required texture height for a loaded image
	 * 
	 * @return Get the ast required texture height for a loaded image
	 */
	public int getTexHeight();

	/**
	 * Load a TGA image from the specified stream
	 * 
	 * @param fis The stream from which we'll load the TGA
	 * @throws IOException Indicates a failure to read the TGA
	 * @return The byte buffer containing texture data
	 */
	public ByteBuffer loadImage(InputStream fis) throws IOException;

	/**
	 * Load a TGA image from the specified stream
	 * 
	 * @param fis The stream from which we'll load the TGA
	 * @param flipped True if we loading in flipped mode (used for cursors)
	 * @return The byte buffer containing texture data
	 * @throws IOException Indicates a failure to read the TGA
	 */
	public ByteBuffer loadImage(InputStream fis, boolean flipped)
			throws IOException;
	
	/**
	 * Load a TGA image from the specified stream
	 * 
	 * @param fis The stream from which we'll load the TGA
	 * @param flipped True if we loading in flipped mode (used for cursors)
	 * @param forceAlpha Force the output to have an alpha channel
	 * @return The byte buffer containing texture data
	 * @throws IOException Indicates a failure to read the TGA
	 */
	public ByteBuffer loadImage(InputStream fis, boolean flipped, boolean forceAlpha)
			throws IOException;
	
	/**
	 * Get the store image
	 * 
	 * @return The stored image
	 */
	public ByteBuffer getImageBufferData();

}