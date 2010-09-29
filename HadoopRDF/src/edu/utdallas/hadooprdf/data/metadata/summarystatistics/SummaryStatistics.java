/**
 * 
 */
package edu.utdallas.hadooprdf.data.metadata.summarystatistics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class SummaryStatistics {
	public static final String ROOT_ELEMENT = "SummaryStatistics";
	public static final String FILE_ELEMENT = "File";
	public static final String NAME_ATTRIBUTE = "Name";
	public static final String PATH_ATTRIBUTE = "Path";
	public static final String LARGEST_SUBJECT_ID_ELEMENT = "LargestSubjectId";
	public static final String SMALLEST_SUBJECT_ID_ELEMENT = "SmallestSubjectId";
	public static final String NUMBER_OF_RECORDS_ELEMENT = "NumberOfRecords";
	
	private Path pathToSummaryStatisticsFile;
	private Configuration hadoopConfiguration;
	private Map<String, FileStatistics> fileStatistics;
	
	public SummaryStatistics(Configuration hadoopConfiguration, Path pathToSummaryStatisticsFile, boolean createIfNecessary) throws SummaryStatisticsException {
		this.pathToSummaryStatisticsFile = pathToSummaryStatisticsFile;
		this.hadoopConfiguration = hadoopConfiguration;
		fileStatistics = new HashMap<String, FileStatistics> ();
		parseFile(createIfNecessary);
	}
	
	private void parseFile(boolean createIfNecessary) throws SummaryStatisticsException {
		try {
			FileSystem fs = FileSystem.get(hadoopConfiguration);
			if (!fs.exists(pathToSummaryStatisticsFile)) {
				if (createIfNecessary)
					return;
				else
					throw new SummaryStatisticsFileDoesNotExistException("Summary statistic file \"" + pathToSummaryStatisticsFile.toString()
						+ "\" does not exist");
			}
			DataInputStream dis = fs.open(pathToSummaryStatisticsFile);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dis);
			NodeList fileElements = doc.getElementsByTagName(FILE_ELEMENT);
			int elements = fileElements.getLength();
			for (int i = 0; i < elements; i++) {
				FileStatistics fstat = getFileStatisticsFromNode(fileElements.item(i));
				fileStatistics.put(fstat.getFileName(), fstat);
			}
			dis.close();
		} catch (IOException e) {
			throw new SummaryStatisticsException("IOException occurred during parsing summary statistics file because\n" + e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new SummaryStatisticsException("ParserConfigurationException occurred during parsing summary statistics file because\n" + e.getMessage());
		} catch (SAXException e) {
			throw new SummaryStatisticsException("SAXException occurred during parsing summary statistics file because\n" + e.getMessage());
		}
	}
	
	private FileStatistics getFileStatisticsFromNode(Node element) {
		String fileName = null;
		String filePath = null;
		long largestSubjectId = 0;
		long smallestSubjectId = 0;
		long numberOfRecords = 0;
		
		NamedNodeMap nnm = element.getAttributes();
		Node node = nnm.getNamedItem(NAME_ATTRIBUTE);
		if (null != node)
			fileName = node.getNodeValue();
		node = nnm.getNamedItem(PATH_ATTRIBUTE);
		if (null != node)
			filePath = node.getNodeValue();
		NodeList nodeList = element.getChildNodes();
		int children = nodeList.getLength();
		for (int i = 0; i < children; i++) {
			node = nodeList.item(i);
			String tagName = node.getNodeName();
			if (tagName.equals(LARGEST_SUBJECT_ID_ELEMENT))
				largestSubjectId = Long.parseLong(node.getFirstChild().getNodeValue());
			else if (tagName.equals(SMALLEST_SUBJECT_ID_ELEMENT))
				smallestSubjectId = Long.parseLong(node.getFirstChild().getNodeValue());
			else
				numberOfRecords = Long.parseLong(node.getFirstChild().getNodeValue());
			
		}
		return new FileStatistics(fileName, filePath, largestSubjectId, smallestSubjectId, numberOfRecords);
	}
	
	public void persist() throws SummaryStatisticsException {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = doc.createElement(ROOT_ELEMENT);
			doc.appendChild(root);
			for (FileStatistics fstat : fileStatistics.values())
				root.appendChild(createFileStatisticsNode(doc, fstat));
			Source source = new DOMSource(doc);
			FileSystem fs = FileSystem.get(hadoopConfiguration);
			DataOutputStream dos = fs.create(pathToSummaryStatisticsFile, true);
			Result result = new StreamResult(dos);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			//xformer.setOutputProperty("indent", "yes");
	        xformer.transform(source, result);
			dos.close();
		} catch (ParserConfigurationException e) {
			throw new SummaryStatisticsException("ParserConfigurationException occurred during creating new DOM document because\n" + e.getMessage());
		} catch (IOException e) {
			throw new SummaryStatisticsException("IOException occurred during writing summary statistics because\n" + e.getMessage());
		} catch (TransformerConfigurationException e) {
			throw new SummaryStatisticsException("TransformerConfigurationException occurred during writing summary statistics because\n" + e.getMessage());
		} catch (TransformerFactoryConfigurationError e) {
			throw new SummaryStatisticsException("TransformerFactoryConfigurationError occurred during writing summary statistics because\n" + e.getMessage());
		} catch (TransformerException e) {
			throw new SummaryStatisticsException("TransformerException occurred during writing summary statistics because\n" + e.getMessage());
		}
	}
	
	private Element createFileStatisticsNode(Document doc, FileStatistics fstat) {
		Element fse = doc.createElement(FILE_ELEMENT);
		fse.setAttribute(NAME_ATTRIBUTE, fstat.getFileName());	// Name attribute
		fse.setAttribute(PATH_ATTRIBUTE, fstat.getFilePath());	// Path attribute
		Element elem = doc.createElement(LARGEST_SUBJECT_ID_ELEMENT);	// Largest subject id element
		Text text = doc.createTextNode(Long.toString(fstat.getLargestSubjectId()));
		elem.appendChild(text);
		fse.appendChild(elem);
		elem = doc.createElement(SMALLEST_SUBJECT_ID_ELEMENT);			// Smallest subject id element
		text = doc.createTextNode(Long.toString(fstat.getSmallestSubjectId()));
		elem.appendChild(text);
		fse.appendChild(elem);
		elem = doc.createElement(NUMBER_OF_RECORDS_ELEMENT);			// Number of records element
		text = doc.createTextNode(Long.toString(fstat.getNumberOfRecords()));
		elem.appendChild(text);
		fse.appendChild(elem);
		return fse;
	}
	
	public Collection<FileStatistics> getAllFileStatistics() {
		return fileStatistics.values();
	}
	
	public FileStatistics getFileStatistics(String fileName) {
		return fileStatistics.get(fileName);
	}
	
	public void addFileStatistics(FileStatistics fstat) {
		fileStatistics.put(fstat.getFileName(), fstat);
	}
	
	public void addFileStatistics(String fileName, String filePath,
			long largestSubjectId, long smallestSubjectId, long numberOfRecords) {
		addFileStatistics(new FileStatistics(fileName, filePath, largestSubjectId, smallestSubjectId, numberOfRecords));
	}
}
