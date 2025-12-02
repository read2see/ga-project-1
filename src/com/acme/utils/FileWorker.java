package com.acme.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileWorker {

    public static void createFile(String fileName, String inputData){

        try{
            BufferedWriter newFile = new BufferedWriter(new FileWriter(fileName));
            newFile.write(inputData);
            newFile.close();
        }catch(IOException e){
            System.out.println(e);
        }

    }

    public static void appendTofile(String filePath, String inputData){
        try{
            BufferedWriter existingFile = new BufferedWriter(new FileWriter(filePath, true));
            existingFile.append(inputData);
            existingFile.close();
        }catch(IOException e){
            System.out.println(e);
        }

    }

    public static List readFile(String filePath){

        List<String> lines = new ArrayList<>();

        try{
            BufferedReader file = new BufferedReader(new FileReader(filePath));
            lines = file.lines().toList();
            file.close();
        }catch(IOException e){
            System.out.println(e);
        }
        return lines;
    }


}
