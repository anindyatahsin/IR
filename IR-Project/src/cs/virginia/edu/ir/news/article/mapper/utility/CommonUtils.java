package cs.virginia.edu.ir.news.article.mapper.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import cs.virginia.edu.ir.news.article.mapper.config.Configuration;

public class CommonUtils {

	public static SentenceModel SentenceDetectionModel;
	
	public static POSModel POSDetectionModel;
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
	
	/* POS TAGS */
	
	/*
	 * 
	 	CC Coordinating conjunction
		CD Cardinal number
		DT Determiner
		EX Existential there
		FW Foreign word
		IN Preposition or subordinating conjunction
		JJ Adjective
		JJR Adjective, comparative
		JJS Adjective, superlative
		LS List item marker
		MD Modal
		NN Noun, singular or mass
		NNS Noun, plural
		NNP Proper noun, singular
		NNPS Proper noun, plural
		PDT Predeterminer
		POS Possessive ending
		PRP Personal pronoun
		PRP$ Possessive pronoun
		RB Adverb
		RBR Adverb, comparative
		RBS Adverb, superlative
		RP Particle
		SYM Symbol
		TO to
		UH Interjection
		VB Verb, base form
		VBD Verb, past tense
		VBG Verb, gerund or present participle
		VBN Verb, past participle
		VBP Verb, non�3rd person singular present
		VBZ Verb, 3rd person singular present
		WDT Wh�determiner
		WP Wh�pronoun
		WP$ Possessive wh�pronoun
		WRB Wh�adverb

	 * 
	 */
	
	public static void tagSentences(String line){
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(POSDetectionModel);
	 
		perfMon.start();
		if (line != null) {
	 
			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
					.tokenize(line);
			String[] tags = tagger.tag(whitespaceTokenizerLine);
	 
			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
			
			sample.toString();
			
			perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();
	}
	
	public static void main(String args[]) {
		List<String> sentences = extractSentences("This is Yan. May I know your name? Oh, Sally!");
		
		for (String line : sentences){ 
			System.out.println(line);	
			tagSentences(line);
		}
	}
}
