package edu.utdallas.hadooprdf.data.metadata;

/**
 * An exception class to be thrown if the extension of
 * the original data files is not set
 * @author Mohammad Farhan Husain
 *
 */
public class DataFileExtensionNotSetException extends Exception {
	private static final long serialVersionUID = -7410962917937730660L;

	/**
	 * The class constructor
	 * @param sMessage the error message
	 */
	public DataFileExtensionNotSetException(String sMessage) {
		super(sMessage);
	}
}
