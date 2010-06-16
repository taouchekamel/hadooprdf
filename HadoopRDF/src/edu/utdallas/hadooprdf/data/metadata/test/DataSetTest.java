package edu.utdallas.hadooprdf.data.metadata.test;

import static org.junit.Assert.fail;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.metadata.DataSetException;
import edu.utdallas.hadooprdf.data.preprocessing.lib.NamespacePrefixParser.NamespacePrefix;

public class DataSetTest {
	private DataSet m_DataSet;

	@Before
	public void setUp() throws Exception {
		// Create cluster configuration
		org.apache.hadoop.conf.Configuration hadoopConfiguration = new Configuration();
		//String sConfDirectoryPath = "conf/SAIALLabCluster";
		String sConfDirectoryPath = "conf/SemanticWebLabCluster";
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/core-site.xml"));
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/hdfs-site.xml"));
		hadoopConfiguration.addResource(new Path(sConfDirectoryPath + "/mapred-site.xml"));
		m_DataSet = new DataSet(new Path("/user/pankil/hadooprdf/data/LUBM1"), hadoopConfiguration);
	}

	@Test
	public void testGetNamespacePrefixes() throws DataSetException {
		NamespacePrefix [] np = m_DataSet.getNamespacePrefixes();
		System.out.println("Number of namespace-prefix pairs: " + np.length);
		for (int i = 0; i < np.length; i++)
			System.out.println("Pair [" + (i + 1) + "]: " + np[i].getNamespace() + '\t' + np[i].getPrefix());
	}

	@Test
	public void testGetPredicateCollection() {
		try {
			System.out.println("There are " + m_DataSet.getPredicateCollection().size() + " predicates");
			for(String sPredicate : m_DataSet.getPredicateCollection())
				System.out.println(sPredicate);
		} catch (DataSetException e) {
			fail(e.getMessage());
		}
	}

}
