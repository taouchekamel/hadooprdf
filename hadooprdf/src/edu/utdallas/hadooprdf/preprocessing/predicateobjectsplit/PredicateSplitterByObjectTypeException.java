package edu.utdallas.hadooprdf.preprocessing.predicateobjectsplit;

/**
 * An exception class for PredicateSplitterByObjectType
 * @author Mohammad Farhan Husain
 *
 */
public class PredicateSplitterByObjectTypeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8607088761314542237L;
	/**
	 * The class constructor
	 * @param sMessage the error message
	 */
	public PredicateSplitterByObjectTypeException(String sMessage) {
		super(sMessage);
	}

}
