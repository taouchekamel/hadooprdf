package edu.utdallas.hadooprdf.data.rdf.uri.prefix;

import java.util.HashMap;
import java.util.Map;

import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.preprocessing.lib.NamespacePrefixParser.NamespacePrefix;
import edu.utdallas.hadooprdf.lib.util.prefixtree.PrefixNode;

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
	 * The namespace prefix pairs
	 */
	private NamespacePrefix [] m_NamespacePrefixes;
	/**
	 * A map where the key is namespace and value is prefix.
	 * It is needed for reverse lookup
	 */
	private Map<String, String> m_NamespacePrefixMap;
	/**
	 * The class constructor
	 * @param namespacePrefixes the namespace prefix pairs
	 */
	public PrefixNamespaceTree(NamespacePrefix [] namespacePrefixes) {
		m_NamespacePrefixes = namespacePrefixes;
		m_NamespacePrefixMap = new HashMap<String, String> ();
		m_TreeRoots = new HashMap<Character, PrefixNode> ();
		for (int i = 0; i < namespacePrefixes.length; i++) {
			addPrefixAndReplacementString(namespacePrefixes[i].getPrefix(), namespacePrefixes[i].getNamespace());
			m_NamespacePrefixMap.put(namespacePrefixes[i].getNamespace(), namespacePrefixes[i].getPrefix());
		}
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
			return returnNamespace.toString() + Constants.NAMESPACE_DELIMITER + s.substring(index);
		return null;
	}
	/**
	 * Matches a namespace and replaces it with prefix
	 * @param s the string to match namespace in
	 * @return the string with the namespace replaced by prefix if a match is found, otherwise the same string
	 */
	public String matchAndReplaceNamespace(String s) {
		int index = s.indexOf(Constants.NAMESPACE_DELIMITER);
		if (index != -1) {
			String sNamespace = s.substring(0, index);
			String sPrefix = m_NamespacePrefixMap.get(sNamespace);
			if (sPrefix != null)
				s = sPrefix + s.substring(index + 1);
		}
		return s;
	}
	/**
	 * @return the m_NamespacePrefixes
	 */
	public NamespacePrefix [] getNamespacePrefixes() {
		return m_NamespacePrefixes;
	}
	/**
	 * @return the m_NamespacePrefixMap
	 */
	public Map<String, String> getNamespacePrefixMap() {
		return m_NamespacePrefixMap;
	}
}
