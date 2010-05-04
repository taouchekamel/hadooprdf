package edu.utdallas.hadooprdf.query.parser;

public class UnhandledElementException extends Exception {

	public UnhandledElementException () {
		super();
	}
	
	public UnhandledElementException (String msg) {
		super (msg);
	}
}