package com.webl.keyvaluestore.memtable;

import com.webl.keyvaluestore.models.KeyValue;
import com.webl.keyvaluestore.observers.FlushObserver;
import com.webl.keyvaluestore.observers.WalObserver;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MemTable {
    private TreeMap<String, String> hashMap;
    private final List<FlushObserver> flushObservers = new ArrayList<>();
    private WalObserver logObserver = null;

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

    public void insertKeyValue(String key, String value) throws IOException {
        this.hashMap.put(key, value);

        if (this.logObserver != null) {
            this.logObserver.writeLog(key, value);
        }
    }

    public void registerSSTableObserver(FlushObserver flushObserver) {
        flushObservers.add(flushObserver);
    }

    public void registerWalObserver(WalObserver walObserver) {
        this.logObserver = walObserver;
    }

    public void flushToDisk() {
        this.flushObservers.forEach(obs -> {
            try {
                obs.onFlush(this.hashMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Logger.info("flushing in memory tree - data is in sstable");
        this.hashMap = new TreeMap<>();
    }
}
