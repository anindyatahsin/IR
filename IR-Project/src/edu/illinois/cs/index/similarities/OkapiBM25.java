package edu.illinois.cs.index.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

import cs.virginia.edu.ir.news.article.mapper.config.WeighingConfiguration;

public class OkapiBM25 extends SimilarityBase {
    /**
     * Returns a score for a single term in the document.
     *
     * @param stats
     *            Provides access to corpus-level statistics
     * @param termFreq
     * @param docLength
     */
    @Override
    protected float score(BasicStats stats, float termFreq, float docLength) {
        int N=(int)stats.getNumberOfDocuments();
        int df=(int)stats.getDocFreq();
        float k1=WeighingConfiguration.BM25_K1;
        float k2=999;
        float b=WeighingConfiguration.BM25_B;
        float c=termFreq;
        double temp1=Math.log((N-df+0.5)/(df+0.5));
        float navg=stats.getAvgFieldLength();
        float temp2=((k1+1)*c)/(k1*(1-b+(b*docLength/navg)+c));
        float temp3=((k2+1))/(k2+1);
       
        float ret=temp2*temp3*(float)temp1;
        return ret;
    }

    @Override
    public String toString() {
        return "Okapi BM25";
    }

}
