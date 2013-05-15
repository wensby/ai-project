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
	
	
	public static ArrayList<Item> readItemFromFile(String filepath)
	{
		ArrayList<Item> items = new ArrayList<Item>();

		try {
			FileReader fIS = new FileReader(filepath);
			BufferedReader bReader= new BufferedReader(fIS);
			
			boolean fileEnd = false;
			while (!fileEnd)
			{
				try {
					String line = bReader.readLine();
					fileEnd = (line == null);
					
					StringTokenizer sTok = new StringTokenizer(line,line);
					int id;
					String cat, keywords;
					
					id = Integer.parseInt(sTok.nextToken());
					cat = sTok.nextToken();
					keywords = sTok.nextToken();
					
					items.add(new Item(id, cat, keywords));
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fileEnd = true;
				}
			}
			
			try {
				bReader.close();
				fIS.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return items;
	}

}
