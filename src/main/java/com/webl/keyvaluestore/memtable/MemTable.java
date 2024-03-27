package com.webl.keyvaluestore.memtable;

import com.webl.keyvaluestore.models.KeyValue;
import com.webl.keyvaluestore.observers.FlushObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MemTable {
    private final TreeMap<String, String> hashMap;
    private final List<FlushObserver> observers = new ArrayList<>();

    public MemTable() {
        this.hashMap = new TreeMap<>();
    }

    public MemTable(List<KeyValue> maybeKeyValues) {
        super();
        this.hashMap = new TreeMap<>();
        if (maybeKeyValues != null) {
            maybeKeyValues.forEach(kv -> this.hashMap.put(kv.key(), kv.value()));
        }
    }

    public String getValue(String key) {
        return this.hashMap.get(key);
    }

    public void insertKeyValue(String key, String value) {
        this.hashMap.put(key, value);
    }

    public void setValue(String key, String value) {
        this.hashMap.put(key, value);
    }

    public void registerSSTableObserver(FlushObserver flushObserver) {
        observers.add(flushObserver);
    }

    public void flushToDisk() {
        this.observers.forEach(obs -> {
            try {
                obs.onFlush(this.hashMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
