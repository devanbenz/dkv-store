package com.webl.keyvaluestore.sstable;

import com.webl.keyvaluestore.observers.FlushObserver;
import org.tinylog.Logger;

import java.io.*;
import java.util.TreeMap;

public class SSTable implements FlushObserver {

    private final ObjectOutputStream dataOutputStream;

    public SSTable(String filePath) throws IOException {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            this.dataOutputStream = new ObjectOutputStream(fileOutputStream);

        } catch (IOException e) {
            Logger.error(e.getMessage());
            throw new IOException(e);
        }

        Logger.info("write ahead log initialized...");
    }
    @Override
    public void onFlush(TreeMap<String, String> treeMap) throws IOException {
        try {
            this.dataOutputStream.writeObject(treeMap);

            Logger.info("flushing memory table to disk");
        } catch (IOException e) {
            Logger.error(e.getMessage());

            throw new IOException(e);
        }
    }
}
