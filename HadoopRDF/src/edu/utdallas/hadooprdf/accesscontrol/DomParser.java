package edu.utdallas.hadooprdf.accesscontrol;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author arindamkhaled
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomParser {


	//No generics
	List groups;
	Document dom;


	public DomParser(){
		//create a list to hold the employee objects
		groups = new ArrayList();
	}

	public void run(String fileName) {

		//parse the xml file and get the dom object
		parseXmlFile(fileName);

		//get each employee element and create a Employee object
		parseDocument();

		//Iterate through the list and print the data
//		printData();

	}

        public ArrayList getGroups()
        {
            return (ArrayList) groups;
        }


	private void parseXmlFile(String fileName){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(fileName);


		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}


	private void parseDocument(){
		//get the root elememt
		Element docEle = dom.getDocumentElement();

		//get a nodelist of <employee> elements
		NodeList nl = docEle.getElementsByTagName("Group");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				//get the employee element
				Element el = (Element)nl.item(i);


				//get the Employee object
				Group e = getGroup(el);

				//add it to list
				groups.add(e);
			}
		}
	}



	private Group getGroup(Element g) {
		ArrayList members = getTextValue(g,"Member");

		Group gr = new Group(members, g.getAttribute("ID"));

		return gr;
	}


	private ArrayList getTextValue(Element ele, String tagName) {
		Member value = null;
                ArrayList members = new ArrayList();
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {

                    for(int i = 0; i < nl.getLength(); i++)
                    {
                        Element e = (Element)nl.item(i);
                        value = new Member(e.getFirstChild().getNodeValue());
                        members.add(value);
                    }
		}

		return members;
	}


	private void printData(){

		System.out.println("No of Groups '" + groups.size() + "'.");

		Iterator it = groups.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}


	public static void main(String[] args){
		//create an instance
		DomParser dpe = new DomParser();

		//call run example
//		dpe.run();
	}


}
