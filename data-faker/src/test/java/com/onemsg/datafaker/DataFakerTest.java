package com.onemsg.datafaker;

import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import net.datafaker.Faker;
import net.datafaker.fileformats.Format;

public class DataFakerTest {

    static final Faker faker = Faker.instance(Locale.CHINA);

    @Test
    public void testJson() {
        var json = Format.toJson()
            .set("name", () -> faker.expression("#{Name.first_name}"))
            .set("age", () -> faker.expression("#{number.number_between '15','50'}"))
            .build();

        for (int i = 0; i < 5; i++) {
            System.out.println(json.generate());
        }
    }

    @Test
    public void testExpression() {
        String name = faker.expression("#{number.number_between '1','10'}");
        System.out.println(name);

        System.out.println(Json.decodeValue("true").getClass());
    }

    @Test
    public void testCreateCreateSupplier0() throws Exception{
        String expression = """
                {
                    "name": {
                        "firstName": "#{Name.first_name}",
                        "lastName": "#{Name.last_name}"
                    },
                    "fullname": "#{Name.full_name}",
                    "age": "#{number.number_between '15','50'}"
                }
        """;

        JsonObject jsonExpression = (JsonObject) Json.decodeValue(expression);
        String locale = "ja";
        Faker faker2 = Faker.instance(new Locale(locale));
        String data = DataFaker.createFormatJson(faker2, jsonExpression).generate();
        System.out.println(data);
        System.out.println( ( (JsonObject) Json.decodeValue(data) ).encodePrettily() );
    }

}
