package edu.utdallas.hadooprdf.data.preprocessing.lib;

/**
 * A class to parse the toString output of Java Map
 * and output pairs of namespace and prefix
 * @author Mohammad Farhan Husain
 */
public class NamespacePrefixParser {
	/**
	 * A class representing a pair of namespace and prefix
	 * @author Mohammad Farhan Husain
	 */
	public class NamespacePrefix {
		/**
		 * The namespace
		 */
		private String m_sNamespace;
		/**
		 * The prefix
		 */
		private String m_sPrefix;
		/**
		 * The class constructor
		 * @param sNamespace the namespace
		 * @param sPrefix the prefix
		 */
		public NamespacePrefix(String sNamespace, String sPrefix) {
			m_sNamespace = sNamespace;
			m_sPrefix = sPrefix;
		}
		/**
		 * @return the m_sNamespace
		 */
		public String getNamespace() {
			return m_sNamespace;
		}
		/**
		 * @return the m_sPrefix
		 */
		public String getPrefix() {
			return m_sPrefix;
		}
	}
	/**
	 * The namespace prefix pairs
	 */
	private NamespacePrefix [] m_NamespacePrefixes;
	/**
	 * The class constructor
	 * @param s the output of toString method of Java Map objects
	 */
	public NamespacePrefixParser(String s) {
		s = s.substring(1, s.length() - 1);
		String splits [] = s.split(",");
		m_NamespacePrefixes = new NamespacePrefix[splits.length];
		for (int i = 0; i < splits.length; i++) {
			String sPair = splits[i].trim();
			int index = sPair.indexOf('=');
			m_NamespacePrefixes[i] = new NamespacePrefix(sPair.substring(0, index), sPair.substring(index + 1));
		}
	}
	/**
	 * @return the m_NamespacePrefixes
	 */
	public NamespacePrefix[] getNamespacePrefixes() {
		return m_NamespacePrefixes;
	}
}
