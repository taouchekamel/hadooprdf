package edu.utdallas.hadooprdf.preprocessing.namespacing;

/**
 * An exception class for errors in PrefixHandler
 * @author Mohammad Farhan Husain
 * @see PrefixHandler
 */
public class PrefixFinderException extends Exception {
	/**
	 * The serial version UID
	 */
	private static final long serialVersionUID = 9048501799514945309L;

	/**
	 * The class constructor
	 * @param sMessage the error message
	 */
	public PrefixFinderException(String sMessage) {
		super(sMessage);
	}
}
