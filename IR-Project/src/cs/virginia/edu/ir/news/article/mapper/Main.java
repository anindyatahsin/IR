package cs.virginia.edu.ir.news.article.mapper;

import java.util.Random;

import cs.virginia.edu.ir.news.article.mapper.Loading.ArchivedNewsLoader;
import cs.virginia.edu.ir.news.article.mapper.analysis.ArticleWeighing;
import cs.virginia.edu.ir.news.article.mapper.analysis.CollectionModel;
import cs.virginia.edu.ir.news.article.mapper.analysis.PassageModel;
import cs.virginia.edu.ir.news.article.mapper.config.DeploymentConfiguration;
import cs.virginia.edu.ir.news.article.mapper.config.RunTimeConfiguration;
import cs.virginia.edu.ir.news.article.mapper.config.WeighingConfiguration;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import edu.illinois.cs.index.Runner;

public class Main {

	public static void main(String args[]) throws Exception {
		
		//CollectionModel.reloadCollectionModel("alzajeera");
		//CollectionModel.reloadCollectionModel("yahoo-news");		
		CollectionModel  collectionModel = CollectionModel.getModel("alzajeera");
		RunTimeConfiguration.CURRENTCOLLECTIONMODEL = collectionModel;
		RunTimeConfiguration.CURRENTARTICLESOURCE = "alzajeera";
		//RunTimeConfiguration.CURRENTARTICLESOURCE = "yahoo-news";
		RunTimeConfiguration.CURRENTARTICLECATEGORY = "americas";
		int articleList[] = {20,39,56,60,81,88,89,106,107,115};
		double map[] = new double[articleList.length];

		for(double parameter=10; parameter <= 100; parameter+=10){
			int i = 0;
			
			WeighingConfiguration.TERMS = (int) parameter;
			System.out.println("Term = " + parameter);
			for (int articleId: articleList){
				RunTimeConfiguration.CURRENTARTICLEID = articleId;
				NewsArticle article = null;
				if(RunTimeConfiguration.CURRENTARTICLESOURCE.equals("alzajeera")){
					article = ArchivedNewsLoader.loadOneAlzajeeraNewsArticle(RunTimeConfiguration.CURRENTARTICLEID, RunTimeConfiguration.CURRENTARTICLECATEGORY);
				}
				else{
					article = ArchivedNewsLoader.loadOneYahooNewsArticle(RunTimeConfiguration.CURRENTARTICLEID);
				}
				if (article == null) {
					System.out.println("No article found for the provided configuration.");
					System.exit(-1);
				}
				
				ArticleWeighing weightingConfig = new ArticleWeighing(article);
				weightingConfig.initializePassageModels();
				//System.out.println("Passage Count: " + weightingConfig.getPassageModels().size());
				weightingConfig.weighTerms();
				//System.out.println("The article no is: " + articleId);
				//System.out.println(article.getContent());
				
				for (PassageModel model : weightingConfig.getPassageModels()) {
					RunTimeConfiguration.CURRENTPASSAGEMODEL = model;
					String Query = model.getTopWords(WeighingConfiguration.TERMS, collectionModel);
					//System.out.println("The Query is: " + Query);
					String FILE_PATH=GetIndexFolder(article);
					//Suppose String Query
					map[i++] = Runner.interactiveSearch(FILE_PATH, Query);
					//model.describeModel();
					System.out.println(map[i-1]);
				}
			}
			System.out.println(parameter + "\t" + average(map) + "\t" + stdev(map));
		}
	}
	
	public static double average(double[] data) {  
	    double sum = 0;

	    for(int i=0; i < data.length; i++){ 
	    	sum = sum + data[i]; 
	    }
	    double average = sum / data.length;; 

	    return average;
	}
	
	public static double stdev(double[] data){
		double mean = average(data);
		double sqrsum = 0;
		
		for(int i=0; i < data.length; i++){ 
	    	sqrsum += (data[i]-mean) * (data[i]-mean); 
	    }
		
		double sd = Math.sqrt(sqrsum/(data.length - 1));
		return sd;
		
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
