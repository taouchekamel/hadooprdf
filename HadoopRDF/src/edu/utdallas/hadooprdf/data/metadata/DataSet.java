package edu.utdallas.hadooprdf.data.metadata;

import java.io.IOException;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.preprocessing.lib.NamespacePrefixParser.NamespacePrefix;
import edu.utdallas.hadooprdf.data.rdf.uri.prefix.PrefixNamespaceTree;
import edu.utdallas.hadooprdf.lib.util.PathFilterOnFilenameExtension;
import edu.utdallas.hadooprdf.lib.util.Utility;

/**
 * This class contains metadata about a dataset
 * @author Mohammad Farhan Husain
 *
 */
public class DataSet {
	/**
	 * Extension of original data files
	 */
	private String m_sOriginalDataFilesExtension;
	/**
	 * The root path of the data set
	 */
	private Path m_DataSetRoot;
	/**
	 * Path to the original data, which can be in any format
	 */
	private Path m_PathToOriginalData;
	/**
	 * Path to data in NTriples format and later using namespaces
	 */
	private Path m_PathToNTriplesData;
	/**
	 * Path to PS data
	 */
	private Path m_PathToPSData;
	/**
	 * Path to POS data
	 */
	private Path m_PathToPOSData;
	/**
	 * Path to metadata of the data set
	 */
	private Path m_PathToMetaData;
	/**
	 * Path to temporary directory
	 */
	private Path m_PathToTemp;
	/**
	 * Path to prefix file
	 */
	private Path m_PathToPrefixFile;
	/**
	 * PrefixNamespaceTree for the data set
	 */
	private PrefixNamespaceTree m_PrefixNamespaceTree;
	/**
	 * The predicate collection of the data set
	 */
	private Collection<String> m_PredicateCollection;
	/**
	 * Class constructor
	 * @param sDataSetRoot the root path of the data set as a String
	 * @param sOriginalDataFilesExtension the extension of original data files in lower case
	 * @throws ConfigurationNotInitializedException
	 * @throws IOException 
	 */
	public DataSet(String sDataSetRoot) throws IOException, ConfigurationNotInitializedException {
		this(new Path(sDataSetRoot));
	}
	/**
	 * Class constructor
	 * @param dataSetRoot the root path of the data set
	 * @throws ConfigurationNotInitializedException
	 * @throws IOException 
	 */
	public DataSet(Path dataSetRoot) throws IOException, ConfigurationNotInitializedException {
		this(dataSetRoot, edu.utdallas.hadooprdf.conf.Configuration.getInstance().getHadoopConfiguration());
	}
	/**
	 * Class constructor
	 * @param dataSetRoot the root path of the data set
	 * @throws ConfigurationNotInitializedException
	 * @throws IOException 
	 */
	public DataSet(Path dataSetRoot, org.apache.hadoop.conf.Configuration hadoopConfiguration) throws IOException {
		m_sOriginalDataFilesExtension = null;
		m_DataSetRoot = dataSetRoot;
		m_PathToOriginalData = new Path(m_DataSetRoot, "Original");
		m_PathToNTriplesData = new Path(m_DataSetRoot, "NTriples");
		m_PathToPSData = new Path(m_DataSetRoot, "PS");
		m_PathToPOSData = new Path(m_DataSetRoot, "POS");
		m_PathToMetaData = new Path(m_DataSetRoot, "metadata");
		Utility.createDirectory(hadoopConfiguration, m_PathToMetaData);
		m_PathToPrefixFile = new Path(m_PathToMetaData, "prefixes");
		m_PathToTemp = new Path(m_DataSetRoot, "tmp");
		m_PrefixNamespaceTree = Utility.getPrefixNamespaceTreeForDataSet(hadoopConfiguration, m_PathToPrefixFile);
		m_PredicateCollection = createPredicateCollection(hadoopConfiguration);
	}
	/**
	 * creates the predicate collection
	 * @param hadoopConfiguration the hadoop cluster configuration
	 * @return the predicate collection
	 * @throws IOException
	 */
	private Collection<String> createPredicateCollection(org.apache.hadoop.conf.Configuration hadoopConfiguration) throws IOException {
		Collection<String> predicateCollection = new TreeSet<String> ();
		FileSystem fs = FileSystem.get(hadoopConfiguration);
		FileStatus [] files = fs.listStatus(m_PathToPOSData, new PathFilterOnFilenameExtension(Constants.POS_EXTENSION));
		for (int i = 0; i < files.length; i++) {
			String sFilename = files[i].getPath().getName();
			int indexOfFirstNamespaceDelimiter = sFilename.indexOf(Constants.NAMESPACE_DELIMITER);
			int secondIndex = sFilename.indexOf(Constants.PREDICATE_OBJECT_TYPE_SEPARATOR, indexOfFirstNamespaceDelimiter + 1);
			if (-1 == secondIndex)
				secondIndex = sFilename.indexOf('.');
			if (secondIndex == indexOfFirstNamespaceDelimiter + 1) // rdf:type
				predicateCollection.add(sFilename.substring(0, indexOfFirstNamespaceDelimiter));
			else
				predicateCollection.add(sFilename.substring(0, secondIndex));
		}
		return predicateCollection;
	}
	/**
	 * Returns the prefix namespace tree for the data set
	 * @return the prefix namespace tree for the data set
	 */
	public PrefixNamespaceTree getPrefixNamespaceTree() {
		return m_PrefixNamespaceTree;
	}
	/**
	 * Gets an array of namespace-prefix pairs
	 * @return an array of namespace-prefix pairs
	 */
	public NamespacePrefix [] getNamespacePrefixes() {
		return m_PrefixNamespaceTree.getNamespacePrefixes();
	}
	/**
	 * @return the m_DataSetRoot
	 */
	public Path getDataSetRoot() {
		return m_DataSetRoot;
	}
	/**
	 * @return the m_PathToOriginalData
	 */
	public Path getPathToOriginalData() {
		return m_PathToOriginalData;
	}
	/**
	 * @return the m_PathToNTriplesData
	 */
	public Path getPathToNTriplesData() {
		return m_PathToNTriplesData;
	}
	/**
	 * @return the m_PathToPSData
	 */
	public Path getPathToPSData() {
		return m_PathToPSData;
	}
	/**
	 * @return the m_PathToPOSData
	 */
	public Path getPathToPOSData() {
		return m_PathToPOSData;
	}
	/**
	 * @return the m_PathToMetaData
	 */
	public Path getPathToMetaData() {
		return m_PathToMetaData;
	}
	/**
	 * @return the m_PathToTemp
	 */
	public Path getPathToTemp() {
		return m_PathToTemp;
	}
	/**
	 * @return the m_PathToPrefixFile
	 */
	public Path getPathToPrefixFile() {
		return m_PathToPrefixFile;
	}
	/**
	 * @param sDataFilesExtension the extension of the original data files
	 */
	public void setOriginalDataFilesExtension(String sDataFilesExtension) {
		m_sOriginalDataFilesExtension = sDataFilesExtension;
	}
	/**
	 * @return the m_sOriginalDataFilesExtension
	 * @throws DataFileExtensionNotSetException 
	 */
	public String getOriginalDataFilesExtension() throws DataFileExtensionNotSetException {
		if (null == m_sOriginalDataFilesExtension) throw new DataFileExtensionNotSetException("Extension of original data files is not set");
		return m_sOriginalDataFilesExtension;
	}
	/**
	 * @return the m_PredicateCollection
	 */
	public Collection<String> getPredicateCollection() {
		return m_PredicateCollection;
	}
}
