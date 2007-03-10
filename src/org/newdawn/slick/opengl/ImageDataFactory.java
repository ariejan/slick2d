package org.newdawn.slick.opengl;

/**
 * A static utility to create the appropriate image data for a particular reference.
 *
 * @author kevin
 */
public class ImageDataFactory {
	/**
	 * Create an image data that is appropriate for the reference supplied
	 * 
	 * @param ref The reference to the image to retrieve
	 * @return The image data that can be used to retrieve the data for that resource
	 */
	public static LoadableImageData getImageDataFor(String ref) {
		LoadableImageData imageData;
		
        if (ref.endsWith(".tga")) {
        	imageData = new TGAImageData();
        } else {
        	imageData = new ImageIOImageData();
        }
        
        return imageData;
	}
}
