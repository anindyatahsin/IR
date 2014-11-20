package cs.virginia.edu.ir.news.article.mapper.object;

public class WordPosition {
	
	private int sentenceNo;
	private int paragraphNo;
	private int wordNo;
	private boolean analyzed;
	
	public WordPosition(int par, int sen, int word){
		paragraphNo = par;
		sentenceNo = sen;
		wordNo = word;
		analyzed = false;
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
	
	public SentencePosition getSentencePosition() {
		return new SentencePosition(paragraphNo, sentenceNo);
	}

	public boolean isAnalyzed() {
		return analyzed;
	}

	public void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
	}
}
