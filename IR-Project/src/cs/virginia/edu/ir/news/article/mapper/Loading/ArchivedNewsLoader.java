package cs.virginia.edu.ir.news.article.mapper.Loading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;

import cs.virginia.edu.ir.news.article.mapper.config.DeploymentConfiguration;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import cs.virginia.edu.ir.news.article.mapper.utility.ArchiveNewsUtils;

public class ArchivedNewsLoader {
	
	private static final String ArticleFileNamePrefix = "Article";
	private static final String CommentFileNamePrefix = "Comments";
	
	public static List<NewsArticle> loadArchivedYahooNewsList() throws Throwable {
		
		List<NewsArticle> newsList = new ArrayList<NewsArticle>();
		File newsDirectory = new File(DeploymentConfiguration.ARCHIVED_YAHOO_NEWS_DIRECTORY);
		
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
		File newsDirectory = new File(DeploymentConfiguration.ARCHIVED_ALZAJEERA_NEWS_DIRECTORY);
		for (File topicSubDirectory : newsDirectory.listFiles()) {
			if (topicSubDirectory.isFile()) continue;
			else {
				for (File articleFile : topicSubDirectory.listFiles()) {
					BufferedReader br = new BufferedReader(new FileReader(articleFile));
					NewsArticle article = gson.fromJson(br, NewsArticle.class);
					article.setFileName(articleFile.getName());
					article.setSource("alzajeera");
					article.setCategory(topicSubDirectory.getName());
					articleList.add(article);
					break;
				}
			}
			break;
		}
		return articleList;
	}
	
	public static NewsArticle loadOneYahooNewsArticle(int articleNo) throws Exception {
		File newsDirectory = new File(DeploymentConfiguration.ARCHIVED_YAHOO_NEWS_DIRECTORY);
		if (!newsDirectory.exists()) return null;
		String articleFileName = "Article" + articleNo + ".txt";
		String filePath = DeploymentConfiguration.ARCHIVED_YAHOO_NEWS_DIRECTORY + articleFileName;
		File articleFile = new File(filePath);
		if (!articleFile.exists()) return null;
		NewsArticle article = ArchiveNewsUtils.readNewsArticleFromFile(articleFile);
		article.setSource("yahoo-news");
		article.setFileName(articleFileName);
		String commentsFileName = "Comments" + articleNo + ".txt";
		filePath = DeploymentConfiguration.ARCHIVED_YAHOO_NEWS_DIRECTORY + commentsFileName;
		File commentsFile = new File(filePath);
		ArchiveNewsUtils.readCommentsForAnArticleFromFile(commentsFile, article);
		return article;
	}
	
	public static NewsArticle loadOneAlzajeeraNewsArticle(int articleNo, String category) throws Exception {
		String directoryStr = DeploymentConfiguration.ARCHIVED_ALZAJEERA_NEWS_DIRECTORY + category + "/";
		File topicDirectory = new File(directoryStr);
		if (!topicDirectory.exists()) return null;
		String fileName = "Article" + articleNo + ".txt";
		String filePath = directoryStr + fileName;
		File articleFile = new File(filePath);
		if (!articleFile.exists()) return null;
		Gson gson = new Gson();
		BufferedReader br = new BufferedReader(new FileReader(articleFile));
		NewsArticle article = gson.fromJson(br, NewsArticle.class);
		article.setSource("alzajeera");
		article.setFileName(fileName);
		return article;
	}
	
	public static List<NewsArticle> loadRandomNewsArticles(int newsCount, String newsSource) throws Exception {
		List<NewsArticle> articleList = new ArrayList<NewsArticle>();
		int attempts = 0;
		int maxAttempts = 1000;
		Random rand = new Random();
		if ("alzajeera".equalsIgnoreCase(newsSource)) {
			File newsDirectory = new File(DeploymentConfiguration.ARCHIVED_ALZAJEERA_NEWS_DIRECTORY);
			while (articleList.size() < newsCount && attempts < maxAttempts) {
				int topics = newsDirectory.listFiles().length;
				int nextTopic = rand.nextInt(topics);
				File topicDirectory = newsDirectory.listFiles()[nextTopic];
				int articles = topicDirectory.listFiles().length;
				int nextArticle = rand.nextInt(articles);
				NewsArticle article = loadOneAlzajeeraNewsArticle(nextArticle, topicDirectory.getName());
				articleList.add(article);
				attempts++;
			}
		} else {
			File newsDirectory = new File(DeploymentConfiguration.ARCHIVED_YAHOO_NEWS_DIRECTORY);
			while (articleList.size() < newsCount && attempts < maxAttempts) {
				int articles = newsDirectory.listFiles().length / 2;
				int nextArticle = rand.nextInt(articles) + 1;
				NewsArticle article = loadOneYahooNewsArticle(nextArticle);
				articleList.add(article);
				attempts++;
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
		NewsArticle article = loadOneAlzajeeraNewsArticle(0, "africa");
//		NewsArticle article = loadOneYahooNewsArticle(410);
		System.out.println(article);
//		System.out.println(article);
//		WordWeighting ww = new WordWeighting();
//		ww.weightArticle(article);
//		List<NewsArticle> newsList = loadRandomNewsArticles(10, "yahoo-news");
//		for (NewsArticle article : newsList) {
//			System.out.println(article.getTitle());
//		}
	}
}