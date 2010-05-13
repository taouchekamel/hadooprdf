package edu.utdallas.hadooprdf.data.rdf.uri.prefix.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import edu.utdallas.hadooprdf.data.preprocessing.lib.NamespacePrefixParser;
import edu.utdallas.hadooprdf.data.rdf.uri.prefix.PrefixNamespaceTree;

public class PrefixNamespaceTreeTest extends TestCase  {
	private NamespacePrefixParser npp;
	private PrefixNamespaceTree pnt;
	
	public PrefixNamespaceTreeTest() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("test/PrefixNamespaceTreeTestInput.txt"));
			String sLine;
			sLine = br.readLine();
			br.close();
			npp = new NamespacePrefixParser(sLine);
			pnt = new PrefixNamespaceTree(npp.getNamespacePrefixes());
		} catch (IOException e) {
			fail("testGetLongestCommonPrefixes failed\n" + e.getMessage());
		}
	}
	
	@Test
	public void testMatchAndReplacePrefix() {
		String s = "http://www.University10";
		String sNew = pnt.matchAndReplacePrefix(s);
		if (null == sNew)
			fail("There should be a new string for " + s);
		else
			System.out.println(s + " has new string: " + sNew);
		s = "http://www.University";
		sNew = pnt.matchAndReplacePrefix(s);
		if (null == sNew)
			fail("There should be a new string for " + s);
		else
			System.out.println(s + " has new string: " + sNew);
		s = "http://www.Universit";
		sNew = pnt.matchAndReplacePrefix(s);
		if (null != sNew)
			fail("There should not be a new string for " + s);
		else
			System.out.println(s + " does not have new string");
		s = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Professor";
		sNew = pnt.matchAndReplacePrefix(s);
		if (null == sNew)
			fail("There should be a new string for " + s);
		else
			System.out.println(s + " has new string: " + sNew);
	}
	
	@Test
	public void testMatchPrefix() {
		PrefixNamespaceTree pnt = new PrefixNamespaceTree(npp.getNamespacePrefixes());
		String s = "http://www.University10";
		StringBuffer returnNamespace = new StringBuffer();
		int index = pnt.matchPrefix(s, returnNamespace);
		if (-1 == index)
			fail("There should be an index for " + s);
		else
			System.out.println(s + " has prefix match: " + index + ' ' + returnNamespace);
		s = "http://www.University";
		index = pnt.matchPrefix(s, returnNamespace);
		if (-1 == index)
			fail("There should be an index for " + s);
		else
			System.out.println(s + " has prefix match: " + index + ' ' + returnNamespace);
		s = "http://www.Universit";
		index = pnt.matchPrefix(s, returnNamespace);
		if (-1 != index)
			fail("There should not be an index for " + s);
		else
			System.out.println(s + " has no prefix match: " + index + ' ' + returnNamespace);
		s = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Professor";
		index = pnt.matchPrefix(s, returnNamespace);
		if (-1 == index)
			fail("There should be an index for " + s);
		else
			System.out.println(s + " has prefix match: " + index + ' ' + returnNamespace);
	}

	@Test
	public void testMatchAndReplaceNamespace() {
		String s = "http://www.University10";
		String sNew = pnt.matchAndReplacePrefix(s);
		if (null == sNew)
			fail("There should be a new string for " + s);
		else {
			System.out.println(s + " has new string: " + sNew);
			sNew = pnt.matchAndReplaceNamespace(sNew);
			System.out.println("Reconstructed string: " + sNew);
			assertEquals(s, sNew);
		}
		s = "http://www.University";
		sNew = pnt.matchAndReplacePrefix(s);
		if (null == sNew)
			fail("There should be a new string for " + s);
		else {
			System.out.println(s + " has new string: " + sNew);
			sNew = pnt.matchAndReplaceNamespace(sNew);
			System.out.println("Reconstructed string: " + sNew);
			assertEquals(s, sNew);
		}
		s = "http://www.Universit";
		sNew = pnt.matchAndReplacePrefix(s);
		if (null != sNew)
			fail("There should not be a new string for " + s);
		else
			System.out.println(s + " does not have new string");
		s = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Professor";
		sNew = pnt.matchAndReplacePrefix(s);
		if (null == sNew)
			fail("There should be a new string for " + s);
		else {
			System.out.println(s + " has new string: " + sNew);
			sNew = pnt.matchAndReplaceNamespace(sNew);
			System.out.println("Reconstructed string: " + sNew);
			assertEquals(s, sNew);
		}
	}
}
