import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.ErrorManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.plaf.IconUIResource;
public class Test {
    //private static ErrorManager logger;
    //private static Logger logger = LogManager.getLogger(Test.class);
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        Lucene testLucene = new Lucene();
        boolean build_index = false;
        String build_index1;
        System.out.println("Do you want to build the index for the files?(y/n)");
        try{
            build_index1 = scanner.next();
            if(build_index1.equals("y")){
                build_index = true;
            }
            else if(build_index1.equals("n")){
                build_index = false;
            }
        }catch(Exception e){
            e.getMessage();
            //logger.error(e.getMessage());
            build_index = false;
        }
        if (build_index) {
            testLucene.testIndex();
        }


        System.out.println("Please choose the content you want to search:");
        System.out.println("1.FileName; 2.content");
        String search;
        int index;
        try{
            search = scanner.next();
            index = Integer.parseInt(search);
        }catch(Exception e){
            e.getMessage();

            index = 0;
        }
        System.out.println("Please type down anything you want to search:");
        String info;
        try{
            info = scanner.next();
        }catch(Exception e){
            e.getMessage();
            //logger.error(e.getMessage());
            info = "";
        }
        System.out.println("Are you sure about the exact information you have just typed?(y/n)");
        String accuracy;
        int mode;
        try{
            accuracy = scanner.next();
            if(accuracy.equals("y")){
                mode = 1;
            }
            else if(accuracy.equals("n")){
                mode = 2;
            }
            else{
                mode = 0;
            }
        }catch(Exception e){
            e.getMessage();
            //logger.error(e.getMessage());
            mode = 0;
        }
        System.out.println("Do you want to print the infomation to files?(y/n)");
        String printToFile;
        boolean print;
        try{
            printToFile = scanner.next();
            if(printToFile.equals("y")){
                print = true;
            }
            else if(printToFile.equals("n")){
                print = false;
            }
            else{
                print =false;
            }
        }catch(Exception e){
            e.getMessage();
            //logger.error(e.getMessage());
            print = false;
        }
        HashMap results = testLucene.testSearch(index,info,mode,print);
        System.out.println(results);
    }
}
