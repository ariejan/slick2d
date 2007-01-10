package org.newdawn.slick.tools.hiero;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class CharSet {
	private int start;
	private int end;
	private String name;
	
	public CharSet(int start, int end, String name) {
		this.start = start;
		this.end = end;
		this.name = name;
	}
	
    public int getStart() {
        return start;
    }
    
    public int getEnd() {
        return end;
    }
    
    public String getName() {
        return name;
    }
    
	public String toString() {
		return name;
	}
}
