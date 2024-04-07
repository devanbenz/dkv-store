package com.webl.keyvaluestore.indexes;

import com.webl.keyvaluestore.models.IndexEntry;
import org.tinylog.Logger;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Indexes {
    public Map<String, IndexEntry> idx = new ConcurrentHashMap<>();

    public Indexes() {}

    public void readIndexesIntoMemory(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Logger.info("no indexes on disk");
            return;
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
            this.idx = (Map<String, IndexEntry>) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeIndexesToFile(Map<String, IndexEntry> indexEntryMap, String filePath) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
            oos.writeObject(indexEntryMap);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
