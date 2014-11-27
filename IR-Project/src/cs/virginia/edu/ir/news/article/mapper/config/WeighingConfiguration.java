package cs.virginia.edu.ir.news.article.mapper.config;

public class WeighingConfiguration {

	// passage length in number of paragraphs.
	public static final int MINIMUM_PASSAGE_LENGTH = 2; 
	
	// minimum length of a paragraph in number of sentences to be considered  significant. If 
	// the length is smaller than this then it will be added  to the preceding paragraph during 
	// analysis
	public static final int MINIMUM_PARAGRAPH_LENGTH = 4;
	
	// a binary weight to vary the significance of term frequency at different level 
	public static final double K  = 10;
	
	// a parameter to control the relative impact of relevance and frequency weights of a term
	public static final float ALPHA = 0.5f;
	
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
	
	// equation for calculating the final weight
	public static final double getFinalWeight(double relevanceWeight, double frequencyWeight) {
		return (1 - ALPHA) * relevanceWeight + ALPHA * frequencyWeight;
	}
}
