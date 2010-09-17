/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary;

import edu.utdallas.hadooprdf.lib.util.prefixtree.PrefixTreeNode;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class DictionaryPrefixTreeNode extends PrefixTreeNode {

	private static final long serialVersionUID = -8682380663211905805L;
	/**
	 * The id of the string ending here, if any
	 */
	private long id;

	/**
	 * @param ch
	 */
	public DictionaryPrefixTreeNode(char ch) {
		super(ch);
		id = -1;
	}

	/**
	 * Recursively adds child to a node
	 * @param s the string being added to the tree
	 * @param index the index the character to be added is at
	 * @param newNodeId the new node id to be used if needed
	 * @return true if the new node id is used, otherwise false
	 */
	public boolean addChild(String s, int index, long newNodeId) {
		if (index == s.length()) {
			if (!isEndOfWord()) // If it not an end of string already, assign the id
				id = newNodeId;
			setEndOfWord();		// Increment the word end counter
			return id == newNodeId;	// true if the new id is used
		}
		char ch = s.charAt(index);
		DictionaryPrefixTreeNode node = (DictionaryPrefixTreeNode) m_Children.get(ch);
		if (null == node) {
			node = new DictionaryPrefixTreeNode(ch);
			m_Children.put(ch, node);
		}
		return node.addChild(s, index + 1, newNodeId);
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

}
