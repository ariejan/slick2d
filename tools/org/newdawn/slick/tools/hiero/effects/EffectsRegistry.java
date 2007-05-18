package org.newdawn.slick.tools.hiero.effects;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.newdawn.slick.tools.hiero.HieroConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A central list of available effects
 *
 * @author kevin
 */
public class EffectsRegistry {
	/** The list of actual effects */
	private static ArrayList effects = new ArrayList();
	
	static {
		registerEffect(new OutlineEffect());
		registerEffect(new ColorEffect());
		registerEffect(new GradientEffect());
		registerEffect(new ShadowEffect());
		registerEffect(new BlurShadowEffect());
		registerEffect(new OffsetGradientEffect());
		
		File[] files = HieroConfig.listFiles(".jar");
		System.out.println("Found: "+files.length+" custom effects");
		for (int i=0;i<files.length;i++) {
			try {
				System.out.println("Loading Effect JAR: "+files[i].getName());
				URLClassLoader loader = new URLClassLoader(new URL[] {files[i].toURL()}, 
														   EffectsRegistry.class.getClassLoader());
				InputStream in = loader.getResourceAsStream("META-INF/effects.xml");
				if (in == null) {
					in = loader.getResourceAsStream("effects.xml");
				}
				
				if (in != null) {
					DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document doc = builder.parse(in);
					NodeList list = doc.getElementsByTagName("effect");
					for (int k=0;k<list.getLength();k++) {
						Element element = (Element) list.item(k);
						
						String clazz = element.getAttribute("class");
						System.out.println("Loading Effect: "+clazz);
						Effect effect = (Effect) loader.loadClass(clazz).newInstance();
						registerEffect(effect);
					}
				} else {
					System.err.println("No effects xml found");
				}
			} catch (Throwable e) {
				System.err.println("Failure loading effect: "+files[i].getName());
				System.err.println("=== start ====");
				e.printStackTrace();
				System.err.println("=== end ====");
			}
		}
	}
	
	/**
	 * Get the registered effects
	 * 
	 * @return The list of registered effects
	 */
	public static Effect[] getEffects() {
		return (Effect[]) effects.toArray(new Effect[0]);
	}
	
	/**
	 * Get a named effect
	 * 
	 * @param name The name of the effect to retrieve
	 * @return The effect or null if no effect by that name is available
	 */
	public static Effect getEffectByName(String name) {
		for (int i=0;i<effects.size();i++) {
			if (((Effect) effects.get(i)).getEffectName().equals(name)) {
				return (Effect) effects.get(i);
			}
		}
		
		return null;
	}
	/**
	 * Register a new effect
	 * 
	 * @param effect The effect to be registered
	 */
	public static void registerEffect(Effect effect) {
		effects.add(effect);
	}
}
