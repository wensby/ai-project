import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;





public class TwitterData {
    private static ArrayList<String> User_profiles = new ArrayList<String>();

    public static void ReadFile_UserProfile(){
        try{
            String file_path = "../data/user_profile.txt";
            File file = new File(file_path);
            BufferedReader in = new BufferedReader(new FileReader(file));

            while (in.ready()) {
                User_profiles.add(in.readLine());
            }


            //System.out.println(in.readLine());

            in.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }




}
