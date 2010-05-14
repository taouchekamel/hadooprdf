package edu.utdallas.hadooprdf.controller;

import java.io.BufferedReader;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.conf.ConfigurationException;
import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.query.QueryExecution;
import edu.utdallas.hadooprdf.query.QueryExecutionFactory;

/**
 * The controller class
 * @author Mohammad Farhan Husain
 *
 */
public class HadoopRDF 
{
	/**
	 * The class constructor
	 * @param sLocalPathToHadoopConfigurationDirectory the path to the Hadoop Configuration directory in local filesystem having the xml configuration files
	 * @param sHDFSPathToStorageRoot the path to the storage root directory in HDFS
	 * @throws HadoopRDFException
	 */
	public HadoopRDF(String sLocalPathToHadoopConfigurationDirectory, String sHDFSPathToStorageRoot) throws HadoopRDFException
	{
		// Create cluster configuration
		org.apache.hadoop.conf.Configuration hadoopConfiguration = new Configuration();
		hadoopConfiguration.addResource(new Path(sLocalPathToHadoopConfigurationDirectory + "/core-site.xml"));
		hadoopConfiguration.addResource(new Path(sLocalPathToHadoopConfigurationDirectory + "/hdfs-site.xml"));
		hadoopConfiguration.addResource(new Path(sLocalPathToHadoopConfigurationDirectory + "/mapred-site.xml"));
		// create framework configuration singleton instance
		try {
			edu.utdallas.hadooprdf.conf.Configuration.createInstance(hadoopConfiguration, sHDFSPathToStorageRoot);
		} catch (ConfigurationException e) {
			throw new HadoopRDFException("Framework could not initializied because\n" + e.getMessage());
		}
	}
	/**
	 * Get the data sets in the storage
	 * @return
	 * @throws HadoopRDFException
	 */
	public Map<String, DataSet> getDataSetMap() throws HadoopRDFException {
		try {
			return edu.utdallas.hadooprdf.conf.Configuration.getInstance().getDataStore().getDataSetMap();
		} catch (ConfigurationNotInitializedException e) {
			throw new HadoopRDFException("Framework could not list data sets because\n" + e.getMessage());
		}
	}
	
	/**
	 * A method that executes the given SPARQL query and returns the result as a BufferedReader
	 * @param queryString - the input SPARQL query as a string
	 * @param dataset - the DataSet object
	 * @return a BufferedReader containing the results
	 * @throws HadoopRDFException 
	 */
	public BufferedReader executeQuery( String queryString, DataSet dataset ) throws HadoopRDFException
	{
		try
		{
			//Create a QueryExecution object
			QueryExecution qexec = QueryExecutionFactory.create( queryString, dataset );
		
			//Get the output stream reader
			return qexec.execSelect();
		}
		catch( Exception e )
		{
			throw new HadoopRDFException( "Framework could not be queried because\n" + e.getMessage() );
		}
	}
}
