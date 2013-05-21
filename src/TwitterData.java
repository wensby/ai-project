
import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;





public class TwitterData {
    private static ArrayList<String> User_profiles = new ArrayList<String>();


    public static void ReadFile_UserProfile(){
        try{
            String filename, folder;
            folder = "../data/";
            filename = "user_sns.txt";


            BufferedInputStream in_stream = new BufferedInputStream(new FileInputStream(folder+filename));
            InputStreamReader rd_stream = new InputStreamReader(in_stream);
            BufferedReader rd = new BufferedReader(rd_stream);

            int counter = 0;
            while (rd.ready()) {

                //User_profiles.add(rd.readLine());
                //rd.readLine();
                counter++;
                if(counter%10000 ==0){
                    System.gc();
                }


                //System.out.println(counter);
            }
            rd.close();

        } catch (Exception e){

            e.printStackTrace();
        }
    }




}
