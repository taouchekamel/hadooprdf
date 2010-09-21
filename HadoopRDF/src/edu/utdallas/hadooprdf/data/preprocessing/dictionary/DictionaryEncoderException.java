/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary;

/**
 * An exception class to provide details information of an exception
 * occurred in <i>DictionaryEncoder</i>.
 * @author Mohammad Farhan Husain
 *
 */
public class DictionaryEncoderException extends Exception {
	private static final long serialVersionUID = 5982907570407316313L;

	public DictionaryEncoderException(String message) {
		super(message);
	}
}
