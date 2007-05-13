package org.newdawn.slick.tools.hiero.effects;

import java.util.Properties;

/**
 * Description of an effect that can be stored to properties
 *
 * @author kevin
 */
public interface StorableEffect extends Effect {
	/** 
	 * Store the effect to the properties
	 * 
	 * @param prefix The prefix that should be used on all properties
	 * @param props The properties to stored into
	 */
	public void store(String prefix, Properties props);

	/** 
	 * Load the effect from the properties
	 * 
	 * @param prefix The prefix that should be used on all properties
	 * @param props The properties that should be loaded from
	 */
	public void load(String prefix, Properties props);
}
