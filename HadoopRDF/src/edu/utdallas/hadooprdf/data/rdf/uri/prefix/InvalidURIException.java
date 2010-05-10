package edu.utdallas.hadooprdf.data.rdf.uri.prefix;

/**
 * This is an exception class to indicate that a string is not a valid URI
 * 
 * @author Mohammad Farhan Husain
 * @version 1.0
 * @since 1.0
 */
public final class InvalidURIException extends Exception {
	/**
	 * The serial version UID
	 */
	private static final long serialVersionUID = 887454041205809835L;
	/**
	 * The class constructor
	 * @param s the invalid URI
	 */
	public InvalidURIException(String s) {
		super("Invalid URI: " + s);
	}
}
