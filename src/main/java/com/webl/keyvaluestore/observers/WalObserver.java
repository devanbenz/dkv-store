package com.webl.keyvaluestore.observers;

import com.webl.keyvaluestore.models.KeyValue;

import java.io.IOException;
import java.util.List;

public interface WalObserver {
    void writeLog(String key, String value) throws IOException;
    List<KeyValue> readLog() throws IOException;
    void clearLog();
}
