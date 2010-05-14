package edu.utdallas.hadooprdf.accesscontrol;

import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author arindamkhaled
 */
public class Group {
	private ArrayList<Member> members;
	private String groupID;

	public Group(ArrayList<Member> mems, String gID) {
		members = new ArrayList<Member>();
		members.addAll(mems);
		groupID = gID;
	}

	public String getGroupID() {
		return groupID;
	}

	public ArrayList<Member> getMembers() {
		return members;
	}

	public String toString() {
		String statement;

		statement = "GroupID = " + groupID + "\n";

		statement = statement + "Members:\n";

		for (int i = 0; i < members.size(); i++) {
			Member m = (Member) members.get(i);
			statement = statement + m.getMember() + "\n";
		}

		return statement;
	}

}
