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
		
		ref = ref.toLowerCase();
		
        if (ref.endsWith(".tga")) {
        	return new TGAImageData();
        } 
        if (ref.endsWith(".png")) {
        	CompositeImageData data = new CompositeImageData();
        	data.add(new PNGImageData());
        	data.add(new ImageIOImageData());
        	
        	return data;
        } 
        
        return new ImageIOImageData();
	}
}
