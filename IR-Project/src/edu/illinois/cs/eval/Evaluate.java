package edu.illinois.cs.eval;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import edu.illinois.cs.index.ResultDoc;
import edu.illinois.cs.index.Runner;
import edu.illinois.cs.index.Searcher;

public class Evaluate {
	/**
	 * Format for judgements.txt is:
	 * 
	 * line 0: <query 1 text> line 1: <space-delimited list of relevant URLs>
	 * line 2: <query 2 text> line 3: <space-delimited list of relevant URLs>
	 * ...
	 * Please keep all these constants!
	 */
	
	private static final String _judgeFile = "npl-judgements.txt";
	final static String _indexPath = "lucene-npl-index";
	static Searcher _searcher = null;

	////This enables you to interact with the program in command line
//	public static void main(String[] args) throws IOException {
//		_searcher = new Searcher(_indexPath);
//		if(args.length == 1)
//			Runner.setSimilarity(_searcher, args[0]);
//		BufferedReader br = new BufferedReader(new FileReader(_judgeFile));
//		String line = null;
//		double avgPrecSum = 0.0;
//		double numQueries = 0.0;
//		while ((line = br.readLine()) != null) {
//			avgPrecSum += eval(line, br.readLine());
//			++numQueries;
//		}
//		br.close();
//
//		System.out.println("\nMAP: " + avgPrecSum / numQueries);
//	}
	
	////This makes it easier for you to run the program in an IDE
	public static void main(String[] args) throws IOException {
		String method = "--bdp";//specify the ranker you want to test
		
		_searcher = new Searcher(_indexPath);		
		Runner.setSimilarity(_searcher, method);
		BufferedReader br = new BufferedReader(new FileReader(_judgeFile));
		String line = null;
		double avgPrecSum = 0.0;
		double numQueries = 0.0;
		while ((line = br.readLine()) != null) {
			avgPrecSum += eval(line, br.readLine());
			++numQueries;
		}
		br.close();

		System.out.println("\nMAP: " + avgPrecSum / numQueries);//this is the final performance of your selected ranker
	}

	private static double eval(String query, String docString) {
		ArrayList<ResultDoc> results = _searcher.search(query).getDocs();
		if (results.size() == 0)
			return 0;

		HashSet<String> relDocs = new HashSet<String>(Arrays.asList(docString
				.split(" ")));
		int i = 1;
		double avgp = 0.0;
		double numRel = 1;
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				avgp += numRel / i;
				++numRel;
				System.out.print("  ");
			} else {
				System.out.print("X ");
			}
			System.out.println(i + ". " + rdoc.title());
			++i;
		}
		System.out.println("Average Precision: " + (avgp / (i - 1)));
		return avgp / (i - 1);
	}
}