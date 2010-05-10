package edu.utdallas.hadooprdf.query.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Triple;

import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGenerator;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGeneratorFactory;
import edu.utdallas.hadooprdf.query.parser.HadoopElement;
import edu.utdallas.hadooprdf.query.parser.NotBasicElementException;
import edu.utdallas.hadooprdf.query.parser.QueryParser;
import edu.utdallas.hadooprdf.query.parser.UnhandledElementException;
import edu.utdallas.hadooprdf.query.parser.HadoopElement.HadoopTriple;

public class QueryTester 
{
	public static void main( String[] args ) throws UnhandledElementException, NotBasicElementException, IOException, Exception 
	{
		String queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X ?Y ?Z " +
		" WHERE " +
		" { " +
		"		?X rdf:type ub:GraduateStudent . " +
		" 		?Y rdf:type ub:University . " +
		" 		?Z rdf:type ub:Department . " +
		"		?X ub:memberOf ?Z . " +
		"		?Y ub:subOrganizationOf ?Y . " +
		"		?X ub:undergraduateDegreeFrom ?Y " +
		" } " ; 

/*		queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X ?Y " +
		" WHERE " +
		" { " +
		"		?X rdf:type ub:Chair . " +
		" 		?Y rdf:type ub:Department . " +
		"		?X ub:worksFor ?Y . " +
		"		?Y ub:subOrganizationOf <http://www.University0.edu> " +
		" } " ; 		
*/		
		List <HadoopElement> eList = QueryParser.parseQuery(queryString);

		for (int i = 0; i < eList.size(); i++) 
		{
			HadoopElement element = eList.get(i);
			ArrayList<HadoopTriple> tripleList = element.getTriple();
			for (int j = 0; j < tripleList.size(); j++) 
			{
				Triple triple = tripleList.get(j);
				System.out.println("-------------------------------------------------------");
				System.out.println("Subject -- " + triple.getSubject().toString());
				System.out.println("Predicate -- "  + triple.getPredicate().toString());
				System.out.println("Object -- " + triple.getObject().toString());
				System.out.println("-------------------------------------------------------");
			}
		}
		
		QueryPlanGenerator qpg = QueryPlanGeneratorFactory.createSimpleQueryPlanGenerator();
		qpg.generateQueryPlan( eList );
	}
}