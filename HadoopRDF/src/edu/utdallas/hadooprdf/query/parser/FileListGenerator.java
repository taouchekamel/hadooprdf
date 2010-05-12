package edu.utdallas.hadooprdf.query.parser;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

import edu.utdallas.hadooprdf.data.commons.Constants;
import edu.utdallas.hadooprdf.data.metadata.DataSet;
import edu.utdallas.hadooprdf.lib.util.PathFilterOnFilenameExtension;

class FileListGenerator {
	private OWLOntologyManager mManager = OWLManager.createOWLOntologyManager();
	private Reasoner mReasoner = new Reasoner(mManager);
	
	public FileListGenerator (edu.utdallas.hadooprdf.query.parser.Query query) throws OWLOntologyCreationException {

		Set <String> keys = query.getNsPrefixKeySet();
		Iterator <String> keySet = keys.iterator();
		while (keySet.hasNext()) {
			String key = keySet.next();
			String ontologyURI = query.getNsPrefix(key);
			System.out.println (ontologyURI);
			try {
				mReasoner.loadOntology(mManager.loadOntology(URI.create(ontologyURI)));
			} catch (OWLOntologyCreationException e) {
				throw e;
			}			
		}
		
		mReasoner.getKB().realize();
	}
	
	public List<String> getFilesAssociatedWithTriple (String uri, String classAssociated, String prefix)
	{
		ArrayList<String> files = new ArrayList<String> ();
		
		if (uri.contains(".owl") == false) 
		{
			//files.add(prefix + ".pos");
			//return files;

			try
			{ 
				edu.utdallas.hadooprdf.conf.Configuration config = edu.utdallas.hadooprdf.conf.Configuration.getInstance();
				org.apache.hadoop.conf.Configuration hadoopConfiguration = new org.apache.hadoop.conf.Configuration(config.getHadoopConfiguration()); // Should create a clone so
			
				FileSystem fs;
				fs = FileSystem.get(hadoopConfiguration); 
				
				//TODO: This DataSet should come from the controller
				FileStatus [] fstatus = fs.listStatus( new DataSet( "/user/farhan/hadooprdf/LUBM1" ).getPathToPOSData(), new PathFilterOnFilenameExtension(Constants.POS_EXTENSION) );
				for (int i = 0; i < fstatus.length; i++) 
				{
					if (!fstatus[i].isDir()) 
					{
						if( fstatus[i].getPath().getName().toString().startsWith( prefix ) )
							files.add( fstatus[i].getPath().getName().toString() );
					}
				}
			}
			catch( Exception e ) { e.printStackTrace(); }
			return files;
		}
		
		OWLDescription mpredClass;
		mpredClass = mManager.getOWLDataFactory().getOWLClass(URI.create(uri));
		
		Set<OWLOntology> ontSet = mManager.getOntologies ();
		
		Iterator <OWLOntology> ontIt = ontSet.iterator ();
		boolean isFilesAdded = false;
		while (ontIt.hasNext ()) {
			OWLOntology ont = ontIt.next ();
			Set<OWLDescription> descSet = mpredClass.asOWLClass ().getSubClasses (ont);
			Iterator <OWLDescription> descIt = descSet.iterator ();
			while (descIt.hasNext ()) {
				OWLDescription dsc =  descIt.next ();
				String fileName = prefix.substring(0, prefix.lastIndexOf("#")) + "#" + dsc + ".pos";	
				files.add (fileName);	
				isFilesAdded = true;
			}			
		}		
		
		if (isFilesAdded == false) {
			files.add(prefix + ".pos");
		}
		
		return files;
	}
}
