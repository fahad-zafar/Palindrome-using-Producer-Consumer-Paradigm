
/* Name: Muhammad Fahad Zafar
 * Email: mfahad.zafar@hotmail.com
 * Section: A
 * 
 * This thread class takes a bag from hashmap, find palindormic words and assign those words in a queue
 */

import java.util.*;

public class PalindromeWorker extends Thread {
	private ArrayList <String> key;
	private ArrayList <String> taskQueue;
	private int start;
	private int end;
	private int count = 0;
	private String name;

	public PalindromeWorker (ArrayList <String> key, ArrayList <String> sharedQueue, int start, int end, String name) {
		this.key = key;
		this.taskQueue = sharedQueue;
		this.start = start;
		this.end = end;
		this.name = name;
	}

	public final void run () 
{
		boolean check = false;
		if (start == 0 && end == 0) {
			check = true;
		}
		for (int k = 0; k < key.size(); k++) {
			ArrayList <String> arr = new ArrayList<String> ();
			arr = Palindrome.mapBagOfTasks.get(key.get(k));
			
			if (check == true)
				end = arr.size();
			for (int i = start; i < end; i++) {
				String word = arr.get(i);
				StringBuffer temp = new StringBuffer (word);
				temp.reverse();
				String reversedWord = temp.toString();
				if (word.equals(reversedWord)) {
					synchronized (taskQueue) {
						taskQueue.add(word);
						taskQueue.notifyAll();
					}
					count++;
				}
				else {
					for (int j = 0; j < arr.size(); j++) {
						word = arr.get(j);
						if (word.equals(reversedWord)) {
							synchronized (taskQueue) {
								taskQueue.add(word);
								taskQueue.notifyAll();
							}
							count++;
						}
					}
				}
			}
		}

		synchronized (taskQueue) {
			String threadEnding = name + ", Palindrome_Count: " + count;
			taskQueue.add(threadEnding);
			taskQueue.notifyAll();
		}
	}
	
	public int getCount () {
		return count;
	}
}