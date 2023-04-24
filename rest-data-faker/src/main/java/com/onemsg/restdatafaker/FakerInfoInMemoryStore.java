package com.onemsg.restdatafaker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.onemsg.restdatafaker.exception.IdNotExistedException;
import com.onemsg.restdatafaker.exception.PathAlreadyExistedException;
import com.onemsg.restdatafaker.model.FakerInfoUpdate;

/**
 * FakerInfo store by in-memory
 */
public class FakerInfoInMemoryStore {

    private final AtomicInteger idGenerator = new AtomicInteger(1);

    private final Map<Integer, FakerInfo> store = new HashMap<>();
    private final Map<String, Integer> pathIndex = new HashMap<>();

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public FakerInfo find(int id) {
        r.lock();
        try {
            return store.get(id);
        } finally {
            r.unlock();
        }
    }

    public FakerInfo findByPath(String path) {
        r.lock();
        try {
            if (pathIndex.containsKey(path)) {
                return store.get(pathIndex.get(path));
            }
            return null;
        } finally {
            r.unlock();
        }
    }

    public Collection<FakerInfo> findAll() {
        r.lock();
        try {
            return Collections.unmodifiableCollection(new ArrayList<>(store.values()));
        } finally {
            r.unlock();
        }
    }

    /**
     * 保存
     * @param fakerInfo
     * @return
     * @throws PathAlreadyExistedException 如果 path 已存在
     */
    public int save(FakerInfo fakerInfo) throws PathAlreadyExistedException {
        w.lock();
        try {
            if (pathIndex.containsKey(fakerInfo.path())) {
                throw new PathAlreadyExistedException(fakerInfo.path());
            }
            int id = nextId();
            var now = fakerInfo.createdTime() != null ? fakerInfo.createdTime() : LocalDateTime.now();
            var saved = FakerInfo.builder(fakerInfo).id(id).createdTime(now).updatedTime(now).build();
            store.put(id, saved);
            pathIndex.put(saved.path(), id);
            return id;
        } finally {
            w.unlock();
        }
    }

    public void update(int id, FakerInfoUpdate update) throws IdNotExistedException {
        w.lock();
        try {
            if (!store.containsKey(id)) {
                throw new IdNotExistedException(id);
            }
            var source = store.get(id);
            var builder = FakerInfo.builder(source);
            if (update.path != null) {
                builder.path(update.path);
            }
            if (update.type != null) {
                builder.type(update.type);
            }
            if (update.name != null) {
                builder.name(update.name);
            }
            if (update.description != null) {
                builder.description(update.description);
            }
            if (update.expression != null) {
                builder.expression(update.expression);
            }
            if (update.locale != null) {
                builder.locale(update.locale);
            }
            if (update.delay != null) {
                builder.delay(update.delay);
            }
            var saved = builder.build();
            store.put(id, saved);
            pathIndex.put(saved.path(), id);
        } finally {
            w.unlock();
        }
    }

    public void remove(int id) {
        w.lock();
        try {
            var fakerInfo = store.remove(id);
            if (fakerInfo != null) {
                pathIndex.remove(fakerInfo.path());
            }
        } finally {
            w.lock();
        }
    }

    private int nextId() {
        return idGenerator.getAndIncrement();
    }
    
}