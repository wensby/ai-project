import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


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
	
	
	public void train(Database db)
	{
		ArrayList<User> users = retrieveUserFraction(db,0.10f);
		
		for( User u : users)
		{
			ArrayList<IntegerPair> trainData = 
					db.getTrainDataFor(u.getUserID());
		}
		
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
			if ( item.getKeywords().contains(keyword))
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
	
	
	private ArrayList<Float> getActionsFactors(Action a, Item i)
	{
		ArrayList<Float> result = new ArrayList<Float>();
		
		
		return result;
	}
}
