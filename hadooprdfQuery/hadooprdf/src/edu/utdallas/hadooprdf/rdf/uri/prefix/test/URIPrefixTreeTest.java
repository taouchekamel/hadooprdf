package edu.utdallas.hadooprdf.rdf.uri.prefix.test;

import junit.framework.TestCase;

import org.junit.Test;

import edu.utdallas.hadooprdf.rdf.uri.prefix.InvalidURIException;
import edu.utdallas.hadooprdf.rdf.uri.prefix.URIPrefixTree;

public class URIPrefixTreeTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void testPrefixTree() {
		URIPrefixTree tree = new URIPrefixTree("p");
		try {
			tree.addURI("http://www.abcd");
			tree.addURI("http://www.abce");
			tree.addURI("bbce");
			tree.addURI("http://www.bdce");
			tree.addURI("e");
			tree.addURI("http://www.bdcf");
			tree.addURI("ftp://mnop");
			tree.addURI("ftp://mnopq");
			System.out.print("The tree using toString\n" + tree.toString('-'));
			System.out.println(tree.getLongestCommonPrefixes());
		} catch (InvalidURIException e) {
			fail(e.getMessage());
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
