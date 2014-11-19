package cs.virginia.edu.ir.news.article.mapper.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import cs.virginia.edu.ir.news.article.mapper.config.Configuration;
import cs.virginia.edu.ir.news.article.mapper.object.SentencePosition;
import cs.virginia.edu.ir.news.article.mapper.object.WordLevel;
import cs.virginia.edu.ir.news.article.mapper.object.WordPosition;
import cs.virginia.edu.ir.news.article.mapper.object.WordProperties;
import cs.virginia.edu.ir.news.article.mapper.output.Output;

public class CommonUtils {

	public static SentenceModel SentenceDetectionModel;
	public static Output out =  new Output();
	public static POSModel POSDetectionModel;
	public static final int MAX = Integer.MAX_VALUE;
	
	static {
		
		InputStream sentenceModelFile = null;
		InputStream posModelFile = null;
		
		try {
			sentenceModelFile = new FileInputStream(Configuration.SENTENCE_MODEL_FILE);
			SentenceDetectionModel = new SentenceModel(sentenceModelFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (sentenceModelFile != null) {
				try {
					sentenceModelFile.close();
				} catch (IOException e) {
					System.out.println("could not load the sentence detector model");
					System.exit(1);
				}
			}
		}
		
		try {
			posModelFile = new FileInputStream(Configuration.POS_MODEL_FILE);
			POSDetectionModel = new POSModel(posModelFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (posModelFile != null) {
				try {
					posModelFile.close();
				} catch (IOException e) {
					System.out.println("could not load the sentence detector model");
					System.exit(1);
				}
			}
		}
		
		
	}
	
	public static List<String> extractSentences(String paragraphContent) {
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(SentenceDetectionModel);
		List<String> sentences = new ArrayList<String>();
		sentences.addAll(Arrays.asList(sentenceDetector.sentDetect(paragraphContent)));
		return sentences;
	}
	
	public static String tagSentences(String line){
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(POSDetectionModel);
		String posTaggedSentence = null;
		perfMon.start();
		if (line != null) {
	 
			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
					.tokenize(line);
			String[] tags = tagger.tag(whitespaceTokenizerLine);
	 
			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
			posTaggedSentence = sample.toString();
			perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();
		return posTaggedSentence;
	}
	
	
	public static void main(String args[]) {
		CommonUtils cmn = new CommonUtils();
		List<String> sentences = extractSentences("This is Yan. May I know your name? Oh, Sally!");
		out.delete("./test.txt");
		boolean title = true;
		int par = 0, sen = 0;
		for (String line : sentences){ 
			System.out.println(line);	
			tagSentences(line);
			title = false;
		}
		//out.writeToFile("./test.txt", printHashmap() + "\n");
	}
}
