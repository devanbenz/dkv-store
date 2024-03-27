package com.webl.keyvaluestore.wal;

import com.webl.keyvaluestore.models.KeyValue;
import org.tinylog.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WriteAheadLog {
    private final FileOutputStream fileOutputStream;
    private final DataOutputStream dataOutputStream;
    private final DataInputStream dataInputStream;
    private final String filePath;

    public WriteAheadLog(String filePath) throws IOException {
        this.filePath = filePath;
        try {
            this.fileOutputStream = new FileOutputStream(filePath);
            this.dataOutputStream = new DataOutputStream(this.fileOutputStream);
            FileInputStream fileInputStream = new FileInputStream(filePath);
            this.dataInputStream = new DataInputStream(fileInputStream);

        } catch (IOException e) {
            Logger.error(e.getMessage());
            throw new IOException(e);
        }

        Logger.info("write ahead log initialized...");
    }

    public void writeLog(String key, String val) throws IOException {
        Logger.info("writing to WAL");
        try {
            this.dataOutputStream.writeInt(key.length());
            this.dataOutputStream.writeInt(val.length());
            this.dataOutputStream.writeBytes(key);
            this.dataOutputStream.writeBytes(val);
        } catch (IOException e) {
            Logger.error(e.getMessage());
            throw new IOException(e);
        }
    }

    public List<KeyValue> readLog() throws IOException {
        Logger.info("reading from log");
        List<KeyValue> keyValues = new ArrayList<>();
        try {
            while (this.dataInputStream.available() > 0) {
                KeyValue kv = this.getKeyAndValue();
                keyValues.add(kv);
            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
            throw new IOException(e);
        }

        return keyValues;
    }

    public void closeWriter() throws IOException {
        if (this.dataOutputStream != null) {
            this.dataOutputStream.close();
        }

        if (this.fileOutputStream != null) {
            this.fileOutputStream.close();
        }
    }

    public void clearLog() {
        Path path = Paths.get(this.filePath);
        try {
            boolean success = Files.deleteIfExists(path);
            if (success) {
                Logger.info("wal cleared");
            } else {
                Logger.info("file does not exist or could not be deleted");
            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    private KeyValue getKeyAndValue() throws IOException {
        int keyLength = this.dataInputStream.readInt();
        int valueLength = this.dataInputStream.readInt();

        byte[] keyBytes = new byte[keyLength];
        byte[] valueBytes = new byte[valueLength];

        this.dataInputStream.readFully(keyBytes);
        this.dataInputStream.readFully(valueBytes);

        String key = new String(keyBytes);
        String value = new String(valueBytes);

        return new KeyValue(key, value);
    }
}
