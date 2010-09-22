/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing.dictionary;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import edu.utdallas.hadooprdf.lib.util.prefixtree.PrefixNode;
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
	 * The frequency of occurance in the data set
	 */
	protected int frequency;

	/**
	 * @param ch
	 */
	public DictionaryPrefixTreeNode(char ch) {
		super(ch);
		id = -1;
		frequency = 0;
	}

	/**
	 * Recursively adds child to a node
	 * @param s the string being added to the tree
	 * @param index the index the character to be added is at
	 * @param newNodeId the new node id to be used if needed
	 * @return true if the new node id is used, otherwise false
	 */
	public boolean addChild(String s, int index, long newNodeId, int frequency) {
		if (index == s.length()) {
			if (!isEndOfWord()) // If it not an end of string already, assign the id
				id = newNodeId;
			setEndOfWord();		// Increment the word end counter
			this.frequency = frequency;	// Set the frequency
			return id == newNodeId;	// true if the new id is used
		}
		char ch = s.charAt(index);
		DictionaryPrefixTreeNode node = (DictionaryPrefixTreeNode) m_Children.get(ch);
		if (null == node) {
			node = new DictionaryPrefixTreeNode(ch);
			m_Children.put(ch, node);
		}
		return node.addChild(s, index + 1, newNodeId, frequency);
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

	/**
	 * Recursively writes string-id pairs to a reducer context
	 * @param context the context to write pairs to
	 * @param sb the string buffer containing the sequence of characters of the path from root to current node
	 * @param index the index of string buffer where the character has to be inserted
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void writeTreeToReducerContext(org.apache.hadoop.mapreduce.Reducer<Text, IntWritable, Text, LongWritable>.Context context, StringBuilder sb, int index) throws IOException, InterruptedException {
		sb.append(m_Character);
		if (isEndOfWord())
			context.write(new Text(sb.toString() + '\t' + frequency), new LongWritable(id));
		for (PrefixNode node : m_Children.values())
			((DictionaryPrefixTreeNode) node).writeTreeToReducerContext(context, sb, index + 1);
		sb.deleteCharAt(index);
	}

	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}
