package edu.ucla.cs.so.data.process.deeplearning;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import edu.ucla.cs.so.mysql.utils.MySQLAccess;

public class QuestionProcess {
	
	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader reader = inputFactory.createXMLStreamReader(
				new FileInputStream("/media/troy/Disk2/StackOverflow Dump/Posts-2018-12-02.xml"));
		
		MySQLAccess access = new MySQLAccess();
		access.connect("deeplearning");
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
					if(tags != null && 
							(tags.contains("deep-learning") 
									|| tags.contains("neural-network")
									|| tags.contains("tensorflow")
									|| tags.contains("keras")
									|| tags.contains("pytorch")
									|| tags.contains("torch")
									|| tags.contains("caffe")
									|| tags.contains("deeplearning4j")
									|| tags.contains("mxnet")
									|| tags.contains("theano")
									|| tags.contains("chainer")
									|| tags.contains("cntk"))) {
						// find out SO questions in Java or Android
						String body = reader.getAttributeValue(null, "Body");
						String accepted = reader.getAttributeValue(null, "AcceptedAnswerId");
						String viewCount = reader.getAttributeValue(null, "ViewCount");
						String title = reader.getAttributeValue(null, "Title");
						String creationDate = reader.getAttributeValue(null, "CreationDate");
						String editDate = reader.getAttributeValue(null, "LastEditDate");
						access.insertQuestionPost(id, accepted, viewCount, tags, title, creationDate, editDate, body);
						System.out.println(id + "**" + accepted + "**" + viewCount 
								+ "**" + tags + "**" + title + "**" + creationDate + "**" + editDate);
					}
				}
			}
		}
		access.close();
		reader.close();
	}
}
