package cs.virginia.edu.ir.news.article.mapper.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import cs.virginia.edu.ir.news.article.mapper.Loading.ArchivedNewsLoader;
import cs.virginia.edu.ir.news.article.mapper.Loading.WordWeighting;
import cs.virginia.edu.ir.news.article.mapper.config.DeploymentConfiguration;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import cs.virginia.edu.ir.news.article.mapper.object.WordProperties;

public class CollectionModel {

	private static Map<String, CollectionModel> collectionModelMap = new HashMap<String, CollectionModel>();
	private String newsSource;
	private Map<String, Double> collectionProbabilities;

	private CollectionModel(String newsSource) {
		this.newsSource = newsSource;
		collectionProbabilities = new HashMap<String, Double>();
	}
	
	public static CollectionModel reloadCollectionModel(String newsSource) throws Exception {
		
		CollectionModel model = new CollectionModel(newsSource);
		
		// read articles and determine the overall collection frequencies of terms
		Map<String, Integer> termFrequencies = new HashMap<String, Integer>();
		if ("alzajeera".equalsIgnoreCase(newsSource)) {
			String newsDirectory = DeploymentConfiguration.ARCHIVED_ALZAJEERA_NEWS_DIRECTORY;
			File dir = new File(newsDirectory);
			if (!dir.exists()) return model;
			for (File topic : dir.listFiles()) {
				int articleCount = topic.listFiles().length;
				for (int i = 0; i < articleCount; i++) {
					NewsArticle article = ArchivedNewsLoader.loadOneAlzajeeraNewsArticle(i, topic.getName());
					updateTermFrequencies(article, termFrequencies);
				}
			}
		} else if ("yahoo-news".equalsIgnoreCase(newsSource)) {
			String newsDirectory = DeploymentConfiguration.ARCHIVED_YAHOO_NEWS_DIRECTORY;
			File dir = new File(newsDirectory);
			if (!dir.exists()) return model;
			int articleCount = dir.listFiles().length / 2;
			for (int i = 1; i < articleCount; i++) {
				NewsArticle article = ArchivedNewsLoader.loadOneYahooNewsArticle(i);
				updateTermFrequencies(article, termFrequencies);
			}
		}
		
		// calculate maximum likelihood probabilities of found terms
		long totalOccurrances = 0;
		for (Integer count : termFrequencies.values()) {
			totalOccurrances += count;
		}
		for (Map.Entry<String, Integer> entry : termFrequencies.entrySet()) {
			String term = entry.getKey();
			int occurranceCount = entry.getValue();
			double probability = 1.0 * occurranceCount / totalOccurrances;
			model.collectionProbabilities.put(term, probability);
		}
		model.saveModel();
		collectionModelMap.put(newsSource, model);
		return model;
	}
	
	public static CollectionModel getModel(String newsSource) throws Exception {
		if (collectionModelMap.containsKey(newsSource)) {
			return collectionModelMap.get(newsSource);
		}
		CollectionModel model = loadCollectionModel(newsSource);
		collectionModelMap.put(newsSource, model);
		return model;
	}
	
	public double getCollectionProbabilityOfTerm(String term) {
		if (collectionProbabilities.containsKey(term)) {
			return collectionProbabilities.get(term);
		}
		return 0.0;
	}
	
	private static void updateTermFrequencies(NewsArticle article, Map<String, Integer> termFrequencies) {
		try {
			WordWeighting ww = new WordWeighting();
			ww.weightArticle(article);
			HashMap<String, WordProperties> termOccurranceMap = ww.getLevel();
			for (Map.Entry<String, WordProperties> entry : termOccurranceMap.entrySet()) {
				String term = entry.getKey();
				int occurranceCount = entry.getValue().getOccuranceCount();
				if (!termFrequencies.containsKey(term)) {
					termFrequencies.put(term, occurranceCount);
				} else {
					int currentCount = termFrequencies.get(term);
					termFrequencies.put(term, currentCount + occurranceCount);
				}
			}
		} catch (Exception ex) {
			String articleFile = article.getSource() + ": " + article.getFileName();
			System.out.println("Skipping article\" " + articleFile + " \" for exception " + ex.getMessage() );
			ex.printStackTrace(System.out);
		}
	}
	
	private static CollectionModel loadCollectionModel(String newsSource) throws Exception {
		CollectionModel model = new CollectionModel(newsSource);
		String filePath = DeploymentConfiguration.COLLECTION_MODELS_DIRECTORY + newsSource + ".txt";
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("collection model file not found for the news source: " + newsSource);
			return model;
		}
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = bufferedReader.readLine();
		while ((line = bufferedReader.readLine()) != null) {
			String components[] = line.split("\\s+");
			String word = components[0].trim();
			double probability = Double.parseDouble(components[1].trim());
			model.collectionProbabilities.put(word, probability);
		}
		bufferedReader.close();
		return model;
	}
	
	private void saveModel() throws Exception {
		String filePath = DeploymentConfiguration.COLLECTION_MODELS_DIRECTORY + newsSource + ".txt";
		FileWriter writer = new FileWriter(filePath, false);
		for (Map.Entry<String, Double> entry : collectionProbabilities.entrySet()) {
			String message = entry.getKey() + " " + entry.getValue() + "\n";
			writer.write(message);
		}
		writer.close();
	}
}
