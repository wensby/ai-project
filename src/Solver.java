
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;


public class Solver 
{
	Solver()
	{
	}
	
	/**
	 * Predict wether or not a user will follow or not a list of
	 * items ID
	 * 
	 * @param userID The userID 
	 * @param itemsID The item presented to a user
	 * @return
	 */
	public ArrayList<Boolean> predict(int userID, ArrayList<Integer> itemsID)
	{
		ArrayList<Boolean> results = new ArrayList<Boolean>();
		
		// Do something
		// Say we used some kind of svm
		Database db;
		try {
			db = new Database("test");
			
			User targetedUser = db.getUserUsingID(userID);
			HashMap<Integer, Double> keywords = db.getKeywords(userID);
			
			
			for (int i=0; i < itemsID.size(); i++)
			{
				Item item = db.getItemUsingID(itemsID.get(i));
				
				// Here we should represent our vector 
				// Basicly it's about how to add more information to this user
				
				double keywordFactor = keywordsLikelyhood(targetedUser, item);
				
				// Do calculation to predict
				boolean prediction = false;
				
				results.add(prediction);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return results;
	}
	
	
	public void train(Database db, HashMap<Integer,Item> items)
	{
		ArrayList<User> users = retrieveUserFraction(db,0.10f);
		
		ArrayList<Integer> dataClass = new ArrayList<Integer>();
		ArrayList<Vector<Integer>> features = new ArrayList<Vector<Integer>>();
		
		for( User u : users)
		{
			ArrayList<IntegerPair> trainData = 
					db.getTrainDataFor(u.getUserID());
			
			for (IntegerPair pair : trainData)
			{
				dataClass.add( pair.K);
				features.add(Feature.getFeatureVector(u, items.get(pair.V)));
			}
		}
		
		// Output the data for SVM
		try {
			FileWriter fWriter = new FileWriter("test.svm");
			int featureSize = features.get(0).size();
			
			for (int i=0; i < dataClass.size(); i++) {
				fWriter.write(dataClass.get(i));
				Vector<Integer> feature = features.get(i);
				
				for (int j=0; j < featureSize; j++)
				{
					fWriter.write(" " + j + ":" + feature.get(j));
				}
				fWriter.write("\n");
			}
			
			fWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Now we should launch the svm
	}
	
	

	/**
	 * Retrieve a fraction of data.
	 * @param fractionOfData
	 */
	public ArrayList<User> retrieveUserFraction(Database db, float fractionOfData)
	{
		ArrayList<User> results = new ArrayList<User>();
		try {
			int tableLength = (db.length("user_profiles"));
			int selectedLength = (int) (tableLength * fractionOfData);
			
			for (int i=0;i<selectedLength;i++)
			{
				int offset = (int) (Math.random()* selectedLength);
				Object[] obj = db.getOneRow("user_profiles", offset);
				
				int id = (Integer)obj[0];
				
				// TODO handle differently actions and follows
				User currentUser = new User(id,db);
				
				results.add(currentUser);
			}
			
			db.closeConnection();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return results;
	}
	
	
	
	// Features______________________________________________________
	
	
	private double keywordsLikelyhood(User user, Item item) {
		
		double sum = 0.0f;
		float matchingKeywords = 0;
		
		HashMap<Integer, Double> userKeywords = user.getKeywords();
		Set<Integer> keySet = userKeywords.keySet();
		
		for ( int keyword : keySet )
		{
			if ( item.getKeywords().containsKey(keyword))
			{
				matchingKeywords++;
				sum += userKeywords.get(keyword);
			}
		}
		
		// In order to take account of the number of keywords;
		// raw factor from 0 to 1 so we make the factor from 
		double raw_factor = matchingKeywords / userKeywords.size();
		sum = ( 1 + Math.cos(Math.PI*(1+raw_factor)/2)) *sum;
		
		return sum;
	}
}