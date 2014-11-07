package cs.virginia.edu.ir.news.article.mapper.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import cs.virginia.edu.ir.news.article.mapper.object.Comment;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import cs.virginia.edu.ir.news.article.mapper.object.Paragraph;

public class ArchiveNewsUtils {
	
	public static NewsArticle readNewsArticleFromFile(File articleFile) throws Exception {
		FileReader fileReader = new FileReader(articleFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		NewsArticle article = new NewsArticle();
		String line = bufferedReader.readLine();
		article.setTitle(line);
		List<Paragraph> paragraphs = new ArrayList<Paragraph>();
		while ((line = bufferedReader.readLine()) != null) {
			List<String> sentences = CommonUtils.extractSentences(line);
			Paragraph paragraph = new Paragraph();
			paragraph.setSentences(sentences);
			paragraphs.add(paragraph);	
		}
		article.setParagraphs(paragraphs);	
		bufferedReader.close();
		return article;
	}
	
	public static void readCommentsForAnArticleFromFile(File commentFile, NewsArticle article) throws Exception {
		FileReader file = new FileReader(commentFile);
		BufferedReader reader = new BufferedReader(file);
		String line = null;
		List<Comment> comments = new ArrayList<Comment>();
		while ((line = reader.readLine()) != null) {
			Comment comment = new Comment();
			comment.setText(line);
			comments.add(comment);
		}
		article.setComments(comments);	
		reader.close();
	}
}
