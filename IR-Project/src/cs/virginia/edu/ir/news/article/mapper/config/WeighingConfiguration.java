package cs.virginia.edu.ir.news.article.mapper.config;

public class WeighingConfiguration {

	// passage length in number of paragraphs.
	public static final int MINIMUM_PASSAGE_LENGTH = 1; 
	
	// minimum length of a paragraph in number of sentences to be considered 
	// significant. If the length is smaller than this then it will be added 
	// to the preceding paragraph during analysis
	public static final int MINIMUM_PARAGRAPH_LENGTH = 4;
	
	public static final double getRelevanceWeight(int level) {
		return 1 / Math.log(Math.pow(2, level));
	}
}
