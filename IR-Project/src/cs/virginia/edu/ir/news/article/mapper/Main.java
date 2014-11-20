package cs.virginia.edu.ir.news.article.mapper;

import cs.virginia.edu.ir.news.article.mapper.Loading.ArchivedNewsLoader;
import cs.virginia.edu.ir.news.article.mapper.analysis.ArticleWeighing;
import cs.virginia.edu.ir.news.article.mapper.analysis.PassageModel;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;

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
	}
}
