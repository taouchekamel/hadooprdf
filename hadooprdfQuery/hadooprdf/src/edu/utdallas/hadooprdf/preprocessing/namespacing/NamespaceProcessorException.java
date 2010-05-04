package edu.utdallas.hadooprdf.preprocessing.namespacing;

/**
 * A exception class for NamespaceProcessor
 * @author Mohammad Farhan Husain
 *
 */
public class NamespaceProcessorException extends Exception {
	private static final long serialVersionUID = -4906847547594172541L;

	/**
	 * The class constructor
	 * @param sMessage the error message
	 */
	public NamespaceProcessorException(String sMessage) {
		super(sMessage);
	}
}
