package edu.utdallas.hadooprdf.data.metadata;

import java.io.IOException;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.commons.Constants;
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
	private String originalDataFilesExtension;
	/**
	 * The root path of the data set
	 */
	private Path dataSetRoot;
	/**
	 * Path to the data directory
	 */
	private Path pathToDataDirectory;
	/**
	 * Path to the original data, which can be in any format
	 */
	private Path pathToOriginalData;
	/**
	 * Path to data in NTriples format and later using namespaces
	 */
	private Path pathToNTriplesData;
	/**
	 * Path to encoded data
	 */
	private Path pathToEncodedData;
	/**
	 * Path to PS data
	 */
	private Path pathToPSData;
	/**
	 * Path to POS data
	 */
	private Path pathToPOSData;
	/**
	 * Path to metadata of the data set
	 */
	private Path pathToMetaData;
	/**
	 * Path to predicate list
	 */
	private Path pathToPredicateList;
	/**
	 * Path to type list
	 */
	private Path pathToTypeList;
	/**
	 * Path to Dictionary
	 */
	private Path pathToDictionary;
	/**
	 * Path to temporary directory
	 */
	private Path pathToTemp;
	/**
	 * The predicate collection of the data set
	 */
	private Collection<String> predicateCollection;
	/**
	 * The Hadoop Configuration
	 */
	private org.apache.hadoop.conf.Configuration hadoopConfiguration;
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
		this.hadoopConfiguration = hadoopConfiguration;
		originalDataFilesExtension = null;
		this.dataSetRoot = dataSetRoot;
		pathToDataDirectory = new Path(dataSetRoot, "data");
		pathToOriginalData = new Path(pathToDataDirectory, "Original");
		pathToNTriplesData = new Path(pathToDataDirectory, "NTriples");
		pathToEncodedData = new Path(pathToDataDirectory, "Encoded");
		pathToPSData = new Path(pathToDataDirectory, "PS");
		pathToPOSData = new Path(pathToDataDirectory, "POS");
		pathToMetaData = new Path(dataSetRoot, "metadata");
		Utility.createDirectory(hadoopConfiguration, pathToMetaData);
		pathToPredicateList = new Path(pathToMetaData, "predicates");
		pathToTypeList = new Path(pathToMetaData, "types");
		pathToDictionary = new Path(pathToMetaData, "dictionary");
		pathToTemp = new Path(dataSetRoot, "tmp");
		predicateCollection = createPredicateCollection(hadoopConfiguration);
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
		FileStatus [] files = fs.listStatus(pathToPOSData, new PathFilterOnFilenameExtension(Constants.POS_EXTENSION));
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
	 * @return the dataSetRoot
	 */
	public Path getDataSetRoot() {
		return dataSetRoot;
	}
	/**
	 * @return the pathToOriginalData
	 * @throws IOException 
	 */
	public Path getPathToOriginalData() throws IOException {
		Utility.createDirectoryIfNotExists(hadoopConfiguration, pathToOriginalData);
		return pathToOriginalData;
	}
	/**
	 * @return the pathToNTriplesData
	 */
	public Path getPathToNTriplesData() {
		return pathToNTriplesData;
	}
	/**
	 * @return the pathToPSData
	 */
	public Path getPathToPSData() {
		return pathToPSData;
	}
	/**
	 * @return the pathToPOSData
	 */
	public Path getPathToPOSData() {
		return pathToPOSData;
	}
	/**
	 * @return the pathToMetaData
	 */
	public Path getPathToMetaData() {
		return pathToMetaData;
	}
	/**
	 * @return the pathToTemp
	 */
	public Path getPathToTemp() {
		return pathToTemp;
	}
	/**
	 * @return the pathToDictionary
	 */
	public Path getPathToDictionary() {
		return pathToDictionary;
	}
	/**
	 * @param sDataFilesExtension the extension of the original data files
	 */
	public void setOriginalDataFilesExtension(String sDataFilesExtension) {
		originalDataFilesExtension = sDataFilesExtension;
	}
	/**
	 * @return the originalDataFilesExtension
	 * @throws DataFileExtensionNotSetException 
	 */
	public String getOriginalDataFilesExtension() throws DataFileExtensionNotSetException {
		if (null == originalDataFilesExtension) throw new DataFileExtensionNotSetException("Extension of original data files is not set");
		return originalDataFilesExtension;
	}
	/**
	 * @return the predicateCollection
	 * @throws DataSetException 
	 */
	public Collection<String> getPredicateCollection() throws DataSetException {
		try {
			if (null == predicateCollection && FileSystem.get(hadoopConfiguration).exists(pathToPOSData))
				predicateCollection = createPredicateCollection(hadoopConfiguration);
		} catch (IOException e) {
			throw new DataSetException("PredicateCollection could not be built because\n" + e.getMessage());
		}
		return predicateCollection;
	}
	/**
	 * @return the pathToEncodedData
	 */
	public Path getPathToEncodedData() {
		return pathToEncodedData;
	}
	/**
	 * @param mPathToEncodedData the pathToEncodedData to set
	 */
	public void setPathToEncodedData(Path mPathToEncodedData) {
		pathToEncodedData = mPathToEncodedData;
	}
	/**
	 * @return the pathToPredicateList
	 */
	public Path getPathToPredicateList() {
		return pathToPredicateList;
	}
	/**
	 * @param mPathToPredicateList the pathToPredicateList to set
	 */
	public void setPathToPredicateList(Path mPathToPredicateList) {
		pathToPredicateList = mPathToPredicateList;
	}
	/**
	 * @return the hadoopConfiguration
	 */
	public org.apache.hadoop.conf.Configuration getHadoopConfiguration() {
		return hadoopConfiguration;
	}
	/**
	 * @return the pathToTypeList
	 */
	public Path getPathToTypeList() {
		return pathToTypeList;
	}
	/**
	 * @param pathToTypeList the pathToTypeList to set
	 */
	public void setPathToTypeList(Path pathToTypeList) {
		this.pathToTypeList = pathToTypeList;
	}
}
