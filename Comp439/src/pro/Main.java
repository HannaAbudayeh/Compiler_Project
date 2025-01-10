package pro;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Scanner tt = new Scanner(System.in);
//        enter the full path for the test file like this C:\Users\Hanna\eclipse-workspace\comp439\hanna.txt
        System.out.println("Enter the full path to the test.txt:");
        String testloc = tt.nextLine();
        String PathHan = "";
        try {
        	PathHan = Files.readString(Path.of(testloc));
        } catch (IOException e){
            System.err.println("Error reading the path: " + e.getMessage());
            return; // stop if there was an error reading the file
        }

        Lexer lexer = new Lexer(PathHan);
        Parser parser = new Parser(lexer);
        parser.parse();
        System.out.println("parsing done with 0 errors");
        tt.close();
       }
}