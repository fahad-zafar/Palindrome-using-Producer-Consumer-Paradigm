
/* Name: Muhammad Fahad Zafar
 * Email: mfahad.zafar@hotmail.com
 * Section: A
 * 
 * This class makes hashmap full of strings and assign bags from hasmap to user entered number of threads
 */

import java.io.*;
import java.util.*;

public class Palindrome {

	static HashMap <String, ArrayList<String>> mapBagOfTasks = new HashMap <String, ArrayList<String>> ();
	
	// returns an ArrayList containing words of same length
	private static ArrayList <String> wordOfSameLengthFinder (int count, BufferedReader in, String inputPath) throws IOException {
		in =  new BufferedReader (new FileReader(inputPath));
		ArrayList <String> arr = new ArrayList<String> ();
		String word;
		while ((word = in.readLine()) != null) {
			int size = word.length();
			if (size == count) {
				arr.add (word);
			}
		}
		in.close();
		return arr;
	}

	public static void main (String ags []) throws IOException, InterruptedException {
		
		int count = 1, maxSize = 1;
		ArrayList <String> allKeys = new ArrayList <String> ();
		ArrayList <String> sharedQueue = new ArrayList <String> ();
		BufferedReader in = null;
		String inputPath = null, outputPath = null;

		try {
			inputPath = "C:/Users/Fahad/workspace/Assignment_1/src/words.txt";
			outputPath = "C:/Users/Fahad/workspace/Assignment_1/src/results.txt";
			in =  new BufferedReader (new FileReader(inputPath));
			
			// to find maximum length of a word in dictionary
			String word;
			while ((word = in.readLine()) != null) {
				int size = word.length();
				if (size > maxSize) {
					maxSize = size;
				}
			}

			// to put words of same length in a single arrayList
			while (count <= maxSize) {
				ArrayList <String> arr = new ArrayList<String> ();
				arr = wordOfSameLengthFinder(count, in, inputPath);
				String key = Integer.toString(count);
				allKeys.add(key);
				mapBagOfTasks.put(key, arr);
				count++;
			}

		} finally {
			if (in != null) {
				in.close();
			}
		}

		// getting worker threads input
		int totalThreads;
		System.out.println ("Enter Worker Threads: ");
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		totalThreads = input.nextInt();

		if (totalThreads == 0) {
			System.out.println("Worker Threads can never be 0. Using 1 Worker Thread as default");
			totalThreads = 1;
		}
	
		ArrayList <PalindromeWorker> workerThreads = new ArrayList <PalindromeWorker> (totalThreads);
		
		// one bag is assigned to each thread
		if (totalThreads == allKeys.size()) {
			for (int i = 0; i < totalThreads; i++) {
				ArrayList <String> key = new ArrayList <String> ();
				key.add(allKeys.get(i));
				String threadName = "Thread " + (i + 1);
				PalindromeWorker worker = new PalindromeWorker (key, sharedQueue, 0, 0, threadName);
				workerThreads.add(worker);
			}
		}
		
		// multiple bags are assigned to each thread
		if (totalThreads < allKeys.size()) {
			int k = 0;
			int bagsPerEachThread = allKeys.size() / totalThreads;
			for (int i = 0, l = (totalThreads * bagsPerEachThread); i < totalThreads; i++) {
				ArrayList <String> key = new ArrayList <String> ();
				for (int j = 0; j < bagsPerEachThread; j++) {
					key.add(allKeys.get(k++));
				}
				if (l != allKeys.size()) {
					key.add(allKeys.get(l++));
				}
				String threadName = "Thread " + (i + 1);
				PalindromeWorker worker = new PalindromeWorker (key, sharedQueue, 0, 0, threadName);
				workerThreads.add(worker);
			}
		}
		
		// bag is shared in multiple threads
		if (totalThreads > allKeys.size()) {
			int partsOfEachBag = (totalThreads / allKeys.size()) + 1;
			for (int i = 0, k = 0; i < totalThreads; k++) {
				int size = mapBagOfTasks.get(allKeys.get(k)).size();
				ArrayList <String> key = new ArrayList <String> ();
				key.add(allKeys.get(k));				
				int startPosition = 0, endPosition = size / partsOfEachBag;
				
				for (int j = 0; j < partsOfEachBag; j++, i++) {
					if (i == totalThreads)
						break;
					String threadName = "Thread " + (i + 1);
					PalindromeWorker worker = new PalindromeWorker (key, sharedQueue, startPosition, endPosition, threadName);
					workerThreads.add(worker);
					startPosition = endPosition;
					if (j == partsOfEachBag - 2)
						endPosition = size;
					else
						endPosition = endPosition + (size / partsOfEachBag);
				}
			}
		}
		
		PalindromeWriter writer = new PalindromeWriter (workerThreads, sharedQueue, outputPath);
		writer.start();
		
		for (int i = 0; i < totalThreads; i++) {
			workerThreads.get(i).start();
		}

		for (int i = 0; i < totalThreads; i++) {
			workerThreads.get(i).join();
		}
		
		writer.join();
	}
}
