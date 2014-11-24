package cs.virginia.edu.ir.news.article.mapper;

import java.util.Random;

import cs.virginia.edu.ir.news.article.mapper.Loading.ArchivedNewsLoader;
import cs.virginia.edu.ir.news.article.mapper.analysis.ArticleWeighing;
import cs.virginia.edu.ir.news.article.mapper.analysis.PassageModel;
import cs.virginia.edu.ir.news.article.mapper.config.DeploymentConfiguration;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import edu.illinois.cs.index.Runner;

public class Main {

	public static void main(String args[]) throws Exception {
		Random rand =  new Random();
		for(int i = 1; i < 10; i++){
			
			int randInt = rand.nextInt(160);
			NewsArticle article = ArchivedNewsLoader.loadOneAlzajeeraNewsArticle(randInt, "europe");
			if (article == null) {
				System.out.println("No article found for the provided configuration.");
				System.exit(-1);
			}
			
			ArticleWeighing weightingConfig = new ArticleWeighing(article);
			weightingConfig.initializePassageModels();
			System.out.println("Passage Count: " + weightingConfig.getPassageModels().size());
			weightingConfig.weighTerms();
			for (PassageModel model : weightingConfig.getPassageModels()) {
				//model.describeModel();
				String Query = model.getTopWords(10);
				System.out.println("The article no is: " + randInt);
				System.out.println("The Query is: " + Query);
				String FILE_PATH=GetIndexFolder(article);
				//Suppose String Query
				
				Runner.interactiveSearch(FILE_PATH, Query);
			}
			
			System.out.println("\n\n************************************************************************************************************\n\n");
		}
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
