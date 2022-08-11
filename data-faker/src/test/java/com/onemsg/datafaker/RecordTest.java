package com.onemsg.datafaker;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class RecordTest {
    

    @Test
    public void testStream() {
        String data = IntStream.range(1, 10)
            .mapToObj(i -> i + "")
            .collect(Collectors.joining(", ", "[", "]"));

        System.out.println(data);
    }
}
