package org.newdawn.slick.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 * A texture to be bound within JOGL. This object is responsible for 
 * keeping track of a given OpenGL texture and for calculating the
 * texturing mapping coordinates of the full image.
 * 
 * Since textures need to be powers of 2 the actual texture may be
 * considerably bigged that the source image and hence the texture
 * mapping coordinates need to be adjusted to matchup drawing the
 * sprite against the texture.
 *
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class Texture {
	/** The last texture that was bound to */
	static Texture lastBind;
	
	/**
	 * Retrieve the last texture bound through the texture interface
	 * 
	 * @return The last texture bound
	 */
	public static Texture getLastBind() {
		return lastBind;
	}
	
    /** The GL target type */
    private int target; 
    /** The GL texture ID */
    private int textureID;
    /** The height of the image */
    private int height;
    /** The width of the image */
    private int width;
    /** The width of the texture */
    private int texWidth;
    /** The height of the texture */
    private int texHeight;
    /** The ratio of the width of the image to the texture */
    private float widthRatio;
    /** The ratio of the height of the image to the texture */
    private float heightRatio;
    /** If this texture has alpha */
    private boolean alpha;
    /** The reference this texture was loaded from */
    private String ref;
    
    /**
     * For subclasses to utilise
     */
    protected Texture() {	
    }
    
    /**
     * Create a new texture
     *
     * @param ref The reference this texture was loaded from
     * @param target The GL target 
     * @param textureID The GL texture ID
     */
    public Texture(String ref, int target,int textureID) {
        this.target = target;
        this.ref = ref;
        this.textureID = textureID;
        lastBind = this;
    }
    
    /**
     * Check if the texture has alpha
     * 
     * @return True if the texture has alpha
     */
    public boolean hasAlpha() {
    	return alpha;
    }
    
    /**
     * Get the reference from which this texture was loaded
     * 
     * @return The reference from which this texture was loaded
     */
    public String getTextureRef() {
    	return ref;
    }
    
    /** 
     * If this texture has alpha
     * 
     * @param alpha True, If this texture has alpha
     */
    public void setAlpha(boolean alpha) {
    	this.alpha = alpha;
    }
    
    /**
     * Clear the binding of the texture
     */
    public static void bindNone() {
    	lastBind = null;
    	GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
    
    /**
     * Clear slick caching of the last bound texture so that an 
     * external texture binder can play with the context before returning 
     * control to slick.
     */
    public static void unbind() {
    	lastBind = null;
    }
    
    /**
     * Bind the  GL context to a texture
     */
    public void bind() {
    	if (lastBind != this) {
    		lastBind = this;
    		GL11.glEnable(GL11.GL_TEXTURE_2D);
    	    GL11.glBindTexture(target, textureID);
    	}
    }
    
    /**
     * Set the height of the image
     *
     * @param height The height of the image
     */
    public void setHeight(int height) {
        this.height = height;
        setHeight();
    }
    
    /**
     * Set the width of the image
     *
     * @param width The width of the image
     */
    public void setWidth(int width) {
        this.width = width;
        setWidth();
    }
    
    /**
     * Get the height of the original image
     *
     * @return The height of the original image
     */
    public int getImageHeight() {
        return height;
    }
    
    /** 
     * Get the width of the original image
     *
     * @return The width of the original image
     */
    public int getImageWidth() {
        return width;
    }
    
    /**
     * Get the height of the physical texture
     *
     * @return The height of physical texture
     */
    public float getHeight() {
        return heightRatio;
    }
    
    /**
     * Get the width of the physical texture
     *
     * @return The width of physical texture
     */
    public float getWidth() {
        return widthRatio;
    }
    
    /**
     * Get the height of the actual texture
     * 
     * @return The height of the actual texture
     */
    public int getTextureHeight() {
    	return texHeight;
    }

    /**
     * Get the width of the actual texture
     * 
     * @return The width of the actual texture
     */
    public int getTextureWidth() {
    	return texWidth;
    }
    
    /**
     * Set the height of this texture 
     *
     * @param texHeight The height of the texture
     */
    public void setTextureHeight(int texHeight) {
        this.texHeight = texHeight;
        setHeight();
    }
    
    /**
     * Set the width of this texture 
     *
     * @param texWidth The width of the texture
     */
    public void setTextureWidth(int texWidth) {
        this.texWidth = texWidth;
        setWidth();
    }
    
    /**
     * Set the height of the texture. This will update the
     * ratio also.
     */
    private void setHeight() {
        if (texHeight != 0) {
            heightRatio = ((float) height)/texHeight;
        }
    }
    
    /**
     * Set the width of the texture. This will update the
     * ratio also.
     */
    private void setWidth() {
        if (texWidth != 0) {
            widthRatio = ((float) width)/texWidth;
        }
    }
    
    /**
     * Destroy the texture reference
     */
    public void release() {
        IntBuffer texBuf = createIntBuffer(1); 
        texBuf.put(textureID);
        texBuf.flip();
        
    	GL11.glDeleteTextures(texBuf);
    }
    
    /**
     * Get the OpenGL texture ID for this texture
     * 
     * @return The OpenGL texture ID
     */
    public int getTextureID() {
    	return textureID;
    }
    
    /**
     * Set the OpenGL texture ID for this texture
     * 
     * @param textureID The OpenGL texture ID
     */
    public void setTextureID(int textureID) {
    	this.textureID = textureID;
    }
    
    /**
     * Creates an integer buffer to hold specified ints
     * - strictly a utility method
     *
     * @param size how many int to contain
     * @return created IntBuffer
     */
    protected IntBuffer createIntBuffer(int size) {
      ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
      temp.order(ByteOrder.nativeOrder());

      return temp.asIntBuffer();
    }    
    
    /**
     * Get the pixel data from the card for this texture
     * 
     * @return The texture data from the card for this texture
     */
    public byte[] getTextureData() {
    	ByteBuffer buffer = BufferUtils.createByteBuffer((hasAlpha() ? 4 : 3) * texWidth * texHeight);
    	GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, hasAlpha() ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, 
    					   buffer);
    	byte[] data = new byte[buffer.limit()];
    	buffer.get(data);
    	buffer.clear();
    	
    	return data;
    }
}
