package edu.ucla.cs.so.data.process.deeplearning;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import edu.ucla.cs.so.data.model.Question;
import edu.ucla.cs.so.mysql.utils.MySQLAccess;

public class PrepareTSV {
	public static void main(String[] args) throws IOException {
		File output = new File("output/pytorch.tsv");
		if(output.exists()) {
			output.delete();
		}
		output.createNewFile();
		
		MySQLAccess dbConn = new MySQLAccess();
		dbConn.connect("deeplearning");
		ArrayList<Question> questions = dbConn.selectQuestionPostByTag("pytorch");
		for(Question q : questions) {
			String record = "https://stackoverflow.com/questions/" + q.id + "\t";
			record += q.title + "\t";
			record += q.view + "\t";
			record += q.tags + "\t";
			record += q.creationTime;
			if(q.accepted != null) {
				record += "\t" + "https://stackoverflow.com/questions/" + q.accepted;
			}
			record += System.lineSeparator();
			FileUtils.writeStringToFile(output, record, Charset.defaultCharset(), true);
		}
		dbConn.close();
	}
}
