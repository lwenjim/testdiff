package com.example.langbase;

import java.io.FileReader;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        String fileName = "/tmp/newFile4.txt";
        try (Scanner sc = new Scanner(new FileReader(fileName))) {
            while (sc.hasNextLine()) { 
                String line = sc.nextLine();
                System.out.println(line);
            }
        }
        try (Scanner sc = new Scanner(new FileReader(fileName))) {
            sc.useDelimiter("\\|"); 
            while (sc.hasNext()) { 
                String str = sc.next();
                System.out.println(str);
            }
        }
        try (Scanner sc = new Scanner(new FileReader(fileName))) {
            sc.useDelimiter("\\|"); 
            while (sc.hasNextInt()) { 
                int intValue = sc.nextInt();
                System.out.println(intValue);
            }
        }
    }
}
