package org.newdawn.slick.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;

/**
 * This is a utility class that allows you to convert a BufferedImage into a
 * texture.
 * 
 * @author James Chambers (Jimmy)
 * @author Jeremy Adams (elias_naur)
 * @author Kevin Glass (kevglass)
 */

public class BufferedImageUtil {

	/**
	 * Load a texture
	 * 
	 * @param resourceName
	 *            The location of the resource to load
	 * @param resourceImage
	 *            The BufferedImage we are converting
	 * @return The loaded texture
	 * @throws IOException
	 *             Indicates a failure to access the resource
	 */
	public static Texture getTexture(String resourceName,
			BufferedImage resourceImage) throws IOException {
		Texture tex = getTexture(resourceName, resourceImage,
				GL11.GL_TEXTURE_2D, // target
				GL11.GL_RGBA, // dest pixel format
				GL11.GL_LINEAR, // min filter (unused)
				GL11.GL_LINEAR);

		return tex;
	}

	/**
	 * Load a texture into OpenGL from a BufferedImage
	 * 
	 * @param resourceName
	 *            The location of the resource to load
	 * @param resourceimage
	 *            The BufferedImage we are converting
	 * @param target
	 *            The GL target to load the texture against
	 * @param dstPixelFormat
	 *            The pixel format of the screen
	 * @param minFilter
	 *            The minimising filter
	 * @param magFilter
	 *            The magnification filter
	 * @return The loaded texture
	 * @throws IOException
	 *             Indicates a failure to access the resource
	 */
	public static Texture getTexture(String resourceName,
			BufferedImage resourceimage, int target, int dstPixelFormat,
			int minFilter, int magFilter) throws IOException {
		int srcPixelFormat = 0;

		// create the texture ID for this texture
		int textureID = InternalTextureLoader.createTextureID();
		TextureImpl texture = new TextureImpl(resourceName, target, textureID);

		// Enable texturing
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		// bind this texture
		GL11.glBindTexture(target, textureID);

		BufferedImage bufferedImage = resourceimage;
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());

		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL11.GL_RGBA;
		} else {
			srcPixelFormat = GL11.GL_RGB;
		}

		// convert that image into a byte buffer of texture data
		ByteBuffer textureBuffer = convertImageData(bufferedImage, texture);

		if (target == GL11.GL_TEXTURE_2D) {
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_S,
							GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_T,
							GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
		}

        GL11.glTexImage2D(target, 
                      0, 
                      dstPixelFormat, 
                      InternalTextureLoader.get2Fold(bufferedImage.getWidth()), 
                      InternalTextureLoader.get2Fold(bufferedImage.getHeight()), 
                      0, 
                      srcPixelFormat, 
                      GL11.GL_UNSIGNED_BYTE, 
                      textureBuffer); 

		return texture;
	}

	/**
	 * Convert the buffered image to a texture
	 * 
	 * @param bufferedImage
	 *            The image to convert to a texture
	 * @param texture
	 *            The texture to store the data into
	 * @return A ByteBuffer containing the data
	 */
	private static ByteBuffer convertImageData(BufferedImage bufferedImage,
			TextureImpl texture) {

		/** The colour model including alpha for the GL image */
		ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace
				.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 },
				true, false, ComponentColorModel.BITMASK, DataBuffer.TYPE_BYTE);

		/** The colour model for the GL image */
		ColorModel glColorModel = new ComponentColorModel(ColorSpace
				.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 0 },
				false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);

		ByteBuffer imageBuffer = null;
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth = 2;
		int texHeight = 2;

		// find the closest power of 2 for the width and height
		// of the produced texture
		while (texWidth < bufferedImage.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bufferedImage.getHeight()) {
			texHeight *= 2;
		}

		texture.setTextureHeight(texHeight);
		texture.setTextureWidth(texWidth);

		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 4, null);
			texImage = new BufferedImage(glAlphaColorModel, raster, false,
					new Hashtable());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 3, null);
			texImage = new BufferedImage(glColorModel, raster, false,
					new Hashtable());
		}

		// copy the source image into the produced image
		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);

		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
				.getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();

		return imageBuffer;
	}
}
