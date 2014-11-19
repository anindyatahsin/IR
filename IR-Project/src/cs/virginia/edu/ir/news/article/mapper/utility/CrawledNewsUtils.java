package cs.virginia.edu.ir.news.article.mapper.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;

import cs.virginia.edu.ir.news.article.mapper.config.DeploymentConfiguration;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;

public class CrawledNewsUtils {

	public static String generateDisqusUrlForAlzajeera(String articleUrl) throws Exception {
		int identifierBegin = articleUrl.lastIndexOf('-');
		int identiferEnd = articleUrl.lastIndexOf('.');
		String articleId = articleUrl.substring(identifierBegin + 1, identiferEnd);
		StringBuilder builder = new StringBuilder();
		builder.append(DeploymentConfiguration.DISQUS_URL_PREFIX);
		builder.append("&t_i=").append(articleId);
		builder.append("&t_u=").append((new URL(articleUrl)).toString());
		return builder.toString();
	}
	
	public static void storeNewsArticleInFile(String newsSourceDirectory, NewsArticle article) throws Exception {
		String articleDirectory = newsSourceDirectory + article.getCategory();
		File directoryFile = new File(articleDirectory);
		if (!directoryFile.exists()) directoryFile.mkdirs();
		int existingFileCount = directoryFile.listFiles().length;
		String fileName = articleDirectory + "/Article" + existingFileCount + ".txt";
		Gson gson = new Gson();
		String jsonString = gson.toJson(article);
		FileWriter writer = new FileWriter(fileName);
		writer.write(jsonString);
		writer.close();
	}
	
	public static Set<String> getUrlsOfAlreadyCrawledArticles(String newsSourceName) throws Exception {
		Set<String> retrievedNewsSet = new HashSet<String>();
		String logFileName = DeploymentConfiguration.LOG_DIRECTORY + newsSourceName + ".log";
		File logFile = new File(logFileName);
		if (!logFile.exists()) {
			logFile.createNewFile();
			return retrievedNewsSet;
		}
		FileReader fileReader = new FileReader(logFileName);
		BufferedReader reader = new BufferedReader(fileReader);
		String line = null;
		while ((line = reader.readLine()) != null) {
			retrievedNewsSet.add(line);
		}
		reader.close();
		return retrievedNewsSet;
	}
	
	public static void logNewsArticleRead(String newsSourceName, NewsArticle article) throws Exception {
		String newsUrl = article.getUrl();
		String logFileName = DeploymentConfiguration.LOG_DIRECTORY + newsSourceName + ".log";
		FileWriter writer = new FileWriter(logFileName , true);
		String message = newsUrl + "\n";
		writer.write(message);
		writer.close();
	}
}
