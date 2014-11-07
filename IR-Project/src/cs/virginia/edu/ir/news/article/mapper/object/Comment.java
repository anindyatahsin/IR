package cs.virginia.edu.ir.news.article.mapper.object;

public class Comment {	
	
	private String id;
	private String author;
	private String text;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "comment:---------" + text + "\n\n";
	}
}
