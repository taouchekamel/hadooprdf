package edu.utdallas.hadooprdf.conf;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class ConfigurationException extends Exception {
	private static final long serialVersionUID = -1526792601049911448L;
	/**
	 * The class constructor
	 * @param sErrorMessage the error message
	 */
	public ConfigurationException(String sErrorMessage) {
		super(sErrorMessage);
	}
}
