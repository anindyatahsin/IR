package cs.virginia.edu.ir.news.article.mapper.extra;

import java.util.Comparator;

import cs.virginia.edu.ir.news.article.mapper.analysis.WordWeight;

/**
 * Class StartComparator<p>
 * Compares two gridlets according to their start time.
 * @author Dalibor Klusacek
 */
public class WeightComparator implements Comparator {
    
    /**
     * Compares two gridlets according to their start time
     */
    public int compare(Object o1, Object o2) {
        WordWeight g1 = (WordWeight) o1;
        WordWeight g2 = (WordWeight) o2;
        double priority1 = (Double) g1.getRelevanceWeight();
        double priority2 = (Double) g2.getRelevanceWeight();
        if(priority1 < priority2) return 1;
        if(priority1 == priority2) return 0;
        if(priority1 > priority2) return -1;
        return 0;
    }
    
}