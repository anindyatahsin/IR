package cs.virginia.edu.ir.news.article.mapper.analysis;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
	
	public void describeModel() {
		for (WordWeight weight : wordWeights.values()) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(weight.getWord()).append(": ");
			buffer.append("Relevance: ").append((float) weight.getRelevanceWeight()).append(" ");
			buffer.append("Frequency: ").append(weight.getFrequency());
			if (weight.getSignificanceLevel() > 0) {
				buffer.append(" Level: ").append(weight.getSignificanceLevel());
			}
			System.out.println(buffer);
		}
	}
}
