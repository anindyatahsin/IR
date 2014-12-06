package edu.illinois.cs.index;

import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cs.virginia.edu.ir.news.article.mapper.config.DeploymentConfiguration;
import cs.virginia.edu.ir.news.article.mapper.config.RunTimeConfiguration;
import cs.virginia.edu.ir.news.article.mapper.io.FileInput;
import edu.illinois.cs.index.similarities.*;

public class Runner {
	//please keep those constants 
    final static String _dataset = "npl";
    final static String _indexPath = "lucene-npl-index";
    final static String _prefix = "data/";
    final static String _file = "npl.txt";	


    public static ArrayList<ResultDoc> interactiveSearchpost(String index_path, String query, double weight) throws IOException {
        String method= "--ok";
    	Searcher searcher = new Searcher(index_path);
        setSimilarity(searcher, method);
        String input=query;
        SearchResult result = searcher.search(input);
        
        ArrayList<ResultDoc> results = result.getDocs();
        int rank = 1;
        if (results.size() == 0){
        	return null;
        }
        
        for(ResultDoc rdoc: results){
        	if(RunTimeConfiguration.DOCMAP.containsKey(rdoc.title())){
        		RunTimeConfiguration.DOCMAP.put(rdoc.title(), RunTimeConfiguration.DOCMAP.get(rdoc.title()) + weight * rdoc.score);
        	} else{
        		RunTimeConfiguration.DOCMAP.put(rdoc.title(), weight * rdoc.score);
        	}
        }
        
        return results;

    }    
    
    /**
     * Feel free to modify this function, if you want different display!
     *
     * @throws IOException
     */
    public static double interactiveSearch(String index_path, String query) throws IOException {
        String method= "--ok";
    	Searcher searcher = new Searcher(index_path);
        setSimilarity(searcher, method);
        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        //System.out.println("Type text to search, blank to quit.");
        //System.out.print("> ");
        String input=query;
        //while ((input = br.readLine()) != null && !input.equals("")) {
        SearchResult result = searcher.search(input);
        ArrayList<ResultDoc> results = result.getDocs();
        int rank = 1;
        if (results.size() == 0)
        	try{
        		RunTimeConfiguration.writer.write("\n"+"No results found!");
        	} catch (Exception e){
        		return 0.0;
        	}	
        return calculateMAP(results);
    }
    
    public static double calculateMAP(ArrayList<ResultDoc> results){
    	File folder = new File(DeploymentConfiguration.FEEDBACK_DIRECTORY);
    	File[] listOfFiles = folder.listFiles();
    	FileInput in = new FileInput();
    	String prefix = "";
    	ArrayList<Integer> relevance = new ArrayList<Integer>();
    	int num_rel = 0;
    	if(RunTimeConfiguration.CURRENTARTICLESOURCE.equals("alzajeera")){
    		prefix += RunTimeConfiguration.CURRENTARTICLESOURCE + "-" 
    				+ RunTimeConfiguration.CURRENTARTICLECATEGORY + "-article-"
    				+ RunTimeConfiguration.CURRENTARTICLEID + "-";
    	} else{
    		prefix += RunTimeConfiguration.CURRENTARTICLESOURCE + "-article-"
    				+ RunTimeConfiguration.CURRENTARTICLEID + "-";
    	}
    	for (int i = 0; i < listOfFiles.length; i++) {
    		if(listOfFiles[i].getName().startsWith(prefix)){
    			ArrayList<String> lines = in.readFromFile(listOfFiles[i].getAbsolutePath());
    			for(String line : lines){
    				String val[] = line.split("\\s+");
    				if(val.length == 7){
    					if(val[6].equals("Relevant")){
    						relevance.add(1);
    						num_rel++;
    					} else{
    						relevance.add(0);
    					}
    				}
    			}
    		}
    	}
    	int i = 1;
    	double avgp = 0.0;
		double numRel = 1;
    	for (ResultDoc rdoc : results) {
    		if (relevance.get(Integer.parseInt(rdoc.title()) - 1) == 1) {
				avgp += numRel / i;
				++numRel;
				//System.out.print("\n"+"  ");
			} else {
				//System.out.print("\n"+"X ");
			}
			++i;
		}
    	
    	return avgp / num_rel;  // returning AP
    	//int exp_i = Math.min(num_rel, i-1);
    	//return (numRel-1) / exp_i; //returning p@5
    }
    
    public static double calculateMAPPost(ArrayList results){
    	File folder = new File(DeploymentConfiguration.FEEDBACK_DIRECTORY);
    	File[] listOfFiles = folder.listFiles();
    	FileInput in = new FileInput();
    	String prefix = "";
    	ArrayList<Integer> relevance = new ArrayList<Integer>();
    	int num_rel = 0;
    	if(RunTimeConfiguration.CURRENTARTICLESOURCE.equals("alzajeera")){
    		prefix += RunTimeConfiguration.CURRENTARTICLESOURCE + "-" 
    				+ RunTimeConfiguration.CURRENTARTICLECATEGORY + "-article-"
    				+ RunTimeConfiguration.CURRENTARTICLEID + "-";
    	} else{
    		prefix += RunTimeConfiguration.CURRENTARTICLESOURCE + "-article-"
    				+ RunTimeConfiguration.CURRENTARTICLEID + "-";
    	}
    	for (int i = 0; i < listOfFiles.length; i++) {
    		if(listOfFiles[i].getName().startsWith(prefix)){
    			ArrayList<String> lines = in.readFromFile(listOfFiles[i].getAbsolutePath());
    			for(String line : lines){
    				String val[] = line.split("\\s+");
    				if(val.length == 7){
    					if(val[6].equals("Relevant")){
    						relevance.add(1);
    						num_rel++;
    					} else{
    						relevance.add(0);
    					}
    				}
    			}
    		}
    	}
    	
    	double avgp = 0.0;
		double numRel = 1;
    	int i;
    	//System.out.println(results);
		for (i = 1; i <= results.size(); i++) {
    		//if(i > 5) break;
			StringTokenizer st = new StringTokenizer(results.get(i-1).toString(), "=");
    		if(st.countTokens() != 2) continue;
    		if (relevance.get(Integer.parseInt(st.nextToken()) - 1) == 1) {
				avgp += numRel / i;
				++numRel;
				//System.out.print("\n"+"  ");
			} else {
				//System.out.print("\n"+"X ");
			}
		}
    	
    	return avgp / num_rel;  // returning AP
    	//int exp_i = Math.min(num_rel, i-1);
    	//return (numRel-1) / exp_i; //returning p@5
    }
    /*
    public static double calculateP5(ArrayList results){
    	File folder = new File(DeploymentConfiguration.FEEDBACK_DIRECTORY);
    	File[] listOfFiles = folder.listFiles();
    	FileInput in = new FileInput();
    	String prefix = "";
    	ArrayList<Integer> relevance = new ArrayList<Integer>();
    	int num_rel = 0;
    	if(RunTimeConfiguration.CURRENTARTICLESOURCE.equals("alzajeera")){
    		prefix += RunTimeConfiguration.CURRENTARTICLESOURCE + "-" 
    				+ RunTimeConfiguration.CURRENTARTICLECATEGORY + "-article-"
    				+ RunTimeConfiguration.CURRENTARTICLEID + "-";
    	} else{
    		prefix += RunTimeConfiguration.CURRENTARTICLESOURCE + "-article-"
    				+ RunTimeConfiguration.CURRENTARTICLEID + "-";
    	}
    	for (int i = 0; i < listOfFiles.length; i++) {
    		if(i == 6) break;
    		if(listOfFiles[i].getName().startsWith(prefix)){
    			ArrayList<String> lines = in.readFromFile(listOfFiles[i].getAbsolutePath());
    			for(String line : lines){
    				String val[] = line.split("\\s+");
    				if(val.length == 7){
    					if(val[6].equals("Relevant")){
    						relevance.add(1);
    						num_rel++;
    					} else{
    						relevance.add(0);
    					}
    				}
    			}
    		}
    	}
    	
    	double avgp = 0.0;
		double numRel = 1;
    	int i = 1;
		for (i = 1; i <= results.size(); i++) {
    		StringTokenizer st = new StringTokenizer(results.get(i-1).toString(), "=");
    		if(st.countTokens() != 2) continue;
    		if (relevance.get(Integer.parseInt(st.nextToken()) - 1) == 1) {
				avgp += numRel / i;
				++numRel;
				//System.out.print("\n"+"  ");
			} else {
				//System.out.print("\n"+"X ");
			}
		}
    	
    	//System.out.println(avgp / num_rel);
    	//return avgp / num_rel;  // returning AP
    	int exp_i = Math.min(num_rel, i-1);
    	return (numRel-1) / exp_i; //returning p@5
    } */

    public static void setSimilarity(Searcher searcher, String method) {
        if(method == null)
            return;
        else if(method.equals("--dp"))
            searcher.setSimilarity(new DirichletPrior());
        else if(method.equals("--jm"))
            searcher.setSimilarity(new JelinekMercer());
        else if(method.equals("--ok"))
            searcher.setSimilarity(new OkapiBM25());
        else if(method.equals("--pl"))
            searcher.setSimilarity(new PivotedLength());
        else if(method.equals("--tfidf"))
            searcher.setSimilarity(new TFIDFDotProduct());
        else if(method.equals("--bdp"))
            searcher.setSimilarity(new BooleanDotProduct());
        else
        {
            System.out.println("[Error]Unknown retrieval function specified!");
            printUsage();
            System.exit(1);
        }
    }

    private static void printUsage()
    {
        System.out.println("To specify a ranking function, make your last argument one of the following:");
        System.out.println("\t--dp\tDirichlet Prior");
        System.out.println("\t--jm\tJelinek-Mercer");
        System.out.println("\t--ok\tOkapi BM25");
        System.out.println("\t--pl\tPivoted Length Normalization");
        System.out.println("\t--tfidf\tTFIDF Dot Product");
        System.out.println("\t--bdp\tBoolean Dot Product");
    }
}
