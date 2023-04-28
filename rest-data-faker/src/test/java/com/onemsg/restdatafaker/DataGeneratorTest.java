package com.onemsg.restdatafaker;

import org.junit.jupiter.api.Test;

import net.datafaker.Faker;

public class DataGeneratorTest {
    
    private Faker faker = new Faker();

    @Test
    public void testJsonBuild() {

        var r = faker.expression("123");
        System.out.println(r.getClass());
    }
}
