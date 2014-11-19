package cs.virginia.edu.ir.news.article.mapper.object;

import java.util.ArrayList;

public class WordProperties {
	
	private String POS_tag;
	private ArrayList<WordPosition> wordPos;
	
	public ArrayList<WordPosition> getWordPos() {
		return wordPos;
	}
	
	public void setWordPos(ArrayList<WordPosition> wordPos) {
		this.wordPos = wordPos;
	}
	
	public String getPOS_tag() {
		return POS_tag;
	}
	
	public void setPOS_tag(String pOS_tag) {
		POS_tag = pOS_tag;
	}
}
