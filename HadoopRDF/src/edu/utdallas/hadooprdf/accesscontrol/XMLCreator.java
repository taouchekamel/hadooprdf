package edu.utdallas.hadooprdf.accesscontrol;

/*
 * To change this template, choose Tools | 
 * and open the template in the editor.
 */

/**
 *
 * @author arindamkhaled
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

//For jdk1.5 with built in xerces parser
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XMLCreator {
	// No generics

	List<Group> myData;
	Document dom;

	public XMLCreator(ArrayList<Group> groups) {
		// create a list to hold the data
		myData = new ArrayList<Group>();

		// initialize the list
		loadData(groups);

		// Get a DOM object
		createDocument();
	}

	public void run() {
		System.out.println("Started .. ");
		createDOMTree();
		printToFile();
		System.out.println("Generated file successfully.");
	}

	/**
	 * Add a list of books to the list In a production system you might populate
	 * the list from a DB
	 */
	private void loadData(ArrayList<Group> groups) {
		ArrayList<Member> members = new ArrayList<Member>();

		for (int i = 0; i < 5; i++) {
			Member m = new Member(Integer.toString(i));
			members.add(m);
		}

		for (int i = 0; i < groups.size(); i++) {
			myData.add(groups.get(i));
		}
		// myData.add(new Group(members, "Developers"));
		// myData.add(new Group(members, "punks"));

		// myData.add(new Book("Head First Design Patterns",
		// "Kathy Sierra .. etc", "Java Architect"));
	}

	/**
	 * Using JAXP in implementation independent manner create a document object
	 * using which we create a xml tree in memory
	 */
	private void createDocument() {

		// get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// create an instance of DOM
			dom = db.newDocument();

		} catch (ParserConfigurationException pce) {
			// dump it
			System.out
					.println("Error while trying to instantiate DocumentBuilder "
							+ pce);
			System.exit(1);
		}

	}

	/**
	 * The real workhorse which creates the XML structure
	 */
	private void createDOMTree() {

		// create the root element <Books>
		Element rootEle = dom.createElement("Groups");
		dom.appendChild(rootEle);

		// No enhanced for
		Iterator<Group> it = myData.iterator();
		while (it.hasNext()) {
			Group b = (Group) it.next();
			// For each Book object create <Book> element and attach it to root
			Element groupEle = createBookElement(b);
			rootEle.appendChild(groupEle);
		}
	}

	/**
	 * Helper method which creates a XML element <Member>
	 * 
	 * @param b
	 *            The book for which we need to create an xml representation
	 * @return XML element snippet representing a book
	 */
	private Element createBookElement(Group g) {

		Element groupEle = dom.createElement("Group");
		groupEle.setAttribute("ID", g.getGroupID());

		// create Member elements and attach them to groupEle

		for (int i = 0; i < g.getMembers().size(); i++) {
			Element memberEle = dom.createElement("Member");
			Member m = (Member) g.getMembers().get(i);
			Text memberName = dom.createTextNode(m.getMember());
			memberEle.appendChild(memberName);
			groupEle.appendChild(memberEle);
			// System.out.println(memberEle);
		}

		return groupEle;

	}

	/**
	 * This method uses Xerces specific classes prints the XML document to file.
	 */
	private void printToFile() {

		try {
			// print
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			// to generate output to console use this serializer
			// XMLSerializer serializer = new XMLSerializer(System.out, format);

			// to generate a file output use fileoutputstream instead of
			// system.out
			XMLSerializer serializer = new XMLSerializer(new FileOutputStream(
					new File("groups.xml")), format);

			serializer.serialize(dom);

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public static void main(String args[]) {

		// create an instance
		// XMLCreator xce = new XMLCreator();

		// run the example
		// xce.run();
	}
}
