package cs.virginia.edu.ir.news.article.mapper.object;

public class SentencePosition {
	private int sentenceNo;
	private int paragraphNo;
	
	public SentencePosition(){}
	
	public SentencePosition(int pn, int sn){
		sentenceNo = sn;
		paragraphNo = pn;
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
}
