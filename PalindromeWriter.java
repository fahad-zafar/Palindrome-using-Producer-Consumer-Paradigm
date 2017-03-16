
/* Name: Muhammad Fahad Zafar
 * Email: mfahad.zafar@hotmail.com
 * Section: A
 * 
 * This thread removes words from queue and write them to an output file
 */

import java.util.*;
import java.io.*;

public class PalindromeWriter extends Thread {
	private ArrayList <PalindromeWorker> workers;
	private ArrayList <String> taskQueue;
	private String outputPath;
	PrintWriter outputStream = null;
	ArrayList <String> endOfEachThread = new ArrayList <String> ();
	
	public PalindromeWriter (ArrayList <PalindromeWorker> workers, ArrayList <String> sharedQueue, String outputPath) {
		this.workers = workers;
		this.taskQueue = sharedQueue;
		this.outputPath = outputPath;
	}
	
	public void run () {
		
		try {
			outputStream = new PrintWriter(new FileWriter(outputPath));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while (true) {
			synchronized (taskQueue)
			{
				while (taskQueue.isEmpty())
				{
					try {
						taskQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				String word = taskQueue.remove(0);
				if (word.contains("Thread "))
					endOfEachThread.add(word);
				else
					outputStream.println(word);
				
				if (endOfEachThread.size() == workers.size())
					break;
			}
		}
		int total = 0;
		for (int i = 0; i < endOfEachThread.size(); i++) {
			outputStream.println(endOfEachThread.get(i));
			total = total + workers.get(i).getCount();
		}
		
		outputStream.println("Total Count of Palindromes: " + total);
		outputStream.close();
	}
}
