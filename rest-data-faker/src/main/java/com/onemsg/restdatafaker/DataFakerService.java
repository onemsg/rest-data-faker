package com.onemsg.restdatafaker;

import static com.onemsg.restdatafaker.web.WebHandlers.requireNonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import com.onemsg.restdatafaker.exception.ExpressionInvalidException;
import com.onemsg.restdatafaker.exception.IdNotExistedException;
import com.onemsg.restdatafaker.exception.PathAlreadyExistedException;
import com.onemsg.restdatafaker.exception.ResponseStatusException;
import com.onemsg.restdatafaker.model.FakerInfoUpdate;

import io.vertx.core.json.JsonArray;
import net.datafaker.Faker;

public class DataFakerService {
    
    public static final String DEFAULT_LOCALE = "zh_CN";

    private static final Faker DEFAULT_FAKER = new Faker(new Locale(DEFAULT_LOCALE));

    private static final Map<String, Faker> fakers = new HashMap<>();
    private final Map<Integer, DataGenerator> generators = new HashMap<>();
    private final FakerInfoInMemoryStore store = new FakerInfoInMemoryStore();

    public FakerInfo get(int id) {
        return store.find(id);
    }

    public FakerInfo getByPath(String path) {
        return store.findByPath(path);
    }

    public Collection<FakerInfo> getAll() {
        return store.findAll();
    }

    public void create(FakerInfo fakerInfo) throws ResponseStatusException {
        try {
            DataGenerator.create(DEFAULT_FAKER, fakerInfo.expression());
            store.save(fakerInfo);
        } catch (ExpressionInvalidException e) {
            throw ResponseStatusException.create(400, e);
        } catch (PathAlreadyExistedException e) {
            throw ResponseStatusException.create(400, "Path [%s] 已存在", e.getPath());
        }
    }

    public void update(int id, FakerInfoUpdate update) throws ResponseStatusException {
        try {
            if (update.expression != null) {
                DataGenerator.create(DEFAULT_FAKER, update.expression);
            }

            store.update(id, update);
            generators.remove(id);
        } catch (ExpressionInvalidException e) {
            throw ResponseStatusException.create(400, e);
        } catch (IdNotExistedException e) {
            throw ResponseStatusException.create(400, "Id [%s] 不存在", e.getId());
        }
    }

    public void remove(int id) {
        store.remove(id);
        generators.remove(id);
    }

    public Object generatFakeData(String path, int limit) throws ResponseStatusException, ExpressionInvalidException {
        var fakerInfo = requireNonNull(store.findByPath(path), 404, null);
        var generator = requireNonNull(getDataGenerator(path), 404, null);
        if (fakerInfo.type() == FakerType.ARRAY) {
            JsonArray array = new JsonArray();
            Stream.generate(generator).limit(limit).forEach(array::add);
            return array;
        } else {
            return generator.get();
        }
    }

    private DataGenerator getDataGenerator(String path) throws ExpressionInvalidException{
        var fakerInfo = store.findByPath(path);
        if (fakerInfo == null) return null;
        var generator = generators.get(fakerInfo.id());
        if (generator == null) {
            var faker = fakers.computeIfAbsent(fakerInfo.locale(), l -> new Faker(new Locale(l)));
            generator = DataGenerator.create(faker, fakerInfo.expression());
            generators.put(fakerInfo.id(), generator);
        }
        return generator;
    }

    /**
     * Evaluation datafaker expressions
     * 
     * @param expression
     * @return the evaluated string expression
     * @throws ResponseStatusException if unable to evaluate the expression
     * @see net.datafaker.Faker.expression
     */
    public static String evaluationExpression(Faker faker, String expression) throws ResponseStatusException {
        try {
            return faker.expression(expression);
        } catch (Exception e) {
            throw ResponseStatusException.create(400, "Expression [%s] invalid", expression);
        }
    }

    public static String validateExpression(String expression) throws ResponseStatusException {
        return evaluationExpression(DEFAULT_FAKER, expression);
    }

}
