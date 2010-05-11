package edu.utdallas.hadooprdf.test;

import java.util.ArrayList;

import com.hp.hpl.jena.graph.Triple;

import edu.utdallas.hadooprdf.main.ConfigPrefixTree;
import edu.utdallas.hadooprdf.main.HadoopElement;
import edu.utdallas.hadooprdf.main.QueryParser;
import edu.utdallas.hadooprdf.main.QueryRewriter;
import edu.utdallas.hadooprdf.rdf.uri.prefix.PrefixNamespaceTree;

public class TestQueryParser {
	public static void main (String [] args) throws Exception {
		
		String hdfsPath = "/user/sharath/hadooprdf/LUBM1";
		String ConfgPath = "/home/hadoop/sharath/hadooprdf/hadooprdf/hadoopRdfQueryParser/conf/SemanticWebLabCluster";
		
		String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
				"SELECT ?X ?Y ?Z " +
				"WHERE " +
				"{" +
				"?X rdf:type ub:GraduateStudent ." + 
				"?Y rdf:type ub:University ." + 
				"?Z rdf:type ub:Department ." +
				"?X ub:memberOf ?Z ." +
				"?Z ub:subOrganizationOf ?Y ." +
				"?X ub:undergraduateDegreeFrom ?Y." +
				"}";
		
		edu.utdallas.hadooprdf.main.Query q = QueryParser.parseQuery(queryString);		
		PrefixNamespaceTree prefixTree = ConfigPrefixTree.getPrefixTree(ConfgPath, hdfsPath, 5); // 5 - Cluster Id		
		ArrayList <HadoopElement> eList1 = (ArrayList<HadoopElement>)QueryRewriter.rewriteQuery(q,prefixTree);
		
		System.out.println("eList -- " + eList1.size());
		for (int i = 0; i < eList1.size(); i++) {
			ArrayList<HadoopElement.HadoopTriple> triple = eList1.get(i).getTriple();
			System.out.println("triple -- " + triple.size());
			for (int j = 0; j < triple.size(); j++) {
				System.out.println("---------------------------------------------------------------");
				System.out.println("Subject -- " + triple.get(j).getSubject().toString());
				System.out.println("Predicate -- " + triple.get(j).getPredicate().toString());
				System.out.println("Object -- " + triple.get(j).getObject().toString());
				System.out.println("Associated Files -- " + triple.get(j).getAssociatedFiles().toString());
				System.out.println("---------------------------------------------------------------");

			}		
		}

	}
}
