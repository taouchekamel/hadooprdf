package edu.utdallas.hadooprdf.query.parser;

public class UnhandledElementException extends Exception 
{
	private static final long serialVersionUID = -4169215760567429486L;

	public UnhandledElementException () { super(); }
	
	public UnhandledElementException (String msg) { super (msg); }
}
