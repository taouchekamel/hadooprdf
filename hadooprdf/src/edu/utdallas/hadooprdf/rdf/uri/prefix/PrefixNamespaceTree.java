package edu.utdallas.hadooprdf.rdf.uri.prefix;

import java.util.HashMap;
import java.util.Map;

import edu.utdallas.hadooprdf.preprocessing.lib.NamespacePrefixParser.NamespacePrefix;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class PrefixNamespaceTree {
	/**
	 * The root of the forest
	 */
	protected Map<Character, PrefixNode> m_TreeRoots;
	/**
	 * The class constructor
	 * @param prefixNamespaces the namespace prefix pairs
	 */
	public PrefixNamespaceTree(NamespacePrefix [] prefixNamespaces) {
		m_TreeRoots = new HashMap<Character, PrefixNode> ();
		for (int i = 0; i < prefixNamespaces.length; i++)
			addPrefixAndReplacementString(prefixNamespaces[i].getPrefix(), prefixNamespaces[i].getNamespace());
	}
	/**
	 * Add a prefix and its corresponding replacement string
	 * @param sPrefix
	 * @param sReplacementString
	 */
	public void addPrefixAndReplacementString(String sPrefix, String sReplacementString) {
		PrefixNamespaceNode node = (PrefixNamespaceNode) m_TreeRoots.get(sPrefix.charAt(0));
		if (null == node) {
			node = new PrefixNamespaceNode(sPrefix.charAt(0));
			m_TreeRoots.put(sPrefix.charAt(0), node);
		}
		node.addChild(sPrefix, 1, sReplacementString);
	}
	/**
	 * The method which matches a prefix
	 * @param s the string to match prefix with
	 * @param returnNamespace the namespace to replace the prefix
	 * @return the index of the first character in the string s which is not part of the prefix
	 */
	public int matchPrefix(String s, StringBuffer returnNamespace) {
		for(PrefixNode node: m_TreeRoots.values()) {
			if (s.charAt(0) == node.getCharacter()) {
				int index = 1;
				PrefixNode parent, child;
				parent = node;
				while (index < s.length() && null != (child = parent.getChildren().get(s.charAt(index)))) {
					parent = child;
					index++;
				}
				if (((PrefixNamespaceNode) parent).hasReplacementPrefix()) {
					returnNamespace.delete(0, returnNamespace.length());
					returnNamespace.append(((PrefixNamespaceNode) parent).getReplacementPrefix());
					return index;
				}
			}
		}
		returnNamespace.delete(0, returnNamespace.length());
		return -1;
	}
	/**
	 * Matches a prefix and if found replaces it with namespace
	 * @param s the string to match prefix in
	 * @return the string with the prefix replaced by namespace if a match is found, otherwise null
	 */
	public String matchAndReplacePrefix(String s) {
		StringBuffer returnNamespace = new StringBuffer();
		int index = matchPrefix(s, returnNamespace);
		if (-1 != index)
			return returnNamespace.toString() + ':'	+ s.substring(index);
		return null;
	}
}
