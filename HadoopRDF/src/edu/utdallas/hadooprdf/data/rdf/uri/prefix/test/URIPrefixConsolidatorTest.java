/**
 * 
 */
package edu.utdallas.hadooprdf.data.rdf.uri.prefix.test;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import edu.utdallas.hadooprdf.data.rdf.uri.prefix.URIPrefixConsolidator;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class URIPrefixConsolidatorTest {

	/**
	 * Test method for {@link edu.utdallas.hadooprdf.lib.util.prefixtree.rdf.uri.prefix.PrefixTree#getLongestCommonPrefixes()}.
	 */
	@Test
	public void testGetLongestCommonPrefixes() {
		URIPrefixConsolidator upc = new URIPrefixConsolidator("p");
		try {
			BufferedReader br = new BufferedReader(new FileReader("test/PrefixConsolidatorTestInput.txt"));
			String sLine, sPair;
			while (null != (sLine = br.readLine())) {
				sLine = sLine.substring(1, sLine.length() - 1);
				String splits [] = sLine.split(",");
				for (int i = 0; i < splits.length; i++) {
					sPair = splits[i].trim();
					int index = sPair.indexOf('=');
					upc.addPrefixAndReplacementString(sPair.substring(index + 1), sPair.substring(0, index));
				}
			}
			br.close();
			System.out.println(upc.getLongestCommonPrefixes());
		} catch (IOException e) {
			fail("testGetLongestCommonPrefixes failed\n" + e.getMessage());
		}
	}

}
