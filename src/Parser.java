import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	public int[] user_profile_txt(String input_line){
		//Given line from user_profile_txt: Parse out (userID) (birthyear) (gender) (#tweets) (Tag-IDs) 
		Pattern p = Pattern.compile("([0-9]+?) ([0-9]+?) ([0-9]+?) ([0-9]+?) ([0-9]+?;.*+)");
		Matcher m = p.matcher(input_line);
		
		m.find();
		int userID =Integer.parseInt(m.group(1));
		int birthYear =Integer.parseInt(m.group(2));
		int gender =Integer.parseInt(m.group(3));
		int tweets =Integer.parseInt(m.group(4));
		
		String tagIDs_not_parsed =m.group(5);
		//Given (Tag-IDs) extracted from input line, extract each tag
		
		List<Integer> tagIDs= new ArrayList<Integer>();
		Pattern p = Pattern.compile("([0-9]+?);");
		Matcher m = p.matcher(tagIDs_not_parsed);
		while(m.find()){
			Integer tagID = Integer.parseInt(m.group(1));
			
		}
	}
}
