package cs.virginia.edu.ir.news.article.mapper.extra;

import java.util.Comparator;

import cs.virginia.edu.ir.news.article.mapper.analysis.CollectionModel;
import cs.virginia.edu.ir.news.article.mapper.analysis.WordWeight;

public class WeightComparator implements Comparator<WordWeight> {
    
	private CollectionModel collectionModel;
		
	public WeightComparator(CollectionModel collectionModel) {
		this.collectionModel = collectionModel;
	}

	@Override
	public int compare(WordWeight arg0, WordWeight arg1) {
		double priority1 = arg0.getProbability(collectionModel);
        double priority2 = arg1.getProbability(collectionModel);
        if(priority1 < priority2) return 1;
        if(priority1 == priority2) return 0;
        if(priority1 > priority2) return -1;
        return 0;
	}
}