package edu.utdallas.hadooprdf.query.test;

import java.util.ArrayList;
import java.util.Iterator;

import edu.utdallas.hadooprdf.data.rdf.uri.prefix.PrefixNamespaceTree;
import edu.utdallas.hadooprdf.query.parser.ConfigPrefixTree;
import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGenerator;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGeneratorFactory;
import edu.utdallas.hadooprdf.query.parser.HadoopElement;
import edu.utdallas.hadooprdf.query.parser.QueryParser;
import edu.utdallas.hadooprdf.query.parser.QueryRewriter;

public class QueryTester 
{
	public static void main (String [] args) throws Exception 
	{	
		String hdfsPath = "/user/sharath/hadooprdf/LUBM1";
		String ConfgPath = "/home/hadoop/sharath/hadooprdf/hadooprdf/hadoopRdfQueryParser/conf/SemanticWebLabCluster";
		
		String queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X ?Y ?Z " +
		" WHERE " +
		" { " +
		" 	?X rdf:type ub:GraduateStudent ." + 
		"	?Y rdf:type ub:University ." + 
		"	?Z rdf:type ub:Department ." +
		"	?X ub:memberOf ?Z ." +
		"	?Z ub:subOrganizationOf ?Y ." +
		"	?X ub:undergraduateDegreeFrom ?Y." +
		" }";
		
		queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X " +
		" WHERE " +
		" { " +
		" 	?X rdf:type ub:GraduateStudent . " +
		"	?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse0> " +
		" } ";
		
		edu.utdallas.hadooprdf.query.parser.Query q = QueryParser.parseQuery(queryString);		
		PrefixNamespaceTree prefixTree = ConfigPrefixTree.getPrefixTree(ConfgPath, hdfsPath, 5); // 5 - Cluster Id		
		ArrayList <HadoopElement> eList1 = (ArrayList<HadoopElement>)QueryRewriter.rewriteQuery(q,prefixTree);

		for( int i = 0; i < eList1.size(); i++ ) 
		{
			ArrayList<HadoopElement.HadoopTriple> triple = eList1.get( i ).getTriple();
			System.out.println( "triple -- " + triple.size() );
			for( int j = 0; j < triple.size(); j++ ) 
			{
				System.out.println( "---------------------------------------------------------------" );
				System.out.println( "Subject -- " + triple.get( j ).getSubject().toString() );
				System.out.println( "Predicate -- " + triple.get( j ).getPredicate().toString() );
				System.out.println( "Object -- " + triple.get( j ).getObject().toString() );
				System.out.println( "---------------------------------------------------------------" );
			}		
		}

		QueryPlanGenerator qpgen = QueryPlanGeneratorFactory.createSimpleQueryPlanGenerator();
		QueryPlan qp = qpgen.generateQueryPlan( eList1 );
		
		Iterator<JobPlan> iterJobPlans = qp.getJobPlans().iterator();
		while( iterJobPlans.hasNext() )
		{
			JobPlan jp = iterJobPlans.next();
			jp.getHadoopJob().waitForCompletion( true );
		}
	}
}