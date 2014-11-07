package cs.virginia.edu.ir.news.article.mapper.object;

import java.util.Date;
import java.util.List;

public class NewsArticle {
	
	private Date publicationDate;
	private String category;
	private String url;
	private String title;
	private String subTitle;
	private String author;
	private List<Paragraph> paragraphs;
	private List<Comment> comments;
	
	public Date getPublicationDate() {
		return publicationDate;
	}
	
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public List<Paragraph> getParagraphs() {
		return paragraphs;
	}

	public void setParagraphs(List<Paragraph> paragraphs) {
		this.paragraphs = paragraphs;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("title: ").append(title).append("\n");
		if (subTitle != null) buffer.append("sub-title: ").append(subTitle).append("\n");
		if (category != null) buffer.append("category: ").append(category).append("\n");
		if (url != null) buffer.append("url: ").append(url).append("\n");
		buffer.append("Content:......................................................... ").append("\n");
		for (Paragraph paragraph : paragraphs) {
			buffer.append(paragraph.toString());
		}
		buffer.append("\n\nComments:......................................................... ").append("\n");
		for (Comment comment : comments) {
			buffer.append(comment.toString());
		}
		return buffer.toString();
	}
}
