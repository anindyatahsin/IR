package cs.virginia.edu.ir.news.article.mapper.analysis;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cs.virginia.edu.ir.news.article.mapper.config.WeighingConfiguration;
import cs.virginia.edu.ir.news.article.mapper.extra.WeightComparator;

public class PassageModel {

	private Set<Integer> paragraphIds;
	private Map<String, WordWeight> wordWeights;
	
	public PassageModel(Set<Integer> paragraphIds) {
		super();
		this.paragraphIds = paragraphIds;
		this.wordWeights = new TreeMap<String, WordWeight>();
	}

	public Set<Integer> getParagraphIds() {
		return paragraphIds;
	}

	public Map<String, WordWeight> getWordWeights() {
		return wordWeights;
	}
	
	public String getTitleWords(){
		StringBuilder buffer = new StringBuilder();
		for(WordWeight weight : wordWeights.values()){
			if(weight.getSignificanceLevel() == 1 && weight.getFrequency() > 0)
				buffer.append(weight.getWord()).append(" ");
		}
		return buffer.toString();
	}
	
	public String getTopWords(int count, CollectionModel collectionModel){
		StringBuilder buffer = new StringBuilder();
		List<WordWeight> weights = new ArrayList<WordWeight>(wordWeights.values());
		Collections.sort(weights, new WeightComparator(collectionModel));
		for(int i = 0; i < count; i++){
			WordWeight weight = weights.get(i);	
			buffer.append(weight.getWord()).append(" ");
		}
		return buffer.toString();
	}

	public List<WordWeight> getTopWordsPost(int count, CollectionModel collectionModel){
		List<WordWeight> Querywords=new ArrayList<WordWeight>(count);
		StringBuilder buffer = new StringBuilder();
		List<WordWeight> weights = new ArrayList<WordWeight>(wordWeights.values());
		Collections.sort(weights, new WeightComparator(collectionModel));
		for(int i = 0; i < count; i++){
			WordWeight weight = weights.get(i);	
			buffer.append(weight.getWord()).append(" ");
			Querywords.add(weight);
		}
		return Querywords;
	}	
	
	public void normalizeRelevanceWeights() {
		double maxRelevanceWeight = 0.0;
		for (WordWeight wordWeight : wordWeights.values()) {
			double relevanceWeight = wordWeight.getRelevanceWeight();
			if (relevanceWeight > maxRelevanceWeight) {
				maxRelevanceWeight = relevanceWeight;
			}
		}
		for (WordWeight wordWeight : wordWeights.values()) {
			wordWeight.normalizeRelevanceWeight(maxRelevanceWeight);
		}
	}
	
	public void calculateNormalizedFrequencyWeights() {
		double maxFrequencyWeight = 0.0;
		for (WordWeight wordWeight : wordWeights.values()) {
			int frequency = wordWeight.getFrequency();
			int significance = wordWeight.getSignificanceLevel();
			double frequencyWeight = WeighingConfiguration.getFrequencyWeight(significance, frequency);
			wordWeight.setFrequencyWeight(frequencyWeight);
			if (frequencyWeight > maxFrequencyWeight) {
				maxFrequencyWeight = frequencyWeight;
			}
		}
		for (WordWeight wordWeight : wordWeights.values()) {
			wordWeight.normalizeFrequencyWeight(maxFrequencyWeight);
		}
	}
	
	public void describeModel() {
		for (WordWeight weight : wordWeights.values()) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(weight.getWord()).append(": ");
			buffer.append("Relevance: ").append((float) weight.getRelevanceWeight()).append(" ");
			buffer.append("Frequency: ").append(weight.getFrequency());
			if (weight.getSignificanceLevel() > 0) {
				buffer.append(" Level: ").append(weight.getSignificanceLevel());
			}
			buffer.append(" TF Weight: ").append((float) weight.getFrequencyWeight());
			buffer.append(" Final Weight: ").append((float) weight.getFinalWeight());
			System.out.println(buffer);
		}
	}
}
