package org.newdawn.slick.opengl;

import java.io.IOException;
import java.io.InputStream;

import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;

/**
 * A texture proxy that can be used to load a texture at a later date while still
 * allowing elements to reference it
 *
 * @author kevin
 */
public class DeferredTexture extends Texture implements DeferredResource {
	/** The stream to read the texture from */
	private InputStream in;
	/** The name of the resource to load */
	private String resourceName;
	/** True if the image should be flipped */
	private boolean flipped;
	/** The filter to apply to the texture */
	private int filter;
	/** The texture we're proxying for */
	private Texture target;
	/** The color to be transparent */
	private int[] trans;
	
	/**
	 * Create a new deferred texture
	 * 
	 * @param in The input stream from which to read the texture
	 * @param resourceName The name to give the resource
 	 * @param flipped True if the image should be flipped
	 * @param filter The filter to apply
	 * @param trans The colour to defined as transparent
	 */
	public DeferredTexture(InputStream in, String resourceName, boolean flipped, int filter, int[] trans) {
		this.in = in;
		this.resourceName = resourceName;
		this.flipped = flipped;
		this.filter = filter;
		this.trans = trans;
		
		LoadingList.get().add(this);
	}

	/**
	 * @see org.newdawn.slick.loading.DeferredResource#load()
	 */
	public void load() throws IOException {
		boolean before = TextureLoader.get().isDeferredLoading();
		TextureLoader.get().setDeferredLoading(false);
		target = TextureLoader.get().getTexture(in, resourceName, flipped, filter, trans);
		TextureLoader.get().setDeferredLoading(before);
	}
	
	/**
	 * Check if the target has been obtained already
	 */
	private void checkTarget() {
		if (target == null) {
			try {
				load();
				LoadingList.get().remove(this);
				return;
			} catch (IOException e) {
				throw new RuntimeException("Attempt to use deferred texture before loading and resource not found: "+resourceName);
			}
		}
	}
	
	/**
	 * @see org.newdawn.slick.opengl.Texture#bind()
	 */
	public void bind() {
		checkTarget();

		target.bind();
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#getHeight()
	 */
	public float getHeight() {
		checkTarget();

		return target.getHeight();
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#getImageHeight()
	 */
	public int getImageHeight() {
		checkTarget();
		return target.getImageHeight();
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#getImageWidth()
	 */
	public int getImageWidth() {
		checkTarget();
		return target.getImageWidth();
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#getTextureHeight()
	 */
	public int getTextureHeight() {
		checkTarget();
		return target.getTextureHeight();
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#getTextureID()
	 */
	public int getTextureID() {
		checkTarget();
		return target.getTextureID();
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#getTextureRef()
	 */
	public String getTextureRef() {
		checkTarget();
		return target.getTextureRef();
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#getTextureWidth()
	 */
	public int getTextureWidth() {
		checkTarget();
		return target.getTextureWidth();
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#getWidth()
	 */
	public float getWidth() {
		checkTarget();
		return target.getWidth();
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#release()
	 */
	public void release() {
		checkTarget();
		target.release();
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#setAlpha(boolean)
	 */
	public void setAlpha(boolean alpha) {
		checkTarget();
		target.setAlpha(alpha);
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#setHeight(int)
	 */
	public void setHeight(int height) {
		checkTarget();
		target.setHeight(height);
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#setTextureHeight(int)
	 */
	public void setTextureHeight(int texHeight) {
		checkTarget();
		target.setTextureHeight(texHeight);
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#setTextureID(int)
	 */
	public void setTextureID(int textureID) {
		checkTarget();
		target.setTextureID(textureID);
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#setTextureWidth(int)
	 */
	public void setTextureWidth(int texWidth) {
		checkTarget();
		target.setTextureWidth(texWidth);
	}

	/**
	 * @see org.newdawn.slick.opengl.Texture#setWidth(int)
	 */
	public void setWidth(int width) {
		checkTarget();
		target.setWidth(width);
	}

	/**
	 * @see org.newdawn.slick.loading.DeferredResource#getDescription()
	 */
	public String getDescription() {
		return resourceName;
	}
}
