package com.webl.keyvaluestore.sstable;

import com.webl.keyvaluestore.indexes.Indexes;
import com.webl.keyvaluestore.models.IndexEntry;
import com.webl.keyvaluestore.observers.FlushObserver;
import org.tinylog.Logger;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class SSTable implements FlushObserver {

    private RandomAccessFile raf;
    private final String baseFile;

    public SSTable(String filePath) {
        this.baseFile = filePath;
    }

    @Override
    public void onFlush(TreeMap<String, String> treeMap, Indexes indexes) throws IOException {
        try {
            String fileName = createNewFile();
            for (Map.Entry<String, String> entry : treeMap.entrySet()) {
                long offset = raf.getFilePointer();

                raf.writeUTF(entry.getKey());
                raf.writeUTF(entry.getValue());

                indexes.idx.put(entry.getKey(), new IndexEntry(fileName, offset));
                indexes.writeIndexesToFile(Map.of(entry.getKey(), new IndexEntry(fileName, offset)), "indexes.dat");
            }

            Logger.info(String.format("flushing memory table to disk %s", fileName));

        } catch (IOException e) {
            Logger.error(e.getMessage());
            throw new IOException(e);

        } finally {
            if (this.raf != null) {
                this.raf.close();
            }
        }
    }

    public String readFromSSTable(IndexEntry indexEntry) throws IOException {
        try {
            RandomAccessFile reader = new RandomAccessFile(indexEntry.ssTableName(), "r");
            Logger.info(String.format("sstable %s offset %s filesize %s", indexEntry.ssTableName(), indexEntry.offset(), reader.length()));

            reader.seek(indexEntry.offset());

            String keyRead = reader.readUTF();
            Logger.info(String.format("sstable key read %s", keyRead));
            return reader.readUTF();

        } catch (IOException e) {
            Logger.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private String createNewFile() throws IOException {
        String filePath = genFilePath();
        try {
            this.raf = new RandomAccessFile(filePath, "rw");

        } catch (IOException e) {
            Logger.error(e.getMessage());
            throw new IOException(e);
        }

        return filePath;
    }

    private String genFilePath() {
        return this.baseFile + "_" + System.currentTimeMillis() + ".sstable";
    }
}
