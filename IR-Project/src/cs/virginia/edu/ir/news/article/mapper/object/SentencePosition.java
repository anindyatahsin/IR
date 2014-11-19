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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + paragraphNo;
		result = prime * result + sentenceNo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SentencePosition other = (SentencePosition) obj;
		if (paragraphNo != other.paragraphNo)
			return false;
		if (sentenceNo != other.sentenceNo)
			return false;
		return true;
	}
}
