package edu.utdallas.hadooprdf.accesscontrol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *This program will check a text file as an input. Then it will check whether
 * the groups.xml file exists. groups.xml file contains the groups (roles) and
 * their members. If groups.xml doesn't exist, then it will be created. Then
 * each group in the input file is checked whether it exists in the groups.xml
 * file, and if it does then the member(s) is/are added to the group if it/they
 * doesn't/don't exist. If the group doesn't exist, a new group is created along
 * with the members.
 * 
 * @author arindamkhaled
 */

public class LoadDataXML {

	// Check whether the groups.xml file exists
	String XMLFileName;
	String inputFileName;
	ArrayList<Group> groups;

	public LoadDataXML(String fName, String iName) {
		groups = new ArrayList<Group>();
		XMLFileName = fName;
		inputFileName = iName;
	}

	public boolean checkXMLFileExists() {
		File file = new File(XMLFileName);
		boolean exists = file.exists();
		if (!exists) {
			return false;
		}

		return true;
	}

	// Load the xml file into groups
	public void loadDataFromXML() {
		DomParser DP = new DomParser();
		DP.run(XMLFileName);
		groups = DP.getGroups();
		// System.out.println(groups.toString());
	}

	// Extract the group and member values from the input file
	// Each line in the file should be in the following format: group member
	// [members]
	public ArrayList<Group> loadInputFile() throws FileNotFoundException,
			IOException {
		ArrayList<Group> g = new ArrayList<Group>();

		FileReader fr = new FileReader(inputFileName);
		BufferedReader br = new BufferedReader(fr);
		String line;
		StringTokenizer st;

		while ((line = br.readLine()) != null) {
			st = new StringTokenizer(line, " ");

			String gID = st.nextToken();
			ArrayList<Member> members = new ArrayList<Member>();

			while (st.hasMoreTokens()) {
				members.add(new Member(st.nextToken()));
			}

			g.add(new Group(members, gID));
		}
		// System.out.println(g.toString());
		return g;
	}

	// Merge the new input data with the existing one
	public void mergeNewDataToExisting() throws FileNotFoundException,
			IOException {
		ArrayList<Group> g = new ArrayList<Group>();
		g = loadInputFile();

		for (int i = 0; i < g.size(); i++) {
			String gID = g.get(i).getGroupID();
			boolean gIDExists = false;

			// System.out.println("GID - " + gID);
			for (int k = 0; k < groups.size() && !gIDExists; k++) {
				// System.out.println("groups.get(k).getGroupID() " + k + " " +
				// groups.get(k).getGroupID());
				// Check if the group exists
				if (groups.get(k).getGroupID().equals(gID)) {
					gIDExists = true;
					// System.out.println("Found the group: " + gID + " " +
					// groups.get(k).getGroupID());

					// Check if the each new member exists in the original
					// Only add to the original one it doesn't exist
					for (int n = 0; n < g.get(i).getMembers().size(); n++) {
						Member m = (Member) g.get(i).getMembers().get(n);
						boolean memberExists = false;

						for (int l = 0; l < groups.get(k).getMembers().size()
								&& !memberExists; l++) {
							Member m2 = (Member) groups.get(k).getMembers()
									.get(l);

							if (m.getMember().equals(m2.getMember())) {
								memberExists = true;
							}
						}

						// If the member doesn't exist after iterating through
						// the group member list
						if (!memberExists) {
							groups.get(k).getMembers().add(
									new Member(m.getMember()));
						}
					}
				}

			}
			// if the group doesn't exist
			// Then add the group along with the members
			if (!gIDExists) {
				// System.out.println("adding " + gID);
				groups.add(new Group(g.get(i).getMembers(), gID));
			}
		}
	}

	public void run() throws FileNotFoundException, IOException {
		if (checkXMLFileExists()) {
			loadDataFromXML();
		}

		mergeNewDataToExisting();
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		if (args.length != 1) {
			System.out.println("Usage: groupUserFileName ");
			System.exit(1);
		}

		LoadDataXML data = new LoadDataXML("groups.xml", args[0]);
		data.run();
		XMLCreator xce = new XMLCreator(data.groups);
		xce.run();
	}
}
