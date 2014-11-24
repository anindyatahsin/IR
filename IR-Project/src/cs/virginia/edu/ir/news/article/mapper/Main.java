package cs.virginia.edu.ir.news.article.mapper;

import cs.virginia.edu.ir.news.article.mapper.Loading.ArchivedNewsLoader;
import cs.virginia.edu.ir.news.article.mapper.analysis.ArticleWeighing;
import cs.virginia.edu.ir.news.article.mapper.analysis.PassageModel;
import cs.virginia.edu.ir.news.article.mapper.config.DeploymentConfiguration;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import edu.illinois.cs.index.Runner;

public class Main {

	public static void main(String args[]) throws Exception {
		
		NewsArticle article = ArchivedNewsLoader.loadOneAlzajeeraNewsArticle(0, "americas");
		if (article == null) {
			System.out.println("No article found for the provided configuration.");
			System.exit(-1);
		}
		
		ArticleWeighing weightingConfig = new ArticleWeighing(article);
		weightingConfig.initializePassageModels();
		System.out.println("Passage Count: " + weightingConfig.getPassageModels().size());
		weightingConfig.weighTerms();
		for (PassageModel model : weightingConfig.getPassageModels()) {
			model.describeModel();
		}
		
		//****ASIF PART******//
		String FILE_PATH=GetIndexFolder(article);
		//Suppose String Query
		String Query="";
		Runner.interactiveSearch(FILE_PATH, Query);
	
	}
	public static String GetIndexFolder(NewsArticle article){
		
		StringBuilder buffer = new StringBuilder(DeploymentConfiguration.INDEX_DIRECTORY);
		buffer.append(article.getSource()).append("/");
		if (article.getCategory() != null) {
			buffer.append(article.getCategory()).append("/");
		}
		String fileName = article.getFileName();
		int extension = fileName.lastIndexOf('.');
		String fileNameWithoutExtension = fileName.substring(0, extension);
		buffer.append(fileNameWithoutExtension).append('/');
		
		return buffer.toString();		
		
	}
}
