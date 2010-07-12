package edu.utdallas.hadooprdf.query.test;

import java.io.BufferedReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.lib.util.JobParameters;
import edu.utdallas.hadooprdf.query.QueryExecution;
import edu.utdallas.hadooprdf.query.QueryExecutionFactory;

public class QueryTester 
{
	public static void main (String [] args) throws Exception 
	{	
/*		//LUBM Query 1
		String queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X " +
		" WHERE " +
		" { " +
		" 	?X rdf:type ub:GraduateStudent . " +
		"	?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse0> " +
		" } ";
*/		
/*		//LUBM Query 2
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
*/
/*		//LUBM Query 3
		String queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X " +
		" WHERE " +
		" { " +
		" 	?X rdf:type ub:Publication . " +
		"	?X ub:publicationAuthor <http://www.Department0.University0.edu/AssistantProfessor0> " +
		" } "; 
*/		
/*		//LUBM Query 4
		String queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X ?Y1 ?Y2 ?Y3 " +
		" WHERE " +
		" { " +
		"	?X rdf:type ub:Professor . " +
		"	?X ub:worksFor <http://www.Department0.University0.edu> . " +
		"	?X ub:name ?Y1 . " +
		"	?X ub:emailAddress ?Y2 . " +
		"	?X ub:telephone ?Y3 " +
		" } "; 
*/
/*		//LUBM Query 5
		String queryString =
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X " +
		" WHERE " +
		" { " +
		"	?X rdf:type ub:Person . " +
		"	?X ub:memberOf <http://www.Department0.University0.edu> " +
		" } ";
*/
/*		//LUBM Query 6
		String queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X WHERE { ?X rdf:type ub:Student } "; 	
*/
/*		//LUBM Query 7
		String queryString =
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X ?Y " +
		" WHERE " +
		" { " +
		"	?X rdf:type ub:Student . " +
		" 	?Y rdf:type ub:Course . " +
		"	?X ub:takesCourse ?Y . " +
		" 	<http://www.Department0.University0.edu/AssociateProfessor0> ub:teacherOf ?Y " +
		" } "; 
*/
/*		//LUBM Query 8
		String queryString =
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X ?Y ?Z " +
		" WHERE " +
		" { " +
		"	?X rdf:type ub:Student . " +	
		"	?Y rdf:type ub:Department . " +
		" 	?X ub:memberOf ?Y . " +
		"	?Y ub:subOrganizationOf <http://www.University0.edu> . " +
		"	?X ub:emailAddress ?Z " +
		" } ";
*/	
/*		//LUBM Query 9
		String queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X ?Y ?Z " +
		" WHERE " +
		" { " +
		"	?X rdf:type ub:Student . " +
		"	?Y rdf:type ub:Faculty . " +
		"	?Z rdf:type ub:Course . " +
		"	?X ub:advisor ?Y . " +
		"	?Y ub:teacherOf ?Z . " +
		"	?X ub:takesCourse ?Z " +
		" } "; 
*/		
/*		//LUBM Query 10
		String queryString =
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X " +
		" WHERE " +
		" { " +
		"	?X rdf:type ub:Student . " +
		"	?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse0> " +
		" } ";
*/		
/*		//LUBM Query 11
		String queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X " +
		" WHERE " +
		" { " +
		"	?X rdf:type ub:ResearchGroup . " +
		"   ?X ub:subOrganizationOf ?Y . " +
		"	?Y ub:subOrganizationOf <http://www.University0.edu> " +
		" } ";
*/		
/*		//LUBM Query 12
		String queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X ?Y " +
		" WHERE " +
		" { " +
//		"	?X rdf:type ub:Chair . " +
		" 	?X rdf:type ub:Professor . " +
		"  	?X ub:headOf ?Y . " +
		"	?Y rdf:type ub:Department . " +
		"	?X ub:worksFor ?Y . " +
		"	?Y ub:subOrganizationOf <http://www.University0.edu> " +
		" } "; 
*/	
/*		//LUBM Query 13
		String queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X " +
		" WHERE " +
		" { " +
		"	?X rdf:type ub:Person . " +
		"	<http://www.University0.edu> ub:hasAlumnus ?X " +
		" } " ; 
*/
/*		//LUBM Query 14
		String queryString = 
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X " +
		" WHERE " +
		" { " +
		"	?X rdf:type ub:UndergraduateStudent " +
		" } "; 
*/
		/*//DBPEDIA SAMPLE
		String queryString =
		" PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" SELECT ?X ?Y ?Z " +
		" WHERE " +
		" { " +
		"	?X foaf:name ?Y . "+
		"	?X foaf:homepage ?Z "+
		" } ";*/
		
		String queryString =
			" PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
			" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			" SELECT ?P ?Q" +
			" WHERE " +
			" { " +
			"	<http://dbpedia.org/resource/Dar_Robinson> <http://dbpedia.org/property_birthPlace> ?P ."+
			"	<http://dbpedia.org/resource/Dar_Robinson> <http://dbpedia.org/property_deathPlace> ?Q ."+
			" } ";
		
		//Create the Hadoop configuration to be used
		//TODO: This should be moved to the QueryExecution ??
		Configuration config = new Configuration();
		config.addResource( new Path( JobParameters.configFileDir + "/core-site.xml" ) );
		config.addResource( new Path( JobParameters.configFileDir + "/mapred-site.xml" ) );
		config.addResource( new Path( JobParameters.configFileDir + "/hdfs-site.xml" ) );
		edu.utdallas.hadooprdf.conf.Configuration.createInstance( config, "/user/pankil/hadooprdf" );

		//Create a QueryExecution object
		QueryExecution qexec = QueryExecutionFactory.create( queryString, new DataSet( new Path("/user/pankil/hadooprdf/data/DBPEDIA"), config ) );
		
		//Get the output file
		BufferedReader resReader = qexec.execSelect();

		//Get the results from the reader
		String resultStr = null; int count = 0;
		while( ( resultStr = resReader.readLine() ) != null )
		{
			count++;
			System.out.println( resultStr );
		}
		System.out.println( "count of results = " + count );
	}
}