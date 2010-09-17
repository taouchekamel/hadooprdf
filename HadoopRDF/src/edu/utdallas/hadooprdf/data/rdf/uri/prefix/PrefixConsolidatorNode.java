package edu.utdallas.hadooprdf.data.rdf.uri.prefix;

import edu.utdallas.hadooprdf.lib.util.prefixtree.PrefixNode;

public class PrefixConsolidatorNode extends PrefixNode {
	/**
	 * The replacement prefix, if the node has any
	 */
	private String m_sReplacementPrefix;
	/**
	 * The class constructor
	 * @param ch the character of the node
	 */
	public PrefixConsolidatorNode(char ch) {
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
			if (index == s.length()) {
				if (null == m_sReplacementPrefix || 
						(m_sReplacementPrefix.length() > sReplacementPrefix.length() &&
								m_sReplacementPrefix.compareTo(sReplacementPrefix) > 0))
					m_sReplacementPrefix = sReplacementPrefix;
			}
			return;
		}
		char ch = s.charAt(index);
		PrefixConsolidatorNode node = (PrefixConsolidatorNode) m_Children.get(ch);
		if (null == node) {
			node = new PrefixConsolidatorNode(ch);
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
