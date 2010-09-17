/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary;

/**
 * An exception class for denoting error in dictionary creation
 * 
 * @author Mohammad Farhan Husain
 */
public class DictionaryCreatorException extends Exception {

	private static final long serialVersionUID = -6916011500284509551L;

	/**
	 * @param errorMessage the error message containing details
	 */
	public DictionaryCreatorException(String errorMessage) {
		super(errorMessage);
	}

}
