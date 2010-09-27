/**
 * 
 */
package edu.utdallas.hadooprdf.data.metadata;

/**
 * An exception class to denote error in {@link PredicateIdPairs}
 * @author Mohammad Farhan Husain
 *
 */
public class StringIdPairsException extends Exception {

	private static final long serialVersionUID = -4265742125813249443L;
	
	public StringIdPairsException(String errorMessage) {
		super(errorMessage);
	}

}
