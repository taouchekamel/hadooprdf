package edu.utdallas.hadooprdf.query.generator.triplepattern;

import edu.utdallas.hadooprdf.query.generator.triplepattern.impl.SimpleTriplePatternImpl;

/**
 * A factory class for different implementations of triple patterns
 * @author sharath, vaibhav
 *
 */
public class TriplePatternFactory 
{
	/** A private constructor **/
	private TriplePatternFactory() { }
	
	/**
	 * A method that returns a simple triple pattern
	 * @return a TriplePattern object
	 */
	public static TriplePattern createSimpleTriplePattern()
	{
		return new SimpleTriplePatternImpl();
	}
}
