package xian.rest.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class RepoInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String author;

	private final String name;

	private final int numberOfCommit;

	private final long startOn;

	private final long lastUpdate;

	private RepoInfo(Builder builder) {
		author = builder.author;
		name = builder.name;
		numberOfCommit = builder.numberOfCommit;
		startOn = builder.startOn;
		lastUpdate = builder.lastUpdate;
	}

	public static class Builder {
		private final String author;
		private final String name;

		private int numberOfCommit = 0;
		private long startOn = 0;
		private long lastUpdate = 0;

		public Builder numOfCommit(final int numOfCommit) {
			numberOfCommit = numOfCommit;
			return this;
		}

		public Builder startOn(final long start) {
			startOn = start;
			return this;
		}

		public Builder lastUpdate(final long last) {
			lastUpdate = last;
			return this;
		}

		public Builder(final String author, final String name) {
			this.author = author;
			this.name = name;
		}

		public RepoInfo build() {
			return new RepoInfo(this);
		}

	}

	public String getAuthor() {
		return author;
	}

	public String getName() {
		return name;
	}

	public int getNumberOfCommit() {
		return numberOfCommit;
	}

	public long getStartOn() {
		return startOn;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

}
