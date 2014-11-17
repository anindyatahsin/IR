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
	public static HashMap<String, Boolean> postag = new HashMap<String, Boolean>();
	public static HashMap<String, WordProperties> level = new HashMap<String, WordProperties>();
	public static HashMap<SentencePosition, List<String>> wordMap = new HashMap<>();  
	public static final int MAX = Integer.MAX_VALUE;
	
	
	
	public static void initialize(){
		postag.put("CC",false); // Coordinating conjunction
		postag.put("CD",false); // Cardinal number
		postag.put("DT",false); // Determiner
		postag.put("EX",false); // Existential there
		postag.put("FW",false); // Foreign word
		postag.put("IN",false); // Preposition or subordinating conjunction
		postag.put("JJ",false); // Adjective
		postag.put("JJR",false); // Adjective, comparative
		postag.put("JJS",false); // Adjective, superlative
		postag.put("LS",false); // List item marker
		postag.put("MD",false); // Modal
		postag.put("NN",true); // Noun, singular or mass
		postag.put("NNS", true); // Noun, plural
		postag.put("NNP",true); // Proper noun, singular
		postag.put("NNPS",true); // Proper noun, plural
		postag.put("PDT",false); // Predeterminer
		postag.put("POS",false); // Possessive ending
		postag.put("PRP",false); // Personal pronoun
		postag.put("PRP$",false); // Possessive pronoun
		postag.put("RB",false); // Adverb
		postag.put("RBR",false); // Adverb, comparative
		postag.put("RBS",false); // Adverb, superlative
		postag.put("PR",false); // Particle
		postag.put("SYM",true); // Symbol
		postag.put("TO",false); // to
		postag.put("UH",false); // Interjection
		postag.put("VB",true); // Verb, base form
		postag.put("VBD",true); // Verb, past tense
		postag.put("VBG",true); // Verb, gerund or present participle
		postag.put("VBN",true); // Verb, past participle
		postag.put("VBP",true); // Verb, non­3rd person singular present
		postag.put("VBZ",true); // Verb, 3rd person singular present
		postag.put("WDT",false); //  Wh­determiner
		postag.put("WP",false); // Wh­pronoun
		postag.put("WP$",false); // Possessive wh­pronoun
		postag.put("WRB",false); // Wh­adverb
		postag.put("#",false);
		postag.put(":",false);
	}
	
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
	
	
	
	public static void tagSentences(String line, boolean isTitle, int par, int sen){
		System.out.println("Trying to tag: " + line);
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(POSDetectionModel);
		List<String> wordsInSentences = new ArrayList<String>();
		SentencePosition spos = new SentencePosition(par,sen);
		perfMon.start();
		if (line != null) {
	 
			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
					.tokenize(line);
			String[] tags = tagger.tag(whitespaceTokenizerLine);
	 
			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
			
			StringTokenizer st = new StringTokenizer(sample.toString()," ");
			int i = 1;
			while(st.hasMoreTokens()){
	            
	            StringTokenizer st1 = new StringTokenizer(st.nextToken(),"_");
	            
	            
	            	//out.writeToFile("./test.txt",st1.nextToken() + " ");
	            	
	            if(isTitle){
	            	while(st1.hasMoreTokens()){
	            		String s = st1.nextToken().toLowerCase().replaceAll("\\p{P}", "");
	            		String pos = st1.nextToken();
	            		//System.out.println(pos);
	            		if(postag.containsKey(pos)){
	            			if(!postag.get(pos)){
	            				continue;
	            			}
	            		}
	            		if(!level.containsKey(s)){
	            			//level.put(s,);
	            			WordProperties wp = new WordProperties();
	            			wp.setPOS_tag(pos);
	            			WordPosition wpos = new WordPosition(0,0,0);
	            			ArrayList<WordPosition> wordpos = new ArrayList<>();
	            			wordpos.add(wpos);
	            			wp.setWordPos(wordpos);
	            			level.put(s, wp);
	            			wordsInSentences.add(s);
	            		}
	            		
	            	}
	            }
	            else{
	            	while(st1.hasMoreTokens()){
	            		String s = st1.nextToken().toLowerCase().replaceAll("\\p{P}", "");
	            		
	            		String pos = st1.nextToken();
	            		//System.out.println(pos);
	            		if(postag.containsKey(pos)){
	            			if(!postag.get(pos)){
	            				continue;
	            			}
	            		}
	            		if(!level.containsKey(s)){
	            			WordProperties wp = new WordProperties();
	            			wp.setPOS_tag(pos);
	            			WordPosition wpos = new WordPosition(par,sen,i++);
	            			ArrayList<WordPosition> wordpos = new ArrayList<>();
	            			wordpos.add(wpos);
	            			wp.setWordPos(wordpos);
	            			level.put(s, wp);
	            			wordsInSentences.add(s);
	            		}
	            		else{
	            			WordProperties wp = level.get(s);
	            			ArrayList<WordPosition> wordpos = wp.getWordPos();
	            			WordPosition wpos = new WordPosition(par,sen,i++);
	            			wordpos.add(wpos);
	            			wp.setWordPos(wordpos);
	            			level.put(s, wp);
	            			wordsInSentences.add(s);
	            		}
	            		
	            	}
	            }
	            
	        }
			wordMap.put(spos, wordsInSentences);
			
			perfMon.incrementCounter();
		}
		//perfMon.stopAndPrintFinalResult();
	}
	
	
	public static String printHashmap(){
		HashMap map = level;
		Set<String> keys = map.keySet();
		String toPrint = "{\n";
		for(String key : keys) { 
			 toPrint += "{" + key + " : ";
			 WordProperties wp = (WordProperties)map.get(key);
			 toPrint += "(" + wp.getPOS_tag() + ", ";
			 ArrayList<WordPosition> wordpos = wp.getWordPos();
			 if(wordpos != null){
				 //toPrint += "<";
				 for(int i = 0; i < wordpos.size(); i++){
					 WordPosition wpos = wordpos.get(i);
					 toPrint +=  "<" + wpos.getParagraphNo() + "," + wpos.getSentenceNo() + "," + wpos.getwordNo() +">";
				 }
				 toPrint += ")";
			 }
			 else{
				 toPrint += "null)";
			 }
			 toPrint += "}\n";
		}
		toPrint += "}";
		return toPrint;
	}
	
	public static String printHashmap2(){
		HashMap map = wordMap;
		Set<SentencePosition> keys = map.keySet();
		String toPrint = "{\n";
		for(SentencePosition key : keys) { 
			 toPrint += "{<" + key.getParagraphNo() + ", " + key.getSentenceNo() + "> : ( ";
			 List<String> wp = (List<String>)map.get(key);
			 for(String st : wp){
				toPrint += st + ", "; 
			 }
			 
			 toPrint += ")}" + "\n";
		}
		toPrint += "}";
		return toPrint;
	}
	
	public static void main(String args[]) {
		CommonUtils cmn = new CommonUtils();
		List<String> sentences = extractSentences("This is Yan. May I know your name? Oh, Sally!");
		out.delete("./test.txt");
		boolean title = true;
		int par = 0, sen = 0;
		for (String line : sentences){ 
			System.out.println(line);	
			tagSentences(line, title, par, sen++);
			title = false;
		}
		out.writeToFile("./test.txt", printHashmap() + "\n");
	}
}
