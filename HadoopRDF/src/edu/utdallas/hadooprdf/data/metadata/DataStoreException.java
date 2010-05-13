package edu.utdallas.hadooprdf.data.metadata;

/**
 * An exception class for DataStore
 * @author Mohammad Farhan Husain
 *
 */
public class DataStoreException extends Exception {
	private static final long serialVersionUID = -1351958523213323912L;
	/**
	 * The class constructor
	 * @param sErrorMessage the error message
	 */
	public DataStoreException(String sErrorMessage) {
		super(sErrorMessage);
	}
}
