package edu.ucla.cs.so.data.process.deeplearning;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import edu.ucla.cs.so.data.model.Answer;
import edu.ucla.cs.so.data.model.Question;
import edu.ucla.cs.so.mysql.utils.MySQLAccess;

public class ExtractMetaData {
	
	public static void extract(String framework, File file) throws IOException, ParseException {
		SimpleDateFormat sdf = 
			     new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		MySQLAccess dbConn = new MySQLAccess();
		dbConn.connect("deeplearning");
		List<Question> questions = dbConn.selectQuestionPostByTag(framework);
		try {
			dbConn.result.close();
			dbConn.prep.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(Question q : questions) {
			String record = q.id + ",";
			// reformat creation time
			record += q.creationTime.substring(0, q.creationTime.length() - 2) + ",";
			
			// find answer posts
			List<Answer> answers = dbConn.findAnswerPostsByQuestionPostId(q.id);
			try {
				dbConn.result.close();
				dbConn.prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if(answers.isEmpty()) {
				record += "NULL,";
			} else {
				// find the first reply
				Answer first = answers.get(0);
				Date earliestDate = sdf.parse(first.creationTime.substring(0, first.creationTime.length() - 2));
				for(int i = 1; i < answers.size(); i++) {
					Answer answer = answers.get(i);
					Date date = sdf.parse(answer.creationTime.substring(0, answer.creationTime.length() - 2));
					if(earliestDate.after(date)) {
						first = answer;
						earliestDate = date;
					}
				}
				record += first.creationTime.substring(0, first.creationTime.length() - 2) + ",";
			}
			
			record += answers.size() + ",";
			
			if(q.accepted != null) {
				record += "1,";
				Answer accepted_answer = null;
				for(Answer a : answers) {
					if(a.id == Integer.parseInt(q.accepted)) {
						accepted_answer = a;
					}
				}
				record += accepted_answer.creationTime.substring(0, accepted_answer.creationTime.length() - 2);
				record += ",";
			} else {
				record += "0,NULL,";
			}
			
			record += framework + System.lineSeparator();
			FileUtils.writeStringToFile(file, record, Charset.defaultCharset(), true);
		}
		
		try {
			dbConn.connect.close();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		File output = new File("output/meta-data.csv");
		if(output.exists()) {
			output.delete();
		}
		output.createNewFile();
		
		// write the header
		FileUtils.writeStringToFile(output, 
				"Id,CreationTime,FirstAnswerTime,AnswerCount,HasAcceptedAnswer,Framework" + System.lineSeparator(),
				Charset.defaultCharset(), true);
		
		MySQLAccess dbConn = new MySQLAccess();
		dbConn.connect("deeplearning");
		String[] frameworks = {"tensorflow", "pytorch", "deeplearning4j"};
		for(String framework : frameworks) {
			extract(framework, output);
		}
	}
}
