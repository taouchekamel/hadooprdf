/**
 * 
 */
package edu.utdallas.hadooprdf.data.preprocessing;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import edu.utdallas.hadooprdf.data.commons.Constants.SerializationFormat;
import edu.utdallas.hadooprdf.data.metadata.DataSet;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class PreprocessorTest {
	private DataSet dataSet;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Create cluster configuration
		org.apache.hadoop.conf.Configuration hadoopConfiguration = new Configuration();
		//String sConfDirectoryPath = "conf/SAIALLabCluster";
		String sConfDirectoryPath = "conf/SemanticWebLabCluster";
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/core-site.xml"));
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/hdfs-site.xml"));
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/mapred-site.xml"));
		// Create application configuration
		edu.utdallas.hadooprdf.conf.Configuration config =
			edu.utdallas.hadooprdf.conf.Configuration.createInstance(hadoopConfiguration, "/farhan/hadooprdf");
		config.setNumberOfTaskTrackersInCluster(5); // 5 for semantic web lab, 10 for SAIAL lab
		dataSet = new DataSet(new Path("/farhan/hadooprdf/data/LUBM1"), hadoopConfiguration);
		dataSet.setOriginalDataFilesExtension(".owl");
	}

	/**
	 * Test method for {@link edu.utdallas.hadooprdf.data.preprocessing.Preprocessor#preprocess()}.
	 * @throws PreprocessorException 
	 */
	@Test
	public void testPreprocess() throws PreprocessorException {
		Preprocessor preprocessor = new Preprocessor(dataSet, SerializationFormat.RDF_XML);
		preprocessor.preprocess();
	}

}
