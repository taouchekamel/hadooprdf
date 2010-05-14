package edu.utdallas.hadooprdf.data.metadata;

/**
 * An exception class for DataSet
 * @author Mohammad Farhan Husain
 *
 */
public class DataSetException extends Exception {
	private static final long serialVersionUID = 4972186706355042678L;
	/**
	 * The class constructor
	 * @param sErrorMessage the error message
	 */
	public DataSetException(String sErrorMessage) {
		super(sErrorMessage);
	}

}
