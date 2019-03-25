package com.node.beans;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class WriteDataToCSV {

	public static void writeObjectToCSV(PrintWriter writer,List<Node> nodes) throws IOException {
		
			try(	CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
	                    .withHeader("Name", "Activity Name", "Owner","Assigned By", "Build Installed", "Type","State","Notes"));
		){
			for (Node n : nodes) {
				List<String> data = Arrays.asList(
						(n.getName() == null)?" ":n.getName(),
						(n.getAssignedActivity()==null)?" ":n.getAssignedActivity(),
						(n.getOwner()==null)?" ":n.getOwner(),
						(n.getAssignedBy()==null)?" ":n.getAssignedBy(),
						(n.getInstalledBuild()==null)?" ":n.getInstalledBuild(),
						(n.getNodeType()==null)?" ":n.getNodeType(),
						(n.getState()==null)?" ":n.getState(),
						(n.getNote()==null)?" ":n.getNote()
					);
				
				csvPrinter.printRecord(data);
			}
			csvPrinter.flush();
			}
		
	}
}
