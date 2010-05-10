package edu.utdallas.hadooprdf.data.lib.mapred.serialization.conversion;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.utdallas.hadooprdf.data.commons.Tags;

/**
 * The reducer class for converting RDF data from one serialization format
 * to other. It uses Jena to do the conversion.
 * @author Mohammad Farhan Husain
 *
 */
public class ConversionReducer extends Reducer<Text, Text, Text, Text> {
	/**
	 * Class members
	 */
	private String m_sBaseURI;		// The base URI
	private String m_sInputFormat;	// The input serialization format
	private String m_sOutputFormat;	// The output serialization format
	
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		m_sBaseURI = context.getConfiguration().get(Tags.INPUT_BASE_URI);
		m_sInputFormat = context.getConfiguration().get(Tags.INPUT_SERIALIZATION_FORMAT);
		m_sOutputFormat = context.getConfiguration().get(Tags.OUTPUT_SERIALIZATION_FORMAT);
	}


	@Override
	protected void reduce(Text key, Iterable<Text> values,
			org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		// Create a TreeMap
		Map<Integer, String> lineMap = new TreeMap<Integer, String> ();
		// Iterate over all the values and insert in the TreeMap having the line number as the key
		int iLineNumber, index;
		String sValue;
		Iterator<Text> iterValues = values.iterator();
		while (iterValues.hasNext()) {
			sValue = iterValues.next().toString();
			index = sValue.indexOf('\t');
			iLineNumber = Integer.parseInt(sValue.substring(0, index));
			lineMap.put(iLineNumber, sValue.substring(index + 1));
		}
		// Create a string buffer
		StringBuffer inputFileContent = new StringBuffer();
		// Iterate over all the values in the TreeMap and append to string buffer to reconstruct the content
		// This values should be provided in the ascending order by their corresponding keys as TreeMap
		// implements SortedMap interface
		Iterator<String> iterMap = lineMap.values().iterator();
		while (iterMap.hasNext()) {
			inputFileContent.append(iterMap.next());
			inputFileContent.append('\n');
		}
		// Create a string reader
		StringReader sr = new StringReader(inputFileContent.toString());
		// Mark the string buffer as null though it should be unnecessary for the garbage collector
		inputFileContent = null;
		// Create a default model and read from the string reader
		Model model = ModelFactory.createDefaultModel();
		model.read(sr, m_sBaseURI, m_sInputFormat);
		// Model is built so no need for the string reader
		sr.close();
		sr = null;
		// Create a string writer to dump the converted content
		StringWriter sw = new StringWriter();
		model.write(sw, m_sOutputFormat);
		// Close the model as the content is already written to the string writer
		model.close();
		model = null;
		// Output the content
		context.write(key, new Text(sw.toString()));
		// Close the string writer and mark it null
		sw.close();
		sw = null;
	}
}
