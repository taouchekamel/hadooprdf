package edu.utdallas.hadooprdf.data.preprocessing.serialization;

/**
 * An exception class to denote a conversion to NTriples has failed. The message
 * should contain the details of it.
 * 
 * @author Mohammad Farhan Husain
 */
public class ConversionToNTriplesException extends Exception {
	/**
	 * The serial version UID
	 */
	private static final long serialVersionUID = 6290705512946611813L;
	/**
	 * The class constructor
	 * 
	 * @param sMessage
	 *            the message containing the details of the failure
	 */
	public ConversionToNTriplesException(String sMessage) {
		super(sMessage);
	}
}
