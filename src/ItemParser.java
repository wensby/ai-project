import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 
 * @author Steel
 *
 */
public class ItemParser {
	
	
	public static Item readItemFromFile(String line)
	{
		StringTokenizer sTok = new StringTokenizer(line);
		int id;
		String cat, keywords;
		
		id = Integer.parseInt(sTok.nextToken());
		cat = sTok.nextToken();
		keywords = sTok.nextToken();
						
		return new Item(id, cat, keywords);
	}

}
