package cs.virginia.edu.ir.news.article.mapper.config;

public class DeploymentConfiguration {

	public static final int MAXIMUM_NEWS_COUNT_PER_SITE = 1000; 
	
	public static final String SENTENCE_MODEL_FILE = "./data/configuration/model/en-sent.bin";
	public static final String POS_MODEL_FILE = "./data/configuration/model/en-pos-perceptron.bin";
	public static final String TOKEN_MODEL_FILE = "./data/configuration/model/en-token.bin";
	
	public static final String ARCHIVED_YAHOO_NEWS_DIRECTORY = "./data/yahoo-news/Yahoo/";
	
	public static final String ALJAZEERA_SEARCH_URL = "http://ajnsearch.aljazeera.net/SearchProxy.aspx?"
			+ "m=search&c=english&f=english_cluster&s=as_q&q=news"
			+ "&r=100&o=url&t=d&cnt=gsaSearch&target=gsaSearch";
	public static final int ALJAZEERA_PAGE_BEGIN_INDEX = 0;
	public static final int ALJAZEERA_PAGE_INCREMENT = 100;
	
	public static final long DELAY_PER_LIST_PAGE_VISIT = 5000l;
	public static final long DELAY_PER_NEWS_ARTICLE_READ = 5000l;
	
	public static final String DISQUS_URL_PREFIX = "http://disqus.com/embed/comments/?"
			+ "base=default&disqus_version=1db76087&f=ajenglish";
	
	public static final int MINIMUM_COMMENTS_PER_ARTICLE = 25;
	
	public static final String ARCHIVED_ALZAJEERA_NEWS_DIRECTORY = "./data/alzajeera/";
	
	public static final String LOG_DIRECTORY = "./data/logs/";
	public static final String INDEX_DIRECTORY = "./data/index/";
	
	public static final int REQUEST_TIMEOUT = 10000;
}
