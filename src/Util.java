import java.util.ArrayList;

public class Util {

    public static void count_file_lines(String file_place) throws Exception{
        //EX: file_place = "../data/rec_log_train.txt";
        Parser.txt file = new Parser.txt(file_place);
        int counter = 0;
        while(file.hasNext()){
            file.next();
            counter++;
        }
        System.out.println("Final line count of file "+ file_place + " :   " + counter);
    }



}
