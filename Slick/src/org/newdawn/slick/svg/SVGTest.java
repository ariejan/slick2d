package org.newdawn.slick.svg;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.zip.GZIPInputStream;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

import com.tinyline.svg.AnimationCallback;
import com.tinyline.svg.ImageLoader;
import com.tinyline.svg.SVG;
import com.tinyline.svg.SVGAttr;
import com.tinyline.svg.SVGDocument;
import com.tinyline.svg.SVGFontElem;
import com.tinyline.svg.SVGImageElem;
import com.tinyline.svg.SVGImageProducer;
import com.tinyline.svg.SVGParser;
import com.tinyline.svg.SVGRaster;
import com.tinyline.tiny2d.TinyBitmap;
import com.tinyline.tiny2d.TinyPixbuf;
import com.tinyline.tiny2d.TinyRect;
import com.tinyline.tiny2d.TinyString;

/**
 * A simple testing area for SVG through TinyLine
 * 
 * @author kevin
 */
public class SVGTest extends BasicGame implements SVGImageProducer, ImageLoader {
	/** The texture generated from the SVG */
	private int texture;
	/** The pixel buffer filled by TinyLine */
	private TinyPixbuf pixBuffer;
	/** The raster rendered to by TinyLine */
	private SVGRaster raster;
	/** The buffer holding the generated texture */
	private ByteBuffer textureBuffer;
	/** The SVG document being rendered */
	private SVGDocument doc;
	/** The position in the time line of animation */
	private int time = 0;
	/** The width of the texture generated */
	private int width = 256;
	/** The height of the texture generated */
	private int height = 256;
	
	/**
	 * SVG test
	 */
	public SVGTest() {
		super("SVGTest");
	}

	/**
	 * Creates an integer pixBuffer to hold specified ints - strictly a utility
	 * method
	 * 
	 * @param size
	 *            how many int to contain
	 * @return created IntBuffer
	 */
	protected IntBuffer createIntBuffer(int size) {
		ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
		temp.order(ByteOrder.nativeOrder());

		return temp.asIntBuffer();
	}

	/**
	 * Create a new texture ID
	 * 
	 * @return A new texture ID
	 */
	private int createTextureID() {
		IntBuffer tmp = createIntBuffer(1);
		GL11.glGenTextures(tmp);
		return tmp.get(0);
	}

	/**
	 * Create the texture that will be updated from SVG
	 * 
	 * @param width The width of the texture
	 * @param height The height of the texture
	 * @return The generated texture ID
	 */
	private int createTexture(int width, int height) {
		int target = GL11.GL_TEXTURE_2D;
		int textureID = createTextureID();
		GL11.glBindTexture(target, textureID);

		byte[] data = new byte[4 * width * height];
		textureBuffer = ByteBuffer.allocateDirect(data.length);
		textureBuffer.order(ByteOrder.nativeOrder());
		textureBuffer.put(data, 0, data.length);

		textureBuffer.flip();

		GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER,
						GL11.GL_LINEAR);
		GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER,
						GL11.GL_LINEAR);
		GL11.glTexImage2D(target, 0, GL11.GL_RGBA, width, height, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureBuffer);

		return textureID;
	}

	/**
	 * Loads an SVGT document from the given URL.
	 * 
	 * @param ref The SVGT document URL or path.
	 * @return An SVGT document.
	 */
	public SVGDocument loadSVG(String ref) {
		try {
			InputStream is = ResourceLoader.getResourceAsStream(ref);
			if (ref.endsWith("svgz")) {
				is = new GZIPInputStream(is);
			}

			final SVGDocument doc = raster.createSVGDocument();

			TinyPixbuf pixbuf = raster.getPixelBuffer();
			SVGAttr attrParser = new SVGAttr(pixbuf.width, pixbuf.height);
			SVGParser parser = new SVGParser(attrParser);
			parser.load(doc, is);

			doc.nActiveAnimations = 0;
			doc.animTargets.count = 0;
			doc.addAnimations(doc.root);
			doc.acb = new AnimationCallback() {
				public void postSMILEvent(int eventType, TinyString event) {
					System.out.println(eventType + ":" + event);
					doc.resolveEventBased(event);
				}
			};
			return doc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		texture = createTexture(width, height);
		TinyPixbuf buffer = new TinyPixbuf(width, height);
		raster = new SVGRaster(buffer);
		raster.setSVGImageProducer(this);
		SVGImageElem.setImageLoader(this);

		SVGDocument fontDoc = loadSVG("org/newdawn/slick/data/helvetica_svg");
		SVGFontElem font = SVGDocument.getFont(fontDoc,
				SVG.VAL_DEFAULT_FONTFAMILY);
		SVGDocument.defaultFont = font;

		doc = loadSVG("testdata/retro4.svgz");

		raster.setAntialiased(true);
		raster.setSVGDocument(doc);
		raster.setCamera();
		raster.update();
		raster.sendPixels();

	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer,
	 *      int)
	 */
	public void update(GameContainer container, int delta)
			throws SlickException {
		time += delta;
		TinyRect dirty = doc.animate(time / 4);
		raster.setDevClip(dirty);
		raster.update();
		raster.sendPixels();
		
		Thread.yield();
	}

	/**
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer,
	 *      org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(10, 50);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(10, 562);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(522, 562);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(522, 50);
		
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(530, 50);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(530, 306);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(786, 306);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(786, 50);
		GL11.glEnd();

		Texture.unbind();
	}

	/**
	 * Entry point to the test
	 * 
	 * @param argv The arguments to the test (normally none)
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new SVGTest(),
					800, 600, false);
			container.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see com.tinyline.svg.SVGImageProducer#hasConsumer()
	 */
	public boolean hasConsumer() {
		return true;
	}

	/**
	 * @see com.tinyline.svg.SVGImageProducer#imageComplete()
	 */
	public void imageComplete() {
		TinyRect dirty = raster.getDevClip();
		int width = dirty.xmax - dirty.xmin;
		int height = dirty.ymax - dirty.ymin;
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		int[] data = raster.getPixelBuffer().pixels32;
		textureBuffer.clear();
		for (int y=dirty.ymin;y<dirty.ymax;y++) {
			for (int x=dirty.xmin;x<dirty.xmax;x++) {
				int i = x + (y*this.width);
				int red = ((data[i] & 0xFF0000) >> 16) & 0xFF;
				int green = ((data[i] & 0xFF00) >> 8) & 0xFF;
				int blue = (data[i] & 0xFF);
				int alpha = ((data[i] & 0xFF000000) >> 24) & 0xFF;
				textureBuffer.put((byte) red);
				textureBuffer.put((byte) green);
				textureBuffer.put((byte) blue);
				textureBuffer.put((byte) alpha);
			}
		}
		
		textureBuffer.flip();
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, dirty.xmin, dirty.ymin, width, height,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureBuffer);

		Texture.unbind();
	}

	/**
	 * @see com.tinyline.svg.SVGImageProducer#sendPixels()
	 */
	public void sendPixels() {
	}

	/**
	 * @see com.tinyline.svg.ImageLoader#createTinyBitmap(com.tinyline.tiny2d.TinyString)
	 */
	public TinyBitmap createTinyBitmap(TinyString arg0) {
		return null;
	}

	/**
	 * @see com.tinyline.svg.ImageLoader#createTinyBitmap(byte[], int, int)
	 */
	public TinyBitmap createTinyBitmap(byte[] arg0, int arg1, int arg2) {
		return null;
	}
}
