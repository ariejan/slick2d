package org.newdawn.slick.tools.hiero;

/**
 * The list of characters to be rendered to a sheet
 *
 * @author kevin
 */
public class CharSet {
	/** The first character */
	private int start;
	/** The last character */
	private int end;
	/** The sybolic name of this set */
	private String name;
	
	/**
	 * Create a new character set
	 * 
	 * @param start The first character
	 * @param end The last character
	 * @param name The symbolic name of this set
	 */
	public CharSet(int start, int end, String name) {
		this.start = start;
		this.end = end;
		this.name = name;
	}
	
	/**
	 * Get the first character in the set
	 * 
	 * @return The first character in the set
	 */
    public int getStart() {
        return start;
    }
    
    /**
     * Get the last character in the set
     * 
     * @return The last character in the set
     */
    public int getEnd() {
        return end;
    }
    
    /**
     * Get the name of the set
     * 
     * @return The name of the set
     */
    public String getName() {
        return name;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
	public String toString() {
		return name;
	}
}
