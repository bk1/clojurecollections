package net.itsky.java.clojurecollections.app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    private static enum Operation {
        ANALYZE_NUM,
        ANALYZE_STR,
        LINE_SORT,
        NUM_SORT;
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--help")) {
            usage(null);
        }
        if (args.length < 2) {
            usage("needs file name and operation");
        }
        Operation operation = Operation.valueOf(args[0]);
        if (operation == null) {
            usage("unknown operation " + args[0]);
        }
        File file = new File(args[1]);
        if (!file.exists()) {
            usage("file=" + file + " does not exist");
        }
        if (!file.canRead()) {
            usage("file=" + file + " is not readable");
        }
        if (!file.isFile()) {
            usage("file=" + file + " is not a regular file");
        }
        System.out.println("file=" + file);

        List<String> lines = new ArrayList<>(12_000_000);
        try (InputStream is = new FileInputStream(file)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
        } catch (IOException ex) {
            usage("exception ex=" + ex);
        }
        System.out.println("" + lines.size() + " lines");

        switch (operation) {
            case LINE_SORT -> {
                SortFileLines sortFileLines = new SortFileLines();
                sortFileLines.sortFileContent(lines);
            }
            case NUM_SORT -> {
                SortFileNumbers sortFileNumbers = new SortFileNumbers();
                sortFileNumbers.sortFileContent(lines);
            }
            case ANALYZE_NUM ->  {
                AnalyzeNum analyzeNum = new AnalyzeNum();
                analyzeNum.analyze(lines);
            }
            case ANALYZE_STR ->  {
                AnalyzeStr analyzeStr = new AnalyzeStr();
                analyzeStr.analyze(lines);
            }
        }
    }

    private static void usage(String msg) {
        if (msg != null) {
            System.out.println(msg);
        }
        System.out.println("""
                USAGE
                                
                $0 [ANALYZE|SORT] filename
                                
                reads a file name and sorts it with differnt methods for performance measurement
                """);
        if (msg != null) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
