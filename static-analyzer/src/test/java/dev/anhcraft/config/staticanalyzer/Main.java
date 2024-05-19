package dev.anhcraft.config.staticanalyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    StaticSchemaScanner.analyzeJavaSource(new FileInputStream("C:\\Users\\huynh\\IdeaProjects\\config\\example\\src\\main\\java\\model\\Item.java"));
  }
}
