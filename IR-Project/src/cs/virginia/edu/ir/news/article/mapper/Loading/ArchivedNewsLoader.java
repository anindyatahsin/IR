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

import cs.virginia.edu.ir.news.article.mapper.config.Configuration;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import cs.virginia.edu.ir.news.article.mapper.object.Paragraph;
import cs.virginia.edu.ir.news.article.mapper.output.Output;
import cs.virginia.edu.ir.news.article.mapper.utility.ArchiveNewsUtils;
import cs.virginia.edu.ir.news.article.mapper.utility.CommonUtils;

public class ArchivedNewsLoader {
	
	private static final String ArticleFileNamePrefix = "Article";
	private static final String CommentFileNamePrefix = "Comments";
	
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
					//System.out.println(article.toString());
					articleList.add(article);
					break;
				}
			}
			break;
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
		
		List<NewsArticle> yahooNewsList = loadArchivedAlzajeeraNewsArticles();
		NewsArticle article = yahooNewsList.get(0);
		WordWeighting ww = new WordWeighting();
		ww.weightArticle(article);
//		List<NewsArticle> yahooNewsList = loadArchivedYahooNewsList();
//		for (NewsArticle article : yahooNewsList) {
//			System.out.println(article.getTitle());
//		}
	}
}