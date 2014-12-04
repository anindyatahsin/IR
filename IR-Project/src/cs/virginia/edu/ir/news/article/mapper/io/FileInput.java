package cs.virginia.edu.ir.news.article.mapper.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileInput {
	BufferedReader reader = null;
	
	public ArrayList<String> readFromFile(String fileName){
		ArrayList<String> lines = new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(new File(fileName)));
			String line = reader.readLine();
			while(line != null){
				lines.add(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception occured while trying to read file");
		}
		return lines;
	}
	
}
