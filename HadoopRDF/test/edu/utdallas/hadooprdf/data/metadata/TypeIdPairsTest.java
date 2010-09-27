/**
 * 
 */
package edu.utdallas.hadooprdf.data.metadata;

import static org.junit.Assert.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class TypeIdPairsTest {
	private TypeIdPairs typeIdPairs;
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
		DataSet dataSet = new DataSet(new Path("/user/farhan/hadooprdf/LUBM1"), hadoopConfiguration);
		typeIdPairs = new TypeIdPairs(dataSet);
	}

	/**
	 * Test method for {@link edu.utdallas.hadooprdf.data.metadata.PredicateIdPairs#getId(java.lang.String)}.
	 */
	@Test
	public void testGetId() {
		Long expected = Long.parseLong("-4611686018427387664");
		assertEquals("Id and Type mismatch", expected, typeIdPairs.getId("<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Department>"));
		expected = Long.parseLong("4179340454199820563");
		assertEquals("Id and Type mismatch", expected, typeIdPairs.getId("<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#TeachingAssistant>"));
		expected = Long.parseLong("864691128455135522");
		assertEquals("Id and Type mismatch", expected, typeIdPairs.getId("<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Lecturer>"));
	}

	/**
	 * Test method for {@link edu.utdallas.hadooprdf.data.metadata.PredicateIdPairs#getPredicate(long)}.
	 */
	@Test
	public void testGetType() {
		String expected = "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#UndergraduateStudent>";
		assertEquals("Id and Type mismatch", expected, typeIdPairs.getPredicate(Long.parseLong("-7205759403792793344")));
		expected = "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#GraduateStudent>";
		assertEquals("Id and Type mismatch", expected, typeIdPairs.getPredicate(Long.parseLong("5476377146882523366")));
		expected = "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#University>";
		assertEquals("Id and Type mismatch", expected, typeIdPairs.getPredicate(Long.parseLong("-6341068275337658137")));
	}
}
