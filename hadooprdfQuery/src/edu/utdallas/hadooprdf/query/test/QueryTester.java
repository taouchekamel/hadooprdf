package edu.utdallas.hadooprdf.query.test;

import java.util.ArrayList;

import com.hp.hpl.jena.graph.Triple;

import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGenerator;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGeneratorFactory;
import edu.utdallas.hadooprdf.query.parser.HadoopElement;
import edu.utdallas.hadooprdf.query.parser.NotBasicElementException;
import edu.utdallas.hadooprdf.query.parser.QueryParser;
import edu.utdallas.hadooprdf.query.parser.UnhandledElementException;

public class QueryTester 
{
	public static void main( String[] args ) throws UnhandledElementException, NotBasicElementException 
	{
		String queryString = 
			" PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
			" SELECT ?url " +
			" WHERE" +
			" { " +
			"	?contributor foaf:name ?y . " +
			"   ?y foaf:weblog ?url . " +
			" } ";

		ArrayList <HadoopElement> eList = QueryParser.parseQuery(queryString);

		for (int i = 0; i < eList.size(); i++) 
		{
			HadoopElement element = eList.get(i);
			ArrayList<Triple> tripleList = element.getTriple();
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
