package edu.utdallas.hadooprdf.query.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class JenaTester 
{
	public static void main( String[] args )
	{
		Model m = ModelFactory.createDefaultModel();
		try
		{
			m.read( new BufferedReader( new InputStreamReader(  new FileInputStream( new File( "/home/hadoop/dbpedia/persondata_en.nt" ) ) ) ), null, "N-TRIPLE" );
			m.read( new BufferedReader( new InputStreamReader(  new FileInputStream( new File( "/home/hadoop/dbpedia/homepages_en.nt" ) ) ) ), null, "N-TRIPLE" );

			System.out.println( m.size() );
			String queryString = 
				" PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
				" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				" SELECT ?X ?Y ?Z " +
				" WHERE " +
				" { " +
				"	?X foaf:name ?Y . "+
				"	?X foaf:homepage ?Z . "+
				" } "; 

			QueryExecution qe = QueryExecutionFactory.create( queryString, m );
			ResultSet rs = qe.execSelect();
			//ResultSetFormatter.out( System.out, rs );
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( ( new File( "/home/hadoop/dbpedia/jena_test.txt" ) ) ) ) );
			while( rs.hasNext() )
			{
				QuerySolution qs = rs.nextSolution();
				Resource sub = qs.getResource( "?X" );
				RDFNode obj1 = qs.getLiteral( "?Y" );
				Resource obj2 = qs.getResource( "?Z" );
				writer.write( sub.toString() + "\t" + "\"" + obj1.asNode().getLiteralValue() + "\"" + "@en" + "\t" + "<" + obj2 + ">" + "\n" ); writer.flush();
			}
			writer.close();
		}
		catch( Exception e ) { e.printStackTrace(); }
	}
}
