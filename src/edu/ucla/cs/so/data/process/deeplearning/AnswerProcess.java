package edu.ucla.cs.so.data.process.deeplearning;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import edu.ucla.cs.so.data.model.Question;
import edu.ucla.cs.so.mysql.utils.MySQLAccess;

public class AnswerProcess {
	private static final int MAX_ENTRIES = 20000;

	public static void main(String[] args) throws FileNotFoundException,
			XMLStreamException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader reader = inputFactory
				.createXMLStreamReader(new FileInputStream(
						"/media/troy/Disk2/StackOverflow Dump/Posts-2018-12-02.xml"));

		MySQLAccess access = new MySQLAccess();
		access.connect("deeplearning");
		
		// add cache
		LinkedHashMap<String, Question> cache = new LinkedHashMap<String, Question>(MAX_ENTRIES + 1, 1.1F, true) {
			protected boolean removeEldestEntry(Map.Entry eldest) {
				   return size() > MAX_ENTRIES;
			}
		};

		while (reader.hasNext()) {
			int event = reader.next();
			if (event == XMLEvent.START_ELEMENT
					&& reader.getLocalName().equals("row")) {
				String id = reader.getAttributeValue(null, "Id");
				int iid = Integer.parseInt(id);
				if(iid <= 50663150) {
					// resume from the last parse exception
					continue;
				}
				
				String postTypeId = reader
						.getAttributeValue(null, "PostTypeId");
				if (postTypeId.equals("2")) {
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
					
					if (q == null) {
						// this is not an answer related to deep learning
						continue;
					}

					String accepted = "0";
					if (id.equals(q.accepted)) {
						accepted = "1";
					}

					String body = reader.getAttributeValue(null, "Body");
					String score = reader.getAttributeValue(null, "Score");
					String createDate = reader.getAttributeValue(null, "CreationDate");
					String editDate = reader.getAttributeValue(null, "LastEditDate");
					access.insertAnswerPost(id, parentId, body, score,
							accepted, q.tags, q.view, createDate, editDate);
					System.out.println(id + "**" + parentId
							+ "** Ignore Body **" + score + "**" + accepted
							+ "**" + q.tags + "**" + q.view + "**" + createDate + "**" + editDate);
				}
			}
		}
		access.close();
		reader.close();
	}
}
