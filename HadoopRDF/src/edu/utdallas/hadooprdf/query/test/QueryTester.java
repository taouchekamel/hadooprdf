package edu.utdallas.hadooprdf.query.test;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.data.rdf.uri.prefix.PrefixNamespaceTree;
import edu.utdallas.hadooprdf.lib.util.JobParameters;
import edu.utdallas.hadooprdf.query.generator.job.JobPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlan;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGenerator;
import edu.utdallas.hadooprdf.query.generator.plan.QueryPlanGeneratorFactory;
import edu.utdallas.hadooprdf.query.parser.ConfigPrefixTree;
import edu.utdallas.hadooprdf.query.parser.HadoopElement;
import edu.utdallas.hadooprdf.query.parser.QueryParser;
import edu.utdallas.hadooprdf.query.parser.QueryRewriter;

public class QueryTester 
{
	public static void main (String [] args) throws Exception 
	{	
		String hdfsPath = "/user/farhan/hadooprdf/LUBM1/";
		String ConfgPath = JobParameters.configFileDir;
		
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
			//Get the current job plan
			JobPlan jp = iterJobPlans.next();
			
			//Serialize the job plan to a file
			ObjectOutputStream objstream = new ObjectOutputStream(new FileOutputStream("/home/hadoop/job.txt"));
	        objstream.writeObject(jp);
	        objstream.close();
	        
	        //Transfer the file to the hdfs
			try
			{ 
				edu.utdallas.hadooprdf.conf.Configuration config = edu.utdallas.hadooprdf.conf.Configuration.getInstance();
				org.apache.hadoop.conf.Configuration hadoopConfiguration = new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
			
				FileSystem fs;
				fs = FileSystem.get(hadoopConfiguration); 
				
				fs.delete( new Path( new DataSet( "/user/farhan/hadooprdf/LUBM1").getPathToPOSData(), "job.txt" ), true );
				fs.copyFromLocalFile( new Path( "/home/hadoop/job.txt" ), new DataSet( "/user/farhan/hadooprdf/LUBM1").getPathToPOSData() );
			}
			catch( Exception e ) { e.printStackTrace(); }

			jp.getHadoopJob().waitForCompletion( true );
		}
	}
}