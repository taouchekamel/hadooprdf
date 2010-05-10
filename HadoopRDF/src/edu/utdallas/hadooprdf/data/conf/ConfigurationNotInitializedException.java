package edu.utdallas.hadooprdf.data.conf;

/**
 * The exception class to be thrown if the singleton
 * Configuration instance is not initialized but is tried
 * to be accessed
 * @author Mohammad Farhan Husain
 *
 */
public class ConfigurationNotInitializedException extends Exception {
	/**
	 * The serial version UID
	 */
	private static final long serialVersionUID = 4199093284612121231L;
	/**
	 * The class constructor
	 * @param sMessage the m_sMessage to set
	 */
	public ConfigurationNotInitializedException(String sMessage) {
		super(sMessage);
	}
}
