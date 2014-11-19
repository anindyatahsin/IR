package cs.virginia.edu.ir.news.article.mapper.crawling;

import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cs.virginia.edu.ir.news.article.mapper.config.DeploymentConfiguration;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import cs.virginia.edu.ir.news.article.mapper.utility.CrawledNewsUtils;

public class NewsListVisitor {
	
	public static void main(String args[]) throws Exception {		
		crawlNewsFromAlzajeera();
	}

	private static void crawlNewsFromAlzajeera() throws Exception {
		
		int examinedNewsCount = 0;
		int pageIndex = DeploymentConfiguration.ALJAZEERA_PAGE_BEGIN_INDEX;
		Set<String> alreadyReadNewsSet = CrawledNewsUtils.getUrlsOfAlreadyCrawledArticles("alzajeera");
		examinedNewsCount = alreadyReadNewsSet.size();
		
		while (examinedNewsCount < DeploymentConfiguration.MAXIMUM_NEWS_COUNT_PER_SITE) {
			String nextPageURL = DeploymentConfiguration.ALJAZEERA_SEARCH_URL + "&p=" + pageIndex;
			Connection connection = Jsoup.connect(nextPageURL);
			Document page = connection.timeout(DeploymentConfiguration.REQUEST_TIMEOUT).get();
			String searchContent = page.toString();
			int nextSearchResult = 0;
			while (true) {
				nextSearchResult = searchContent.indexOf("indexText-Bold2", nextSearchResult);
				if (nextSearchResult == -1) break;
				int newsURLEnd = searchContent.indexOf("html", nextSearchResult);
				String htmlContent = searchContent.substring(nextSearchResult, newsURLEnd + 4);
				int newsUrlBegin = htmlContent.indexOf("http://");
				String newsUrl = htmlContent.substring(newsUrlBegin);
				if (!alreadyReadNewsSet.contains(newsUrl)) {
					try {
						NewsArticle article = ArticleInterpreter.ReadNewsArticle(newsUrl);
						if (article != null) {
							System.out.println("Read: " + article.getTitle());
							CrawledNewsUtils.storeNewsArticleInFile(
									DeploymentConfiguration.ARCHIVED_ALZAJEERA_NEWS_DIRECTORY, article);
							CrawledNewsUtils.logNewsArticleRead("alzajeera", article);
							examinedNewsCount++;
							alreadyReadNewsSet.add(newsUrl);
						} else {
							//System.out.println("Discarded: " + newsUrl);
						}
					} catch (Exception ex) {
						alreadyReadNewsSet.add(newsUrl);
					}
				}
				nextSearchResult = newsURLEnd;
			};
			
			pageIndex += DeploymentConfiguration.ALJAZEERA_PAGE_INCREMENT;
			Thread.sleep(DeploymentConfiguration.DELAY_PER_LIST_PAGE_VISIT);
		}
	}
}
