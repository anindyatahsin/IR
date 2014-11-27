package cs.virginia.edu.ir.news.article.mapper.analysis;

import cs.virginia.edu.ir.news.article.mapper.config.WeighingConfiguration;


public class WordWeight {

	private String word;
	private boolean visited;
	private double relevanceWeight;
	private int frequency;
	private double frequencyWeight;
	private int significanceLevel;
	
	public WordWeight(String word) {
		super();
		this.word = word;
		relevanceWeight = 0.0;
		frequency = 0;
	}

	public boolean isVisited() {
		return visited;
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public String getWord() {
		return word;
	}

	public double getRelevanceWeight() {
		return relevanceWeight;
	}
	
	public void normalizeRelevanceWeight(double normalizationFactor) {
		relevanceWeight = relevanceWeight / normalizationFactor;
	}

	public void addRelevanceWeight(double relevanceWeight) {
		this.relevanceWeight += relevanceWeight;
	}

	public int getFrequency() {
		return frequency;
	}

	public void addOccurrance() {
		frequency++;
	}

	public int getSignificanceLevel() {
		return significanceLevel;
	}

	public void setSignificanceLevel(int significanceLevel) {
		this.significanceLevel = significanceLevel;
	}
	
	public double getFrequencyWeight() {
		return frequencyWeight;
	}

	public void setFrequencyWeight(double frequencyWeight) {
		this.frequencyWeight = frequencyWeight;
	}
	
	public void normalizeFrequencyWeight(double normalizationFactor) {
		frequencyWeight = frequencyWeight / normalizationFactor;
	}
	
	public double getFinalWeight() {
		return WeighingConfiguration.getFinalWeight(relevanceWeight, frequencyWeight);
	}

	public double getProbability(CollectionModel model) {
		return WeighingConfiguration.getFinalWeight(word, 
				relevanceWeight, frequencyWeight, model);
	}
}
