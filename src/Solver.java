import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


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
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		// Do something
		
		return result;
	}
	
	/**
	 * Retrieve a fraction of data.
	 * @param fractionOfData
	 */
	public ArrayList<User> retrieveUserFraction(float fractionOfData)
	{
		ArrayList<User> results = new ArrayList<User>();
		Database db;
		try {
			db = new Database();
			
			int tableLength = (db.length("users"));
			int selectedLength = (int) (tableLength * fractionOfData);
			
			for (int i=0;i<selectedLength;i++)
			{
				int offset = (int) (Math.random()* selectedLength);
				Object[] obj = db.getOneRow("users", offset);
				
				int id = (Integer)obj[0];
				int yearofBirth = (Integer)obj[1];
				int gender = (Integer)obj[2];
				int numberOfTweet = (Integer)obj[3];
				
				// TODO handle differently actions and follows
				User currentUser = new User(id,yearofBirth,gender,
						numberOfTweet,null,null);
				
				results.add(currentUser);
			}
			
			db.close_connection();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return results;
	}
}
