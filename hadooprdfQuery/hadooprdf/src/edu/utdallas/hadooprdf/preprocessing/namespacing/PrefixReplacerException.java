package edu.utdallas.hadooprdf.preprocessing.namespacing;

/**
 * An exception class for prefix replacer
 * @author Mohammad Farhan Husain
 */
public class PrefixReplacerException extends Exception {
	private static final long serialVersionUID = -9144309148009765200L;
	/**
	 * The class constructor
	 * @param sMessage the error message
	 */
	public PrefixReplacerException(String sMessage) {
		super(sMessage);
	}

}
