package edu.utdallas.hadooprdf.controller;

/**
 * An exception class for HadoopRDF
 * @author Mohammad Farhan Husain
 *
 */
public class HadoopRDFException extends Exception {
	private static final long serialVersionUID = -151411817050451504L;
	/**
	 * The class constructor
	 * @param sErrorMessage the error message
	 */
	public HadoopRDFException(String sErrorMessage) {
		super(sErrorMessage);
	}
}
