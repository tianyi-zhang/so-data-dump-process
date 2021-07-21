package edu.ucla.cs.so.mysql.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.ucla.cs.so.data.model.Answer;
import edu.ucla.cs.so.data.model.Question;

public class MySQLAccess {
	final String url = "jdbc:mysql://localhost:3306";
	final String username = "root";
	final String password = "5887526";
	public Connection connect = null;
	public Statement statement = null;
	public ResultSet result = null;
	public PreparedStatement prep = null;

	public void connect(String database) {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(url + "/" + database, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertQuestionPost(String id, String accept, String view,
			String tags, String title) {
		if (connect != null) {
			try {
				prep = connect
						.prepareStatement("insert into questions (Id, AcceptedAnswerId, ViewCount, Tags, Title) values (?, ?, ?, ?, ?)");
				prep.setString(1, id);
				prep.setString(2, accept);
				prep.setString(3, view);
				prep.setString(4, tags);
				prep.setString(5, title);
				prep.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void insertQuestionPost(String id, String accept, String view,
			String tags, String title, String creationDate, String editDate, String body) {
		if (connect != null) {
			try {
				if (editDate != null) {
					prep = connect.prepareStatement(
							"insert into questions (Id, AcceptedAnswerId, ViewCount, "
							+ "Tags, Title, CreationDate, LastEditDate, Body) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?)");
					prep.setString(1, id);
					prep.setString(2, accept);
					prep.setString(3, view);
					prep.setString(4, tags);
					prep.setString(5, title);
					prep.setString(6, creationDate);
					prep.setString(7, editDate);
					prep.setString(8, body);
				} else {
					prep = connect.prepareStatement(
							"insert into questions (Id, AcceptedAnswerId, ViewCount, Tags, Title, CreationDate, Body) "
							+ "values (?, ?, ?, ?, ?, ?, ?)");
					prep.setString(1, id);
					prep.setString(2, accept);
					prep.setString(3, view);
					prep.setString(4, tags);
					prep.setString(5, title);
					prep.setString(6, creationDate);
					prep.setString(7, body);
				}
				prep.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateTable(String table, String id, String column, String value) {
		if (connect != null) {
			try {
				prep = connect
						.prepareStatement("update " + table + " set " + column + " = (?) where id = " + id);
				prep.setString(1, value);
				prep.execute();
				prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateTable(String table, String id, ArrayList<String> columns, ArrayList<String> values) {
		if (connect != null) {
			try {
				String statement = "update " + table + " set ";
				for(int i = 0; i < columns.size() - 1; i++) {
					statement += columns.get(i) + " = (?), ";
 				}
				statement += columns.get(columns.size() - 1) + " = (?) where id = " + id;
				prep = connect
						.prepareStatement(statement);
				for(int i = 0; i < values.size(); i++) {
					prep.setString(i + 1, values.get(i));
				}
				prep.execute();
				prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void insertAnswerPost(String id, String parentId, String body,
			String score, String accept, String tags, String view) {
		if (connect != null) {
			try {
				prep = connect
						.prepareStatement("insert into answers "
								+ "(Id, ParentId, Body, Score, "
								+ "IsAccepted, Tags, ViewCount) "
								+ "values (?, ?, ?, ?, ?, ?, ?)");
				prep.setString(1, id);
				prep.setString(2, parentId);
				prep.setString(3, body);
				prep.setString(4, score);
				prep.setString(5, accept);
				prep.setString(6, tags);
				prep.setString(7, view);
				prep.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void insertAnswerPost(String id, String parentId, String body,
			String score, String accept, String tags, String view, String creationDate, String editDate) {
		if (connect != null) {
			try {
				if(editDate != null) {
					prep = connect
							.prepareStatement("insert into answers (Id, ParentId, Body, Score, "
									+ "IsAccepted, Tags, ViewCount, CreationDate, LastEditDate) "
									+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
					prep.setString(1, id);
					prep.setString(2, parentId);
					prep.setString(3, body);
					prep.setString(4, score);
					prep.setString(5, accept);
					prep.setString(6, tags);
					prep.setString(7, view);
					prep.setString(8, creationDate);
					prep.setString(9, editDate);
				} else {
					prep = connect
							.prepareStatement("insert into answers (Id, ParentId, Body, Score, "
									+ "IsAccepted, Tags, ViewCount, CreationDate) "
									+ "values (?, ?, ?, ?, ?, ?, ?, ?)");
					prep.setString(1, id);
					prep.setString(2, parentId);
					prep.setString(3, body);
					prep.setString(4, score);
					prep.setString(5, accept);
					prep.setString(6, tags);
					prep.setString(7, view);
					prep.setString(8, creationDate);
				}
				
				prep.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Question selectQuestionPosts(String id) {
		if (connect != null) {
			try {
				prep = connect
						.prepareStatement("select * from questions where id = "
								+ id + ";");
				result = prep.executeQuery();
				Question q;
				if (result.next()) {
					q = new Question(id, result.getString("AcceptedAnswerId"),
							result.getString("Tags"),
							result.getString("ViewCount"));
				} else {
					q = null;
				}

				result.close();
				return q;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	
	public boolean isAnswerExistInDatabase(String id) {
		if (connect != null) {
			try {
				prep = connect
						.prepareStatement("select * from answers where id = "
								+ id + ";");
				result = prep.executeQuery();
				boolean exists = false;
				if (result.next()) {
					exists = true;
				}

				result.close();
				return exists;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public List<Answer> findAnswerPostsByQuestionPostId(String questionPostId) {
		if (connect != null) {
			try {
				prep = connect
						.prepareStatement("select * from answers where parentid = "
								+ questionPostId + ";");
				result = prep.executeQuery();
				List<Answer> answers = new ArrayList<Answer>();
				
				while (result.next()) {
					String id = result.getString("Id");
					String parentId = result.getString("ParentId");
					String body = result.getString("Body");
					String score = result.getString("Score");
					String isAccepted = result.getString("IsAccepted");
					String tags = result.getString("Tags");
					String viewCount = result.getString("ViewCount");
					String creationTime = result.getString("CreationDate");
					String lastEditTime = result.getString("LastEditDate");
					Answer answer = new Answer(id, parentId, body, score, isAccepted, tags, viewCount,
							creationTime, lastEditTime);
					answers.add(answer);
				}

				result.close();
				return answers;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public ArrayList<Answer> searchCodeSnippets(HashSet<String> keywords) {
		ArrayList<Answer> answers = new ArrayList<Answer>();
		
		if (connect != null) {
			try {
				// construct the query
				String query = "select * from answers";
				if(!keywords.isEmpty()) {
					query += " where";
					for(String keyword : keywords) {
						query += " body like \"%" + keyword + "%\" and";
					}
					query = query.substring(0, query.length() - 4);
				}
				query += ";";
				
				prep = connect.prepareStatement(query);
				result = prep.executeQuery();
				while(result.next()) {
					String id = result.getString("Id");
					String parentId = result.getString("ParentId");
					String body = result.getString("Body");
					String score = result.getString("Score");
					String isAccepted = result.getString("IsAccepted");
					String tags = result.getString("Tags");
					String viewCount = result.getString("ViewCount");
					String creationTime = result.getString("CreationDate");
					String lastEditTime = result.getString("LastEditDate");
					Answer answer = new Answer(id, parentId, body, score, isAccepted, tags, viewCount,
							creationTime, lastEditTime);
					answers.add(answer);
				}
				
				result.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return answers;
	}
	
	public ArrayList<Question> selectQuestionPostByTag(String tag) {
		ArrayList<Question> questions = new ArrayList<Question>();
		
		if (connect != null) {
			try {
				// construct the query
				String query = "select * from questions where tags like \"%" + tag + "%\";";
				prep = connect.prepareStatement(query);
				result = prep.executeQuery();
				while(result.next()) {
					String id = result.getString("Id");
					String acceptedAnswerId = result.getString("AcceptedAnswerId");
					String tags = result.getString("Tags");
					String viewCount = result.getString("ViewCount");
					String title = result.getString("Title");
					String creationTime = result.getString("CreationDate");
					String lastEditTime = result.getString("LastEditDate");
					Question question = new Question(id, acceptedAnswerId, tags, viewCount, title, creationTime, lastEditTime);
					questions.add(question);
				}
				
				result.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return questions;
	}
	
	public ArrayList<Question> mineTrainingAnomalyQuestions() {
		ArrayList<Question> questions = new ArrayList<Question>();
		
		if (connect != null) {
			try {
				// construct the query
				String query = "select * from questions;";
				prep = connect.prepareStatement(query);
				result = prep.executeQuery();
				while(result.next()) {
					String id = result.getString("Id");
					String acceptedAnswerId = result.getString("AcceptedAnswerId");
					String tags = result.getString("Tags");
					String viewCount = result.getString("ViewCount");
					String title = result.getString("Title");
					String creationTime = result.getString("CreationDate");
					String lastEditTime = result.getString("LastEditDate");
					String body = result.getString("Body");
					body = StringEscapeUtils.unescapeHtml4(body);
					Document doc = Jsoup.parse(body);
					body = doc.body().text();
					
					String contentToQuery = title.toLowerCase() + " " + body.toLowerCase();
					String[] ss = contentToQuery.split(" ");
					HashSet<String> words = new HashSet<String>();
					for(String s : ss) {
						words.add(s);
					}
					
					if((words.contains("training") || words.contains("train"))
							&& (words.contains("MSE") || words.contains("accuracy")
							|| words.contains("loss")
							|| words.contains("infinity")
							|| words.contains("NaN")
							|| words.contains("expected")
							|| words.contains("unexpected")
							|| words.contains("expect")
							|| words.contains("diverge")
							|| words.contains("divergence")
							|| words.contains("diverges")
							|| words.contains("divergent")
							|| words.contains("diverged"))) {
						// mention any two of DL frameworks and 
						// also use keywords like analog, equivalent, migrate, translate
						Question question = new Question(id, acceptedAnswerId, tags, viewCount, title, creationTime, lastEditTime);
						questions.add(question);
					}
				}
				
				result.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return questions;
	}
	
	public ArrayList<Question> minePerformanceQuestions() {
		ArrayList<Question> questions = new ArrayList<Question>();
		
		if (connect != null) {
			try {
				// construct the query
				String query = "select * from questions;";
				prep = connect.prepareStatement(query);
				result = prep.executeQuery();
				while(result.next()) {
					String id = result.getString("Id");
					String acceptedAnswerId = result.getString("AcceptedAnswerId");
					String tags = result.getString("Tags");
					String viewCount = result.getString("ViewCount");
					String title = result.getString("Title");
					String creationTime = result.getString("CreationDate");
					String lastEditTime = result.getString("LastEditDate");
					String body = result.getString("Body");
					body = StringEscapeUtils.unescapeHtml4(body);
					Document doc = Jsoup.parse(body);
					body = doc.body().text();
					
					String contentToQuery = title.toLowerCase() + " " + body.toLowerCase();
					String[] ss = contentToQuery.split(" ");
					HashSet<String> words = new HashSet<String>();
					for(String s : ss) {
						words.add(s);
					}
					
					if(words.contains("performance")
							|| words.contains("slow") || words.contains("slower")
							|| (words.contains("utilization") && (words.contains("CPU") || words.contains("GPU") || words.contains("memory")))
							|| words.contains("profile") || words.contains("profiler")
							|| words.contains("bottleneck")
							|| words.contains("ResourceExhaustedError")
							|| words.contains("OOM")
							|| words.contains("OutOfMemory")
							|| words.contains("OutOfMemoryError")
							|| words.contains("java.lang.OutOfMemoryError")
							|| words.contains("optimize")
							|| words.contains("optimization")
							|| words.contains("stuck")) {
						// mention any two of DL frameworks and 
						// also use keywords like analog, equivalent, migrate, translate
						Question question = new Question(id, acceptedAnswerId, tags, viewCount, title, creationTime, lastEditTime);
						questions.add(question);
					}
				}
				
				result.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return questions;
	}
	
	public ArrayList<Question> mineCodeOrModelMigrationQuestions() {
		ArrayList<Question> questions = new ArrayList<Question>();
		
		if (connect != null) {
			try {
				// construct the query
				String query = "select * from questions;";
				prep = connect.prepareStatement(query);
				result = prep.executeQuery();
				while(result.next()) {
					String id = result.getString("Id");
					String acceptedAnswerId = result.getString("AcceptedAnswerId");
					String tags = result.getString("Tags");
					String viewCount = result.getString("ViewCount");
					String title = result.getString("Title");
					String creationTime = result.getString("CreationDate");
					String lastEditTime = result.getString("LastEditDate");
					String body = result.getString("Body");
					body = StringEscapeUtils.unescapeHtml4(body);
					Document doc = Jsoup.parse(body);
					body = doc.body().text();
					
					String contentToQuery = title.toLowerCase() + " " + body.toLowerCase();
					String[] ss = contentToQuery.split(" ");
					HashSet<String> words = new HashSet<String>();
					for(String s : ss) {
						words.add(s);
					}
					
					HashSet<String> frameworks = new HashSet<String>();
					frameworks.add("tensorflow");
					frameworks.add("pytorch");
					frameworks.add("torch");
					frameworks.add("deeplearning4j");
					frameworks.add("keras");
					frameworks.add("caffe");
					frameworks.add("theano");
					frameworks.add("mxnet");
					frameworks.add("cntk");
					frameworks.add("chainer");
					
					HashSet<String> keywords = new HashSet<String>();
					keywords.add("equivalent");
					keywords.add("translate");
					keywords.add("translating");
					keywords.add("translates");
					keywords.add("translation");
					keywords.add("analog");
					keywords.add("migrate");
					keywords.add("migrates");
					keywords.add("migrating");
					keywords.add("migration");
					keywords.add("convert");
					keywords.add("converting");
					keywords.add("converts");
					keywords.add("conversion");
					
					frameworks.retainAll(words);
					keywords.retainAll(words);
					
					if(frameworks.size() > 1 && keywords.size() > 0) {
						// mention any two of DL frameworks and 
						// also use keywords like analog, equivalent, migrate, translate
						Question question = new Question(id, acceptedAnswerId, tags, viewCount, title, creationTime, lastEditTime);
						questions.add(question);
					}
				}
				
				result.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return questions;
	}
	
	public void extractQuestionTitleAndBody(File dir) {
		if (connect != null) {
			try {
				// construct the query
				String query = "select * from questions;";
				prep = connect.prepareStatement(query);
				result = prep.executeQuery();
				while(result.next()) {
					String id = result.getString("Id");
					String title = result.getString("Title");
					String body = result.getString("Body");
					File file = new File(dir.getAbsolutePath() + File.separator + id + ".txt");
					String content = "Title: " + title + System.lineSeparator();
					content += "Body: " + System.lineSeparator();
					content += body;
					FileUtils.write(file, content, Charset.defaultCharset(), false);
				}
				
				result.close();
			} catch(SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			if (result != null)
				result.close();
			if (statement != null)
				statement.close();
			if (prep != null)
				prep.close();
			if (connect != null)
				connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
