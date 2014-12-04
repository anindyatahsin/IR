package cs.virginia.edu.ir.news.article.mapper.config;


import java.io.Writer;

import cs.virginia.edu.ir.news.article.mapper.analysis.CollectionModel;
import cs.virginia.edu.ir.news.article.mapper.analysis.PassageModel;

public class RunTimeConfiguration {
	public static PassageModel CURRENTPASSAGEMODEL;
	public static CollectionModel CURRENTCOLLECTIONMODEL;
	public static int CURRENTARTICLEID;
	public static String CURRENTARTICLECATEGORY;
	public static String CURRENTARTICLESOURCE;
	public static int CURRENTQUERYSIZE=40;
	public static float k1=(float)1.6;
	public static float k2=999;
	public static float b=.9f;
	public static Writer writer;
	public static Double Pi=.4;
}
