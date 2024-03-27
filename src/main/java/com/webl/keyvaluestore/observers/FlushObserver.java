package com.webl.keyvaluestore.observers;

import java.io.IOException;
import java.util.TreeMap;

public interface FlushObserver {
    void onFlush(TreeMap<String, String> treeMap) throws IOException;
}
