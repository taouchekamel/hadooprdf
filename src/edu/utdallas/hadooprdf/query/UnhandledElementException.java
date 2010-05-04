package edu.utdallas.hadooprdf.query;

public class UnhandledElementException extends Exception {

	public UnhandledElementException () {
		super();
	}
	
	public UnhandledElementException (String msg) {
		super (msg);
	}
}
