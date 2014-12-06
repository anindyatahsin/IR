package cs.virginia.edu.ir.news.article.mapper;
import java.io.*;
import java.util.*; 

import cs.virginia.edu.ir.news.article.mapper.Loading.ArchivedNewsLoader;
import cs.virginia.edu.ir.news.article.mapper.analysis.ArticleWeighing;
import cs.virginia.edu.ir.news.article.mapper.analysis.CollectionModel;
import cs.virginia.edu.ir.news.article.mapper.analysis.PassageModel;
import cs.virginia.edu.ir.news.article.mapper.analysis.WordWeight;
import cs.virginia.edu.ir.news.article.mapper.config.DeploymentConfiguration;
import cs.virginia.edu.ir.news.article.mapper.config.RunTimeConfiguration;
import cs.virginia.edu.ir.news.article.mapper.config.WeighingConfiguration;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import edu.illinois.cs.index.*;

import java.util.*;

public class PostMain {
	public static void main(String args[]) throws Exception {
		
		CollectionModel collectionModel = CollectionModel.getModel("alzajeera");
		RunTimeConfiguration.CURRENTCOLLECTIONMODEL = collectionModel;
		Random rand = new Random();

		int randInt = rand.nextInt(160);
		RunTimeConfiguration.CURRENTARTICLESOURCE = "alzajeera";
		//RunTimeConfiguration.CURRENTARTICLESOURCE = "yahoo-news";
		RunTimeConfiguration.CURRENTARTICLECATEGORY = "americas";
		RunTimeConfiguration.CURRENTARTICLEID = 89;
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
		System.out.println("Passage Count: " + weightingConfig.getPassageModels().size());
		weightingConfig.weighTerms();
		System.out.println("The article no is: " + randInt);
		System.out.println(article.getContent());
		
		for (PassageModel model : weightingConfig.getPassageModels()) {
			Hashtable<String,Double> DocumentScore=new Hashtable<String,Double>();
			RunTimeConfiguration.CURRENTPASSAGEMODEL = model;
			List<WordWeight> Querywords = model.getTopWordsPost(RunTimeConfiguration.CURRENTQUERYSIZE, collectionModel);
			
			String FILE_PATH=GetIndexFolder(article);
			//Suppose String Query
			ArrayList<ResultDoc> results;
			for (Iterator<WordWeight> iter = Querywords.iterator(); iter.hasNext(); ) {
				WordWeight QueryWordlist = iter.next();
				
				try{
					results=Runner.interactiveSearchpost(FILE_PATH, QueryWordlist.getWord(),QueryWordlist.getProbability(RunTimeConfiguration.CURRENTCOLLECTIONMODEL));
					Double wordProb=QueryWordlist.getProbability(collectionModel);
					for (Iterator<ResultDoc> iter_result = results.iterator(); iter_result.hasNext(); ) {
						ResultDoc tempResultDoc=iter_result.next();
						if(DocumentScore.containsKey(tempResultDoc.title())){
							Double tempscore=DocumentScore.get(tempResultDoc.title());
							tempscore=tempscore+ (1-RunTimeConfiguration.Pi)*wordProb+ RunTimeConfiguration.Pi*tempResultDoc.score;
							DocumentScore.put(tempResultDoc.title(), tempscore);
						}
						else{
							Double tempscore=(1-RunTimeConfiguration.Pi)*wordProb+ RunTimeConfiguration.Pi*tempResultDoc.score;
							DocumentScore.put(tempResultDoc.title(), tempscore);
						}
						
					}					
				}
				catch(Exception e){
					
				}
			}
			
			ArrayList resultlist=sortValue(DocumentScore);
			System.out.println(resultlist);
			//Runner.interactiveSearch(FILE_PATH, Query);
			//model.describeModel();
		}
	}
	
    public static ArrayList sortValue(Hashtable<String, Double> t){

        //Transfer as List and sort it
        ArrayList<Map.Entry<String, Double>> l = new ArrayList(t.entrySet());
        Collections.sort(l, new Comparator<Map.Entry<String, Double>>(){

          public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
             return o1.getValue().compareTo(o2.getValue());
         }});

        return l;
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
