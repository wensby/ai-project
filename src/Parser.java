import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	public void user_profile_txt(String input_line){
		
		//Given line from user_profile_txt: Parse out (userID) (birthyear) (gender) (#tweets) (Tag-IDs) 
		StringTokenizer st = new StringTokenizer(line);
		
		int userID =Integer.parseInt(st.nextToken());
		String birthYear =st.nextToken();
		int gender =Integer.parseInt(st.nextToken());
		int tweets =Integer.parseInt(st.nextToken());
		String tagIDs_not_parsed =m.group(st.nextToken());
		
		//Given (tagIDs_not_parsed) extracted from input line, extract each tag
		StringTokenizer stTagID = new StringTokenizer(tagIDs_not_parsed,';');
		
		List<Integer> tagIDs= new ArrayList<Integer>();
		
		while(st.hasMoreTokens()){
			Integer tagID = Integer.parseInt(stTagID.nextToken());
					tagIDs.add(tagID);
		}
	}
}
