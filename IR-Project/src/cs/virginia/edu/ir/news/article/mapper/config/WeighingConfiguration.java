package cs.virginia.edu.ir.news.article.mapper.config;

import cs.virginia.edu.ir.news.article.mapper.analysis.CollectionModel;

public class WeighingConfiguration {

	// passage length in number of paragraphs.
	public static final int MINIMUM_PASSAGE_LENGTH = 2; 
	
	// minimum length of a paragraph in number of sentences to be considered  significant. If 
	// the length is smaller than this then it will be added  to the preceding paragraph during 
	// analysis
	public static final int MINIMUM_PARAGRAPH_LENGTH = 4;
	
	// a binary weight to vary the significance of term frequency at different level 
	public static double K  = 10;
	
	// a parameter to control the relative impact of relevance and frequency weights of a term
	public static float ALPHA = .5f;
	
	// a smoothing parameter to smooth using collection probabilities
	public static float BETA = 0.9f;
	
	public static float BM25_B = 0.1f;
	public static float BM25_K1 = 1.6f;
	
	public static int TERMS = 60;
	// equation for calculating relevance weight
	public static final double getRelevanceWeight(int level) {
		return Math.max(0, 1 - Math.log10(level));
	}
	
	// equation for calculating frequency weight
	public static final double getFrequencyWeight(int significanceLevel, int frequency) {
		double multiplyingFactor = (significanceLevel + K) 
				/ (significanceLevel * (K + 1));
		return frequency * multiplyingFactor;
	}
	
	// equation for calculating the final weight of a term as a probability measure
	public static final double getFinalWeight(String term, double relevanceWeight, 
			double frequencyWeight, CollectionModel collectionModel) {
		
		double articleProb = (1 - ALPHA) * relevanceWeight + ALPHA * frequencyWeight;
		double collectionProb = collectionModel.getCollectionProbabilityOfTerm(term);
		return (1 - BETA) * articleProb + BETA * collectionProb;
	}
	
	// equation that does not use smoothing
	public static final double getFinalWeight(double relevanceWeight, 
			double frequencyWeight) {
		return (1 - ALPHA) * relevanceWeight + ALPHA * frequencyWeight;
	}
}
