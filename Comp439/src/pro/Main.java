package pro;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String input = "";
        try {
//            input = new String(Files.readAllBytes(Paths.get("test.txt")));//make sure that file name is test.txt
//            input = new String(Files.readAllBytes(Paths.get("test1.txt"))); 
//            input = new String(Files.readAllBytes(Paths.get("test2.txt"))); 
//            input = new String(Files.readAllBytes(Paths.get("test3.txt"))); 
//            input = new String(Files.readAllBytes(Paths.get("test4.txt"))); 
//            input = new String(Files.readAllBytes(Paths.get("3.txt"))); 
//            input = new String(Files.readAllBytes(Paths.get("valid2.txt"))); 
//            input = new String(Files.readAllBytes(Paths.get("valid.txt"))); 
            input = new String(Files.readAllBytes(Paths.get("TestCode.txt"))); 


        } catch (IOException e) {
            System.err.println("Error reading from test.txt: " + e.getMessage());
            return; // Exit if there was an error reading the file
        }

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        parser.parse();
        System.out.println("parsing done with zero errors");
    }
}