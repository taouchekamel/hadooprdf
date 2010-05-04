package edu.utdallas.hadooprdf.rdf.uri.prefix;

import java.util.HashMap;
import java.util.Map;

/**
 * A parent class for prefix tree
 * node data structure
 * @author Mohammad Farhan Husain
 *
 */
public abstract class PrefixNode {
	/**
	 * The character of this node
	 */
	protected char m_Character;
	/**
	 * The children nodes
	 */
	protected Map<Character, PrefixNode> m_Children;
	/**
	 * The class constructor
	 * @param ch the character of the node
	 */
	protected PrefixNode(char ch) {
		m_Character = ch;
		m_Children = new HashMap<Character, PrefixNode> ();
	}
	/**
	 * Get the character of the node
	 * @return the character of the node
	 */
	public char getCharacter() {
		return m_Character;
	}
	/**
	 * Get number of children
	 * @return number of children
	 */
	public int getNumberOfChildren() {
		return m_Children.size();
	}
	/**
	 * Gets the single child
	 * @return the single child, if the number of children is not 1, return null
	 */
	public PrefixNode getSingleChild() {
		return (1 == m_Children.size()) ? ((PrefixNode) m_Children.values().iterator().next()) : null;
	}
	/**
	 * @return the m_Children
	 */
	public Map<Character, PrefixNode> getChildren() {
		return m_Children;
	}
}
