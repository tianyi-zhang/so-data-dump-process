package edu.ucla.cs.so.data.model;

public class Question {
	public String id;
	public String accepted;
	public String tags;
	public String view;
	public String title;
	public String creationTime;
	public String lastEditTime;

	public Question(String id, String accepted, String tags, String view) {
		this.id = id;
		this.accepted = accepted;
		this.tags = tags;
		this.view = view;
		this.title = null;
		this.creationTime = null;
		this.lastEditTime = null;
	}
	
	public Question(String id, String accepted, String tags, String view, String title, 
			String creationTime, String lastEditTime) {
		this.id = id;
		this.accepted = accepted;
		this.tags = tags;
		this.view = view;
		this.title = title;
		this.creationTime = creationTime;
		this.lastEditTime = lastEditTime;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Question) {
			Question other = (Question) obj;
			return this.id.equals(other.id)
					&& this.accepted.equals(other.accepted)
					&& this.tags.equals(other.tags)
					&& this.view.equals(other.view);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hash = 37;
		hash += 31 + 7 * this.id.hashCode();
		hash += 31 + 7 * this.accepted.hashCode();
		hash += 31 + 7 * this.tags.hashCode();
		hash += 31 + 7 * this.view.hashCode();
		return hash;
	}
}
