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
public class PredicateIdPairsTest {
	private PredicateIdPairs predIdPairs;
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
		predIdPairs = new PredicateIdPairs(dataSet);
	}

	/**
	 * Test method for {@link edu.utdallas.hadooprdf.data.metadata.PredicateIdPairs#getId(java.lang.String)}.
	 */
	@Test
	public void testGetId() {
		Long expected = Long.parseLong("-4323455642275675903");
		assertEquals("Id and Predicate mismatch", expected, predIdPairs.getId("<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone>"));
		expected = Long.parseLong("432345564227567862");
		assertEquals("Id and Predicate mismatch", expected, predIdPairs.getId("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"));
		expected = Long.parseLong("8791026472627208426");
		assertEquals("Id and Predicate mismatch", expected, predIdPairs.getId("<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#undergraduateDegreeFrom>"));
	}

	/**
	 * Test method for {@link edu.utdallas.hadooprdf.data.metadata.PredicateIdPairs#getPredicate(long)}.
	 */
	@Test
	public void testGetPredicate() {
		String expected = "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#telephone>";
		assertEquals("Id and Predicate mismatch", expected, predIdPairs.getPredicate(Long.parseLong("-4323455642275675903")));
		expected = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
		assertEquals("Id and Predicate mismatch", expected, predIdPairs.getPredicate(Long.parseLong("432345564227567862")));
		expected = "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#undergraduateDegreeFrom>";
		assertEquals("Id and Predicate mismatch", expected, predIdPairs.getPredicate(Long.parseLong("8791026472627208426")));
	}

}
