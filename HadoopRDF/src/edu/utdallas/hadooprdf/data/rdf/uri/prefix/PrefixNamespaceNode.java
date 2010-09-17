package edu.utdallas.hadooprdf.data.rdf.uri.prefix;

import edu.utdallas.hadooprdf.lib.util.prefixtree.PrefixNode;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class PrefixNamespaceNode extends PrefixNode {
	/**
	 * The replacement prefix, if the node has any
	 */
	private String m_sReplacementPrefix;
	/**
	 * The class constructor
	 * @param ch the character of this node
	 */
	protected PrefixNamespaceNode(char ch) {
		super(ch);
		m_sReplacementPrefix = null;
	}
	/**
	 * Adds a child node, if not present, and calls the same method of that node to add next child
	 * @param charArray
	 * @param index
	 */
	public void addChild(String s, int index, String sReplacementPrefix) {
		if (index >= s.length()) {
			if (index == s.length())
				m_sReplacementPrefix = sReplacementPrefix;
			return;
		}
		char ch = s.charAt(index);
		PrefixNamespaceNode node = (PrefixNamespaceNode) m_Children.get(ch);
		if (null == node) {
			node = new PrefixNamespaceNode(ch);
			m_Children.put(ch, node);
		}
		node.addChild(s, index + 1, sReplacementPrefix);
	}
	/**
	 * @return the m_bHasReplacementPrefix
	 */
	public boolean hasReplacementPrefix() {
		return null != m_sReplacementPrefix;
	}
	/**
	 * @return the m_sReplacementPrefix
	 */
	public String getReplacementPrefix() {
		return m_sReplacementPrefix;
	}
}
