package edu.utdallas.hadooprdf.query.parser;

public class NotBasicElementException extends Exception 
{
	private static final long serialVersionUID = 3392943385695876585L;

	public NotBasicElementException () { super(); }
	
	public NotBasicElementException (String msg) { super (msg); }
}