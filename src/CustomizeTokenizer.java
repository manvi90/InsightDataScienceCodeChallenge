import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.Comparator;

/**
 * This is a class providing two functionality: word count and Running median
 *
 */
public class CustomizeTokenizer {
	private static List<File> wc_input = new ArrayList<File>();
	private static TreeMap<String, Integer> wordCount = new TreeMap<String, Integer>();
	private static PriorityQueue<Integer> upperQueue;
	private static PriorityQueue<Integer> lowerQueue;
	private static String inputFolder = "wc_input";
	private static String outputFolder = "wc_output";
	private static String parent = "";
	
	/**
	 * Initialized priority queues for running median calculation in
	 * computeMedian()
	 *
	 */
	static {
		lowerQueue = new PriorityQueue<Integer>(20, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {

				return -o1.compareTo(o2);
			}

		});
		upperQueue = new PriorityQueue<Integer>();
		upperQueue.add(Integer.MAX_VALUE);
		lowerQueue.add(Integer.MIN_VALUE);
	}

	public static void main(String args[]) {
		if(args.length >= 2) {
			inputFolder = args[0];
			outputFolder = args[1];
		}
		getFiles(inputFolder);
		ReadFile();
		readSortedFile();

	}

	/**
	 * Read input .txt files from wc_input
	 */
	public static void getFiles(String file) {
		// check for all the files in the folder wc_input
		File f = new File(file);
		for (File i : f.listFiles()) {
			if (i.isFile() && !i.isHidden()) {
				wc_input.add(i);
			} else if (i.isDirectory()) {
				getFiles(i.getPath());
			}
		}

	}

	/**
	 * Read text file line by line and run word tokenizer to calculate word
	 * counts
	 */
	public static void ReadFile() {
		for (File i : wc_input) {
			FileReader filereader;
			try {
				filereader = new FileReader(i);
				BufferedReader bufferedReader = new BufferedReader(filereader);
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					wordTokenizer(line);
				}
				filereader.close();

			} catch (FileNotFoundException e) {
				e.getMessage();
			} catch (IOException e) {
				e.getMessage();
			}
		}

	}

	/**
	 * Method to read files in sorted order
	 */
	public static void readSortedFile() {
		// Reading file in sorted order
		Collections.sort(wc_input);
		File outputFile1 = new File(outputFolder + "/med_result.txt");
		FileWriter medWriter;
		try {
			medWriter = new FileWriter(outputFile1.getAbsolutePath());
			BufferedWriter med = new BufferedWriter(medWriter);

			for (File i : wc_input) {
				FileReader filereader;

				filereader = new FileReader(i);
				BufferedReader bufferedReader = new BufferedReader(filereader);
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					int count = sentenceTokenizer(line);
					double median = computeMedian(count);
					System.out.println(median);
					med.write(median + "\n");
				}
				med.close();
				filereader.close();
			}
		} catch (FileNotFoundException e) {
			e.getMessage();
		} catch (IOException e) {
			e.getMessage();
		}
	}

	/**
	 * Method to tokenize text file into word tokens
	 */
	public static void wordTokenizer(String st) {
		String[] tokens = st.toLowerCase().split("[\\s.,!?\";:+-=*'$%~^&{}()]");
		Integer count = 0;
		File outputFile = new File(outputFolder + "/wc_result.txt");
		for (String i : tokens) {
			if (i == null) {
				continue;
			} else if (!wordCount.containsKey(i)) {
				wordCount.put(i, count + 1);
			} else {
				wordCount.put(i, wordCount.get(i) + 1);
			}
		}
		try {
			FileWriter wordWriter = new FileWriter(outputFile.getAbsolutePath());
			BufferedWriter wc = new BufferedWriter(wordWriter);
			for (String key : wordCount.keySet()) {
				wc.write(key + " " + wordCount.get(key) + "\n");
			}
			wc.close();
		} catch (IOException e) {
			e.getMessage();
		}
	}

	/**
	 * Method to tokenize sentences from the text file and calculate count of
	 * number of word in line
	 */
	public static int sentenceTokenizer(String st) {
		String[] tokens = st.toLowerCase().split("[\\s.,!?\";:+-=*'$%~^&{}()]");
		return tokens.length;
	}

	/**
	 * Method to compute median using mimimum heap and maximum heap
	 */
	public static double computeMedian(int num) {
		// adding the number to proper heap
		if (num >= upperQueue.peek())
			upperQueue.add(num);
		else
			lowerQueue.add(num);
		// balancing the heaps
		if (upperQueue.size() - lowerQueue.size() == 2)
			lowerQueue.add(upperQueue.poll());
		else if (lowerQueue.size() - upperQueue.size() == 2)
			upperQueue.add(lowerQueue.poll());
		// returning the median
		if (upperQueue.size() == lowerQueue.size())
			return (upperQueue.peek() + lowerQueue.peek()) / 2.0;
		else if (upperQueue.size() > lowerQueue.size())
			return upperQueue.peek();
		else
			return lowerQueue.peek();
	}

}
