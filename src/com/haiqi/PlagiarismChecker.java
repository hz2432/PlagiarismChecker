package com.haiqi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PlagiarismChecker {
	/**
	 * Main for the command line
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if(args.length > 0) {
			if(args.length < 3 || args.length > 4) {
				System.err.println("Wrong Input Format");
				System.exit(-1);
			}
			String fileName1 = args[0];
			String fileName2 = args[1];
			String synonymsFileName = args[2];
			int N = 3;
			if(args.length == 4) {
				N = Integer.valueOf(args[3]);
			}
			getPlagiarismScore(fileName1, fileName2, synonymsFileName, N);
		}
	}
	
	/**
	 * Output the score
	 * 
	 * @param fileName1: The file path which we want to get the score
	 * @param fileName2: The file path which we want to detect how many grams the file1 appear in
	 * @param synonymsFileName: The synonyms file
	 * @param N: The number of gram
	 * @throws IOException
	 */
	public static void getPlagiarismScore(String fileName1, String fileName2, String synonymsFileName, int N) throws IOException {
		Map<String, String> synonymsDict =  getNSynonymGramDict(synonymsFileName);
		Set<String> source = getNSynonymGram(fileName1, synonymsDict, N);
		Set<String> gramDict = getNSynonymGram(fileName2, synonymsDict, N);
		int count = 0;
		Iterator<String> it = source.iterator();
		while(it.hasNext()) {
			String st = it.next();
			if(gramDict.contains(st)) count++;
		}
		double rst = 0.0;
		if(source.size() > 0) {
			rst = (double)count / source.size();
			System.out.println("Grams match: " + rst);
		}
	}

	/**
	 * Get the Synonym dictionary
	 * 
	 * @param fileName: The synonym dictionary file name
	 * @output Return a HashMap which key is the word in the dictionary and value is the first synonym word
	 * @throws IOException
	 */
	private static Map<String, String> getNSynonymGramDict(String fileName) throws IOException {
		BufferedReader br = null;
		Map<String, String> dict = new HashMap<>();
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while((line = br.readLine()) != null) {
				String[] words = line.toLowerCase().trim().split(" ");
				if(words.length == 0) continue;
				String unic = words[0];
				for(String word : words) {
					dict.put(word, unic);
				}
			}
		} finally {
			if(br != null) {
				br.close();
			}
		}
		return dict;
	}

	/**
	 * Get the N gram synonym string
	 * 
	 * @param fileName: text file path
	 * @param dict: synonym dictionary
	 * @param N: number of gram
	 * @output Return the combination of all N N gram synonym string of this file
	 * @throws IOException
	 */
	private static Set<String> getNSynonymGram(String fileName, Map<String, String> dict, int N) throws IOException {
		BufferedReader br = null;
		Set<String> set = new HashSet<>();
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while((line = br.readLine()) != null) {
				String newLine = stringProcess(line);
				String[] words = newLine.toLowerCase().trim().split(" ");
				if(words.length < N) break;
				for(int i = 0; i <= words.length - N; i++) {
					StringBuilder sb = new StringBuilder();
					for(int j = i; j < i + N; j++) {
						if(dict.containsKey(words[j])) sb.append(dict.get(words[j])).append(" ");
						else sb.append(words[j]).append(" ");
					}
					set.add(sb.toString().trim());
				}
			}
		} finally {
			if(br != null) {
				br.close();
			}
		}
		
		return set;
	}
	
	/**
	 * Ingnore all the symbols in the string
	 * 
	 * @param s
	 * @throws IOException
	 */
	private static String stringProcess(String s) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);
			if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == ' ') {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}