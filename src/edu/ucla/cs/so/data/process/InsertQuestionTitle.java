package edu.ucla.cs.so.data.process;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import edu.ucla.cs.so.mysql.utils.MySQLAccess;

public class InsertQuestionTitle {
	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader reader = inputFactory.createXMLStreamReader(
				new FileInputStream("/media/troy/Disk2/StackOverflow Dump/Posts-2018-12-02.xml"));
		
		MySQLAccess access = new MySQLAccess();
		access.connect("stackoverflow");
		while (reader.hasNext()) {
			int event = reader.next();
			if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("row")) {
				String id = reader.getAttributeValue(null, "Id");
				int iid = Integer.parseInt(id);
				if(iid <= 50663446) {
					// resume from the last parse exception
					continue;
				}
				String postTypeId = reader.getAttributeValue(null, "PostTypeId");
				if(postTypeId.equals("1")) {
					// process question posts
					String tags = reader.getAttributeValue(null, "Tags");
					if(tags != null && (tags.contains("<java>") || tags.contains("<android>"))) {
						// find out SO questions in Java or Android
						String title = reader.getAttributeValue(null, "Title");
						System.out.println(id + "**" + title + "**" + tags);
						access.updateTable("questions", id, "title", title);
					}
				}
			}
		}
		access.close();
		reader.close();
	}
}
