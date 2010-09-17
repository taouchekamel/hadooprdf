package edu.utdallas.hadooprdf.lib.util.prefixtree;

import java.io.Serializable;

/**
 * The class represents a node in the prefix tree
 * @author Mohammad Farhan Husain
 *
 */
public class PrefixTreeNode extends PrefixNode implements Serializable {
	private static final long serialVersionUID = -8093684209453463612L;
	/**
	 * Count of the words which end at this node
	 */
	private int m_iWordEndsHere;
	
	/**
	 * The class constructor
	 * @param ch the character of the node
	 */
	public PrefixTreeNode(char ch) {
		super(ch);
		m_iWordEndsHere = 0;
	}
	/**
	 * Adds a child node, if not present, and calls the same method of that node to add next child
	 * @param charArray
	 * @param index
	 */
	public void addChild(String s, int index) {
		if (index >= s.length()) {
			if (index == s.length())
				m_iWordEndsHere++;
			return;
		}
		char ch = s.charAt(index);
		PrefixTreeNode node = (PrefixTreeNode) m_Children.get(ch);
		if (null == node) {
			node = new PrefixTreeNode(ch);
			m_Children.put(ch, node);
		}
		node.addChild(s, index + 1);
	}
	/**
	 * Is end of a word?
	 * @return true if end of a word
	 */
	public boolean isEndOfWord() {
		return 0 != m_iWordEndsHere;
	}
	/**
	 * Get the number of words which end at this node
	 * @return the number of words which end at this node
	 */
	public int getNumberOfWordsEndingHere() {
		return m_iWordEndsHere;
	}
	/**
	 * Increments the count of words ending here
	 */
	public void setEndOfWord() {
		m_iWordEndsHere++;
	}
	/**
	 * Gets the String representation of the tree rooted at this node
	 * @param sPrecedingWhiteSpaces the preceding whitespace string  
	 * @param chWhiteSpace the whitespace character to be used for indentation
	 * @return the String representation of the tree rooted at this node
	 */
	public String toString(String sPrecedingWhiteSpaces, char chWhiteSpace) {
		if (null == sPrecedingWhiteSpaces) sPrecedingWhiteSpaces = "";
		StringBuffer sb = new StringBuffer();
		sb.append(sPrecedingWhiteSpaces + m_Character + '\n');
		String sPrecedingWhiteSpacesForChildren = sPrecedingWhiteSpaces + chWhiteSpace;
		for (PrefixNode child : m_Children.values()) {
			sb.append(((PrefixTreeNode) child).toString(sPrecedingWhiteSpacesForChildren, chWhiteSpace));
		}
		return sb.toString();
	}
}
