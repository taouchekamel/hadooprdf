package edu.utdallas.hadooprdf.data.preprocessing;

/**
 * An exception class for Preprocessor
 * @author Mohammad Farhan Husain
 *
 */
public class PreprocessorException extends Exception {
	private static final long serialVersionUID = 7183646310596811109L;
	/**
	 * The class constructor
	 * @param sErrorMessage the error message
	 */
	public PreprocessorException(String sErrorMessage) {
		super(sErrorMessage);
	}
}
