package cs.virginia.edu.ir.news.article.mapper.Loading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.tartarus.snowball.ext.porterStemmer;

import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import cs.virginia.edu.ir.news.article.mapper.object.Paragraph;
import cs.virginia.edu.ir.news.article.mapper.object.SentencePosition;
import cs.virginia.edu.ir.news.article.mapper.object.WordPosition;
import cs.virginia.edu.ir.news.article.mapper.object.WordProperties;
import cs.virginia.edu.ir.news.article.mapper.output.Output;
import cs.virginia.edu.ir.news.article.mapper.utility.CommonUtils;

public class WordWeighting {

	private HashMap<String, Boolean> postag;
	private HashMap<String, WordProperties> level;
	private HashMap<SentencePosition, List<String>> wordMap;  
	private Output out = new Output();

	public WordWeighting() {
		postag = new HashMap<String, Boolean>();
		wordMap = new HashMap<SentencePosition, List<String>>();
		level = new HashMap<String, WordProperties>();
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
		postag.put("VBP",true); // Verb, non�3rd person singular present
		postag.put("VBZ",true); // Verb, 3rd person singular present
		postag.put("WDT",false); //  Wh�determiner
		postag.put("WP",false); // Wh�pronoun
		postag.put("WP$",false); // Possessive wh�pronoun
		postag.put("WRB",false); // Wh�adverb
		postag.put("#",false);
		postag.put(":",false);
	}

	public HashMap<String, Boolean> getPostag() {
		return postag;
	}

	public void setPostag(HashMap<String, Boolean> postag) {
		this.postag = postag;
	}

	public HashMap<String, WordProperties> getLevel() {
		return level;
	}

	public void setLevel(HashMap<String, WordProperties> level) {
		this.level = level;
	}

	public HashMap<SentencePosition, List<String>> getWordMap() {
		return wordMap;
	}

	public void setWordMap(HashMap<SentencePosition, List<String>> wordMap) {
		this.wordMap = wordMap;
	}

	public Output getOut() {
		return out;
	}

	public void setOut(Output out) {
		this.out = out;
	}
	
	

	public void weightArticle(NewsArticle article){
		String title = CommonUtils.tagSentences(article.getTitle());
		weightWords(title, true, 0, 0);
		int i = 1;
		for(Paragraph par : article.getParagraphs()){
			int j = 1;
			for(String line : par.getSentences()){
				String newLine = CommonUtils.tagSentences(line);
				weightWords(newLine, false, i, j++);
			}
			i++;
		}
	}

	public void weightWords(String line, boolean isTitle, int par, int sen){
		List<String> wordsInSentences = new ArrayList<String>();
		SentencePosition spos = new SentencePosition(par,sen);
		StringTokenizer st = new StringTokenizer(line," ");
		int i = 1;
		while(st.hasMoreTokens()){           
			StringTokenizer st1 = new StringTokenizer(st.nextToken(),"_");
			if(isTitle){
				while(st1.hasMoreTokens()){
					String s = st1.nextToken().toLowerCase().replaceAll("\\p{P}", "");
					s = CommonUtils.normalizeToken(s);	
					String pos = st1.nextToken();
					if(postag.containsKey(pos)){
						if(!postag.get(pos)){
							continue;
						}
					}
					if(!level.containsKey(s)){
						WordProperties wp = new WordProperties();
						wp.setPOS_tag(pos);
						WordPosition wpos = new WordPosition(0,0,0);
						ArrayList<WordPosition> wordpos = new ArrayList<WordPosition>();
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
					s = CommonUtils.normalizeToken(s);
					String pos = st1.nextToken();
					if(postag.containsKey(pos)){
						if(!postag.get(pos)){
							continue;
						}
					}
					if(!level.containsKey(s)){
						WordProperties wp = new WordProperties();
						wp.setPOS_tag(pos);
						WordPosition wpos = new WordPosition(par,sen,i++);
						ArrayList<WordPosition> wordpos = new ArrayList<WordPosition>();
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
	}

	public String printHashmap(){
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

	public String printHashmap2(){
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
}
