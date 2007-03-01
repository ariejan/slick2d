package org.newdawn.slick.util;

import java.io.PrintStream;
import java.util.Date;

/**
 * A simple central logging system
 * 
 * @author kevin
 */
public final class Log {
	/** The output stream for dumping the log out on */
	public static PrintStream out = System.out;
	/** True if we're doing verbose logging INFO and DEBUG */
	private static boolean verbose = true;
	/** true if activated by the system property "org.newdawn.slick.forceVerboseLog" */
	private static boolean forcedVerbose = false;
	
	/**
	 * The debug property which can be set via JNLP or startup parameter to switch
	 * logging mode to verbose for games that were released without verbose logging
	 * value must be "true"
	 */
	public static final String forceVerboseProperty = "org.newdawn.slick.forceVerboseLog";
	
	/**
	 * the verbose property must be set to "true" to switch on verbose logging
	 */
	public static final String forceVerbosePropertyOnValue = "true";
	
	/**
	 * The log is a simple static utility, no construction
	 */
	private Log() {
		
	}
	
	/**
	 * Indicate that we want verbose logging.
	 * The call is ignored if verbose logging is forced by the system property
	 * "org.newdawn.slick.forceVerboseLog"
	 * 
	 * @param v True if we want verbose logging (INFO and DEBUG)
	 */
	public static void setVerbose(boolean v) {
		if (forcedVerbose)
			return;
		verbose = v;
	}
	
	/**
	 * Indicate that we want verbose logging, even if switched off in game code.
	 * Only be called when system property "org.newdawn.slick.forceVerboseLog" is set to true.
	 * You must not call this method directly.
	 */
	public static void setForcedVerboseOn() {
		forcedVerbose = true;
		verbose = true;
	}
	
	/**
	 * Log an error
	 * 
	 * @param message The message describing the error
	 * @param e The exception causing the error
	 */
	public static void error(String message, Throwable e) {
		error(message);
		error(e);
	}

	/**
	 * Log an error
	 * 
	 * @param e The exception causing the error
	 */
	public static void error(Throwable e) {
		out.println(new Date()+" ERROR:" +e.getMessage());
		e.printStackTrace(out);
	}

	/**
	 * Log an error
	 * 
	 * @param message The message describing the error
	 */
	public static void error(String message) {
		out.println(new Date()+" ERROR:" +message);
	}

	/**
	 * Log a warning
	 * 
	 * @param message The message describing the warning
	 */
	public static void warn(String message) {
		out.println(new Date()+" WARN:" +message);
	}

	/**
	 * Log an information message
	 * 
	 * @param message The message describing the infomation
	 */
	public static void info(String message) {
		if (verbose || forcedVerbose) {
			out.println(new Date()+" INFO:" +message);
		}
	}

	/**
	 * Log a debug message
	 * 
	 * @param message The message describing the debug
	 */
	public static void debug(String message) {
		if (verbose || forcedVerbose) {
			out.println(new Date()+" DEBUG:" +message);
		}
	}
}
