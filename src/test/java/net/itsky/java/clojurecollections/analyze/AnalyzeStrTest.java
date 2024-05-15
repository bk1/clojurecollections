package net.itsky.java.clojurecollections.analyze;

import net.itsky.java.sort.TestData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

public class AnalyzeStrTest {
    private AnalyzeStr analyzeStr = new AnalyzeStr();

    @Test
    void testAnalyzeLong() {
        List<String> listWithMultiples
                = IntStream.range(0, 100)
                .mapToObj(i -> TestData.UKRAINIAN_WORDS)
                .flatMap(List::stream)
                .toList();
        analyzeStr.analyzeFileContent(listWithMultiples);
    }
}
