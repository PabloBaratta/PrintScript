package org.example;

import static org.example.Runner.readFileAsString;

public class Main {

    public static void main(String[] args) throws Exception {
        Runner.run("cli/src/main/java/org/example/file.txt");
        //System.out.println(readFileAsString("cli/src/main/java/org/example/file.txt"));
    }

}
