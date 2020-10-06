import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class SpamFilter {

	public static Map<String, Word> words = new HashMap<>();
	
	/*
	 * amount of words in the spam corpus
	 */
	public static double amountSpam;
	
	/*
	 * amount of words in the ham corpus
	 */
	public static double amountHam;

	/*
	 * overall probability of mails beeing spam
	 */
	public static double overallProbSpam;
	
	/*
	 * overall probability of mails beeing ham
	 */
	public static double overallProbHam;
	
//******************************| Train |******************************	
	
	/*
	 * reads the train data from zip files
	 */
	public static void readData(String file, boolean spam) throws IOException {
		
		@SuppressWarnings("resource")
		ZipFile zipFile = new ZipFile(file);

	    Enumeration<? extends ZipEntry> entries = zipFile.entries();
	    String text;
	    
	    while(entries.hasMoreElements()){
	        ZipEntry entry = entries.nextElement();
	        InputStream stream = zipFile.getInputStream(entry);
	        text = new String(stream.readAllBytes());
	        String[] words = text.split("\\s+");
	        for (int i = 0; i < words.length; i++) {
				toWordMap(words[i], spam);
			}
	       
	    }
	    
		
	}
	
	/*
	 * adds words to the bag of words and increases amount
	 */
	public static void toWordMap(String word, boolean spam) {
		if(words.containsKey(word)) {
			if(spam) {
				words.get(word).countSpam++;
			} else {
				words.get(word).countHam++;
			}
		} else {
			Word w = new Word(word, 1, spam);
			words.put(word, w);
		}
	}
	
	/*
	 * if a word in the bag exists in ham mails, but not in spam, amount 0 gets replaced with a
	 * and vice versa.
	 */
	public static void applyAlpha(double a) {
		words.entrySet().forEach(entry -> {
			if(entry.getValue().countSpam == 0) {
				entry.getValue().countSpam = a;
			} else if(entry.getValue().countHam == 0) {
				entry.getValue().countHam = a;
			}
		});
	}
	
	/*
	 * calculates total probabilities and total amounts of words from the corpus.
	 */
	public static void calculateTotals() {
		words.entrySet().forEach(entry -> {
			amountSpam += entry.getValue().countSpam;
			amountHam += entry.getValue().countHam;
		});
		overallProbSpam = amountSpam / (amountHam + amountSpam);
		overallProbHam = amountHam / (amountHam + amountSpam);
	}
	
	/*
	 * calculates the bayesProbability of a mail beeing spam based on a single word given.
	 */
	public static double bayesProb(String word) {
		double WS = words.get(word).getSpamProb();
		double WH = words.get(word).getHamProb();
		return  (WS * overallProbSpam) / ((WS * overallProbSpam) + (WH * overallProbHam));  
	}
	
	/*
	 * applies the bayesProbability to the words in the bag of words
	 */
	public static void applyBayesProb() {
		words.entrySet().forEach(entry -> {
			entry.getValue().bayesProb = bayesProb(entry.getKey());
		});
	}
	
//******************************| Evaluate |******************************	

	/*
	 * Classifies if a mail is spam or ham. tresh is the Threshold, at which probability a mail should be classified as spam.
	 * boolean spam does not have any influence of the classification, but is used for a proper output.
	 */
	public static void evaluateMails(double thresh, String file, boolean spam) throws IOException {
		@SuppressWarnings("resource")
		ZipFile zipFile = new ZipFile(file);

	    Enumeration<? extends ZipEntry> entries = zipFile.entries();
	    String text;
	    
	    double countHam = 0;
    	double countSpam = 0;
    	int totCounter = 0;
	    while(entries.hasMoreElements()){
	    	double bayesSum = 0;
	    	int countWords = 0;
	        ZipEntry entry = entries.nextElement();
	        InputStream stream = zipFile.getInputStream(entry);
	        text = new String(stream.readAllBytes());
	        String[] wordsArray = text.split("\\s+");
	        for (int i = 0; i < wordsArray.length; i++) {
	        	if(words.containsKey(wordsArray[i])) {
	        		bayesSum += words.get(wordsArray[i]).bayesProb;
					countWords++;
	        	}
			}
	        if((bayesSum/countWords) >= thresh) {
	        	countSpam++;
	        } else {
	        	countHam++;
	        }
	        totCounter++;
	    }
	    if(spam) {
	    	System.out.println("True Spammails: " + totCounter);
	    	System.out.println("Spam labeled: " + (int)countSpam);
	    	System.out.println("Ham labeled: " + (int)countHam);
	    	System.out.println("Spam ratio: " + countSpam / (countHam + countSpam));
	    } else {
	    	System.out.println("True Hammails: " + totCounter);
	    	System.out.println("Ham labeled: " + (int)countHam);
	    	System.out.println("Spam labeled: " + (int)countSpam);
	    	System.out.println("Ham ratio: " + countHam / (countHam + countSpam));
	    }
	}
	
	
}
