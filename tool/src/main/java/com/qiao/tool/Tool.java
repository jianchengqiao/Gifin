package com.qiao.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Tool {
    public static void main(String[] args) {
        System.out.println("start");
        File file = new File("tool/src/res");
        search(file);
    }

    private static void search(File file) {
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()){
                    search(file);
                    return false;
                }
                if (file.getName().endsWith(".xml"))
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine())!=null){
                        builder.append(line);
                    }
                    reader.close();
                    boolean contains = builder.toString().contains("drawable/bby");
                    if (contains){
                        System.out.println(file.getName());
                        System.out.println(builder.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }
}
