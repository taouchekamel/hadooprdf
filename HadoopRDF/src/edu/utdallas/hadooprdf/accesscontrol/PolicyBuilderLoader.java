package edu.utdallas.hadooprdf.accesscontrol;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import edu.utdallas.hadooprdf.lib.util.Utility;
public class PolicyBuilderLoader {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Please give input file.");
			System.exit(1);
		}
		
	    File file = new File(args[0]);
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;

	    try {
	      fis = new FileInputStream(file);
	      bis = new BufferedInputStream(fis);
	      dis = new DataInputStream(bis);
	      while (dis.available() != 0) {
	    	  String line =dis.readLine();
	    	  String lineSplit[] = line.split("	");//Tab Seperated
	    	  String fileName = lineSplit[0];
	    	  fileName = Utility.replaceHash(fileName);
	    	  String groupName[] = lineSplit[1].split(" ");
	    	  //System.out.println(groupName.length);

	    	  PolicyBuilder policyBuilder = new PolicyBuilder();
	    	  policyBuilder.run(fileName, groupName);
	      }
	      fis.close();
	      bis.close();
	      dis.close();

	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }
}
