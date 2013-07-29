package xian.rest.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class RepoInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String author;
	
	private String name;
	
	private int numberOfCommit;
	
	private long startOn;
	
	private long lastUpdate;

	public RepoInfo(){
		
	}
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getNumberOfCommit() {
		return numberOfCommit;
	}

	public void setNumberOfCommit(final int numberOfCommit) {
		this.numberOfCommit = numberOfCommit;
	}

	public long getStartOn() {
		return startOn;
	}

	public void setStartOn(final long startOn) {
		this.startOn = startOn;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(final long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}	
	
}
