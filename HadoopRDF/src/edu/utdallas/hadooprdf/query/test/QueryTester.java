package edu.utdallas.hadooprdf.query.test;

import java.io.BufferedReader;
import java.io.FileReader;

import edu.utdallas.hadooprdf.data.metadata.DataSet;
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
		queryString = 
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
		
		//LUBM Query 5
		String queryString =
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		" PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +
		" SELECT ?X " +
		" WHERE " +
		" { " +
		"	?X rdf:type ub:Person . " +
		"	?X ub:memberOf <http://www.Department0.University0.edu> " +
		" } ";

		//Create a QueryExecution object
		QueryExecution qexec = QueryExecutionFactory.create(queryString, new DataSet( "/user/farhan/hadooprdf/data/LUBM1" ) );
		
		//Get the output file
		String opFile = qexec.execSelect();

		//Get the results from the output file
		String resultStr = null;
		BufferedReader inReader = new BufferedReader( new FileReader( opFile ) );
		while( ( resultStr = inReader.readLine() ) != null )
		{
			System.out.println( resultStr );
		}
	}
}