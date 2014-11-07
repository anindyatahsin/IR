package cs.virginia.edu.ir.news.article.mapper.crawling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cs.virginia.edu.ir.news.article.mapper.config.Configuration;
import cs.virginia.edu.ir.news.article.mapper.object.Comment;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import cs.virginia.edu.ir.news.article.mapper.object.Paragraph;
import cs.virginia.edu.ir.news.article.mapper.utility.CommonUtils;
import cs.virginia.edu.ir.news.article.mapper.utility.CrawledNewsUtils;

public class ArticleInterpreter {
	
	public static NewsArticle ReadNewsArticle(String articleURL) throws Exception {
		
		List<String> commentList = retrieveComments(articleURL);
		if (commentList.size() < Configuration.MINIMUM_COMMENTS_PER_ARTICLE) {
			return null;
		}
		
		Connection connection = Jsoup.connect(articleURL);
		Document page = connection.timeout(Configuration.REQUEST_TIMEOUT).get();
		Element titleElement = page.getElementById("DetailedTitle");
		String title = titleElement.childNode(0).toString().trim();
		Elements subTitleElement = page.getElementsByClass("articleSumm");
		String subTitle = null;
		if (subTitleElement != null) {
			subTitle = subTitleElement.get(0).childNode(0).toString().trim();
		}
		Element articleContent = page.getElementById("ctl00_cphBody_tdTextContent");
		if (articleContent == null) return null;
		Elements paragraphsHolder = articleContent.select("p");
		Iterator<Element> paragraphsIterator = paragraphsHolder.listIterator();
		Element paragraphElement = null;
		List<String> paragraphList = new ArrayList<String>();
		while (true) {
			try {
				paragraphElement = paragraphsIterator.next();
				String paragraphText = paragraphElement.text();
				paragraphList.add(filterHtmlDirectives(paragraphText));
			} catch (NoSuchElementException ex) {
				break;
			}
		}
		
		NewsArticle article = new NewsArticle();
		article.setUrl(articleURL);
		article.setTitle(title);
		article.setSubTitle(subTitle);
		article.setCategory(getArticleCategory(articleURL));
		List<Paragraph> paragraphs = new ArrayList<Paragraph>();
		for (String content : paragraphList) {
			Paragraph paragraph = new  Paragraph();
			paragraph.setSentences(CommonUtils.extractSentences(content));
			paragraphs.add(paragraph);
		}
		article.setParagraphs(paragraphs);
		addCommentsInArticle(article, commentList);
		return article;
	}
	
	private static String getArticleCategory(String articleUrl) {
		int beginIndex = articleUrl.indexOf("news/");
		int endIndex = articleUrl.indexOf('/', beginIndex + 5);
		return articleUrl.substring(beginIndex + 5, endIndex);
	}

	private static List<String> retrieveComments(String articleURL) throws Exception {
		Document commentPage = Jsoup.connect(CrawledNewsUtils.generateDisqusUrlForAlzajeera(articleURL)).get();
		Element commentsHolder = commentPage.getElementById("disqus-threadData");
		String commentsStr = commentsHolder.toString();
		List<String> commentList = new ArrayList<String>();
		int nextCommentLocation = 0;
		while (true) {
			int commentStart = commentsStr.indexOf("raw_message", nextCommentLocation);
			if (commentStart == - 1) break;
			int commentEnd = commentsStr.indexOf("\",\"", commentStart);
			String comment = commentsStr.substring(commentStart + 14, commentEnd);
			commentList.add(comment);
			nextCommentLocation = commentEnd;
		}
		return commentList;
	}
	
	private static String filterHtmlDirectives(String oldText) {
		String newText = oldText.replace("&quot;", "\"");
		newText = newText.replace("&nbsp;", " ");
		return newText.replaceAll("<[^>]*>?", "");
	}
	
	private static void addCommentsInArticle(NewsArticle article, List<String> textComments) {
		List<Comment> commentList = new ArrayList<Comment>();
		for (String content : textComments) {
			Comment comment = new Comment();
			comment.setText(content);
			commentList.add(comment);
		}
		article.setComments(commentList);
	}
}
