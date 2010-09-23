/**
 * 
 */
package edu.utdallas.hadooprdf.lib.util;

/**
 * A generic utility class to hold a pair of data of any data type 
 * @author Mohammad Farhan Husain
 *
 */
public class Pair<A, B> {
	private A first;
	private B second;
	
	public Pair() {
	}

	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * @return the first
	 */
	public A getFirst() {
		return first;
	}

	/**
	 * @param first the first to set
	 */
	public void setFirst(A first) {
		this.first = first;
	}

	/**
	 * @return the second
	 */
	public B getSecond() {
		return second;
	}

	/**
	 * @param second the second to set
	 */
	public void setSecond(B second) {
		this.second = second;
	}
}
