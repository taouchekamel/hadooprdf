/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import edu.utdallas.hadooprdf.lib.util.Utility;
import edu.utdallas.hadooprdf.lib.util.prefixtree.PrefixNode;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class DictionaryPrefixTree {
	/**
	 * The current id of a string
	 */
	private long nodeId;
	/**
	 * The root of the forest
	 */
	protected Map<Character, PrefixNode> m_TreeRoots;

	/**
	 * @param sPrefixPrefix
	 */
	public DictionaryPrefixTree(long startingId) {
		nodeId = startingId;
		m_TreeRoots = new HashMap<Character, PrefixNode> ();
	}

	/**
	 * Adds a string to the tree
	 * @param s
	 */
	public void addString(String s, int frequency) {
		if (Utility.isEmpty(s))
			return;
		final char ch = s.charAt(0);
		DictionaryPrefixTreeNode node = (DictionaryPrefixTreeNode) m_TreeRoots.get(ch);
		if (null == node) {
			node = new DictionaryPrefixTreeNode(ch);
			m_TreeRoots.put(ch, node);
		}
		if (s.length() > 1)
			if (node.addChild(s, 1, nodeId, frequency))
				nodeId++;
		else {
			node.setId(nodeId++);
			node.setEndOfWord();
			node.setFrequency(frequency);
		}
	}
	
	/**
	 * The method recursively writes the string-id pairs to a reducer context
	 * @param context
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void writeTreeToReducerContext(org.apache.hadoop.mapreduce.Reducer<Text, IntWritable, Text, LongWritable>.Context context) throws IOException, InterruptedException {
		// Recursively write the dictionary here
		for (PrefixNode node : m_TreeRoots.values())
			((DictionaryPrefixTreeNode) node).writeTreeToReducerContext(context, new StringBuffer(""), 0);
	}
}
