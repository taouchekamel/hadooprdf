package edu.utdallas.hadooprdf.controller.test;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import edu.utdallas.hadooprdf.controller.HadoopRDF;
import edu.utdallas.hadooprdf.controller.HadoopRDFException;

/**
 * A test class for the controller class of the framework
 * @author Mohammad Farhan Husain
 *
 */
public class HadoopRDFTest {
	private HadoopRDF m_HadoopRDF;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		m_HadoopRDF = new HadoopRDF("conf/SemanticWebLabCluster", "/user/farhan/hadooprdf");
	}

	/**
	 * Test method for {@link edu.utdallas.hadooprdf.controller.HadoopRDF#getDataSetMap()}.
	 */
	@Test
	public void testGetDataSetMap() {
		try {
			System.out.println("There are " + m_HadoopRDF.getDataSetMap().size()
					+ " datasets in the store");
			System.out.println("DataSets: " + m_HadoopRDF.getDataSetMap().keySet());
		} catch (HadoopRDFException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}

}
