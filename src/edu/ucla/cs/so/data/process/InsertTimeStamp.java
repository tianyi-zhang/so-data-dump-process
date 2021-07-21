package edu.ucla.cs.so.data.process;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import edu.ucla.cs.so.data.model.Question;
import edu.ucla.cs.so.mysql.utils.MySQLAccess;

public class InsertTimeStamp {
	private static final int MAX_ENTRIES = 20000;
	
	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader reader = inputFactory.createXMLStreamReader(
				new FileInputStream("/media/troy/Disk2/research/data/StackOverflow Dump/Posts.xml"));
		
		MySQLAccess access = new MySQLAccess();
		access.connect("stackoverflow");
		
		// add cache
		LinkedHashMap<String, Question> cache = new LinkedHashMap<String, Question>(MAX_ENTRIES + 1, 1.1F, true) {
			protected boolean removeEldestEntry(Map.Entry eldest) {
				   return size() > MAX_ENTRIES;
			}
		};
		
		while (reader.hasNext()) {
			int event = reader.next();
			if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("row")) {
				String id = reader.getAttributeValue(null, "Id");
				int iid = Integer.parseInt(id);
				if(iid <= 36136863) {
					// resume from the last parse exception
					continue;
				}
				String postTypeId = reader.getAttributeValue(null, "PostTypeId");
				if(postTypeId.equals("2")) {
					// process answer posts
					String parentId = reader
							.getAttributeValue(null, "ParentId");
					// check whether this post is associated with a Java
					// or Android question
					Question q;
					if(cache.containsKey(parentId)) {
						q = cache.get(parentId);
					} else {
						// query the database
						q = access.selectQuestionPosts(parentId);
						// update the cache
						cache.put(parentId, q);
					}
//					Question q = access.selectQuestionPosts(parentId);
					
					if (q == null) {
						// this is not an answer related to Java or Android
						continue;
					}
					
					String body = reader.getAttributeValue(null, "Body");
					
					if(!body.contains("<code>")) {
						// only answer posts with code snippets are stored in the database
						continue;
					}
					
					String createDate = reader.getAttributeValue(null, "CreationDate");
					String editDate = reader.getAttributeValue(null, "LastEditDate");
					System.out.println(id + "**" + createDate + "**" + editDate);
					if(editDate != null) {
						ArrayList<String> columns = new ArrayList<String>();
						columns.add("CreationDate");
						columns.add("LastEditDate");
						ArrayList<String> values = new ArrayList<String>();
						values.add(createDate);
						values.add(editDate);
						access.updateTable("answers", id, columns, values);
					} else {
						access.updateTable("answers", id, "CreationDate", createDate);
					}
				}
			}
		}
		access.close();
		reader.close();
	}
}
