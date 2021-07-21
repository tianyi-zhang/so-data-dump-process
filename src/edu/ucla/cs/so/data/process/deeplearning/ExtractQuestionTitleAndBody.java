package edu.ucla.cs.so.data.process.deeplearning;

import java.io.File;
import java.io.IOException;

import edu.ucla.cs.so.mysql.utils.MySQLAccess;

public class ExtractQuestionTitleAndBody {
	public static void main(String[] args) throws IOException {
		File outputDir = new File("output/questions");
		if(!outputDir.exists()) {
			outputDir.mkdir();
		}
		
		MySQLAccess dbConn = new MySQLAccess();
		dbConn.connect("deeplearning");
		dbConn.extractQuestionTitleAndBody(outputDir);
		dbConn.close();
	}
}
