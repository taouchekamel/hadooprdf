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
public class ExtractGroupNames {
    ArrayList <String> groups;
    String fileName;
    String user;

    public ExtractGroupNames(String fname, String usr)
    {
        groups = new ArrayList();
        fileName = fname;
        user = usr;
    }

    public void run()
    {
        DomParser DP = new DomParser();
        DP.run(fileName);
        ArrayList <Group> allGroups = new ArrayList();
        allGroups = DP.getGroups();
//        System.out.println(allGroups.size());

        for(int i = 0; i < allGroups.size(); i++)
        {
//            groups.add(allGroups.get(i).getGroupID());
//            System.out.println(allGroups.get(i).toString());
            for(int k = 0; k < allGroups.get(i).getMembers().size(); k++)
            {
//                System.out.println(((Member)allGroups.get(i).getMembers().get(k)).getMember());
                if(((Member)allGroups.get(i).getMembers().get(k)).getMember().equals(user))
                {
                    groups.add(allGroups.get(i).getGroupID());
//                    System.out.println(allGroups.get(i).getGroupID());
                }
            }
        }
    }

    public ArrayList <String> getGroups()
    {
        return groups;
    }

    public static void main(String[] args)
    {
        ExtractGroupNames ex = new ExtractGroupNames("groups.xml", "tim");
        ex.run();

    }

}
