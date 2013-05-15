import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class ActionParser {
	
	public static ArrayList<Action> readActionFromFile(String filepath)
	{
		ArrayList<Action> actions = new ArrayList<Action>();

		try {
			FileReader fIS = new FileReader(filepath);
			BufferedReader bReader= new BufferedReader(fIS);
			
			boolean fileEnd = false;
			while (!fileEnd)
			{
				try {
					String line = bReader.readLine();
					fileEnd = (line == null);
					
					if (!fileEnd)
					{
						StringTokenizer st = new StringTokenizer(line);
						
						int userSource = Integer.parseInt(st.nextToken());
						int userDestination = Integer.parseInt(st.nextToken());
						int references = Integer.parseInt(st.nextToken());
						int retweets = Integer.parseInt(st.nextToken());
						int comments = Integer.parseInt(st.nextToken());
						
						actions.add(new Action(userSource, userDestination,
								references, retweets, comments));
					}
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
		
		return actions;
	}

}

