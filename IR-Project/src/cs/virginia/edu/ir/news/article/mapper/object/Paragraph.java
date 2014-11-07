package cs.virginia.edu.ir.news.article.mapper.object;

import java.util.ArrayList;
import java.util.List;

public class Paragraph {
	
	private List<String> sentences;

	public Paragraph() {
		sentences = new ArrayList<String>();
	}
	
	public List<String> getSentences() {
		return sentences;
	}

	public void setSentences(List<String> sentences) {
		this.sentences = sentences;
	}

	public void addSentence(String sentence) {
		sentences.add(sentence);
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for (String sentence : sentences) {
			buffer.append(sentence).append(' ');
		}
		buffer.append("\n");
		return buffer.toString();
	}
}
