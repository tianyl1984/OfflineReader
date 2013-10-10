package com.tianyl.android.offlinereader.model;

public class Article {

	public Article() {

	}

	public Article(String title) {
		this.title = title;
	}

	private long id;

	private String title;

	private String url;

	private String status;

	private String pathId;

	public static final String STATUS_START = "0";

	public static final String STATUS_END = "1";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isFinish() {
		return STATUS_END.equals(status);
	}

	public String getPathId() {
		return pathId;
	}

	public void setPathId(String pathId) {
		this.pathId = pathId;
	}

}
