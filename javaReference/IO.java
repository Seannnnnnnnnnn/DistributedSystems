package com.distributedsystems;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class IO {
    public static void main(String[] args) {
        streamInput();
        streamOutput();
    }

    static void streamInput(){
        /*
         Input can come from many sources: a console (from the user), a file or even the network.
         Java unifies these into Streams so we can treat them all the same.
         For reading text, the InputStreamReader class provides basic functionality to read characters
         from a sources. It's quite low level though. Here's how we might use InputStreamReader to access
         the console:
         */
        /*
         We don't want to read a Stream one character at a time though. That would be slow and difficult.
         Let's use a BufferedReader, which provides extra functionality on top of an InputStreamReader.
         Notice how we still use the InputStreamReader by passing it to the BufferedReader constructor.
         */
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        System.out.println("Enter some text:\n");
        try {
            String inputString = bufferedReader.readLine();
            System.out.format("your line was: %s\n", inputString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void streamOutput() {
        /*
         Output with Streams is similar, but we can make our life a little easier. The PrintWriter class provides
         some useful functions for formatting text that we'll make use of.
         Conveniently, we can wrap a PrintWriter directly around an output Stream.
         */
        PrintWriter printWriter = new PrintWriter(System.out);
        printWriter.println("Hello, world");
        printWriter.format("This is a number: %d\n", 10);
        printWriter.flush();  // note that we must call flush() to print all buffered data
    }
}
