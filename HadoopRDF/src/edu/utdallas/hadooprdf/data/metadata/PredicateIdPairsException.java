/**
 * 
 */
package edu.utdallas.hadooprdf.data.metadata;

/**
 * An exception class to denote error in {@link PredicateIdPairs}
 * @author Mohammad Farhan Husain
 *
 */
public class PredicateIdPairsException extends Exception {

	private static final long serialVersionUID = -4265742125813249443L;
	
	public PredicateIdPairsException(String errorMessage) {
		super(errorMessage);
	}

}
