package cs.virginia.edu.ir.news.article.mapper.analysis;

import java.io.FileInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import cs.virginia.edu.ir.news.article.mapper.Loading.WordWeighting;
import cs.virginia.edu.ir.news.article.mapper.config.DeploymentConfiguration;
import cs.virginia.edu.ir.news.article.mapper.config.WeighingConfiguration;
import cs.virginia.edu.ir.news.article.mapper.object.NewsArticle;
import cs.virginia.edu.ir.news.article.mapper.object.Paragraph;
import cs.virginia.edu.ir.news.article.mapper.object.SentencePosition;
import cs.virginia.edu.ir.news.article.mapper.object.WordPosition;
import cs.virginia.edu.ir.news.article.mapper.object.WordProperties;

public class ArticleWeighing {
	
	private NewsArticle article;
	private List<PassageModel> passageModels;
	
	public ArticleWeighing(NewsArticle article) {
		this.article = article;
		passageModels = new ArrayList<PassageModel>();
	}

	public NewsArticle getArticle() {
		return article;
	}

	public List<PassageModel> getPassageModels() {
		return passageModels;
	}
	
	public void initializePassageModels() {
		List<Paragraph> paragraphList = article.getParagraphs();
		Iterator<Paragraph> iterator = paragraphList.iterator();
		Set<Integer> paragraphsOfCurrentPassage = new HashSet<Integer>();
		int paragraphId = 1;
		int includedParagraphCount = 0;
		Paragraph paragraph = null;
		while (iterator.hasNext()) {
			paragraph = iterator.next();
			if (paragraph.getLength() < WeighingConfiguration.MINIMUM_PARAGRAPH_LENGTH) {
				paragraphsOfCurrentPassage.add(paragraphId);
			} else {
				if (includedParagraphCount >= WeighingConfiguration.MINIMUM_PASSAGE_LENGTH) {
					PassageModel model = new PassageModel(paragraphsOfCurrentPassage);
					passageModels.add(model);
					includedParagraphCount = 0;
					paragraphsOfCurrentPassage = new HashSet<Integer>();
				} else {
					includedParagraphCount++;
					paragraphsOfCurrentPassage.add(paragraphId);
				}
			}
			paragraphId++;
		}
		if (!paragraphsOfCurrentPassage.isEmpty()) {
			passageModels.add(new PassageModel(paragraphsOfCurrentPassage));
		}
	}
	
	public void weighTerms() throws Exception {
		
		WordWeighting ww = new WordWeighting();
		ww.weightArticle(article);
		
		FileInputStream tokenStream = new FileInputStream(DeploymentConfiguration.TOKEN_MODEL_FILE);
		Tokenizer tokenizer = new TokenizerME(new TokenizerModel(tokenStream));
		String title = article.getTitle();
		Set<String> rootWords = new HashSet<String>();
		String[] tokens = tokenizer.tokenize(title);
		for (String token : tokens) {
			rootWords.add(token.toLowerCase());
		}
		if (article.getSubTitle() != null) {
			tokens = tokenizer.tokenize(article.getSubTitle());
			for (String token : tokens) {
				rootWords.add(token.toLowerCase());
			}
		}
		
		for (PassageModel model : passageModels) {
			
			Map<String, WordWeight> wordWeightMap = model.getWordWeights();
			Set<String> visitedWordSet = new HashSet<String>();
			ArrayDeque<String> wordQueue = new ArrayDeque<String>();
			Set<Integer> paragraphIds = model.getParagraphIds();
			
			for (String rootWord : rootWords) {
				WordWeight wordWeight = new WordWeight(rootWord);
				wordWeight.setSignificanceLevel(1);
				wordWeightMap.put(rootWord, wordWeight);
				wordQueue.add(rootWord);
			}
			
			// assign relevance weight to words occurring in current passage
			while (!wordQueue.isEmpty()) {
				String word = wordQueue.remove();
				if (visitedWordSet.contains(word)) continue;
				
				WordProperties wordProperties = ww.getLevel().get(word);
				if (wordProperties == null) continue;
				
				List<WordPosition> wordPositionList = wordProperties.getWordPos();
				wordPositionList = filterWordPositionListByParagraphs(paragraphIds, wordPositionList);
				if (wordPositionList.isEmpty()) continue;
				
				for (WordPosition wordPosition : wordPositionList) {
					SentencePosition sentenceInfo = wordPosition.getSentencePosition();
					List<String> wordsInSentence = ww.getWordMap().get(sentenceInfo);
					Set<String> remainingWords = filterOutComparedWord(word, wordsInSentence);
					assignRelevanceWeightToWords(wordWeightMap.get(word), remainingWords, wordWeightMap);			
					for (String remaingWord : remainingWords) wordQueue.add(remaingWord); 
				}
		
				visitedWordSet.add(word);
			}
			
			// assign frequency weight to words occurring in current passage
			for (Map.Entry<SentencePosition, List<String>> entry : ww.getWordMap().entrySet()) {
				if (!paragraphIds.contains(entry.getKey().getParagraphNo())) continue;
				for (String word: entry.getValue()) {
					if (!wordWeightMap.containsKey(word)) {
						WordWeight weight = new WordWeight(word);
						wordWeightMap.put(word, weight);
					}
					wordWeightMap.get(word).addOccurrance();
				}
			}
			
		}
	}
	
	private List<WordPosition> filterWordPositionListByParagraphs(Set<Integer> paragraphIds, 
			List<WordPosition> wordPositionList) {
		List<WordPosition> filteredList = new ArrayList<WordPosition>();
		for (WordPosition wordPosition : wordPositionList) {
			SentencePosition sentencePosition = wordPosition.getSentencePosition();
			if (paragraphIds.contains(sentencePosition.getParagraphNo())) {
				filteredList.add(wordPosition);
			}
		}
		return filteredList;
	}
	
	private Set<String> filterOutComparedWord(String comparedWord, List<String> wordList) {
		Set<String> filteredWords = new HashSet<String>();
		for (String word : wordList) {
			if (!word.equals(comparedWord)) filteredWords.add(word);
		}
		return filteredWords;
	}
	
	private void assignRelevanceWeightToWords(WordWeight currentWord, 
			Set<String> coexistingWords, Map<String, WordWeight> wordWeightMap) {
		
		int currentWordLevel = currentWord.getSignificanceLevel();
		for (String word : coexistingWords) {
			WordWeight otherWord = null;
			if (wordWeightMap.containsKey(word)) {
				otherWord = wordWeightMap.get(word);
				
				if (otherWord.getSignificanceLevel() < currentWordLevel) {
					// do not weigh a more significant word for a less significant one
					continue;
				} else if (otherWord.getSignificanceLevel() == currentWordLevel) {
					// We have not decided yet what to do about co-occurrence of two words 
					// in the same level. For now this is been ignored
					continue;
				} else {
					// other word is less significant than current word so add weight
					double weight = WeighingConfiguration.getRelevanceWeight(currentWordLevel + 1);
					otherWord.addRelevanceWeight(weight);
					if (otherWord.getSignificanceLevel() > currentWordLevel + 1) {
						otherWord.setSignificanceLevel(currentWordLevel + 1);
					}
				}
			} else {
				otherWord = new WordWeight(word);
				double weight = WeighingConfiguration.getRelevanceWeight(currentWordLevel + 1);
				otherWord.addRelevanceWeight(weight);
				otherWord.setSignificanceLevel(currentWordLevel + 1);
				wordWeightMap.put(word, otherWord);
			}
		}
	}
}
