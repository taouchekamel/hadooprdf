package edu.utdallas.hadooprdf.data.rdf.uri.prefix;

import java.io.Serializable;

/**
 * This class is a prefix tree for URIs ({@link http://tools.ietf.org/html/rfc3305})
 * 
 * @author Mohammad Farhan Husain
 * @version 1.0
 * @since 1.0
 */
public class URIPrefixTree extends PrefixTree implements Serializable {
	private static final long serialVersionUID = 6357529393225766799L;
	/**
	 * The default class constructor
	 */
	public URIPrefixTree(String sPrefixPrefix) {
		super(sPrefixPrefix);
	}
	/**
	 * @see edu.utdallas.hadooprdf.rdf.uri.prefix.PrefixTree#generateLongestCommonPrefixes(edu.utdallas.hadooprdf.rdf.uri.prefix.PrefixNode, java.lang.StringBuffer) 
	 */
	@Override
	protected void generateLongestCommonPrefixes(PrefixNode node, StringBuffer sbPrefix) {
		sbPrefix.append(node.getCharacter());
		String sReplacementPrefix = m_sPrefixPrefix + m_iPrefixCount;
		if (((PrefixTreeNode) node).isEndOfWord() && sReplacementPrefix.length() < sbPrefix.length()) {
			m_LongestCommonPrefixes.put(sReplacementPrefix, sbPrefix.toString());
			m_iPrefixCount++;
		}
		else if (1 == node.getNumberOfChildren())
			generateLongestCommonPrefixes(node.getSingleChild(), sbPrefix);
		else if (sbPrefix.toString().endsWith("://") || sbPrefix.toString().equalsIgnoreCase(("http://www."))) {
			for(PrefixNode child : node.getChildren().values()) {
				generateLongestCommonPrefixes(child, new StringBuffer(sbPrefix));
			}
		}
		else if (sReplacementPrefix.length() < sbPrefix.length()) {
			m_LongestCommonPrefixes.put(sReplacementPrefix, sbPrefix.toString());
			m_iPrefixCount++;
		}
	}
	/**
	 * This static method checks whether a string is a URI of non-zero length {@link http://tools.ietf.org/html/rfc3305}
	 * The method is incomplete.
	 * @param s the string to be checked.
	 * @return	true if s is a URI, otherwise returns false.
	 */
	public static boolean isURI(String s) {
		if (s.length() == 0) return false;
		return true;
	}
	
	/**
	 * Adds an URI to the prefix tree
	 * 
	 * @param s
	 * @throws InvalidURIException
	 */
	public void addURI(String s) throws InvalidURIException {
		if (!isURI(s)) throw new InvalidURIException(s);
		PrefixTreeNode node = (PrefixTreeNode) m_TreeRoots.get(s.charAt(0));
		if (null == node) {
			node = new PrefixTreeNode(s.charAt(0));
			m_TreeRoots.put(s.charAt(0), node);
		}
		node.addChild(s, 1);
		m_bPrefixesValid = false;
	}
	
	/**
	 * Removes an URI from the prefix tree
	 * 
	 * @param s
	 * @throws InvalidURIException
	 */
	public void removeURI(String s) throws InvalidURIException {
		if (!isURI(s)) throw new InvalidURIException(s);
	}
	
	/**
	 * Gets the String representation of the tree
	 * @param chWhiteSpace the whitespace character to be used for indentation
	 * @return the String representation of the tree
	 */
	public String toString(char chWhiteSpace) {
		StringBuffer sb = new StringBuffer();
		for (PrefixNode root : m_TreeRoots.values())
			sb.append(((PrefixTreeNode) root).toString("", chWhiteSpace));
		return sb.toString();
	}
}
