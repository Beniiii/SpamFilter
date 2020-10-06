
public class Word {
	
	public String word;
	public double countHam;
	public double countSpam;
	public double bayesProb;
		
	public Word (String word, int count, boolean spam) {
		this.word = word;
		if(spam) countSpam = count;
		else countHam = count;
		this.bayesProb = 0.0;
	}
	
	/*
	 * Returns the probability of the word beeing in a spam mail
	 */
	public double getSpamProb() {
		return countSpam / (countHam + countSpam);	
	}
	
	/*
	 * Returns the probability of the word beeing in a ham mail
	 */
	public double getHamProb() {
		return countHam / (countHam + countSpam);	
	}
}