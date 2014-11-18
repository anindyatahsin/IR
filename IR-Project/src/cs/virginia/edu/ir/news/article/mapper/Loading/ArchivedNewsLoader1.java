package cs.virginia.edu.ir.news.article.mapper.Loading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import cs.virginia.edu.ir.news.article.mapper.object.Comment;
import cs.virginia.edu.ir.news.article.mapper.config.Configuration;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import cs.virginia.edu.ir.news.article.mapper.utility.ArchiveNewsUtils;
import edu.illinois.cs.index.*;

public class ArchivedNewsLoader1 {
	
	private static final String ArticleFileNamePrefix = "Article";
	private static final String CommentFileNamePrefix = "Comments";
	private static String _indexPath = "data/yahoo-news/IndexYahoo/";
	private static String _prefix = "data/";
	private static String _file = "npl.txt";
    
	public static List<NewsArticle> loadArchivedYahooNewsList() throws Throwable {
		
		List<NewsArticle> newsList = new ArrayList<NewsArticle>();
		File newsDirectory = new File(Configuration.ARCHIVED_YAHOO_NEWS_DIRECTORY);
		
		if (newsDirectory.exists()) {
			Map<Integer, NewsArticle> articleMap = new HashMap<Integer, NewsArticle>();
			File articleFiles[] = newsDirectory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(ArticleFileNamePrefix);
				}
			});
			for (File articleFile : articleFiles) {
				NewsArticle article = ArchiveNewsUtils.readNewsArticleFromFile(articleFile);
				int articleId = getArticleId(articleFile);
				article.setFileName(articleFile.getName());
				article.setSource("yahoo-news");
				articleMap.put(articleId, article);
			}
			
			File commentsFiles[] = newsDirectory.listFiles(new FilenameFilter() {			
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(CommentFileNamePrefix);
				}
			});
			for (File commentsFile : commentsFiles) {
				NewsArticle article = getArticleForComments(commentsFile, articleMap);
				ArchiveNewsUtils.readCommentsForAnArticleFromFile(commentsFile, article);
			}
			newsList.addAll(articleMap.values());
		} else {
			System.out.println("Couldn't find archive news directory.");
		}
		return newsList;
	}
	
	public static List<NewsArticle> loadArchivedAlzajeeraNewsArticles() throws Exception {
		List<NewsArticle> articleList = new ArrayList<NewsArticle>();
		Gson gson = new Gson();
		File newsDirectory = new File(Configuration.ARCHIVED_ALZAJEERA_NEWS_DIRECTORY);
		for (File topicSubDirectory : newsDirectory.listFiles()) {
			if (topicSubDirectory.isFile()) continue;
			else {
				for (File articleFile : topicSubDirectory.listFiles()) {
					BufferedReader br = new BufferedReader(new FileReader(articleFile));
					NewsArticle article = gson.fromJson(br, NewsArticle.class);
					System.out.println(article.toString());
					articleList.add(article);
					System.exit(0);
				}
			}
		}
		return articleList;
	}
	
	public static int getArticleId(File articleFile) {
		String fileName = articleFile.getName();
		int articleIdIndex = ArticleFileNamePrefix.length();
		int idIndexEnd = fileName.indexOf('.');
		String stringId = fileName.substring(articleIdIndex, idIndexEnd);
		return Integer.parseInt(stringId);
	} 
	
	public static NewsArticle getArticleForComments(File commentsFile, Map<Integer, NewsArticle> articleMap) {
		String fileName = commentsFile.getName();
		int articleIdIndex = CommentFileNamePrefix.length();
		int idIndexEnd = fileName.indexOf('.');
		String stringId = fileName.substring(articleIdIndex, idIndexEnd);
		return articleMap.get(Integer.parseInt(stringId));
	}
	
	public static void main(String args[]) throws Throwable {
		//loadArchivedAlzajeeraNewsArticles();    //data/yahoo-news/IndexYahoo/
		List<NewsArticle> yahooNewsList = loadArchivedYahooNewsList();
		
		for (NewsArticle article : yahooNewsList) {
			StringBuilder buffer = new StringBuilder(Configuration.INDEX_DIRECTORY);
			buffer.append(article.getSource()).append("/");
			if (article.getCategory() != null) {
				buffer.append(article.getCategory()).append("/");
			}
			String fileName = article.getFileName();
			int extension = fileName.lastIndexOf('.');
			String fileNameWithoutExtension = fileName.substring(0, extension);
			buffer.append(fileNameWithoutExtension).append('/');
			File directory = new File(buffer.toString());
			if (directory.exists()) continue;
			directory.mkdirs();
			Indexer.index(buffer.toString(), article);
			break;

		}
	}
}