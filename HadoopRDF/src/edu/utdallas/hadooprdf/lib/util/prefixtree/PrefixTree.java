package edu.utdallas.hadooprdf.lib.util.prefixtree;

import java.util.HashMap;
import java.util.Map;

/**
 * An abstract root class for prefix trees
 * @author Mohammad Farhan Husain
 *
 */
public abstract class PrefixTree {
	/**
	 * The total number of generated prefixes, used as a suffix in them
	 */
	protected int m_iPrefixCount;
	/**
	 * The prefix of prefixes
	 */
	protected String m_sPrefixPrefix;
	/**
	 * The root of the forest
	 */
	protected Map<Character, PrefixNode> m_TreeRoots;
	/**
	 * The prefix map
	 */
	protected Map<String, String> m_LongestCommonPrefixes;
	/**
	 * A boolean to keep track whether new URI is inserted after prefixes are generated
	 */
	protected boolean m_bPrefixesValid;
	/**
	 * The class constructor
	 */
	public PrefixTree(String sPrefixPrefix) {
		m_iPrefixCount = 0;
		m_sPrefixPrefix = sPrefixPrefix;
		m_TreeRoots = new HashMap<Character, PrefixNode> ();
		m_LongestCommonPrefixes = new HashMap<String, String> ();
		m_bPrefixesValid = false;
	}
	/**
	 * Get longest common prefixes in the forest and their replacement prefixes
	 * @return a Map where the replacement prefix is the key and the prefix to be replaced is the value
	 */
	public Map<String, String> getLongestCommonPrefixes() {
		if (!m_bPrefixesValid)
			generateLongestCommonPrefixes();
		return m_LongestCommonPrefixes;
	}
	/**
	 * Generates the replacement prefixes for longest common prefixes
	 */
	protected void generateLongestCommonPrefixes() {
		m_LongestCommonPrefixes.clear();
		for (PrefixNode node : m_TreeRoots.values()) {
			generateLongestCommonPrefixes(node, new StringBuffer());
		}
		m_bPrefixesValid = true;
	}
	/**
	 * Recursively generates the replacement prefixes for longest common prefixes
	 * @param node the node to visit
	 * @param sbPrefix the current prefix
	 */
	protected abstract void generateLongestCommonPrefixes(PrefixNode node, StringBuffer sbPrefix);
}
