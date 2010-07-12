package edu.utdallas.hadooprdf.data.preprocessing.serialization.test;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import edu.utdallas.hadooprdf.conf.ConfigurationException;
import edu.utdallas.hadooprdf.conf.ConfigurationNotInitializedException;
import edu.utdallas.hadooprdf.data.commons.Constants.SerializationFormat;
import edu.utdallas.hadooprdf.data.metadata.DataFileExtensionNotSetException;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.preprocessing.serialization.ConversionToNTriplesException;
import edu.utdallas.hadooprdf.data.preprocessing.serialization.ConvertToNTriples;
import edu.utdallas.hadooprdf.data.preprocessing.*;
public class ConvertToNTriplesTest {

	@Test
	public void testDoConversion() throws PreprocessorException {
		// Create cluster configuration
		org.apache.hadoop.conf.Configuration hadoopConfiguration = new Configuration();
		String sConfDirectoryPath = "conf/SAIALLabCluster";
		//String sConfDirectoryPath = "conf/SemanticWebLabCluster";
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/core-site.xml"));
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/hdfs-site.xml"));
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/mapred-site.xml"));
		try {
			// Create application configuration
			edu.utdallas.hadooprdf.conf.Configuration config =
				edu.utdallas.hadooprdf.conf.Configuration.createInstance(hadoopConfiguration, "/user/pankil/hadooprdf");
			config.setNumberOfTaskTrackersInCluster(10); // 5 for semantic web lab, 10 for SAIAL lab
			DataSet ds = new DataSet("/user/test/hadooprdf/data/DBPEDIA");
			ds.setOriginalDataFilesExtension("nt");
			ConvertToNTriples ctn = new ConvertToNTriples(SerializationFormat.RDF_XML, ds);
			//ctn.doConversion();
			Preprocessor preprocessor = new Preprocessor(ds,SerializationFormat.NTRIPLES);
			preprocessor.preprocess();
		}/* catch (ConversionToNTriplesException e) {
			System.err.println("ConversionToNTriplesException occurred while testing ConvertToNTriples.doConversion\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		}*/ catch (ConfigurationNotInitializedException e) {
			System.err.println("ConfigurationNotInitializedException occurred while testing ConvertToNTriples.doConversion\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException occurred while testing ConvertToNTriples.doConversion\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		} catch (DataFileExtensionNotSetException e) {
			System.err.println("DataFileExtensionNotSetException occurred while testing ConvertToNTriples.doConversion\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		} catch (ConfigurationException e) {
			System.err.println("ConfigurationException occurred while testing PrefixFinder.findPrefixes\n" + e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
