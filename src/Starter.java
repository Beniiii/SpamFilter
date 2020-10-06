import java.io.IOException;

/*
 * Run this class to start the application
 */
public class Starter {

	public static void main(String[] args) {
		double alpha = 0.001;
		double thresh = 0.17;
		try {
			System.out.println("Alpha: " + alpha);
			System.out.println("Thresh: " + thresh);
			System.out.println();
			System.out.println("******************************");
			System.out.println();
			
		//******************************| Train |******************************	
			
			SpamFilter.readData("./data/ham-anlern.zip", false);
			SpamFilter.readData("./data/spam-anlern.zip", true);
			SpamFilter.applyAlpha(alpha);
			SpamFilter.calculateTotals();
			SpamFilter.applyBayesProb();
			
		//******************************| Evaluate |******************************
			
			System.out.println("Evaluate Ham: ");
			SpamFilter.evaluateMails(thresh, "./data/ham-kallibrierung.zip", false);
			System.out.println();
			System.out.println("Evaluate Spam: ");
			SpamFilter.evaluateMails(thresh, "./data/spam-kallibrierung.zip", true);
			System.out.println();
			System.out.println("******************************");
			System.out.println();
			
		//******************************| Test |******************************	
			
			System.out.println("Test Ham: ");
			SpamFilter.evaluateMails(thresh, "./data/ham-test.zip", false);
			System.out.println();
			System.out.println("Test Spam: ");
			SpamFilter.evaluateMails(thresh, "./data/spam-test.zip", true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
