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
	
	/**
	 * The log is a simple static utility, no construction
	 */
	private Log() {
		
	}
	
	/**
	 * Indicate that we want verbose logging
	 * 
	 * @param v True if we want verbose logging (INFO and DEBUG)
	 */
	public static void setVerbose(boolean v) {
		verbose = v;
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
		if (verbose) {
			out.println(new Date()+" INFO:" +message);
		}
	}

	/**
	 * Log a debug message
	 * 
	 * @param message The message describing the debug
	 */
	public static void debug(String message) {
		if (verbose) {
			out.println(new Date()+" DEBUG:" +message);
		}
	}
}
