package cs.virginia.edu.ir.news.article.mapper.object;

public class WordPosition {
	private int sentenceNo;
	private int paragraphNo;
	private int wordNo;
	
	public WordPosition(int par, int sen, int word){
		paragraphNo = par;
		sentenceNo = sen;
		wordNo = word;
	}
	 
	public int getSentenceNo() {
		return sentenceNo;
	}
	public void setSentenceNo(int sentenceNo) {
		this.sentenceNo = sentenceNo;
	}
	public int getParagraphNo() {
		return paragraphNo;
	}
	public void setParagraphNo(int paragraphNo) {
		this.paragraphNo = paragraphNo;
	}
	public int getwordNo() {
		return wordNo;
	}
	public void setwordNo(int wordNo) {
		this.wordNo = wordNo;
	}
	
	
	
}
